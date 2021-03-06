package me.ragan262.quester.triggers;

import me.ragan262.commandmanager.annotations.Command;
import me.ragan262.commandmanager.exceptions.CommandException;
import me.ragan262.quester.commandmanager.QuesterCommandContext;
import me.ragan262.quester.elements.QElement;
import me.ragan262.quester.elements.Trigger;
import me.ragan262.quester.elements.TriggerContext;
import me.ragan262.quester.storage.StorageKey;
import org.bukkit.entity.Player;

@QElement("NPC")
public class NpcTrigger extends Trigger {
	
	private final int index;
	
	public NpcTrigger(final int id) {
		index = id;
	}
	
	@Override
	protected String info() {
		return String.valueOf(index);
	}
	
	@Override
	protected boolean evaluate0(final Player player, final TriggerContext context) {
		if(context.getType().equals("NPC_CLICK")) {
			try {
				return index == (Integer)context.get("CLICKEDNPC");
			}
			catch(final Exception ignore) {}
		}
		return false;
	}
	
	@Command(min = 1, max = 1, usage = "<id>")
	public static Trigger fromCommand(final QuesterCommandContext context) throws CommandException {
		final int id = context.getInt(0);
		if(id < 0) {
			throw new CommandException(context.getSenderLang().get("ERROR_CMD_BAD_ID"));
		}
		return new NpcTrigger(id);
	}
	
	@Override
	protected void save(final StorageKey key) {
		key.setInt("index", index);
	}
	
	protected static Trigger load(final StorageKey key) {
		final int id = key.getInt("index", -1);
		if(id < 0) {
			return null;
		}
		return new NpcTrigger(id);
	}
	
}
