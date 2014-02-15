package emoapp;

import processing.core.PApplet;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public class EdkConn {
	PApplet p;
	// Emo Headset variables
	Pointer eEvent = Edk.INSTANCE.EE_EmoEngineEventCreate();
	Pointer eState = Edk.INSTANCE.EE_EmoStateCreate();
	IntByReference userID = new IntByReference(0);
	// connection port for headset
	short composerPort = 1726;
	int state = 0;
	boolean connected = false;
	boolean stateChanged = false;
	
	int signal = 0, headsetOn = 0, avgContactQlty = 0;

	float excitement, engagement, meditation, frustration;
	float smile, clench;
	int blink, winkLeft, winkRight;

	public EdkConn(PApplet p) {
		this.p = p;
	}

	public void edkConn(int option) {
		switch (option) {
		case 1: {
			if (Edk.INSTANCE.EE_EngineConnect("Emotiv Systems-5") != EdkErrorCode.EDK_OK
					.ToInt()) {
				System.out.println("Emotiv Engine start up failed.");
				return;
			}
			connected = true;
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
			connected = true;
			break;
		}
		default:
			System.out.println("Invalid option...");
			return;
		}
	}

	// called by EmoApp.draw() every frame
	public boolean edkRun() {
		// is headset on
		headsetOn = EmoState.INSTANCE.ES_GetHeadsetOn(eState);
		// wireless signal quality
		signal = EmoState.INSTANCE.ES_GetWirelessSignalStatus(eState);
		// calculate average electrode contact quality (0-2)
		int i = EmoState.INSTANCE.ES_GetNumContactQualityChannels(eState);
		for (int j = 0; j < i; j++) {
			avgContactQlty += (EmoState.INSTANCE
					.ES_GetContactQuality(eState, j));
		}
		avgContactQlty = avgContactQlty / i;
		
		state = Edk.INSTANCE.EE_EngineGetNextEvent(eEvent);
		// New event needs to be handled
		if (state == EdkErrorCode.EDK_OK.ToInt()) {
			int eventType = Edk.INSTANCE.EE_EmoEngineEventGetType(eEvent);
			Edk.INSTANCE.EE_EmoEngineEventGetUserId(eEvent, userID);

			// Log the EmoState if it has been updated
			if (eventType == Edk.EE_Event_t.EE_EmoStateUpdated.ToInt()) {
				Edk.INSTANCE.EE_EmoEngineEventGetEmoState(eEvent, eState);

				// get emotion values if they are active
				// otherwise they'll be 0, as per initialization
				if (EmoState.INSTANCE.ES_AffectivIsActive(eState,
						EmoState.EE_AffectivAlgo_t.AFF_EXCITEMENT.ToInt()) == 1)
					excitement = EmoState.INSTANCE
							.ES_AffectivGetExcitementShortTermScore(eState);

				if (EmoState.INSTANCE.ES_AffectivIsActive(eState,
						EmoState.EE_AffectivAlgo_t.AFF_ENGAGEMENT_BOREDOM
								.ToInt()) == 1)
					engagement = EmoState.INSTANCE
							.ES_AffectivGetEngagementBoredomScore(eState);

				if (EmoState.INSTANCE.ES_AffectivIsActive(eState,
						EmoState.EE_AffectivAlgo_t.AFF_MEDITATION.ToInt()) == 1)
					meditation = EmoState.INSTANCE
							.ES_AffectivGetMeditationScore(eState);

				if (EmoState.INSTANCE.ES_AffectivIsActive(eState,
						EmoState.EE_AffectivAlgo_t.AFF_FRUSTRATION.ToInt()) == 1)
					frustration = EmoState.INSTANCE
							.ES_AffectivGetFrustrationScore(eState);

				// facial
				smile = EmoState.INSTANCE
						.ES_ExpressivGetSmileExtent(eState);
				clench = EmoState.INSTANCE
						.ES_ExpressivGetClenchExtent(eState);
				blink = EmoState.INSTANCE.ES_ExpressivIsBlink(eState);
				winkLeft = EmoState.INSTANCE.ES_ExpressivIsLeftWink(eState);
				winkRight = EmoState.INSTANCE
						.ES_ExpressivIsRightWink(eState);

				// indicates if an Emo event occurred
				stateChanged = true;

			}
		}
		else if (state != EdkErrorCode.EDK_NO_EVENT.ToInt()) {
			System.out
					.println("EmoComposer not running or internal error in Emotiv Engine. Disconnected.");
			// Break draw() loop on error
			p.noLoop();
			Edk.INSTANCE.EE_EngineDisconnect();

		}

		return stateChanged;
	}
}
