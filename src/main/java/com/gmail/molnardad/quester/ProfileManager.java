package com.gmail.molnardad.quester;

public class ProfileManager {

	private static ProfileManager instance = null;
	
	// TODO separate profile manager
	
	protected static void setInstance(ProfileManager profileManager) {
		instance = profileManager;
	}
	
	public static ProfileManager getInstance() {
		return instance;
	}
}
