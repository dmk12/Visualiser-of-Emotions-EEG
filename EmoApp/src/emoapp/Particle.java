/*Fireflies code credit - http://www.local-guru.net/processing/fireflies/fireflies.pde*/
package emoapp;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

public class Particle extends Effect {
	PApplet p;
	PGraphics pg1;
	float x;
	float y;
	float theta;
	float a;
	PVector v;
	PImage img;
	// float timer; // timer

	public Particle(PApplet p, float emoValue, float timer) {
		this.p = p;
		int size = (int) (emoValue * 20);
		//prevent crash if no size value received - exit w/o drawing
		if (size > 0) {
			this.pg1 = this.makeTexture(size);
		} else {
			return;
		}
		this.a = (float) (emoValue * 150);// amplitude
		this.theta = emoValue;
		v = PVector.random2D();
		super.timer = timer;
	}

	public void draw() {
		// System.out.println("draw Particle");
		// add z and trail
		//fade();
		img = this.pg1;
		
		theta++;
		x = a * PApplet.sin(theta) + p.width / 2;
		y = a * PApplet.cos(theta) + p.height / 2;
		// x = v.x;
		// y = v.y;
		
		p.blend(img, 0, 0, img.width, img.height, (int) x - img.width / 2,
				(int) y - img.height / 2, img.width, img.height, PConstants.ADD);
		
		super.countdown(1 / p.frameRate);
		
	}
	
	public void fade(){
		img = this.pg1.get();
		
		p.tint(255,127);
		p.image(img,x,y);
	}
	public PGraphics makeTexture(int r) {
		PGraphics res = p.createGraphics(r * 6, r * 6, PConstants.P2D);
		res.beginDraw();
		res.loadPixels();
		for (int x = 0; x < res.width; x++) {
			for (int y = 0; y < res.height; y++) {
				float d = PApplet.min(
						512,
						50 * PApplet.sq(r
								/ PApplet.sqrt(PApplet.sq(x - 3 * r)
										+ PApplet.sq(y - 3 * r))));
				// colour values
				res.pixels[y * res.width + x] = p.color(PApplet.min(0, d), PApplet.min(255, (float) (d * 0.8)), (float) (d * 0.5));
			}
		}
		res.updatePixels();
		res.endDraw();

		return res;
	}

}
// other possible trajectories

// drawParticle(pg1, width / 2 + 60 * sin(alph + 2), height / 2 + 50 *
// cos((float) (alph * 0.6)));
// drawParticle(pg1, width / 2 + 70 * sin((float) (alph * 0.2)), height / 2
// + 30 * cos((float) (alph * 1.6)));
// drawParticle(pg1, width / 2 + 50 * sin(alph), height / 2 + 100 *
// cos((float) (alph * 0.7)));
// drawParticle(pg1, width / 2 + 50 * sin((float) (alph * 0.4)), height / 2
// + 100 * cos((float) (alph * 1.1)));