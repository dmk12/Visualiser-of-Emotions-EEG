/*Fireflies code credit - http://www.local-guru.net/processing/fireflies/fireflies.pde*/
package emoapp;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

public class Star extends Effect {
	PApplet p;
	PGraphics pg1;
	float x;
	float y;
	float alph;
	float a;
//	PVector v;
	float timer; // timer

	public Star(PApplet p, int size, float alph, float lifespan) {
		this.p = p;
		this.pg1 = this.makeTexture(size);
		this.a = (float) (alph * 100);//amplitude
		this.alph = alph;
	//	v = PVector.random2D();
		this.timer = lifespan;
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
		timer-=1/p.frameRate;
	}

	public PGraphics makeTexture(int r) {
		PGraphics res = p.createGraphics(r * 6, r * 6, PConstants.P2D);
		res.beginDraw();
		res.loadPixels();
		for (int x = 0; x < res.width; x++) {
			for (int y = 0; y < res.height; y++) {
				float d = PApplet.min(512,50 * PApplet.sq(r/PApplet.sqrt(PApplet.sq(x - 3 * r) + PApplet.sq(y - 3 * r))));
				//colour values
				res.pixels[y * res.width + x] = p.color(PApplet.min(255, d), PApplet.min(255, (float) (d * 0.8)), (float) (d * 0.5));
			}
		}
		res.updatePixels();
		res.endDraw();

		return res;
	}
	public boolean dead()
	{
		if (timer <= 0.0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

}
//other possible trajectories

// drawStar(pg1, width / 2 + 60 * sin(alph + 2), height / 2 + 50 *
// cos((float) (alph * 0.6)));
// drawStar(pg1, width / 2 + 70 * sin((float) (alph * 0.2)), height / 2
// + 30 * cos((float) (alph * 1.6)));
// drawStar(pg1, width / 2 + 50 * sin(alph), height / 2 + 100 *
// cos((float) (alph * 0.7)));
// drawStar(pg1, width / 2 + 50 * sin((float) (alph * 0.4)), height / 2
// + 100 * cos((float) (alph * 1.1)));