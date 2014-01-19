package emoapp;

import processing.core.PApplet;
import processing.core.PVector;

class Particle
{
	PVector loc; // location of particle
	PVector vel; // velocity
	PVector acc; // acceleration
	PVector col; // colour

	int life;
	int age; // age
	float radius; // radius
	PApplet p;

	float timer; // timer

	Particle(PApplet p, PVector location, PVector colour, PVector accel, int lifespan)
	{
		this.p = p;
		loc = location.get(); // get x,y,z of pointer.

		vel = new PVector(0, -40, 0); // velocity is 0 initially
		acc = new PVector(p.random(-accel.x, accel.x), 3, p.random(-3, 3)); // get x,y,z of acceleration
		// acc = accel.get();

		life = lifespan; // lifespan
		age = 0; // age is zero initially
		radius = 10;

		col = colour.get(); // col.x=r, col.y=g, col.z=b

		timer = life;
	}

	void run()
	{
		update();
		render();
	}

	void update()
	{
		if (loc.y >= 350)
		{
			vel.set(0, 0, 0);
			acc.set(0, 0, 0);
		}
		else
		{
			vel.add(acc);
			loc.add(vel.x, p.random(vel.y), 0);
			timer -= 1.0;
			age++;

		}
		col.y--;
	}

	void render()
	{
		p.stroke(col.x, col.y, col.z, timer);
		p.point(loc.x, loc.y, loc.z);

	}

	boolean dead()
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