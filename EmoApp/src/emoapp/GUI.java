package emoapp;

import processing.core.PApplet;
import controlP5.Accordion;
import controlP5.Button;
import controlP5.ControlEvent;
import controlP5.ControlP5;
import controlP5.ControlP5Constants;
import controlP5.Group;
import controlP5.Textarea;
import controlP5.Textfield;
import controlP5.Textlabel;
import controlP5.Toggle;

public class GUI {
	PApplet p;
	ControlP5 cp5;
	Accordion accordion;
	Group gWelcome, gInfo, gRec, gLoad, gHelp, gErr;
	Button bConnect, bReset, bSave, bLoad, bReconnect;
	Textfield tfFilename;
	Textarea info, errorMsg;
	Toggle toggleGui, toggleRec, toggleConnTo;
	Textlabel tlTime, tlFilename, tlConn;
	Textarea tlConnTo;
	int bgC, startTime = 0, timeMs = 0, pausedTime = 0;
	boolean guiVisible = false,
			recording = false,
			reset = false,
			recDisabled = false;
	String nowPlayingFilename = "";

	public GUI(PApplet p) {
		this.p = p;
		bgC = p.color(255, 64);
		cp5 = new ControlP5(p);
		welcomeMsg();
	}

	public void welcomeMsg() {
		// welcome message
		gWelcome = cp5.addGroup("welcomeGroup")
				.setPosition(p.width / 2 - 50, p.height / 2 - 50)
				.disableCollapse().hideBar();
		cp5.addTextarea("welcome")
				.setText("Please turn on headset and connect")
				.setGroup(gWelcome);
		cp5.addButton("connect").setGroup(gWelcome);

		// error message
		gErr = cp5.addGroup("errGroup")
				.setPosition(p.width / 2 - 50, p.height / 2)
				.disableCollapse()
				.hideBar()
				.hide();
		errorMsg = cp5.addTextarea("errorMsg").setText("").bringToFront()
				.setLineHeight(15)
				.setGroup(gErr);
		cp5.addButton("okErrMsg").setCaptionLabel("OK").setGroup(gErr);
	}

	public void setup() {
		// toggle button to show/hide controls
		toggleGui = cp5.addToggle("toggleGui")
				.setCaptionLabel("hide controls")
				.setMode(ControlP5Constants.SWITCH)
				.setPosition(10, 10);

		// connection status - always visible
		tlConn = cp5.addTextlabel("conneStat").setPosition(80, 33);

		// connection info - headset on/off,wireless signal strength and
		// electrode contact quality
		gInfo = cp5.addGroup("infoGroup")
				.setBackgroundColor(bgC)
				.setBackgroundHeight(150)
				.setTitle("Connection info")
				.setHeight(20);
		info = cp5.addTextarea("info")
				.setPosition(10, 10)
				.setGroup(gInfo);
		// toggle between headset and emocomposer
		toggleConnTo = cp5.addToggle("connTo")
				.setMode(ControlP5Constants.SWITCH)
				.setCaptionLabel("headset")
				.setPosition(10, 85)
				.setGroup(gInfo);
		tlConnTo = cp5.addTextarea("tlConnTo")
				.setPosition(60, 85)
				.setGroup(gInfo);
		// record
		gRec = cp5.addGroup("recGroup")
				.setBackgroundColor(bgC)
				.setTitle("Record")
				.setHeight(20);
		toggleRec = cp5.addToggle("toggleRec")
				.setCaptionLabel("start recording")
				.setPosition(10, 10)
				.setGroup(gRec);
		tlTime = cp5.addTextlabel("time")
				.setGroup(gRec)
				.setText("00:00:00")
				.setPosition(60, 15);
		// save file
		cp5.addButton("save").setGroup(gRec)
				.setPosition(120, 10);
		bReset = cp5.addButton("reset")
				.setPosition(120, 40)
				.setGroup(gRec);

		// load and play recording
		gLoad = cp5.addGroup("loadGroup")
				.setBackgroundColor(bgC)
				.setTitle("Load")
				.setHeight(20);
		bLoad = cp5.addButton("load")
				.setCaptionLabel("load recording")
				.setGroup(gLoad)
				.setPosition(10, 10);
		tlFilename = cp5.addTextlabel("filename")
				.setGroup(gLoad)
				.setPosition(80, 15);
		bReconnect = cp5.addButton("reconnect")
				.setGroup(gLoad)
				.setCaptionLabel("go back to live mode")
				.setWidth(130)
				.setPosition(10, 40)
				.hide();

		// help, explanation on how to interact
		gHelp = cp5.addGroup("helpGroup")
				.setBackgroundColor(bgC)
				.setBackgroundHeight(200)
				.setTitle("Help")
				.setHeight(20);
		cp5.addTextarea("help")
				.setPosition(10, 10)
				.setGroup(gHelp)
				.setHeight(200)
				.setText("Excitement up/down - more red/blue.\n\n" +
						"Frustration up/down - larger/smaller sphere.\n\n" +
						"Smile - flatten sphere.\n\n" +
						"Blink - scatter particles.\n\n" +
						"Wink left/right - tilt left/right." +
						"\n\n----------\n\n" +
						"Record data and save in .csv format.\n\n" +
						"Load and play .csv data. Recording is disabled during playback.");

		// place all groups into an "accordion"
		accordion = cp5.addAccordion("acc")
				.setCollapseMode(Accordion.MULTI)
				.setPosition(10, 60)
				.setWidth(200)
				.addItem(gInfo)
				.addItem(gRec)
				.addItem(gLoad)
				.addItem(gHelp)
				.open(0);
	}

