package me.ragan262.quester.conditions;

import me.ragan262.quester.commandbase.QCommand;
import me.ragan262.quester.commandbase.QCommandContext;
import me.ragan262.quester.commandbase.exceptions.QCommandException;
import me.ragan262.quester.elements.Condition;
import me.ragan262.quester.elements.QElement;
import me.ragan262.quester.storage.StorageKey;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@QElement("SLOT")
public final class SlotCondition extends Condition {
	
	private final int amount;
	
	private SlotCondition(final int amt) {
		amount = amt;
	}
	
	@Override
	public boolean isMet(final Player player) {
		int amt = 0;
		final ItemStack[] contents = player.getInventory().getContents();
		
		for(final ItemStack i : contents) {
			if(i == null) {
				amt++;
			}
		}
		
		return amt >= amount;
	}
	
	@Override
	protected String parseDescription(final Player player, final String description) {
		return description.replaceAll("%amt", String.valueOf(amount));
	}
	
	@Override
	protected String show() {
		final String slot = amount == 1 ? "slot" : "slots";
		return "Must have at least " + amount + " inventory " + slot + " empty.";
	}
	
	@Override
	protected String info() {
		return String.valueOf(amount);
	}
	
	@QCommand(min = 1, max = 1, usage = "<amount>")
	public static Condition fromCommand(final QCommandContext context) throws QCommandException {
		final int amt = context.getInt(0);
		if(amt < 1) {
			throw new QCommandException(context.getSenderLang().get("ERROR_CMD_AMOUNT_POSITIVE"));
		}
		return new SlotCondition(amt);
	}
	
	@Override
	protected void save(final StorageKey key) {
		key.setInt("amount", amount);
	}
	
	protected static Condition load(final StorageKey key) {
		final int amt = key.getInt("amount", 0);
		if(amt < 1) {
			return null;
		}
		return new SlotCondition(amt);
	}
}
