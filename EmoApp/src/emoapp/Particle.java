/*Code credit - Hayato Moritan, http://www.openprocessing.org/sketch/5662, thanks.*/
package emoapp;

import processing.core.PApplet;
import processing.core.PConstants;

public class Particle {
	PApplet p;
	ParticleSphere pSph;

	float theta, u;
	float vTheta, vU;
	float x, y, z;

	int theColor;

	float xDiff, yDiff, zDiff;
	float nextX, nextY, nextZ;

	Particle(PApplet p, ParticleSphere pSph, int c, int nx, int ny, int nz,
			float Theta, float U) {
		this.p = p;
		this.pSph = pSph;

		x = p.width / 2;
		y = p.height / 2;

		nextX = p.width / 2;
		nextY = p.height / 2;
		nextZ = p.width % p.height;

		theColor = c;
		theta = Theta;
		u = U;
		vTheta = 0;
		vU = 0;
	}

	void update(int moveMode, float flatten, int winkL, int winkR) {
		vTheta = p.random((float) -0.001, (float) 0.001);
		theta += vTheta;

		if (theta < 0 || theta > PConstants.TWO_PI) {
			theta *= -1;
		}

		vU += p.random((float) -0.001, (float) 0.001);
		u += vU;
		if (u < -1 || u > 1) {
			vU *= -1;
		}

		vU *= 0.95;
		vTheta *= 0.95;

		// switch with moving mode.
		switch (moveMode) {
		case 0: // 0. => Gathering
			/*
			 * Elliptoid formula:
			 * 
			 * x = a*cos(u)*sin(v)
			 * 
			 * y = b*sin(u)*sin(v)
			 * 
			 * z = c*cos(v).
			 * 
			 * for u in [0,2pi) and v in [0,pi].
			 */
			flatten = (float) (1.01 - flatten);
			nextX = (1 / flatten * pSph.radius * PApplet.cos(theta) * PApplet
					.sqrt(1 - (u * u)));
			nextY = (flatten * pSph.radius * PApplet.sin(theta) * PApplet
					.sqrt(1 - (u * u)));
			nextZ = u * pSph.radius;

			// calculate rotated positions
			float radX = 0;
			float radY = 180;
			float radZ = 0;
			if (winkL == 1) {
				radZ = -45;
			}
			if (winkR == 1) {
				radZ = 45;
			}

			float x1,
			y1,
			z1,
			x2,
			y2;

			x1 = nextX * PApplet.cos(radY) + nextZ * PApplet.sin(radY);
			y1 = nextY;
			z1 = -nextX * PApplet.sin(radY) + nextZ * PApplet.cos(radY);

			x2 = x1;
			y2 = y1 * PApplet.cos(radX) - z1 * PApplet.sin(radX);
			nextX = x2 * PApplet.cos(radZ) - y2 * PApplet.sin(radZ) + p.width
					/ 2;
			nextY = x2 * PApplet.sin(radZ) + y2 * PApplet.cos(radZ) + p.height
					/ 2;
			break;

		case 1:
			// 1.=> Spreading
			nextX += p.random(-p.width / 4, p.width / 4);
			nextY += p.random(-p.height / 4, p.height / 4);
			nextZ += p.random(-p.height / 4, p.height / 4);
		}

		// calculate the move position
		xDiff = (x - nextX) * pSph.speed;
		yDiff = (y - nextY) * pSph.speed;
		zDiff = (z - nextZ) * pSph.speed;

		x -= xDiff--;
		y -= yDiff--;
		z -= zDiff--;

	}

	void render(float exc, float med) {
		int fromColor = theColor;//a shade of blue, set in ParticleSphere constructor
		int toColor = p.color(196,16,121);//a shade of red
		/*resultColor is the ration between meditation (more blue) and excitement (more red) */
		int resultColor = p.lerpColor(fromColor, toColor, exc/(med+1));
		if ((x >= 0) && (x < p.width - 1) && (y >= 0) && (y < p.height - 1)) {
			int currC = pSph.currFrame[(int) x + ((int) y) * p.width];
			pSph.currFrame[(int) x + ((int) y) * p.width] = PApplet.blendColor(
					resultColor,
					currC, PConstants.ADD);
		}

	}
}