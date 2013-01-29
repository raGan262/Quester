package com.gmail.molnardad.quester.qevents;

import net.aufdemrand.denizen.Denizen;
import net.aufdemrand.denizen.npc.DenizenNPC;
import net.citizensnpcs.api.CitizensAPI;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.elements.Qevent;
import com.gmail.molnardad.quester.exceptions.CustomException;
import com.gmail.molnardad.quester.exceptions.QuesterException;

@QElement("DSCRIPT")
public final class DenizenScriptQevent extends Qevent {

	private final String script;
	private final int npc;
	private final boolean playerContext;
	private final boolean focusNPC;
	
	public DenizenScriptQevent(String script, int npc, boolean playerContext, boolean focusNPC) {
		this.script = script;
		this.npc = npc;
		this.playerContext = playerContext;
		this.focusNPC = focusNPC;
	}
	
	@Override
	public String info() {
		String npcStr = (npc >= 0) ? npc+"" : "none";
		String focStr = focusNPC ? "NPC" : "PLAYER";
		return script + "; NPC: " + npcStr + "; PLAYER: " + playerContext + "; FOCUS: " + focStr;
	}

	@Override
	protected void run(Player player, Quester plugin) {
		try {
			if(Quester.denizen) {
				Denizen den = (Denizen) Bukkit.getPluginManager().getPlugin("Denizen");
				if(den == null) {
					throw new CustomException("Denizen plugin not found.");
				}
				if(!playerContext && (npc < 0)) {
					throw new CustomException("Not enough information to run script. (should not happen, bug)");
				}
				else {
					boolean success = false;
					DenizenNPC denNpc = null;
					try {
						denNpc = den.getNPCRegistry().getDenizen(CitizensAPI.getNPCRegistry().getById(npc));
					} 
					catch (Exception ignore) {}
					if(playerContext) {
						if(npc >= 0) {
							if(denNpc == null) {
								throw new CustomException("Couldn't resolve DENIZEN npc.");
							}
							if(focusNPC) {
								success = den.getScriptEngine().getScriptBuilder().runTaskScript(denNpc, player, script);
							}
							else {
								success = den.getScriptEngine().getScriptBuilder().runTaskScript(player, denNpc, script);
							}
						}
						else {
							success = den.getScriptEngine().getScriptBuilder().runTaskScript(player, script);
						}
					}
					else {
						if(denNpc == null) {
							throw new CustomException("Couldn't resolve DENIZEN npc.");
						}
						success = den.getScriptEngine().getScriptBuilder().runTaskScript(denNpc, script);
					}
					if(!success) {
						throw new CustomException("Script not found or brokens.");
					}
				}
			}
		} 
		catch (QuesterException e) {
			Quester.log.warning("Failed to run DSCRIPT event. Info: " + e.getMessage());
		}
	}

	@QCommand(
			min = 1,
			max = 2,
			usage = "<script> [npc ID] (-cn)")
	public static Qevent fromCommand(QCommandContext context) {
		String script = context.getString(0);
		int npc = -1;
		if(context.length() > 1) {
			npc = context.getInt(1);
		}
		boolean playerContext = !context.hasFlag('c');
		boolean focusNPC = context.hasFlag('n');
		return new DenizenScriptQevent(script, npc, playerContext, focusNPC);
	}

	// TODO serialization
	
	public void serialize(ConfigurationSection section) {
		section.set("script", script);
		if(npc >= 0) {
			section.set("npc", npc);
		}
		if(!playerContext) {
			section.set("playercontext", playerContext);
		}
		if(focusNPC) {
			section.set("focusnpc", focusNPC);
		}
	}
	
	public static DenizenScriptQevent deser(ConfigurationSection section) {
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
		
		return new DenizenScriptQevent(scrpt, npc, pcont, focNpc);
	}
}
