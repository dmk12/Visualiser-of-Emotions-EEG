package emoapp;

import processing.core.PApplet;

/**
 * This class is modified from Hayato Moritan
 * http://www.openprocessing.org/sketch/5662, who in turn modified it from
 * "Noise Particle 06" by Marcin Ignac
 * http://www.openprocessing.org/visuals/?visualID=1163
 */
public class ImgProc {
	PApplet p;

	public ImgProc(PApplet p) {
		this.p = p;
	}

	public void drawPixelArray(int[] src, int dx, int dy, int w, int h) {

		p.loadPixels();
		int x;
		int y;

		for (int i = 0; i < w * h; i++) {
			x = dx + i % w;
			y = dy + i / w;
			p.pixels[x + y * w] = src[i];
		}
		p.updatePixels();
	}

	// Blur effects.
	public void blur(int[] src, int[] dst, int w, int h) {

		int c;
		int r, g, b;

		for (int y = 1; y < h - 1; y++) {
			for (int x = 1; x < w - 1; x++) {

				r = 0;
				g = 0;
				b = 0;

				for (int yb = -1; yb <= 1; yb++) {
					for (int xb = -1; xb <= 1; xb++) {
						c = src[(x + xb) + (y - yb) * w];
						r += p.red(c);
						g += p.green(c);
						b += p.blue(c);
					}
				}

				r /= 9;
				g /= 9;
				b /= 9;
				dst[x + y * w] = 0xff000000 | (r << 16) | (g << 8) | b;
			}
		}
	}

	// Scale Brightness effects.
	public void scaleBrightness(int[] src, int[] dst, int w, int h, float s) {

		int r, g, b;
		int c;
		int a;
		float as = s;

		s = 1;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {

				c = src[x + y * w];
				a = (int) (as * (p.alpha(c)));
				r = (int) (s * (p.red(c)));
				g = (int) (s * (p.green(c)));
				b = (int) (s * (p.blue(c)));
				dst[x + y * w] = (a << 24) | (r << 16) | (g << 8) | b;
			}
		}
	}
}
