package emoapp;

import processing.core.PApplet;
import processing.core.PShape;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;


public class EmoApp extends PApplet {

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
	//blink
	int blink = 0;
	float blkX;
	float blkY;
	//circles to represent each emotion
	private PShape excCrc;
	private PShape engCrc;
	private PShape medCrc;
	private PShape frsCrc;
	//circle to represent blink
	private PShape blkCrc;
	
	// Setup can be used like in the processing tool.
	public void setup() {
		// Set the canvas size
		size(500, 200, P3D);
		// Let's use anti aliasing!
		smooth();
		lights();
		excCrc = createShape(ELLIPSE, 0, 0, 20, 20);
		engCrc = createShape(ELLIPSE, 0, 0, 20, 20);
		medCrc = createShape(ELLIPSE, 0, 0, 20, 20);
		frsCrc = createShape(ELLIPSE, 0, 0, 20, 20);
		
		blkCrc = createShape(ELLIPSE, 0, 0, 30, 30);
		
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

		//set fills
		excCrc.setFill(color(150*exc+50,0,0));
		engCrc.setFill(color(0,150*eng+50,0));
		medCrc.setFill(color(0,0,150*med+50));
		frsCrc.setFill(color(150*frs+50,0,150*frs+50));
		
		//draw shapes
		shape(excCrc, 100, -exc * 100 + 120);
		shape(engCrc, 200, -eng * 100 + 120);
		shape(medCrc, 300, -med * 100 + 120);
		shape(frsCrc, 400, -frs * 100 + 120);


		//detect blink
		if (blink == 1) {
			blkX = (float) Math.random() * displayWidth;
			blkY = (float) Math.random() * displayHeight;
			shape(blkCrc, blkX, blkY);
		}		
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
				//detect blink
				blink = EmoState.INSTANCE.ES_ExpressivIsBlink(eState);
				
				//console  feedback
				System.out.println("Excitement: "+exc);
				System.out.println("Engagement/Boredom: "+eng);
				System.out.println("Meditation: "+med);
				System.out.println("Frustration: "+frs);
				if (blink == 1) {
					System.out.println("You blinked!");
				}
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
