package emoapp;
// Be sure to import the PGraphics class
import processing.core.PGraphics;

public class Circle {

	/*
	 * Two private float variables, that we use to save our x and y position.
	 * This variables are set to private, because we don't want them to be
	 * changed from any other class. The only way to modify them is to call our
	 * update method and we will set x and y ourself.
	 */
	private float x, y;

	/**
	 * Update our x and y position
	 * 
	 * @param mouseX
	 *            (can also be another input like a sudden motion value)
	 * @param mouseY
	 *            (can also be another input like a sudden motion value)
	 */
	public void update(float mouseX, float mouseY) {
		x = mouseX;
		y = mouseY;
	}

	/*
	 * Because we don't extend PApplet this class has no running PGraphics
	 * instance. To be able to use it and to be sure ALL drawing classes use the
	 * same 'Window' (PGraphics instance) our parent class 'ProcessingSketch'
	 * will deliver us a PGraphics instance to draw on. It's common in almost
	 * every graphic library to call this instance 'g'.
	 */
	public void draw(PGraphics g) {
		// Use our parents 'g' to draw ourself
		g.ellipse(x, y, 20, 20);
	}
}