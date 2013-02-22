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
	
	public PointCondition(int amount) {
		this.amount = amount;
	}

	@Override
	protected String parseDescription(String description) {
		return description.replaceAll("%amt", amount+"");
	}
	
	@Override
	public boolean isMet(Player player, Quester plugin) {
		return plugin.getProfileManager().getProfile(player.getName()).getPoints() >= amount;
	}
	
	@Override
	public String show() {
		return "Must have " + amount + " quest points.";
	}
	
	@Override
	public String info() {
		return String.valueOf(amount);
	}
	
	@QCommand(
			min = 1,
			max = 1,
			usage = "<amount>")
	public static Condition fromCommand(QCommandContext context) throws QCommandException {
		try {
			int amt = context.getInt(0);
			return new PointCondition(amt);
			}
		catch (NumberFormatException e) {
			throw new QCommandException(context.getSenderLang().ERROR_CMD_AMOUNT_GENERAL);
		}
	}
	
	protected void save(StorageKey key) {
		key.setInt("amount", amount);
	}

	protected static PointCondition load(StorageKey key) {
		int amt;
		
		try {
			amt = Integer.parseInt(key.getString("amount"));
		}
		catch (Exception e) {
			return null;
		}
		
		return new PointCondition(amt);
	}
}
