package me.ragan262.quester.objectives;

import me.ragan262.commandmanager.annotations.Command;
import me.ragan262.commandmanager.exceptions.CommandException;
import me.ragan262.quester.commandmanager.QuesterCommandContext;
import me.ragan262.quester.elements.Objective;
import me.ragan262.quester.elements.QElement;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.SerUtils;
import org.bukkit.DyeColor;

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
	
	@Command(min = 1, max = 2, usage = "<amount> {[color]}")
	public static Objective fromCommand(final QuesterCommandContext context) throws CommandException {
		DyeColor col = null;
		final int amt = context.getInt(0);
		if(amt < 1) {
			throw new CommandException(context.getSenderLang().get("ERROR_CMD_AMOUNT_POSITIVE"));
		}
		if(context.length() > 1) {
			col = SerUtils.parseColor(context.getString(1));
			if(col == null) {
				throw new CommandException(context.getSenderLang().get("ERROR_CMD_COLOR_UNKNOWN"));
			}
		}
		return new ShearObjective(amt, col);
	}
	
	@Override
	protected void save(final StorageKey key) {
		if(color != null) {
			key.setString("color", SerUtils.serializeColor(color));
		}
		if(amount > 1) {
			key.setInt("amount", amount);
		}
	}
	
	protected static Objective load(final StorageKey key) {
		int amt = 1;
		DyeColor col = null;
		col = SerUtils.parseColor(key.getString("color", ""));
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
