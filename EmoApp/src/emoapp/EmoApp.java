/*Fireflies code credit - http://www.local-guru.net/processing/fireflies/fireflies.pde*/
package emoapp;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;

public class EmoApp extends PApplet {
	// edk (headset) conn.
	EdkConn ec;
	// blink circle coords
	float blinkX;
	float blinkY;

	// circles to represent each emotion
	private PShape excCrc;
	private PShape engCrc;
	private PShape medCrc;
	private PShape frsCrc;

	// circle to represent blink
	private PShape blinkCrc;

	// fireflies
	PGraphics pg1;
	float alph;

	// Setup can be used like in the processing tool.
	public void setup() {
		// Set the canvas size
		size(500, 200, P3D);
		// anti aliasing!
		smooth();
		lights();

		excCrc = createShape(ELLIPSE, 0, 0, 20, 20);
		engCrc = createShape(ELLIPSE, 0, 0, 20, 20);
		medCrc = createShape(ELLIPSE, 0, 0, 20, 20);
		frsCrc = createShape(ELLIPSE, 0, 0, 20, 20);
		blinkCrc = createShape(ELLIPSE, 0, 0, 30, 30);

		pg1 = makeTexture(40);
		alph = 0;

		noStroke();

		// Connect to headset
		ec = new EdkConn();
		ec.edkConn();
	}

	// Draw is used like in the processing tool.
	public void draw() {
		// Run headset event listener loop each time draw() is called
		ec.edkRun(this);
		// Redraw the background with black
		background(0);

		// set fills
		excCrc.setFill(color(200, 0, 0));
		engCrc.setFill(color(0, 200, 0));
		medCrc.setFill(color(0, 0, 200));
		frsCrc.setFill(color(200, 0, 200));

		// draw shapes
		shape(excCrc, 100, -ec.exc * 100 + 120);
		shape(engCrc, 200, -ec.eng * 100 + 120);
		shape(medCrc, 300, -ec.med * 100 + 120);
		shape(frsCrc, 400, -ec.frs * 100 + 120);

		// detect blink
		if (ec.blink == 1) {
			blinkX = (float) Math.random() * displayWidth;
			blinkY = (float) Math.random() * displayHeight;
			shape(blinkCrc, blinkX, blinkY);
		}

		// fireflies
		alph += 0.1;
		drawStar(pg1, width / 2 + 50 * sin(alph), height / 2 + 50 * cos(alph));
		drawStar(pg1, width / 2 + 60 * sin(alph + 2), height / 2 + 50
				* cos((float) (alph * 0.6)));
		drawStar(pg1, width / 2 + 70 * sin((float) (alph * 0.2)), height / 2
				+ 30 * cos((float) (alph * 1.6)));
		drawStar(pg1, width / 2 + 50 * sin(alph), height / 2 + 100
				* cos((float) (alph * 0.7)));
		drawStar(pg1, width / 2 + 50 * sin((float) (alph * 0.4)), height / 2
				+ 100 * cos((float) (alph * 1.1)));
	}

	public void drawStar(PImage img, float x, float y) {
		blend(img, 0, 0, img.width, img.height, (int) x - img.width / 2,
				(int) y - img.height / 2, img.width, img.height, ADD);
	}

	public PGraphics makeTexture(int r) {
		PGraphics res = createGraphics(r * 6, r * 6, P2D);
		res.beginDraw();
		res.loadPixels();
		for (int x = 0; x < res.width; x++) {
			for (int y = 0; y < res.height; y++) {
				float d = min(512, 50 * sq(r
						/ sqrt(sq(x - 3 * r) + sq(y - 3 * r))));
				// if ( d < 10 ) d = 0;
				res.pixels[y * res.width + x] = color(min(255, d),
						min(255, (float) (d * 0.8)), (float) (d * 0.5));
			}
		}
		res.updatePixels();
		res.endDraw();

		return res;
	}

	public static void main(String _args[]) {
		PApplet.main(new String[] { emoapp.EmoApp.class.getName() });
	}
}
