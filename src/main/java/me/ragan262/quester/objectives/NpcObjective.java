package me.ragan262.quester.objectives;

import me.ragan262.quester.commandbase.QCommand;
import me.ragan262.quester.commandbase.QCommandContext;
import me.ragan262.quester.commandbase.exceptions.QCommandException;
import me.ragan262.quester.elements.Objective;
import me.ragan262.quester.elements.QElement;
import me.ragan262.quester.storage.StorageKey;

@QElement("NPC")
public final class NpcObjective extends Objective {
	
	private final int index;
	private final boolean cancel;
	
	public NpcObjective(final int id, final boolean ccl) {
		index = id;
		cancel = ccl;
	}
	
	@Override
	public int getTargetAmount() {
		return 1;
	}
	
	@Override
	protected String show(final int progress) {
		return "Interact with NPC ID " + index + ".";
	}
	
	@Override
	protected String info() {
		return index + "; CANCEL: " + cancel;
	}
	
	@QCommand(min = 1, max = 1, usage = "<id> (-c)")
	public static Objective fromCommand(final QCommandContext context) throws QCommandException {
		final int id = context.getInt(0);
		final boolean ccl = context.hasFlag('c');
		if(id < 0) {
			throw new QCommandException(context.getSenderLang().get("ERROR_CMD_BAD_ID"));
		}
		return new NpcObjective(id, ccl);
	}
	
	@Override
	protected void save(final StorageKey key) {
		key.setInt("index", index);
		if(cancel) {
			key.setBoolean("cancel", cancel);
		}
	}
	
	protected static Objective load(final StorageKey key) {
		int id = -1;
		boolean ccl = false;
		id = key.getInt("index", -1);
		if(id < 0) {
			return null;
		}
		ccl = key.getBoolean("cancel", false);
		return new NpcObjective(id, ccl);
	}
	
	// Custom methods
	
	public boolean checkNpc(final int npc) {
		return npc == index;
	}
	
	public boolean getCancel() {
		return cancel;
	}
}
