package com.gmail.molnardad.quester.objectives;

import org.bukkit.entity.EntityType;

import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.utils.Util;

@QElement("TAME")
public final class TameObjective extends Objective {

	/**
	 * @uml.property  name="entity"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private final EntityType entity;
	/**
	 * @uml.property  name="amount"
	 */
	private final int amount;

	public TameObjective(int amt, EntityType ent) {
		entity = ent;
		amount = amt;
	}

	@Override
	public int getTargetAmount() {
		return amount;
	}

	@Override
	protected String show(int progress) {
		String mob = entity == null ? "any mob" : entity.getName();
		return "Tame " + mob + " - " + (amount - progress) + "x";
	}
	
	@Override
	protected String info() {
		String entStr = entity == null ? "ANY" : entity.getName();
		return entStr + "; AMT: " + amount;
	}
	
	@QCommand(
			min = 1,
			max = 2,
			usage = "<amount> {[entity]}")
	public static Objective fromCommand(QCommandContext context) throws QCommandException {
		EntityType ent = null;
		int amt = context.getInt(0);
		if(amt < 1) {
			throw new QCommandException(context.getSenderLang().ERROR_CMD_AMOUNT_POSITIVE);
		}
		if(context.length() > 1) {
			ent = Util.parseEntity(context.getString(1));
			if(ent == null) {
				throw new QCommandException(context.getSenderLang().ERROR_CMD_ENTITY_UNKNOWN);
			}
		}
		return new TameObjective(amt, ent);
	}

	@Override
	protected void save(StorageKey key) {
		if(amount > 1) {
			key.setInt("amount", amount);
		}
		if(entity != null) {
			key.setString("entity","" + entity.getTypeId());
		}
	}
	
	protected static Objective load(StorageKey key) {
		int amt = 1;
		EntityType ent = null;
		try {
			ent = Util.parseEntity(key.getString("entity"));
		} catch (Exception e) {}
		amt = key.getInt("amount", 1);
		if(amt < 1) {
			amt = 1;
		}
		return new TameObjective(amt, ent);
	}
	
	//Custom methods
	
	public boolean check(EntityType ent) {
		if(entity == null) {
			return true;
		} else {
			return (entity.getTypeId() == ent.getTypeId());
		}
	}
}
