package com.gmail.molnardad.quester.qevents;

import java.util.HashMap;
import java.util.Map;

import net.aufdemrand.denizen.Denizen;
import net.aufdemrand.denizen.npc.dNPC;
import net.aufdemrand.denizen.scripts.ScriptRegistry;
import net.aufdemrand.denizen.scripts.containers.core.TaskScriptContainer;
import net.citizensnpcs.api.CitizensAPI;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.elements.Qevent;
import com.gmail.molnardad.quester.exceptions.CustomException;
import com.gmail.molnardad.quester.storage.StorageKey;

@QElement("DSCRIPT")
public final class DenizenScriptQevent extends Qevent {
	
	private final String script;
	private final int npc;
	private final Map<String, String> context;
	
	public DenizenScriptQevent(final String script, final int npc, final Map<String, String> context) {
		this.script = script;
		this.npc = npc;
		this.context = context;
	}
	
	@Override
	public String info() {
		final String npcStr = npc >= 0 ? npc + "" : "none";
		final StringBuilder csb = new StringBuilder();
		if(context != null) {
			for(final String ck : context.keySet()) {
				csb.append(", ").append(ck).append(':').append(context.get(ck));
			}
		}
		else {
			csb.append("none");
		}
		return script + "; NPC: " + npcStr + "; CONT: " + csb.toString();
	}
	
	@Override
	protected void run(final Player player, final Quester plugin) {
		try {
			if(Quester.denizen) {
				final Denizen den = (Denizen) Bukkit.getPluginManager().getPlugin("Denizen");
				if(den == null) {
					throw new CustomException("Denizen plugin not found.");
				}
				else {
					dNPC denNpc = null;
					try {
						denNpc = den.getNPCRegistry().getDenizen(
								CitizensAPI.getNPCRegistry().getById(npc));
					}
					catch (final Exception ignore) {}
					final TaskScriptContainer taskScript = ScriptRegistry.getScriptContainerAs(
							script, TaskScriptContainer.class);
					if(npc >= 0 && denNpc == null) {
						throw new CustomException("Couldn't resolve DENIZEN npc.");
					}
					if(taskScript.runTaskScript(player, denNpc, context) == null) {
						throw new CustomException("Something went wrong.");
					}
				}
			}
		}
		catch (final Exception e) {
			Quester.log.warning("Failed to run DSCRIPT event. Info: " + e.getMessage());
		}
	}
	
	@QCommand(min = 1, usage = "<script> [npc ID] [context key:value]...")
	public static Qevent fromCommand(final QCommandContext context) throws QCommandException {
		final String script = context.getString(0);
		int npc = -1;
		Map<String, String> cont = null;
		if(context.length() > 1) {
			npc = context.getInt(1);
			if(context.length() > 2) {
				String[] ss;
				for(int i = 2; i < context.length(); i++) {
					cont = new HashMap<String, String>();
					ss = context.getString(i).split(":");
					if(ss.length != 2) {
						throw new QCommandException(
								context.getSenderLang().ERROR_CMD_ARG_CANT_PARSE.replaceAll("%arg",
										context.getString(i)));
					}
					cont.put(ss[0], ss[1]);
				}
			}
		}
		return new DenizenScriptQevent(script, npc, cont);
	}
	
	@Override
	protected void save(final StorageKey key) {
		key.setString("script", script);
		if(npc >= 0) {
			key.setInt("npc", npc);
		}
		if(context != null) {
			final StorageKey subKey = key.getSubKey("context");
			for(final String s : context.keySet()) {
				subKey.setString(s, context.get(s));
			}
		}
	}
	
	protected static Qevent load(final StorageKey key) {
		String scrpt;
		int npc;
		Map<String, String> context = null;
		npc = key.getInt("npc", -1);
		scrpt = key.getString("script");
		if(scrpt == null) {
			return null;
		}
		if(key.getSubKey("context").hasSubKeys()) {
			context = new HashMap<String, String>();
			String s = null;
			for(final StorageKey k : key.getSubKey("context").getSubKeys()) {
				s = k.getString("", null);
				if(s != null) {
					context.put(k.getName(), s);
				}
			}
			if(context.isEmpty()) {
				context = null;
			}
		}
		return new DenizenScriptQevent(scrpt, npc, context);
	}
}
