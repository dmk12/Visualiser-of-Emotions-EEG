package emoapp;

import processing.core.PApplet;
import processing.core.PShape;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;


public class EmoApp extends PApplet {

	private int niceRed = color(200, 0, 0);

	// Emo Headset variables
	Pointer eEvent = Edk.INSTANCE.EE_EmoEngineEventCreate();
	Pointer eState = Edk.INSTANCE.EE_EmoStateCreate();
	IntByReference userID = new IntByReference(0);
	// connection port for headset
	short composerPort = 1726;
	// alternate between headset (1) and emocomposer (2)
	int option = 2;
	int state = 0;

	//emotion variables
	float exc = 0;//excitement
	float eng = 0;//engagement/boredom
	float med = 0;//meditation
	float frs = 0;//frustration
	//circles to represent each emotion
	//private PShape excCrc;
	private Circle excCrc = new Circle();
	private Circle engCrc = new Circle();
	private Circle medCrc = new Circle();
	private Circle frsCrc = new Circle();
	
	// Setup can be used like in the processing tool.
	public void setup() {
		// Set the canvas size
		size(500, 200, P2D);
		// Let's use anti aliasing!
		smooth();
		//doesn't show!!!
		//excCrc = createShape(ELLIPSE, 100, 100, 50, 50);

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
		excCrc.update(100, -exc * 100 + 120);
		//excCrc.scale(exc * 100, exc * 100);
		engCrc.update(200, -eng * 100 + 120);
		medCrc.update(300, -med * 100 + 120);
		frsCrc.update(400, -frs * 100 + 120);
		/*
		 * Tell circle that it should draw itself now. To do so, circle needs
		 * our PGraphics instance named 'g'. 'g' is automatically created by
		 * PApplet and because we extend PApplet it exists at this point!
		 */
		excCrc.draw(g);
		engCrc.draw(g);
		medCrc.draw(g);
		frsCrc.draw(g);
	}

	public void edkConn() {
		switch (option) {
		case 1: {
			if (Edk.INSTANCE.EE_EngineConnect("Emotiv Systems-5") != EdkErrorCode.EDK_OK.ToInt()) {
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
				//get emotion values
				exc = EmoState.INSTANCE.ES_AffectivGetExcitementShortTermScore(eState);
				eng = EmoState.INSTANCE.ES_AffectivGetEngagementBoredomScore(eState);
				med = EmoState.INSTANCE.ES_AffectivGetMeditationScore(eState);
				frs = EmoState.INSTANCE.ES_AffectivGetFrustrationScore(eState);
				
				//System.out.println("Excitement level: " + n);

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
