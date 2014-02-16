package emoapp;

import processing.core.PApplet;
import controlP5.Accordion;
import controlP5.Button;
import controlP5.ControlEvent;
import controlP5.ControlP5;
import controlP5.Group;
import controlP5.Textarea;
import controlP5.Toggle;

public class GUI {
	PApplet p;
	ControlP5 cp5;
	Accordion accordion;
	Group gWelcome, gInfo, gRec, gLoad, gHelp;
	Button connect;
	Textarea info;
	int bgC;
	Toggle toggleGui;

	boolean guiVisible = false;

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

	}

	public void gui() {
		toggleGui = cp5.addToggle("toggleGui").setCaptionLabel("hide controls")
				.setPosition(10, 10);
		gInfo = cp5.addGroup("infoGroup")
				.setBackgroundColor(bgC)
				.setTitle("Connection info")
				.setHeight(20);
		info = cp5.addTextarea("info").setPosition(10, 10).setGroup(gInfo);

		gRec = cp5.addGroup("recGroup")
				.setBackgroundColor(bgC)
				.setTitle("Record")
				.setHeight(20);

		gLoad = cp5.addGroup("loadGroup")
				.setBackgroundColor(bgC)
				.setTitle("Load")
				.setHeight(20);
		gHelp = cp5.addGroup("helpGroup")
				.setBackgroundColor(bgC).setBackgroundHeight(140)
				.setTitle("Help")
				.setHeight(20);
		cp5.addTextarea("help").setPosition(10, 10).setGroup(gHelp)
				.setHeight(140)
				.setText("Excitement up/down - more red/blue.\n\n" +
						"Frustration up/down - larger/smaller sphere.\n\n" +
						"Smile - flatten sphere.\n\n" +
						"Blink - scatter particles.\n\n" +
						"Wink left/right - tilt left/right.");

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

	public void updateInfo(int hOn, int sig, int contQ) {

		String headsetOn = "Not connected", signal = "N/A", contactQ = "N/A";

		switch (hOn) {
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
		case 2:
			contactQ = "OK";
			break;
		}
		info.setText("Headset status: " + headsetOn
				+ "\n\n" +
				"Wireless signal: " + signal
				+ "\n\n" +
				"Contact quality: " + contactQ);

	}
}
