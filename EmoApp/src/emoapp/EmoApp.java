package emoapp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import processing.core.PApplet;
import processing.core.PFont;

@SuppressWarnings("serial")
public class EmoApp extends PApplet {
	// edk (headset) conn.
	EdkConn ec;
	// headset data
	float exc, eng, med, frs;
	//int blink;
	// blink circle coords
	//float blinkX, blinkY;

	// circle to represent blink
	//private PShape blinkCrc;

	List<Effect> effList = new ArrayList<Effect>();
	String eff;

	
	// Setup can be used like in the processing tool.
	public void setup() {
		// Set the canvas size
		size(displayWidth / 2, displayHeight / 2, P2D);
		background(0);
		// anti aliasing!
		smooth();
		PFont fnt = loadFont("Monaco-12.vlw");
		textFont(fnt);
		textLeading(17);
		// default effect is particle
		eff = "star";
		frameRate(10);
		// Connect to headset
		ec = new EdkConn(this);
		ec.edkConn();
	}

	// Draw is used like in the processing tool.
	public void draw() {
		// Run headset event listener loop each time draw() is called
		boolean stateChanged = ec.edkRun();
		// Redraw the background with black
		background(0);
		showInfo();
		// spotLight(255, 255, 0, width / 2, height / 2, 400, 0, 0, -1, PI / 4,
		// 2);
		// camera(mouseX, mouseY, (height / 2) / tan(PI / 6), width / 2, height
		// / 2, 0, 0, 1, 0);
		
		//blink = ec.getBlink();
		// print instructions in the corner
		

		// fireflies
		
		if (stateChanged) {
			exc = ec.getExcitement();
			eng = ec.getEngagement();
			med = ec.getMeditation();
			frs = ec.getFrustration();
		
			// create new effect
			switch (eff) {
			case "star": {
				effList.add(new Star(this, exc, 3,"exc"));
				effList.add(new Star(this, eng, 3,"eng"));
				effList.add(new Star(this, med, 3,"med"));
				effList.add(new Star(this, frs, 3,"frs"));
				break;
			}
			case "particle": {   
				effList.add(new Particle(this, exc, 1));
				effList.add(new Particle(this, eng, 1));
				effList.add(new Particle(this, med, 1));
				effList.add(new Particle(this, frs, 1));
				break;
			}
			}
		}
		Iterator<Effect> it = effList.iterator();
		while (it.hasNext())
		{
			Effect s = it.next();
			if (s.dead()) {
				it.remove();
			} else {
				s.draw();
			}
		}
		
	}

	public void keyReleased() {
		if (keyCode == RIGHT)
			eff = "star";
		else if (keyCode == LEFT)
			eff = "particle";
		
	}

	public void showInfo() {
		String s = "";
		s += "Toggle effects:\n";
		s += "right arrow: star\n";
		s += "left arrow: particle\n";
		text(s, 10, 20);
	}

	public static void main(String _args[]) {
		PApplet.main(new String[] { emoapp.EmoApp.class.getName() });
	}
}
