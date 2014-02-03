package emoapp;

import processing.core.PApplet;
import processing.data.Table;
import processing.data.TableRow;
import controlP5.ControlEvent;
import controlP5.ControlP5;
import controlP5.RadioButton;

@SuppressWarnings("serial")
public class EmoApp extends PApplet {
	// edk (headset) conn.
	private EdkConn ec;
	// headset data
	private float exc, eng, med, frs;

	private String[] effects = { "star", "bluestar", "brush" };
	private String eff;
	private Table emoValues = new Table();

	// loaded csv table data
	private Table loadedValues = new Table();
	boolean loading = false;
	boolean loaded = false;
	int loadedRowCounter = 0;

	// UI controls
	ControlP5 cp5;
	RadioButton r;
	// vertical spacing between cp5 controls
	int vs = 30;

	public void setup() {
		size(displayWidth / 2, displayHeight / 2, P3D);
		background(0);
		// custom font for messages
		textFont(loadFont("Monaco-12.vlw"));

		frameRate(10);
		// Connect to headset
		ec = new EdkConn(this);

		// add columns to emoValues table
		emoValues.addColumn("exc");
		emoValues.addColumn("eng");
		emoValues.addColumn("med");
		emoValues.addColumn("frs");

		cp5 = new ControlP5(this);
		cp5.addButton("connect");
		cp5.addButton("load").setPosition(10, vs * 2);
		cp5.addButton("save").setPosition(10, vs * 3);
		r = cp5.addRadioButton("selectEffect").setPosition(10, vs * 4)
				.setSpacingRow(10).setSize(15, 15);

		for (int i = 0; i < effects.length; i++) {
			r.addItem(effects[i], i + 1);
		}
		// default effect is "0" in array of effect names
		eff = effects[0];
		// set radio button "0" to active
		r.activate(0);
	}

	// Draw is used like in the processing tool.
	public void draw() {
		background(0);
		if (ec.connected && !loaded && !loading) {
			// Run headset event listener loop each time draw() is called
			boolean stateChanged = ec.edkRun();
			if (stateChanged) {
				exc = ec.getExcitement();
				eng = ec.getEngagement();
				med = ec.getMeditation();
				frs = ec.getFrustration();

				TableRow newRow = emoValues.addRow();
				newRow.setFloat("exc", exc);
				newRow.setFloat("eng", eng);
				newRow.setFloat("med", med);
				newRow.setFloat("frs", frs);

				for (int i = 0; i < emoValues.getColumnCount(); i++) {
					drawEffect(emoValues.getColumnTitle(i), newRow.getFloat(i));
				}
			}
		}
		if (loading) {
			text("loading saved data", width / 2 - 100, height / 2);
			println("loading");
		} else if (loaded && !loading) {
			if (loadedRowCounter < loadedValues.getRowCount()) {
				TableRow row = loadedValues.getRow(loadedRowCounter);
				for (int i = 0; i < loadedValues.getColumnCount(); i++) {
					text("playing loaded", width - 150, height - 30);
					drawEffect(loadedValues.getColumnTitle(i), row.getFloat(i));
				}
				loadedRowCounter++;
			} else {
				text("done playing", width - 200, height - 30);
				loaded = false;
				loadedRowCounter = 0;
			}
		}
	}

	// handles "start" button press, starts connection with headset/emocomposer
	// emulator
	public void connect() {
		// if param="1" conn. to headset, "2" conn. to emocomposer
		ec.edkConn(2);
	}

	// handles "load" button press, loads saved csv table data
	public void load() {
		thread("loadEmoTable");
	}

	// handles "save" button press
	public void save() {
		saveTable(emoValues, "emodata/saved.csv");
		text("saved", width - 50, height - 30);
	}

	// loads csv table of saved data, called by load()
	// runs as a thread (not to hang up anim. loop)
	public void loadEmoTable() {
		try {
			loading = true;
			loadedValues = loadTable("emodata/saved.csv", "header");
			loaded = true;
		} catch (Exception e) {
			println("No saved data found.");
		}
		loading = false;
	}

	// handles "selectEffect" radioButton
	public void selectEffect(int a) {
		eff = effects[a - 1];
	}

	// handles keyboard events
	public void keyPressed() {
		switch (key) {
		// cases 1-3 handle switching between effects
		case ('1'):
			r.activate(0);
			break;
		case ('2'):
			r.activate(1);
			break;
		case ('3'):
			r.activate(2);
			break;
		}
	}

	// handles ControlP5 events
	public void controlEvent(ControlEvent theEvent) {
		// handles keyboard events for "selectEffect" radioButton list
		if (theEvent.isFrom(r)) {
			int a = (int) theEvent.getValue();
			eff = effects[a - 1];
		}
	}

	// draws an effect according to current value of "eff" variable
	public void drawEffect(String effName, float value) {
		switch (eff) {
		case "star":
			new Star(this, value, 3, effName);
			break;

		case "bluestar":
			new BlueStar(this, value, 3);
			break;

		case "brush":
			new Brush(this);
			break;
		}
	}

	public static void main(String _args[]) {
		PApplet.main(new String[] { emoapp.EmoApp.class.getName() });
	}
}
