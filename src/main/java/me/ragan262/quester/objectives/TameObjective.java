package me.ragan262.quester.objectives;

import me.ragan262.commandmanager.annotations.Command;
import me.ragan262.commandmanager.exceptions.CommandException;
import me.ragan262.quester.commandmanager.QuesterCommandContext;
import me.ragan262.quester.elements.Objective;
import me.ragan262.quester.elements.QElement;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.SerUtils;
import org.bukkit.entity.EntityType;

@QElement("TAME")
public final class TameObjective extends Objective {
	
	private final EntityType entity;
	private final int amount;
	
	public TameObjective(final int amt, final EntityType ent) {
		entity = ent;
		amount = amt;
	}
	
	@Override
	public int getTargetAmount() {
		return amount;
	}
	
	@Override
	protected String show(final int progress) {
		final String mob = entity == null ? "any mob" : entity.getName();
		return "Tame " + mob + " - " + (amount - progress) + "x";
	}
	
	@Override
	protected String info() {
		final String entStr = entity == null ? "ANY" : entity.getName();
		return entStr + "; AMT: " + amount;
	}
	
	@Command(min = 1, max = 2, usage = "<amount> {[entity]}")
	public static Objective fromCommand(final QuesterCommandContext context) throws CommandException {
		EntityType ent = null;
		final int amt = context.getInt(0);
		if(amt < 1) {
			throw new CommandException(context.getSenderLang().get("ERROR_CMD_AMOUNT_POSITIVE"));
		}
		if(context.length() > 1) {
			ent = SerUtils.parseEntity(context.getString(1));
			if(ent == null) {
				throw new CommandException(context.getSenderLang().get("ERROR_CMD_ENTITY_UNKNOWN"));
			}
		}
		return new TameObjective(amt, ent);
	}
	
	@Override
	protected void save(final StorageKey key) {
		if(amount > 1) {
			key.setInt("amount", amount);
		}
		if(entity != null) {
			key.setString("entity", "" + entity.getTypeId());
		}
	}
	
	protected static Objective load(final StorageKey key) {
		int amt = 1;
		EntityType ent = null;
		try {
			ent = SerUtils.parseEntity(key.getString("entity"));
		}
		catch(final Exception ignored) {}
		amt = key.getInt("amount", 1);
		if(amt < 1) {
			amt = 1;
		}
		return new TameObjective(amt, ent);
	}
	
	// Custom methods
	
	public boolean check(final EntityType ent) {
		if(entity == null) {
			return true;
		}
		else {
			return entity.getTypeId() == ent.getTypeId();
		}
	}
}
