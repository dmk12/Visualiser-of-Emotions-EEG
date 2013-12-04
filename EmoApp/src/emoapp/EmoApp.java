package emoapp;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

import processing.core.PApplet;


public class EmoApp extends PApplet {

	private int niceRed = color(200, 0, 0);
	/*
	 * Let's create a new variable of the Type 'Circle' and fill it with a new
	 * instance of Circle.
	 */
	private Circle circle = new Circle();

	// Emo Headset variables
	Pointer eEvent = Edk.INSTANCE.EE_EmoEngineEventCreate();
	Pointer eState = Edk.INSTANCE.EE_EmoStateCreate();
	IntByReference userID = new IntByReference(0);
	// connection port for headset
	short composerPort = 1726;
	// alternate between headset (1) and emocomposer (2)
	int option = 2;
	int state = 0;
	float n = 0;

	// Setup can be used like in the processing tool.
	public void setup() {
		// Set the canvas size
		size(200, 200, P2D);
		// Let's use anti aliasing!
		smooth();
		// Don't draw strokes on shapes
		noStroke();
		// Connect to headset
		edkConn();
	}

	// Draw is used like in the processing tool.
	public void draw() {
		// Run headset event listener loop each time draw() is called
		edkRun();
		// Redraw the background with black
		background(0);
		// Fill every following shape with a 'niceRed'
		fill(niceRed);
		/*
		 * Tell circle where the mouse is. We can use ANY input we want (e.g. a
		 * sudden motion sensor input). Circle just wants two float variables,
		 * it doesn't matters how they're created
		 */
		circle.update(n * 100, n * 100);
		/*
		 * Tell circle that it should draw itself now. To do so, circle needs
		 * our PGraphics instance named 'g'. 'g' is automatically created by
		 * PApplet and because we extend PApplet it exists at this point!
		 */
		circle.draw(g);
	}

	public void edkConn() {
		switch (option) {
		case 1: {
			if (Edk.INSTANCE.EE_EngineConnect("Emotiv Systems-5") != EdkErrorCode.EDK_OK
					.ToInt()) {
				System.out.println("Emotiv Engine start up failed.");
				return;
			}
			break;
		}
		case 2: {
			System.out.println("Target IP of EmoComposer: [127.0.0.1] ");
			if (Edk.INSTANCE.EE_EngineRemoteConnect("127.0.0.1", composerPort,
					"Emotiv Systems-5") != EdkErrorCode.EDK_OK.ToInt()) {
				System.out
						.println("Cannot connect to EmoComposer on [127.0.0.1]");
				return;
			}
			System.out.println("Connected to EmoComposer on [127.0.0.1]");
			break;
		}
		default:
			System.out.println("Invalid option...");
			return;
		}
	}

	public void edkRun() {
		state = Edk.INSTANCE.EE_EngineGetNextEvent(eEvent);
		// New event needs to be handled
		if (state == EdkErrorCode.EDK_OK.ToInt()) {
			int eventType = Edk.INSTANCE.EE_EmoEngineEventGetType(eEvent);
			Edk.INSTANCE.EE_EmoEngineEventGetUserId(eEvent, userID);

			// Log the EmoState if it has been updated
			if (eventType == Edk.EE_Event_t.EE_EmoStateUpdated.ToInt()) {
				Edk.INSTANCE.EE_EmoEngineEventGetEmoState(eEvent, eState);
				n = EmoState.INSTANCE
						.ES_AffectivGetExcitementShortTermScore(eState);

				System.out.println("Excitement level: " + n);

			}
		} else if (state != EdkErrorCode.EDK_NO_EVENT.ToInt()) {
			System.out.println("Internal error in Emotiv Engine!");
			// Break draw() loop on error
			noLoop();
			Edk.INSTANCE.EE_EngineDisconnect();
			System.out.println("Disconnected!");
		}

	}

	public static void main(String _args[]) {
		PApplet.main(new String[] { emoapp.EmoApp.class.getName() });
	}
}
