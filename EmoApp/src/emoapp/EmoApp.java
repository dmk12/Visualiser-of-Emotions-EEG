package emoapp;

import java.io.File;

import processing.core.PApplet;
import processing.data.Table;
import processing.data.TableRow;
import controlP5.ControlEvent;

@SuppressWarnings("serial")
public class EmoApp extends PApplet {
	// edk (headset) conn.
	EdkConn ec;
	// headset data
	float exc = 0, eng = 0, med = 0, frs = 0;
	float smile = 0, clench = 0;
	int blink = 0, winkL = 0, winkR = 0;

	Table emoValuesTbl = new Table();
	// loaded csv table data
	Table loadedValues = new Table();
	boolean loading = false;
	boolean loaded = false;
	int loadedRowCounter = 0;

	ParticleSphere pSph;
	GUI gui;

	public void setup() {
		size(1200, 800, P3D);
		background(0);
		frameRate(24);

		// Connect to headset
		ec = new EdkConn(this);
		gui = new GUI(this);
		pSph = new ParticleSphere(this);
		pSph.setup();

		// add columns to emoValues table
		emoValuesTbl.addColumn("exc");
		emoValuesTbl.addColumn("eng");
		emoValuesTbl.addColumn("med");
		emoValuesTbl.addColumn("frs");
		emoValuesTbl.addColumn("blink");
		emoValuesTbl.addColumn("smile");
		emoValuesTbl.addColumn("clench");
		emoValuesTbl.addColumn("winkL");
		emoValuesTbl.addColumn("winkR");
	}

	public void initEmoValues() {
		exc = 0;
		eng = 0;
		med = 0;
		frs = 0;
		blink = 0;
		smile = 0;
		clench = 0;
		winkL = 0;
		winkR = 0;
	}

	// Draw is used like in the processing tool.
	public void draw() {
		background(0);
		// live data
		if (ec.connected && !loaded && !loading) {
			// Run headset event listener loop each time draw() is called
			boolean stateChanged = ec.edkRun();
			// avgContactQlty will be more than 2 only when using emocomposer
			// only goes up to 2 with headset
			if (ec.avgContactQlty >= 1 && ec.avgContactQlty <= 4) {
				if (stateChanged) {
					exc = ec.excitement;
					eng = ec.engagement;
					med = ec.meditation;
					frs = ec.frustration;
					blink = ec.blink;
					smile = ec.smile;
					clench = ec.clench;
					winkL = ec.winkLeft;
					winkR = ec.winkRight;
					if (gui.recording) {
						TableRow newRow = emoValuesTbl.addRow();
						newRow.setFloat("exc", exc);
						newRow.setFloat("eng", eng);
						newRow.setFloat("med", med);
						newRow.setFloat("frs", frs);
						newRow.setInt("blink", blink);
						newRow.setFloat("smile", smile);
						newRow.setFloat("clench", clench);
						newRow.setInt("winkL", winkL);
						newRow.setInt("winkR", winkR);
					}
					pSph.draw(exc, eng, med, frs, blink, smile, clench, winkL,
							winkR);
				}
			} else {
				initEmoValues();
			}
			if (gui.reset) {
				emoValuesTbl.clearRows();
				gui.reset = false;
			}
			gui.update(ec.headsetOn, ec.signal, ec.avgContactQlty);
		}

		// loaded data
		if (loading) {
			text("loading saved data", width / 2 - 100, height / 2);
			println("loading");
		} else if (loaded && !loading) {

			if (loadedRowCounter < loadedValues.getRowCount()) {

				text("playing loaded", width - 150, height - 30);
				exc = loadedValues.getFloat(loadedRowCounter, "exc");
				eng = loadedValues.getFloat(loadedRowCounter, "eng");
				med = loadedValues.getFloat(loadedRowCounter, "med");
				frs = loadedValues.getFloat(loadedRowCounter, "frs");
				blink = loadedValues.getInt(loadedRowCounter, "blink");
				smile = loadedValues.getFloat(loadedRowCounter, "smile");
				clench = loadedValues.getFloat(loadedRowCounter, "clench");
				winkL = loadedValues.getInt(loadedRowCounter, "winkL");
				winkR = loadedValues.getInt(loadedRowCounter, "winkR");

				loadedRowCounter++;

			} else {
				text("done playing", width - 200, height - 30);
				loaded = false;
				loadedRowCounter = 0;
				initEmoValues();
			}
			pSph.draw(exc, eng, med, frs, blink, smile, clench, winkL, winkR);
		}
	}

	public void controlEvent(ControlEvent theEvent) {
		// connect button is handled here and not in GUI
		// in order to avoid creating an instance of EdkConn inside GUI
		if (theEvent.isFrom("connect")) {
			// if param="1" conn. to headset, "2" conn. to emocomposer
			ec.edkConn(1);
		}
		if (ec.connError) {
			gui.errorMsg(ec.errorMsg);
		}
		if(ec.connected){
			gui.clearErrorMsg();
			gui.handler(theEvent);
		}
	}

	public void fileSelected(File selection) {
		if (selection == null) {
			println("Window was closed or the user hit cancel.");
		} else {
			String filename = selection.getAbsolutePath();

			if (!filename.endsWith("csv")) {
				filename = filename.concat(".csv");
			}
			// will throw uncatchable exception if file is open
			// while trying to save to it
			// but app will keep working
			saveTable(emoValuesTbl, filename);
		}
	}

	// handles "load" button press, loads saved csv table data
	public void load() {
		ec.disconnect();
		try {
			loading = true;
			loadedValues =
					loadTable("emodata/saved.csv", "header");
			loaded = true;
		} catch (Exception e) {
			println("No saved data found.");
		}
		loading = false;
	}

	public static void main(String _args[]) {
		PApplet.main(new String[] { emoapp.EmoApp.class.getName() });
	}
}
