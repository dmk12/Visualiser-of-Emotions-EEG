package emoapp;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

public class Star {
	PApplet p;
	PGraphics pg1;
	
	public Star(PApplet p, int size) {
		this.p = p;
		this.pg1 = this.makeTexture(size);
	}

	public void drawStar(float x, float y) {
		//add z and trail
		PImage img = this.pg1;
		p.blend(img, 0, 0, img.width, img.height, (int) x - img.width / 2,
				(int) y - img.height / 2, img.width, img.height, PConstants.ADD);
	}

	public PGraphics makeTexture(int r) {
		PGraphics res = p.createGraphics(r * 6, r * 6, PConstants.P2D);
		res.beginDraw();
		res.loadPixels();
		for (int x = 0; x < res.width; x++) {
			for (int y = 0; y < res.height; y++) {
				float d = PApplet.min(512, 50 * PApplet.sq(r / PApplet.sqrt(PApplet.sq(x - 3 * r) + PApplet.sq(y - 3 * r))));
				// if ( d < 10 ) d = 0;
				res.pixels[y * res.width + x] = p.color(PApplet.min(255, d),
						PApplet.min(255, (float) (d * 0.8)), (float) (d * 0.5));
			}
		}
		res.updatePixels();
		res.endDraw();

		return res;
	}

}
