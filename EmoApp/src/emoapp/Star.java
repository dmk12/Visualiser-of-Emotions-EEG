/*Fireflies code credit - http://www.local-guru.net/processing/fireflies/fireflies.pde*/
package emoapp;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

public class Star extends Effect {
	PApplet p;
	PGraphics pg1;
	float x;
	float y;
	float alph;
	float a;
	String emoName;
	float emoValue;
	// PVector v;
	// float timer; // timer

	public Star(PApplet p, float emoValue, float timer, String emoName) {
		this.p = p;
		this.emoName = emoName;
		this.emoValue = emoValue;
		int size = (int) (emoValue * 10);		
		this.a = (float) (emoValue * 100);// amplitude
		this.alph = emoValue;
		// v = PVector.random2D();
		//prevent crash if no size value received - exit w/o drawing
		if (size > 0) {
			this.pg1 = this.makeTexture(size);
			draw();
		} else {
			return;
		}
		super.timer = timer;
	}

	public void draw() {
		System.out.println("draw Star");
		// add z and trail
		PImage img = this.pg1;
		alph++;
		x = a * PApplet.sin(alph) + p.width / 2;
		y = a * PApplet.cos(alph) + p.height / 2;
		p.blend(img, 0, 0, img.width, img.height, (int) x - img.width / 2,
				(int) y - img.height / 2, img.width, img.height, PConstants.ADD);
		/*if(this.emoName == "eng"){
			float c = PApplet.map(this.emoValue, 0, 1, 1, 3);
			p.scale(c,c);
		}*/
		super.countdown(1 / p.frameRate);
	}

	public PGraphics makeTexture(int r) {
		
		PGraphics res = p.createGraphics(r * 6, r * 6, PConstants.P2D);
		res.beginDraw();
		res.loadPixels();
		for (int x = 0; x < res.width; x++) {
			for (int y = 0; y < res.height; y++) {
				float d = PApplet.min(512,100 * PApplet.sq(r/ PApplet.sqrt(PApplet.sq(x - 3 * r)+ PApplet.sq(y - 3 * r))));
				// colour values
				float colourValue = PApplet.map(this.emoValue, 0, 1, 0, 255);
				int red = (int) (this.emoName == "exc" ? PApplet.min(
						colourValue, d) : 0);
				int green = (int) (this.emoName == "med" ? PApplet.min(
						colourValue, d) : 0);
				int blue = (int) (this.emoName == "frs" ? PApplet.min(
						colourValue, d) : 0);

				res.pixels[y * res.width + x] = p.color(red,
						green, blue);
			}
		}
		res.updatePixels();
		res.endDraw();

		return res;
	}

}
// other possible trajectories

// drawStar(pg1, width / 2 + 60 * sin(alph + 2), height / 2 + 50 *
// cos((float) (alph * 0.6)));
// drawStar(pg1, width / 2 + 70 * sin((float) (alph * 0.2)), height / 2
// + 30 * cos((float) (alph * 1.6)));
// drawStar(pg1, width / 2 + 50 * sin(alph), height / 2 + 100 *
// cos((float) (alph * 0.7)));
// drawStar(pg1, width / 2 + 50 * sin((float) (alph * 0.4)), height / 2
// + 100 * cos((float) (alph * 1.1)));