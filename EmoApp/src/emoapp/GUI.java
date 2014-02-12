package emoapp;

import processing.core.PApplet;
import controlP5.Accordion;
import controlP5.ControlP5;
import controlP5.Group;
import controlP5.Textarea;

public class GUI {
	PApplet p;
	// EdkConn ec;

	ControlP5 cp5;
	Accordion accordion;
	Group gInfo, gRec, gLoad;
	Textarea info;

	public GUI(PApplet p) {
		this.p = p;
		// ec = new EdkConn(p);
		cp5 = new ControlP5(p);
		gui();
	}

	public void gui() {
		gInfo = cp5.addGroup("infoGroup")
				.setBackgroundColor(p.color(255, 64))
				.setTitle("Connection info")
				.setHeight(20);

		gRec = cp5.addGroup("recGroup")
				.setBackgroundColor(p.color(255, 64))
				.setTitle("Record")
				.setHeight(20);

		gLoad = cp5.addGroup("loadGroup")
				.setBackgroundColor(p.color(255, 64))
				.setTitle("Load")
				.setHeight(20);

		accordion = cp5.addAccordion("acc")
				.setCollapseMode(Accordion.MULTI)
				.setPosition(10, 10)
				.setWidth(200)
				.addItem(gInfo)
				.addItem(gRec)
				.addItem(gLoad)
				.open(0);
		info = cp5.addTextarea("info").setPosition(10, 10).moveTo(gInfo);
	}

	public void update(int a, int b, int c) {

		String signal = "N/A", headsetOn = "N/A", contactQ = "N/A";

		switch (a) {
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
		switch (b) {
		case 0:
			headsetOn = "OFF";
			break;
		case 1:
			headsetOn = "ON";
			break;
		}
		switch (c) {
		case 0:
			contactQ = "NONE";
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
