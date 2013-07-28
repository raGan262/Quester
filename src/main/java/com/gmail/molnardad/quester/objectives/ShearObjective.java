package com.gmail.molnardad.quester.objectives;

import static com.gmail.molnardad.quester.utils.Util.parseColor;

import org.bukkit.DyeColor;

import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.utils.Util;

@QElement("SHEAR")
public final class ShearObjective extends Objective {
	
	private final DyeColor color;
	private final int amount;
	
	public ShearObjective(final int amt, final DyeColor col) {
		amount = amt;
		color = col;
	}
	
	@Override
	public int getTargetAmount() {
		return amount;
	}
	
	@Override
	protected String show(final int progress) {
		final String strCol = color == null ? "any" : color.name().replace('_', ' ').toLowerCase();
		return "Shear " + strCol + " sheep - " + (amount - progress) + "x";
	}
	
	@Override
	protected String info() {
		final String strCol = color == null ? "ANY" : color.name();
		return strCol + "; AMT: " + amount;
	}
	
	@QCommand(min = 1, max = 2, usage = "<amount> {[color]}")
	public static Objective fromCommand(final QCommandContext context) throws QCommandException {
		DyeColor col = null;
		final int amt = context.getInt(0);
		if(amt < 1) {
			throw new QCommandException(context.getSenderLang().ERROR_CMD_AMOUNT_POSITIVE);
		}
		if(context.length() > 1) {
			col = parseColor(context.getString(1));
			if(col == null) {
				throw new QCommandException(context.getSenderLang().ERROR_CMD_COLOR_UNKNOWN);
			}
		}
		return new ShearObjective(amt, col);
	}
	
	@Override
	protected void save(final StorageKey key) {
		if(color != null) {
			key.setString("color", Util.serializeColor(color));
		}
		if(amount > 1) {
			key.setInt("amount", amount);
		}
	}
	
	protected static Objective load(final StorageKey key) {
		int amt = 1;
		DyeColor col = null;
		col = Util.parseColor(key.getString("color", ""));
		amt = key.getInt("amount");
		if(amt < 1) {
			amt = 1;
		}
		return new ShearObjective(amt, col);
	}
	
	// Custom methods
	
	public boolean check(final DyeColor col) {
		if(col == color || color == null) {
			return true;
		}
		return false;
	}
}
