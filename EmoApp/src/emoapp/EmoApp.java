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
	int connTo = 1;
	// headset data
	float exc = 0, eng = 0, med = 0, frs = 0;
	float smile = 0, clench = 0;
	int blink = 0, winkL = 0, winkR = 0;

	Table emoValuesTbl = new Table();
	// loaded csv table data
	Table loadedValuesTbl = new Table();
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

	// Draw is used like in the processing tool.
	public void draw() {
		background(0);

		// live data
		if (ec.connected && !loaded && !loading) {
			// Run headset event listener loop each time draw() is called
			ec.edkRun();
			// avgContactQlty will be more than 2 only when using emocomposer
			// only goes up to 2 with headset
			if (ec.avgContactQlty >= 1 && ec.avgContactQlty <= 4) {
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
			if (gui.resetRec) {
				emoValuesTbl.clearRows();
				gui.resetRec = false;
			}
			gui.updateConnInfo(ec.headsetOn, ec.signal, ec.avgContactQlty);
			gui.updateRecClock();
		}

		// loaded data
		if (loading) {
			gui.loadHandler("loading");
		} else if (loaded && !loading) {

			if (loadedRowCounter < loadedValuesTbl.getRowCount()) {

				gui.loadHandler("playing");
				// checkColumnIndex() creates the column if it doesn't exist
				loadedValuesTbl.checkColumnIndex("exc");
				exc = loadedValuesTbl.getFloat(loadedRowCounter, "exc");

				loadedValuesTbl.checkColumnIndex("eng");
				eng = loadedValuesTbl.getFloat(loadedRowCounter, "eng");

				loadedValuesTbl.checkColumnIndex("med");
				med = loadedValuesTbl.getFloat(loadedRowCounter, "med");

				loadedValuesTbl.checkColumnIndex("frs");
				frs = loadedValuesTbl.getFloat(loadedRowCounter, "frs");

				loadedValuesTbl.checkColumnIndex("blink");
				blink = loadedValuesTbl.getInt(loadedRowCounter, "blink");

				loadedValuesTbl.checkColumnIndex("smile");
				smile = loadedValuesTbl.getFloat(loadedRowCounter, "smile");

				loadedValuesTbl.checkColumnIndex("clench");
				clench = loadedValuesTbl.getFloat(loadedRowCounter, "clench");

				loadedValuesTbl.checkColumnIndex("winkL");
				winkL = loadedValuesTbl.getInt(loadedRowCounter, "winkL");

				loadedValuesTbl.checkColumnIndex("winkR");
				winkR = loadedValuesTbl.getInt(loadedRowCounter, "winkR");

				loadedRowCounter++;

			} else {
				playbackDone();
				gui.tlFilename.setText("Done playing");
			}
			pSph.draw(exc, eng, med, frs, blink, smile, clench, winkL, winkR);
		}
	}

	public void playbackDone() {
		loading = false;
		loaded = false;
		loadedRowCounter = 0;
		gui.loadHandler("done");
	}

	public void controlEvent(ControlEvent theEvent) {
		// delegate some event handlers to GUI class
		// as some things are easier to control from
		gui.handler(theEvent);

		// connect button is handled here and not in GUI
		// in order to avoid creating an instance of EdkConn inside GUI
		if (theEvent.isFrom("connect")) {
			// if param="1" conn. to headset, "2" conn. to emocomposer
			ec.edkConn(connTo);
			// connection error
			if (ec.connError) {
				gui.errorMsg(ec.errorMsg);
			} else {
				// if conn successful hide reconnect btn & clear err msg
				gui.clearErrorMsg();
				gui.gWelcome.hide();
				gui.setup();
			}
		}

		// reconnect button, shows after loaded done playing
		if (theEvent.isFrom("reconnect")) {
			// if not connected, attempt to reconnect
			if (!ec.connected) {
				playbackDone();
				ec.edkConn(connTo);
				// if conn error show error message
				if (ec.connError) {
					gui.errorMsg(ec.errorMsg);
				} else {
					// if conn successful hide reconnect btn & clear err msg
					gui.bReconnect.hide();

					gui.tlFilename.setText("");
					gui.clearErrorMsg();
				}
			}
		}

		// switch between headset/emoComposer
		if (theEvent.isFrom("connTo")) {
			ec.disconnect();
			playbackDone();
			gui.bReconnect.hide();
			if (gui.toggleConnTo.getState()) {
				connTo = 1;
				gui.toggleConnTo.setCaptionLabel("headset");
			} else {
				connTo = 2;
				gui.toggleConnTo.setCaptionLabel("emocomposer");
			}
			ec.edkConn(connTo);
			// if conn error show error message
			if (ec.connError) {
				gui.errorMsg(ec.errorMsg);
			} else {
				// if conn successful hide reconnect btn & clear err msg
				gui.clearErrorMsg();
			}
		}

		// change gui when conn state changes (live/loaded or
		// headset/emocomposer)
		if (ec.connected) {
			// live data, recording panel visible
			gui.gRec.show();
			gui.tlConn.setText("Live data");
			// switch between headset and emocomposer
			if (connTo == 1) {
				gui.tlConnTo.setText("Headset mode");
			} else if (connTo == 2) {
				gui.tlConnTo.setText("EmoComposer mode");
			}
		} else {
			// loaded data, recording panel hidden
			gui.gRec.hide();
			if (!ec.connError) {
				gui.tlConn.setText("Loaded data");
				gui.tlConnTo.clear();
			} else if (ec.connError) {
				gui.tlConn.setText("");
				gui.tlConnTo.setText("Connection error");
			}
		}

		// ok btn to dismiss err msg
		if (theEvent.isFrom("okErrMsg")) {
			gui.clearErrorMsg();
		}
	}

	public void saveToFile(File selection) {
		if (selection == null) {
			println("Window was closed or the user hit cancel.");
		} else {
			String filename = selection.getAbsolutePath();

			if (!filename.endsWith("csv")) {
				filename = filename.concat(".csv");
			}
			// on windows will throw uncatchable exception if file is open
			// while trying to save to it
			// but app will keep working
			saveTable(emoValuesTbl, filename);
		}
	}

	// handles "load" button press, loads saved csv table data
	public void loadFile(File selection) {
		if (selection != null) {
			ec.disconnect();
			gui.bReconnect.show();
			try {
				loading = true;
				loadedValuesTbl = loadTable(selection.getAbsolutePath(),
						"header");
				loaded = true;
				gui.nowPlayingFilename = selection.getName();
			} catch (Exception e) {
				gui.errorMsg("No saved data found.");
				// TODO - add choice
				ec.edkConn(connTo);
			}
			loading = false;
		}
	}

	public static void main(String _args[]) {
		PApplet.main(new String[] { emoapp.EmoApp.class.getName() });
	}
}