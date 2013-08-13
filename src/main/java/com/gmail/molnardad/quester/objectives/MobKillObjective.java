package com.gmail.molnardad.quester.objectives;

import org.bukkit.entity.EntityType;

import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.utils.SerUtils;

@QElement("MOBKILL")
public final class MobKillObjective extends Objective {
	
	private final EntityType entity;
	private final int amount;
	
	public MobKillObjective(final int amt, final EntityType ent) {
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
		return "Kill " + mob + " - " + (amount - progress) + "x";
	}
	
	@Override
	protected String info() {
		final String entStr = entity == null ? "ANY" : entity.getName();
		return entStr + "; AMT: " + amount;
	}
	
	@QCommand(min = 1, max = 2, usage = "<amount> {[entity]}")
	public static Objective fromCommand(final QCommandContext context) throws QCommandException {
		EntityType ent = null;
		final int amt = context.getInt(0);
		if(amt < 1) {
			throw new QCommandException(context.getSenderLang().ERROR_CMD_AMOUNT_POSITIVE);
		}
		if(context.length() > 1) {
			ent = SerUtils.parseEntity(context.getString(1));
		}
		return new MobKillObjective(amt, ent);
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
		catch (final Exception ignore) {}
		amt = key.getInt("amount", 1);
		if(amt < 1) {
			amt = 1;
		}
		return new MobKillObjective(amt, ent);
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
