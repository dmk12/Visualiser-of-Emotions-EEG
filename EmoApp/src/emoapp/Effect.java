package emoapp;

public abstract class Effect {
	float timer;
	
	public Effect() {

	}
	public abstract void draw();
	public void countdown(float count){
		timer -= count;
	}
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
