package emoapp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import processing.core.PApplet;
import processing.core.PShape;

public class EmoApp extends PApplet {
	// edk (headset) conn.
	EdkConn ec;
	// headset data
	float exc, eng, med, frs;
	int blink;
	// blink circle coords
	float blinkX, blinkY;

	// circle to represent blink
	private PShape blinkCrc;

	List<Effect> effList = new ArrayList<Effect>();
	String eff;
	// Setup can be used like in the processing tool.
	public void setup() {
		// Set the canvas size
		size(displayWidth / 2, displayHeight / 2, P2D);
		background(0);
		// anti aliasing!
		smooth();
		eff = "particle";
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
		//	spotLight(255, 255, 0, width / 2, height / 2, 400, 0, 0, -1, PI / 4, 2);
		//  camera(mouseX, mouseY, (height / 2) / tan(PI / 6), width / 2,				height / 2, 0, 0, 1, 0);
		exc = ec.getExcitement();
		eng = ec.getEngagement();
		med = ec.getMeditation();
		frs = ec.getFrustration();
		blink = ec.getBlink();

		// fireflies
		if (stateChanged && exc > 0.01) {
			// create new star
			switch (eff) {
				case "star": {
					effList.add(new Star(this, 10, exc, 3));
					break;
				}
				case "particle": {
					effList.add(new Particle(this, 20, exc, 3));
					break;
				}
			}			
		}
		Iterator<Effect> it = effList.iterator();
		while(it.hasNext())
		{
			Effect s = it.next();
			if(s.dead()){
				it.remove();				
			}else{
				s.draw();
				
			}
		}
	}
	public void keyReleased() {
	  if (keyCode == RIGHT)      eff = "star";
	  else if (keyCode == LEFT)  eff = "particle";
	}
	public static void main(String _args[]) {
		PApplet.main(new String[] { emoapp.EmoApp.class.getName() });
	}
}
