package emoapp;

import processing.core.PApplet;
import controlP5.Accordion;
import controlP5.Button;
import controlP5.ControlEvent;
import controlP5.ControlP5;
import controlP5.Group;
import controlP5.Textarea;
import controlP5.Textlabel;
import controlP5.Toggle;

public class GUI {
	PApplet p;
	ControlP5 cp5;
	Accordion accordion;
	Group gWelcome, gInfo, gRec, gLoad, gHelp;
	Button connect;
	Textarea info;
	int bgC, startTime = 0, timeMs = 0;
	Toggle toggleGui, toggleRec;
	Textlabel tlTime;

	boolean guiVisible = false, recording = false;

	public GUI(PApplet p) {
		this.p = p;
		bgC = p.color(255, 64);
		cp5 = new ControlP5(p);
		welcomeMsg();
	}

	public void welcomeMsg() {
		gWelcome = cp5.addGroup("welcomeGroup")
				.setPosition(p.width / 2 - 50, p.height / 2 - 50)
				.disableCollapse().hideBar();
		cp5.addTextarea("welcome")
				.setText("Please turn on headset and connect")
				.setGroup(gWelcome);
		cp5.addButton("connect").setGroup(gWelcome);
	}

	public void handler(ControlEvent theEvent) {
		if (theEvent.isFrom("connect")) {
			gWelcome.hide();
			gui();
		}
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
		// start/stop recording
		if (theEvent.isFrom("toggleRec")) {
			if (toggleRec.getState()) {
				toggleRec.setCaptionLabel("pause recording");
				recording = true;
				if (startTime == 0)
					startTime = p.millis();
			} else {
				toggleRec.setCaptionLabel("start recording");
				recording = false;
			}
		}

	}

	public void gui() {
		// toggle button to show/hide controls
		toggleGui = cp5.addToggle("toggleGui").setCaptionLabel("hide controls")
				.setPosition(10, 10);

		// connection info - headset on/off,wireless signal strength and
		// electrode contact quality
		gInfo = cp5.addGroup("infoGroup")
				.setBackgroundColor(bgC)
				.setTitle("Connection info")
				.setHeight(20);
		info = cp5.addTextarea("info")
				.setPosition(10, 10)
				.setGroup(gInfo);

		// record
		gRec = cp5.addGroup("recGroup")
				.setBackgroundColor(bgC)
				.setTitle("Record")
				.setHeight(20);
		toggleRec = cp5.addToggle("toggleRec")
				.setCaptionLabel("start recording")
				.setPosition(10, 10).setGroup(gRec);
		tlTime = cp5.addTextlabel("time")
				.setGroup(gRec)
				.setText("00:00:00")
				.setPosition(60, 15);

		// load and play recording
		gLoad = cp5.addGroup("loadGroup")
				.setBackgroundColor(bgC)
				.setTitle("Load")
				.setHeight(20);

		// help, explanation on how to interact
		gHelp = cp5.addGroup("helpGroup")
				.setBackgroundColor(bgC)
				.setBackgroundHeight(140)
				.setTitle("Help")
				.setHeight(20);
		cp5.addTextarea("help")
				.setPosition(10, 10)
				.setGroup(gHelp)
				.setHeight(140)
				.setText("Excitement up/down - more red/blue.\n\n" +
						"Frustration up/down - larger/smaller sphere.\n\n" +
						"Smile - flatten sphere.\n\n" +
						"Blink - scatter particles.\n\n" +
						"Wink left/right - tilt left/right.");

		// place all groups into an "accordion"
		accordion = cp5.addAccordion("acc")
				.setCollapseMode(Accordion.MULTI)
				.setPosition(10, 60)
				.setWidth(200)
				.addItem(gInfo)
				.addItem(gRec)
				.addItem(gLoad)
				.addItem(gHelp)
				.open(0, 1);
	}

	public void updateTimer() {

	}

	public void update(int hOn, int sig, int contQ) {

		String headsetOn = "Not connected", signal = "N/A", contactQ = "N/A";

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

		if (recording) {
			timeMs = p.millis() - startTime;
			tlTime.setText(formatClock(timeMs));
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
		cp5.addTextarea("errorMsg").setText(msg).bringToFront()
				.setLineHeight(15)
				.setPosition(p.width / 2 - 50, p.height / 2);
	}
}
