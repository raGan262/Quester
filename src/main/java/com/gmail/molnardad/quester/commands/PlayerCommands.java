package com.gmail.molnardad.quester.commands;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.managers.ProfileManager;

public class PlayerCommands {
	
	/**
	 * @uml.property  name="profMan"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	ProfileManager profMan = null;
	
	public PlayerCommands(Quester plugin) {
		this.profMan = plugin.getProfileManager();
	}
	
}
