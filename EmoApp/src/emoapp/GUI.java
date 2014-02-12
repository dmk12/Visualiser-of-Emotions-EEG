package emoapp;

import processing.core.PApplet;
import controlP5.Accordion;
import controlP5.ControlP5;
import controlP5.Group;

public class GUI {
	PApplet p;
	ControlP5 cp5;
	Accordion accordion;

	public GUI(PApplet p) {
		this.p = p;
		cp5 = new ControlP5(p);
		gui();
	}

	public void gui() {
		
		
		Group gColor = cp5.addGroup("colorGroup")
				.setBackgroundColor(p.color(255, 64))
				.setTitle("Pick colors")
				.setHeight(20);
				
		Group gRec = cp5.addGroup("recGroup")
				.setBackgroundColor(p.color(255, 64))
				.setTitle("Record")
				.setHeight(20);
				
		Group gLoad = cp5.addGroup("loadGroup")
				.setBackgroundColor(p.color(255, 64))
				.setTitle("Load")
				.setHeight(20);
				
		accordion = cp5.addAccordion("acc")
				.setCollapseMode(Accordion.MULTI)
				.setPosition(10, 10)
				.setWidth(200)
				.addItem(gColor)
				.addItem(gRec)
				.addItem(gLoad)
				.open(0);
	}

}
