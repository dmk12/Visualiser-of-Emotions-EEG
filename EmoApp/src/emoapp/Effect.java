package emoapp;

public abstract class Effect {
	float timer;
	public Effect() {

	}
	public abstract void draw();
	public boolean dead(){
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
