package com.gmail.molnardad.quester.commands;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.profiles.ProfileManager;

public class PlayerCommands {
	
	ProfileManager profMan = null;
	
	public PlayerCommands(final Quester plugin) {
		profMan = plugin.getProfileManager();
	}
	
}
