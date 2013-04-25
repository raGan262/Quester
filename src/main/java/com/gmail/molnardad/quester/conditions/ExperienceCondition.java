package com.gmail.molnardad.quester.conditions;

import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.Condition;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.utils.ExpManager;

@QElement("EXP")
public final class ExperienceCondition extends Condition {

	/**
	 * @uml.property  name="amount"
	 */
	private final int amount;
	/**
	 * @uml.property  name="inverted"
	 */
	private final boolean inverted;
	/**
	 * @uml.property  name="isLevel"
	 */
	private final boolean isLevel;
	
	public ExperienceCondition(int amount, boolean isLevel, boolean invert) {
		this.amount = amount;
		this.inverted = invert;
		this.isLevel = isLevel;
	}

	@Override
	protected String parseDescription(String description) {
		return description.replaceAll("%amt", amount+"");
	}
	
	@Override
	public boolean isMet(Player player, Quester plugin) {
		ExpManager expMan = new ExpManager(player);
		int value = expMan.getCurrentExp();
		if(isLevel) {
			value = expMan.getLevelForExp(value);
		}
		return (value >= amount) != inverted;
	}
	
	@Override
	public String show() {
		String flag = inverted ? "Must have at least ": "Must have less than ";
		String lvlpt = isLevel ? " experience levels." : " experience points.";
		return flag + amount + lvlpt;
	}
	
	@Override
	public String info() {
		StringBuilder flag = new StringBuilder();
		if(inverted || isLevel) {
			flag.append(" (-");
			if(isLevel) {
				flag.append('l');
			}
			if(inverted) {
				flag.append('i');
			}
			flag.append(')');
		}
		return String.valueOf(amount) + flag.toString();
	}
	
	@QCommand(
			min = 1,
			max = 1,
			usage = "<amount> (-li)")
	public static Condition fromCommand(QCommandContext context) throws QCommandException {
			int amt = context.getInt(0);
			if(amt < 1) {
				throw new QCommandException(context.getSenderLang().ERROR_CMD_AMOUNT_POSITIVE);
			}
			return new ExperienceCondition(amt,context.hasFlag('l'), context.hasFlag('i'));
	}
	
	protected void save(StorageKey key) {
		key.setInt("amount", amount);
		if(inverted) {
			key.setBoolean("inverted", inverted);
		}
		if(isLevel) {
			key.setBoolean("islevel", isLevel);
		}
	}

	protected static Condition load(StorageKey key) {
		int amt;
		
		try {
			amt = Integer.parseInt(key.getString("amount"));
		}
		catch (Exception e) {
			return null;
		}
		
		return new ExperienceCondition(amt, key.getBoolean("islevel", false), key.getBoolean("inverted", false));
	}
}
