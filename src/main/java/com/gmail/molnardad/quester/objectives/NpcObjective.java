package com.gmail.molnardad.quester.objectives;

import org.bukkit.configuration.ConfigurationSection;

import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.exceptions.QuesterException;

@QElement("NPC")
public final class NpcObjective extends Objective {

	private final int index;
	private final boolean cancel;
	
	public NpcObjective(int id, boolean ccl) {
		index = id;
		cancel = ccl;
	}
	
	@Override
	public int getTargetAmount() {
		return 1;
	}
	
	@Override
	protected String show(int progress) {
		return "Interact with NPC ID " + index + ".";
	}
	
	@Override
	protected String info() {
		return index + "; CANCEL: " + cancel;
	}
	
	@QCommand(
			min = 1,
			max = 1,
			usage = "<id> (-c)")
	public static Objective fromCommand(QCommandContext context) throws QCommandException {
		int id = context.getInt(0);
		boolean ccl = context.hasFlag('c');
		if(id < 0) {
			throw new QCommandException(context.getSenderLang().ERROR_CMD_BAD_ID);
		}
		return new NpcObjective(id, ccl);
	}

	// TODO serialization
	
	public void serialize(ConfigurationSection section) {
		section.set("index", index);
		if(cancel) {
			section.set("cancel", cancel);
		}
	}
	
	public static Objective deser(ConfigurationSection section) throws QuesterException {
		int id = -1;
		boolean ccl = false;
		id = section.getInt("index", -1);
		if(id < 0)
			return null;
		ccl = section.getBoolean("cancel", false);
		return new NpcObjective(id, ccl);
	}
	
	//Custom methods
	
	public boolean checkNpc(int npc) {
		return npc == index;
	}
	
	public boolean getCancel() {
		return cancel;
	}
}
