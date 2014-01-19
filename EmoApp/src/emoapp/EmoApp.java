/*Fireflies code credit - http://www.local-guru.net/processing/fireflies/fireflies.pde*/
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

	// circles to represent each emotion
	private PShape excCrc, engCrc, medCrc, frsCrc;

	// circle to represent blink
	private PShape blinkCrc;

	// fireflies
	// Star star;
	float alph;
	List<Star> stars = new ArrayList<Star>();
	List<Particle> particles = new ArrayList<Particle>();

	// Setup can be used like in the processing tool.
	public void setup() {
		// Set the canvas size
		size(displayWidth / 2, displayHeight / 2, P2D);
		background(0);
		// anti aliasing!
		smooth();
		alph = 0;
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
		//camera(mouseX, mouseY, (height / 2) / tan(PI / 6), width / 2,				height / 2, 0, 0, 1, 0);
		exc = ec.getExcitement();
		eng = ec.getEngagement();
		med = ec.getMeditation();
		frs = ec.getFrustration();
		blink = ec.getBlink();

		/*
		 * // set fills excCrc.setStroke(color(200, 0, 0));
		 * engCrc.setStroke(color(0, 200, 0)); medCrc.setStroke(color(0, 0,
		 * 200)); frsCrc.setStroke(color(200, 0, 200));
		 * 
		 * // draw shapes shape(excCrc, 100, -exc * 100 + 120); shape(engCrc,
		 * 200, -eng * 100 + 120); shape(medCrc, 300, -med * 100 + 120);
		 * shape(frsCrc, 400, -frs * 100 + 120);
		 * 
		 * // detect blink if (blink == 1) { blinkX = (float) Math.random() *
		 * displayWidth; blinkY = (float) Math.random() * displayHeight;
		 * shape(blinkCrc, blinkX, blinkY); }
		 */

		// fireflies
		//alph += 0.1;
		// need to cleanup - add timer
		if (stateChanged && exc > 0.01) {
			// create new star
			stars.add(new Star(this, 10, exc, 3));
			//System.out.println(exc);
			//Particle p = new Particle(this, new PVector(50,50,0), new PVector(200,0,0), new PVector(1,1,0),1000); 
			//p.run();
			
		}
		Iterator<Star> it = stars.iterator();
		while(it.hasNext())
		{
			Star s = it.next();
			if(s.dead()){
				it.remove();				
			}else{
				s.drawStar();
				
			}
		}
	}

	public static void main(String _args[]) {
		PApplet.main(new String[] { emoapp.EmoApp.class.getName() });
	}
}
