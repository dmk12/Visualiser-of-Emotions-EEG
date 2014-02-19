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
	boolean connected = false, stateChanged = false, connError = false;
	String errorMsg = "";

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
				connected = false;
				connError = true;
				errorMsg = "Emotiv Engine start up failed.";
				System.out.println(errorMsg);
				return;
			}
			connected = true;
			connError = false;
			break;
		}
		case 2: {
			System.out.println("Target IP of EmoComposer: [127.0.0.1] ");
			if (Edk.INSTANCE.EE_EngineRemoteConnect("127.0.0.1", composerPort,
					"Emotiv Systems-5") != EdkErrorCode.EDK_OK.ToInt()) {
				connected = false;
				connError = true;
				errorMsg = "Cannot connect to EmoComposer. Make sure EmoComposer is running and try again.";
				System.out.println(errorMsg);
				return;
			}
			System.out.println("Connected to EmoComposer on [127.0.0.1]");
			connected = true;
			connError = false;
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

				// get facial values if they are active
				// otherwise they'll be 0, as per initialization
				if (EmoState.INSTANCE.ES_ExpressivIsActive(eState,
						EmoState.EE_ExpressivAlgo_t.EXP_SMILE.ToInt()) == 1)
					smile = EmoState.INSTANCE
							.ES_ExpressivGetSmileExtent(eState);
				if (EmoState.INSTANCE.ES_ExpressivIsActive(eState,
						EmoState.EE_ExpressivAlgo_t.EXP_CLENCH.ToInt()) == 1)
					clench = EmoState.INSTANCE
							.ES_ExpressivGetClenchExtent(eState);
				if (EmoState.INSTANCE.ES_ExpressivIsActive(eState,
						EmoState.EE_ExpressivAlgo_t.EXP_BLINK.ToInt()) == 1)
					blink = EmoState.INSTANCE.ES_ExpressivIsBlink(eState);
				if (EmoState.INSTANCE.ES_ExpressivIsActive(eState,
						EmoState.EE_ExpressivAlgo_t.EXP_WINK_LEFT.ToInt()) == 1)
					winkLeft = EmoState.INSTANCE.ES_ExpressivIsLeftWink(eState);
				if (EmoState.INSTANCE.ES_ExpressivIsActive(eState,
						EmoState.EE_ExpressivAlgo_t.EXP_WINK_RIGHT.ToInt()) == 1)
					winkRight = EmoState.INSTANCE
							.ES_ExpressivIsRightWink(eState);

				// indicates if an Emo event occurred
				stateChanged = true;

			}
		}
		else if (state != EdkErrorCode.EDK_NO_EVENT.ToInt()) {
			errorMsg = "EmoComposer not running or internal error in Emotiv Engine. Disconnected.";
			System.out.println(errorMsg);
			disconnect();
		}
		return stateChanged;
	}

	public void disconnect() {
		Edk.INSTANCE.EE_EngineDisconnect();
		connected = false;
	}
}
