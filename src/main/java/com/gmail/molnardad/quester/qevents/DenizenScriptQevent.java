package com.gmail.molnardad.quester.qevents;

import net.aufdemrand.denizen.Denizen;
import net.aufdemrand.denizen.npc.DenizenNPC;
import net.aufdemrand.denizen.utilities.arguments.aH;
import net.citizensnpcs.api.CitizensAPI;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.exceptions.QuesterException;

public final class DenizenScriptQevent extends Qevent {

	public static final String TYPE = "DSCRIPT";
	private final String script;
	private final int npc;
	private final boolean playerContext;
	private final boolean focusNPC;
	
	public DenizenScriptQevent(int occ, int del, String script, int npc, boolean playerContext, boolean focusNPC) {
		super(occ, del);
		this.script = script;
		this.npc = npc;
		this.playerContext = playerContext;
		this.focusNPC = focusNPC;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	@Override
	public int getOccasion() {
		return occasion;
	}
	
	@Override
	public String toString() {
		String npcStr = (npc >= 0) ? npc+"" : "none";
		String focStr = focusNPC ? "NPC" : "PLAYER";
		return TYPE + ": " + script + "; NPC: " + npcStr + "; PLAYER: " + playerContext + "; FOCUS: " + focStr + appendSuper();
	}

	@Override
	public void serialize(ConfigurationSection section) {
		super.serialize(section, TYPE);
		section.set("script", script);
		if(npc >= 0) {
			section.set("npc", npc);
		}
		if(!playerContext) {
			section.set("playercontext", playerContext);
		}
		if(!focusNPC) {
			section.set("focusnpc", focusNPC);
		}
	}
	
	public static DenizenScriptQevent deser(int occ, int del, ConfigurationSection section) {
		String scrpt;
		int npc;
		boolean pcont, focNpc;
		pcont = section.getBoolean("playercontext", true);
		focNpc = section.getBoolean("focusnpc", false);
		npc = section.getInt("npc", -1);
		scrpt = section.getString("script", "");
		if(scrpt.isEmpty() || (!pcont && (npc < 0))) {
			return null;
		}
		
		return new DenizenScriptQevent(occ, del, scrpt, npc, pcont, focNpc);
	}

	@Override
	void run(Player player) {
		// TODO finish this thing + command
		try {
			if(Quester.denizen) {
				Denizen den = (Denizen) Bukkit.getPluginManager().getPlugin("Denizen");
				if(den == null) {
					throw new QuesterException("Denizen plugin not found.");
				}
				if(!aH.matchesScript("script:" + script)) {
					throw new QuesterException("Script not found.");
				}
				if(!playerContext && (npc < 0)) {
					throw new QuesterException("Not enough information to run script. (should not happen, bug)");
				}
				else {
					DenizenNPC denNpc = null;
					try {
						denNpc = den.getNPCRegistry().getDenizen(CitizensAPI.getNPCRegistry().getById(npc));
					} 
					catch (Exception ignore) {}
					if(playerContext) {
						if(npc >= 0) {
							if(denNpc == null) {
								throw new QuesterException("Couldn't resolve DENIZEN npc.");
							}
							if(focusNPC) {
								den.getScriptEngine().getScriptBuilder().runTaskScript(denNpc, player, script);
							}
							else {
								den.getScriptEngine().getScriptBuilder().runTaskScript(player, denNpc, script);
							}
						}
						else {
							den.getScriptEngine().getScriptBuilder().runTaskScript(player, script);
						}
					}
					else {
						if(denNpc == null) {
							throw new QuesterException("Couldn't resolve DENIZEN npc.");
						}
						den.getScriptEngine().getScriptBuilder().runTaskScript(denNpc, script);
					}
				}
			}
		} 
		catch (QuesterException e) {
			Quester.log.warning("Failed to run DSCRIPT event. Info: " + e.message());
		}
	}
}
