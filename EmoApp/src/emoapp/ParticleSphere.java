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
	int particlesDensity = 16; // Particles density
	int particleMargin = 64; // Particles margin

	Particle[] particles;

	int startingRadius, radius = 90;
	int centerX, centerY;

	int moveMode = 0;
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
		prevFrame = new int[p.width * p.height]; // create frame data of previous
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

	public void draw(float exc, float eng, float med, float frs, 
			int blink, float smile, float clench, int winkL, int winkR) {
		//run pixel array containing pixels of previous frame through 
		//blur algorithm inside the ImgProc class
		//and store blurred pixels inside temporary pixel array
		imgProc.blur(prevFrame, tempFrame, p.width, p.height);

		//copy temporary pixel array into current frame pixel array
		PApplet.arrayCopy(tempFrame, currFrame);
		
		//frustration reading determines sphere radius
		frs = PApplet.map(frs, 0, 1, 1, 2);
		radius = (int) ((frs) * startingRadius);

		//scatter particles when blinking (moveMode=1)
		//otherwise display sphere (moveMode=0)
		moveMode = blink;
		
		//control "flattening" of sphere by either smile or clench
		float flatten = 0;
		if (smile > 0) {
			flatten = smile;
		} else if (clench > 0) {
			flatten = clench;
		}
		//to prevent complete flattening set a constrain of 0.8 
		flatten = PApplet.constrain(flatten, 0, (float) 0.8); 
		
		//meditation reading, constrained to minimum of 0.01 to prevent division by 0 
		//as excitement value is divided by it inside Particle class render() method
		med = PApplet.constrain(med,(float) 0.01,1);
		
		//update and render particles
		for (int i = 0; i < particles.length; i++) {
			//update() sets particle positions => controls shape of sphere
			particles[i].update(moveMode, flatten, winkL, winkR);
			//render() sets the colour
			particles[i].render(exc, med);
		}

		// draw the updated pixels of the current frame
		imgProc.drawPixelArray(currFrame, 0, 0, p.width, p.height);
		
		//copy current frame pixel array into previous frame 
		//pixel array for next iteration
		PApplet.arrayCopy(currFrame, prevFrame);

	}	
}
