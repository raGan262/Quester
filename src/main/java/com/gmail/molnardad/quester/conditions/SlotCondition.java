package com.gmail.molnardad.quester.conditions;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.Condition;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.storage.StorageKey;

@QElement("SLOT")
public final class SlotCondition extends Condition {

	private final int amount;
	
	private SlotCondition(int amt) {
		this.amount = amt;
	}

	@Override
	public boolean isMet(Player player, Quester plugin) {
        int amt = 0;
        ItemStack[] contents = player.getInventory().getContents();
       
        for (ItemStack i : contents) {
        	if(i == null) {
        		amt++;
        	}
        }
       
        return (amt >= amount);
	}
	


	@Override
	protected String parseDescription(String description) {
		return description.replaceAll("%amt", String.valueOf(amount));
	}
	
	@Override
	protected String show() {
		String slot = amount == 1 ? "slot" : "slots";
		return "Must have at least " + amount + " inventory " + slot +" empty.";
	}

	@Override
	protected String info() {
		return String.valueOf(amount);
	}
	
	@QCommand(
			min = 1,
			max = 1,
			usage = "<amount>")
	public static Condition fromCommand(QCommandContext context) throws QCommandException {
		int amt = context.getInt(0);
		if(amt < 1) {
			throw new QCommandException(context.getSenderLang().ERROR_CMD_AMOUNT_POSITIVE);
		}
		return new SlotCondition(amt);
	}
	
	@Override
	protected void save(StorageKey key) {
		key.setInt("amount", amount);
	}

	protected static Condition load(StorageKey key) {
		int amt = key.getInt("amount", 0);
		if(amt < 1) {
			return null;
		}
		return new SlotCondition(amt);
	}
}