	public void handler(ControlEvent theEvent) {
		// show/hide GUI controls
		if (theEvent.isFrom("toggleGui")) {
			if (toggleGui.getState()) {
				accordion.hide();
				toggleGui.setCaptionLabel("show controls");
				guiVisible = false;

			} else {
				accordion.show();
				toggleGui.setCaptionLabel("hide controls");
				guiVisible = true;
			}
		}
		if (!recDisabled) {
			// start/stop recording
			if (theEvent.isFrom("toggleRec")) {
				if (toggleRec.getState()) {
					recording = true;
					toggleRec.setCaptionLabel("pause recording");
					if (startTime == 0)
						startTime = p.millis();
				} else {
					recording = false;
					toggleRec.setCaptionLabel("start recording");
				}
			}
			// reset recording
			if (theEvent.isFrom("reset")) {
				startTime = 0;
				pausedTime = 0;
				toggleRec.setState(false).setCaptionLabel("start recording");
				recording = false;
				tlTime.setText("00:00:00");
				reset = true;
			}
			// save recording
			if (theEvent.isFrom("save")) {
				toggleRec.setState(false).setCaptionLabel("start recording");
				recording = false;
				p.selectOutput("Save as:", "saveToFile");
			}
		}
		// load recording
		if (theEvent.isFrom("load")) {
			p.selectInput("Select file to open:", "loadFile");
		}

	}

	public void loadHandler(String state) {
		toggleRec.setState(false).setCaptionLabel("start recording");
		recording = false;
		recDisabled = true;
		bReconnect.setCaptionLabel("Stop and switch to live data");
		
		if (state == "loading") {
			bLoad.setCaptionLabel("loading");
		}
		if (state == "playing") {
			bLoad.setCaptionLabel("playing");
			tlFilename.setText(nowPlayingFilename);
		}
		if (state == "done") {
			bLoad.setCaptionLabel("load recording");
			tlFilename.setText("");
			bReconnect.setCaptionLabel("Reconnect to live data");
		}
	}

	public void updateInfo(int hOn, int sig, int contQ) {

		String headsetOn = "N/A", signal = "N/A", contactQ = "N/A";

		switch (hOn) {
		// once turned on never seems to detect when turned off
		// so shows either "not connected" or "on", never "off"
		case 0:
			headsetOn = "OFF";
			break;
		case 1:
			headsetOn = "ON";
			break;
		}
		switch (sig) {
		case 0:
			signal = "NONE";
			break;
		case 1:
			signal = "POOR";
			break;
		case 2:
			signal = "OK";
			break;
		}
		switch (contQ) {
		case 0:
			contactQ = "NONE/NOISE";
			break;
		case 1:
			contactQ = "POOR";
			break;
		// only goes up to 2 for headset
		// but goes up to 4 for emocomposer
		case 2:
		case 3:
		case 4:
			contactQ = "OK";
			break;
		}
		info.setText("Headset status: " + headsetOn
				+ "\n\n" +
				"Wireless signal: " + signal
				+ "\n\n" +
				"Contact quality: " + contactQ);
	}

	public void updateClock() {
		// update clock when recording
		if (recording) {
			timeMs = p.millis() - startTime - pausedTime;
			tlTime.setText(formatClock(timeMs));
		}
		else if (!recording && startTime != 0) {
			pausedTime = p.millis() - timeMs - startTime;
		}
	}

	public String formatClock(int tMilsecs) {
		int t = tMilsecs / 1000;
		int seconds = t % 60;
		int minutes = (t / 60) % 60;
		int hours = t / 60 / 60;

		String formattedClock =
				(hours < 10 ? "0" + hours : hours) + ":"
						+ (minutes < 10 ? "0" + minutes : minutes) + ":"
						+ (seconds < 10 ? "0" + seconds : seconds);
		return formattedClock;
	}

	public void errorMsg(String msg) {
		errorMsg.setText(msg);
		gErr.show();
	}

	public void clearErrorMsg() {
		errorMsg.clear();
		gErr.hide();
	}
}
