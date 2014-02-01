package emoapp;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import processing.core.PFont;
import processing.data.Table;
import processing.data.TableRow;

@SuppressWarnings("serial")
public class EmoApp extends PApplet {
	// edk (headset) conn.
	private EdkConn ec;
	// headset data
	private float exc, eng, med, frs;
	// int blink;
	// blink circle coords
	// float blinkX, blinkY;

	// circle to represent blink
	// private PShape blinkCrc;

	private String eff;
	private Table emoValues = new Table();
	private Table loadedValues = new Table();
	boolean loading = false;
	boolean loaded = false;
	int loadedRowCounter = 0;
	
	// Setup can be used like in the processing tool.
	public void setup() {
		// Set the canvas size
		size(displayWidth / 2, displayHeight / 2, P3D);
		background(0);
		// anti aliasing!
		smooth();
		PFont fnt = loadFont("Monaco-12.vlw");
		textFont(fnt);
		textLeading(17);
		// default effect is
		eff = "star";
		frameRate(10);
		// Connect to headset
		ec = new EdkConn(this);
		ec.edkConn();
		emoValues.addColumn("exc");
		emoValues.addColumn("eng");
		emoValues.addColumn("med");
		emoValues.addColumn("frs");
	}

	// Draw is used like in the processing tool.
	public void draw() {
		// Redraw the background with black
		background(0);
		showInfo();
		// spotLight(255, 255, 0, width / 2, height / 2, 400, 0, 0, -1, PI / 4,
		// 2);
		// camera(mouseX, mouseY, (height / 2) / tan(PI / 6), width / 2, height
		// / 2, 0, 0, 1, 0);

		// blink = ec.getBlink();

		if (!loaded && !loading) {
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
		if(loading){
			//background(0);
			text("loading saved data", width/2, height/2);
			System.out.println("loading");
		}else if(loaded && !loading){
			text("playing loaded", width - 50, height - 30);
			if(loadedRowCounter < loadedValues.getRowCount()){
				TableRow row = loadedValues.getRow(loadedRowCounter);
				for (int i = 0; i < loadedValues.getColumnCount(); i++) {
					//drawEffect(loadedValues.getColumnTitle(i), row.getFloat(i)); // ???
					drawEffect(loadedValues.getColumnTitle(i),row.getFloat(i));
					//System.out.println(loadedValues.getColumnTitle(i)+", "+ row.getFloat(i));
				}
				loadedRowCounter++;
			}else{
				text("done playing", width - 50, height - 30);
				loaded = false;				
			}
		}
	}

	public void drawEffect(String effName, float value) {
		// create new effect
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

	public void keyReleased() {
		if (keyCode == RIGHT)
			eff = "star";
		else if (keyCode == LEFT)
			eff = "bluestar";
		else if (keyCode == UP)
			eff = "brush";
		else if (key == 's') {
			saveTable(emoValues, "emodata/new.csv");
			text("saved", width - 50, height - 30);
		}
		else if (key == 'l') {
			thread("loadEmoTable");
		}
	}

	public void loadEmoTable() {
		loading = true;
		loadedValues = loadTable("emodata/new.csv", "header");
		loading = false;
		loaded = true;
	}

	public void showInfo() {
		String s = "";
		s += "Toggle effects:\n";
		s += "right arrow: star\n";
		s += "left arrow: bluestar\n";
		s += "up arrow: brush\n";
		s += "l: load\n";
		s += "s: save\n";
		text(s, 10, 20);
	}

	public static void main(String _args[]) {
		PApplet.main(new String[] { emoapp.EmoApp.class.getName() });
	}
}
