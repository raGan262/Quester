package me.ragan262.quester.conditions;

import me.ragan262.commandmanager.annotations.Command;
import me.ragan262.commandmanager.exceptions.CommandException;
import me.ragan262.quester.commandmanager.QuesterCommandContext;
import me.ragan262.quester.elements.Condition;
import me.ragan262.quester.elements.QElement;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.ExpManager;
import org.bukkit.entity.Player;

@QElement("EXP")
public final class ExperienceCondition extends Condition {
	
	private final int amount;
	private final boolean inverted;
	private final boolean isLevel;
	
	public ExperienceCondition(final int amount, final boolean isLevel, final boolean invert) {
		this.amount = amount;
		inverted = invert;
		this.isLevel = isLevel;
	}
	
	@Override
	protected String parseDescription(final Player player, final String description) {
		return description.replaceAll("%amt", amount + "");
	}
	
	@Override
	public boolean isMet(final Player player) {
		final ExpManager expMan = new ExpManager(player);
		int value = expMan.getCurrentExp();
		if(isLevel) {
			value = expMan.getLevelForExp(value);
		}
		return value < amount == inverted;
	}
	
	@Override
	public String show() {
		final String flag = inverted ? "Must have less than " : "Must have at least ";
		final String lvlpt = isLevel ? " experience levels." : " experience points.";
		return flag + amount + lvlpt;
	}
	
	@Override
	public String info() {
		final StringBuilder flag = new StringBuilder();
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
	
	@Command(min = 1, max = 1, usage = "<amount> (-li)")
	public static Condition fromCommand(final QuesterCommandContext context) throws CommandException {
		final int amt = context.getInt(0);
		if(amt < 1) {
			throw new CommandException(context.getSenderLang().get("ERROR_CMD_AMOUNT_POSITIVE"));
		}
		return new ExperienceCondition(amt, context.hasFlag('l'), context.hasFlag('i'));
	}
	
	@Override
	protected void save(final StorageKey key) {
		key.setInt("amount", amount);
		if(inverted) {
			key.setBoolean("inverted", true);
		}
		if(isLevel) {
			key.setBoolean("islevel", true);
		}
	}
	
	protected static Condition load(final StorageKey key) {
		int amt;
		
		try {
			amt = Integer.parseInt(key.getString("amount"));
		}
		catch(final Exception e) {
			return null;
		}
		
		return new ExperienceCondition(amt, key.getBoolean("islevel", false), key.getBoolean("inverted", false));
	}
}
