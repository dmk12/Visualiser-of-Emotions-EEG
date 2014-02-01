package emoapp;

import processing.core.PApplet;
import processing.core.PConstants;

public class Brush {
	PApplet p;
	float angle;
	int components[];
	float x, y;
	int clr;

	Brush(PApplet p) {
		this.p = p;
		angle = p.random(PConstants.TWO_PI);
		x = p.random(p.width);
		y = p.random(p.height);
		clr = p.color(p.random(255), p.random(255), p.random(255),5);
		components = new int[2];
		for (int i = 0; i < 2; i++) {
			components[i] = (int) p.random(1, 5);
		}
		paint();
	}

	void paint() {
		System.out.println("brush");
		float a = 0;
		float r = 0;
		float x1 = x;
		float y1 = y;
		float u = p.random((float) 0.5, 1);

		p.fill(clr);
		p.noStroke();

		p.beginShape();
		while (a < PConstants.TWO_PI) {
			p.vertex(x1, y1);
			float v = p.random((float) 0.85, 1);
			x1 = x + r * PApplet.cos(angle + a) * u * v;
			y1 = y + r * PApplet.sin(angle + a) * u * v;
			a += PConstants.PI / 180;
			for (int i = 0; i < 2; i++) {
				r += PApplet.sin(a * components[i]);
			}
		}
		p.endShape(PConstants.CLOSE);

		if (x < 0 || x > p.width || y < 0 || y > p.height) {
			angle += PConstants.HALF_PI;
		}

		x += 2 * PApplet.cos(angle);
		y += 2 * PApplet.sin(angle);
		angle += p.random((float) -0.15, (float) 0.15);
	}
}
