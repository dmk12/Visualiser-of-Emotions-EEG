package emoapp;

import processing.core.PApplet;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public class EdkConn {
	// Emo Headset variables
	Pointer eEvent = Edk.INSTANCE.EE_EmoEngineEventCreate();
	Pointer eState = Edk.INSTANCE.EE_EmoStateCreate();
	IntByReference userID = new IntByReference(0);
	// connection port for headset
	short composerPort = 1726;
	// alternate between headset (1) and emocomposer (2)
	int option = 2;
	int state = 0;
	
	public float exc, eng, med, frs, blink;
	
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

	public void edkRun(PApplet p) {
		state = Edk.INSTANCE.EE_EngineGetNextEvent(eEvent);
		// New event needs to be handled
		if (state == EdkErrorCode.EDK_OK.ToInt()) {
			int eventType = Edk.INSTANCE.EE_EmoEngineEventGetType(eEvent);
			Edk.INSTANCE.EE_EmoEngineEventGetUserId(eEvent, userID);

			// Log the EmoState if it has been updated
			if (eventType == Edk.EE_Event_t.EE_EmoStateUpdated.ToInt()) {
				Edk.INSTANCE.EE_EmoEngineEventGetEmoState(eEvent, eState);
				// get emotion values
				exc = EmoState.INSTANCE
						.ES_AffectivGetExcitementShortTermScore(eState);
				eng = EmoState.INSTANCE
						.ES_AffectivGetEngagementBoredomScore(eState);
				med = EmoState.INSTANCE.ES_AffectivGetMeditationScore(eState);
				frs = EmoState.INSTANCE.ES_AffectivGetFrustrationScore(eState);
				// detect blink
				blink = EmoState.INSTANCE.ES_ExpressivIsBlink(eState);

				// console feedback
				System.out.println("Excitement: " + exc);
				System.out.println("Engagement/Boredom: " + eng);
				System.out.println("Meditation: " + med);
				System.out.println("Frustration: " + frs);
				if (blink == 1) {
					System.out.println("You blinked!");
				}
			}
		} else if (state != EdkErrorCode.EDK_NO_EVENT.ToInt()) {
			System.out.println("Internal error in Emotiv Engine!");
			// Break draw() loop on error
			p.noLoop();
			Edk.INSTANCE.EE_EngineDisconnect();
			System.out.println("Disconnected!");
		}

	}

}
