package com.gmail.molnardad.quester.conditions;

import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.Condition;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.storage.StorageKey;

@QElement("POINT")
public final class PointCondition extends Condition {
	
	private final int amount;
	private final boolean inverted;
	
	public PointCondition(final int amount, final boolean invert) {
		this.amount = amount;
		inverted = invert;
	}
	
	@Override
	protected String parseDescription(final Player player, final String description) {
		return description.replaceAll("%amt", amount + "");
	}
	
	@Override
	public boolean isMet(final Player player, final Quester plugin) {
		return plugin.getProfileManager().getProfile(player.getName()).getPoints() >= amount != inverted;
	}
	
	@Override
	public String show() {
		final String flag = inverted ? "less than " : "at least ";
		return "Must have " + flag + amount + " quest points.";
	}
	
	@Override
	public String info() {
		final String flag = inverted ? " (-i)" : "";
		return String.valueOf(amount) + flag;
	}
	
	@QCommand(min = 1, max = 1, usage = "<amount> (-i)")
	public static Condition fromCommand(final QCommandContext context) throws QCommandException {
		try {
			final int amt = context.getInt(0);
			return new PointCondition(amt, context.hasFlag('i'));
		}
		catch (final NumberFormatException e) {
			throw new QCommandException(context.getSenderLang().get("ERROR_CMD_AMOUNT_GENERAL"));
		}
	}
	
	@Override
	protected void save(final StorageKey key) {
		key.setInt("amount", amount);
		if(inverted) {
			key.setBoolean("inverted", inverted);
		}
	}
	
	protected static Condition load(final StorageKey key) {
		int amt;
		
		try {
			amt = Integer.parseInt(key.getString("amount"));
		}
		catch (final Exception e) {
			return null;
		}
		
		return new PointCondition(amt, key.getBoolean("inverted", false));
	}
}
