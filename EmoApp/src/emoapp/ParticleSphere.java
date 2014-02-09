package emoapp;

import processing.core.PApplet;
import processing.core.PConstants;

/*Credit - code modified from Hayato Moritan, http://www.openprocessing.org/sketch/5662.*/
public class ParticleSphere {
	PApplet p;
	ImgProc imgProc; // Class ImgProc from [Noise Particle 06] by Marcin Ignac
	int[] currFrame; // Frame data current
	int[] prevFrame; // Frame data previous
	int[] tempFrame; // Frame data temp
	int particlesDensity = 14; // Particles density
	int particleMargin = 64; // Particles margin

	Particle[] particles;

	int startingRadius, radius = 150;
	int centerX, centerY;

	int moveMode = 1;
	float speed = (float) 0.8;

	public ParticleSphere(PApplet p) {
		this.p = p;
		imgProc = new ImgProc(p);
		centerX = p.width / 2;
		centerY = p.height / 2;
		startingRadius = (int) (p.height / 2 - p.height / 10);
	}

	public void setup() {

		currFrame = new int[p.width * p.height]; // create frame data of current
		prevFrame = new int[p.width * p.height]; // create frame data of
													// previous
		tempFrame = new int[p.width * p.height]; // create frame data of temp

		// set begining color of frames
		for (int i = 0; i < p.width * p.height; i++) {
			currFrame[i] = p.color(0, 0, 0);
			prevFrame[i] = p.color(0, 0, 0);
			tempFrame[i] = p.color(0, 0, 0);
		}

		// create array of particles, size of array inside []
		particles = new Particle[(p.width + particleMargin * 2)
				/ particlesDensity *
				(p.height + particleMargin * 2) / particlesDensity];

		// Create particles
		// -------------------------------------
		int i = 0;
		for (int y = -particleMargin; y < p.height + particleMargin; y += particlesDensity) {
			for (int x = -particleMargin; x < p.width + particleMargin; x += particlesDensity) {
				if (i == particles.length) {
					break;
				}

				float theta = p.random(0, PConstants.TWO_PI);
				float u = p.random(-1, 1);

				int c = p.color(
						50 + 50 * PApplet.sin(PConstants.PI * x / p.width),// red
						127,// green
						255 * PApplet.sin(PConstants.PI * y / p.width));// blue
				particles[i++] = new Particle(p, this, c, (int) p.random(64),
						(int) p.random(64), (int) p.random(64), theta, u);
			}
		}
	}

	public void draw(float exc, float eng, int blink) {
		// Blur effects, keep as 1st line!
		imgProc.blur(prevFrame, tempFrame, p.width, p.height);
		PApplet.arrayCopy(tempFrame, currFrame);
		// excitement
		speed = exc;

		// engagement
		radius = (int) ((eng + 1) * startingRadius);
		moveMode = blink;
	
		for (int i = 0; i < particles.length; i++) {
			particles[i].update(moveMode);
			particles[i].render();
		}

		// draw the pixels in frame
		imgProc.drawPixelArray(currFrame, 0, 0, p.width, p.height);
		PApplet.arrayCopy(currFrame, prevFrame);

	}

	/*
	 * public void mousePressed() { switch (moveMode) { case 0: moveMode = 1;
	 * speed = (float) 0.2; break;
	 * 
	 * case 1: moveMode = 0; speed = (float) 0.01;
	 * 
	 * } }
	 */
}
