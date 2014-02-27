package me.ragan262.quester.conditions;

import me.ragan262.quester.Quester;
import me.ragan262.quester.commandbase.QCommand;
import me.ragan262.quester.commandbase.QCommandContext;
import me.ragan262.quester.commandbase.exceptions.QCommandException;
import me.ragan262.quester.elements.Condition;
import me.ragan262.quester.elements.QElement;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.SerUtils;
import me.ragan262.quester.utils.Util;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@QElement("ITEM")
public final class ItemCondition extends Condition {
	
	private final Material material;
	private final short data;
	private final int amount;
	private final boolean inverted;
	private final boolean questItem;
	
	private ItemCondition(final Material mat, final int amt, final int dat, final boolean invert, final boolean quest) {
		material = mat;
		amount = amt;
		data = (short) dat;
		inverted = invert;
		questItem = quest;
	}
	
	@Override
	public boolean isMet(final Player player, final Quester plugin) {
		int amt = 0;
		final ItemStack[] contents = player.getInventory().getContents();
		
		for(final ItemStack i : contents) {
			if(i == null) {
				continue;
			}
			if(Util.isQuestItem(i) != questItem) {
				continue;
			}
			if(i.getTypeId() == material.getId()) {
				if(i.getDurability() == data || data < 0) {
					amt += i.getAmount();
				}
				if(amt >= amount) {
					break;
				}
			}
		}
		
		return amt >= amount != inverted;
	}
	
	@Override
	protected String parseDescription(final Player player, final String description) {
		return description.replaceAll("%amt", amount + "").replaceAll("%data", data + "")
				.replaceAll("%id", material.getId() + "");
	}
	
	@Override
	protected String show() {
		final String status = inverted ? "Must not have " : "Must have ";
		final String datStr = data < 0 ? " (any)" : " (data " + data + ")";
		final String spec = questItem ? " special" : "";
		final String pcs = amount == 1 ? " piece of " : " pieces of ";
		final String mat = material.getId() == 351 ? "dye" : material.name().toLowerCase();
		return status + amount + spec + pcs + mat + datStr + ".";
	}
	
	@Override
	protected String info() {
		String flags = "";
		if(questItem || inverted) {
			flags += "; (-" + (questItem ? "q" : "") + (inverted ? "i" : "") + ")";
		}
		final String dataStr = data < 0 ? "" : ":" + data;
		return material.name() + "[" + material.getId() + dataStr + "]; AMT: " + amount + flags;
	}
	
	@QCommand(min = 1, max = 2, usage = "{<item>} <amount> (-qi)")
	public static Condition fromCommand(final QCommandContext context) throws QCommandException {
		final int[] itm = SerUtils.parseItem(context.getString(0));
		final Material mat = Material.getMaterial(itm[0]);
		final int dat = itm[1];
		int amt;
		try {
			amt = context.getInt(1);
			if(amt < 1 || dat < -1) {
				throw new NumberFormatException();
			}
		}
		catch (final NumberFormatException e) {
			throw new QCommandException(context.getSenderLang().get("ERROR_CMD_ITEM_NUMBERS"));
		}
		catch (final IllegalArgumentException e) {
			throw new QCommandException(e.getMessage());
		}
		return new ItemCondition(mat, amt, dat, context.hasFlag('i'), context.hasFlag('q'));
	}
	
	@Override
	protected void save(final StorageKey key) {
		key.setString("item", SerUtils.serializeItem(material.getId(), data));
		key.setInt("amount", amount);
		if(inverted) {
			key.setBoolean("inverted", inverted);
		}
		if(questItem) {
			key.setBoolean("questitem", questItem);
		}
	}
	
	protected static Condition load(final StorageKey key) {
		int amt = 1, dat;
		Material mat;
		final boolean invert = key.getBoolean("inverted", false);
		final boolean quest = key.getBoolean("questitem", false);
		try {
			final int[] itm = SerUtils.parseItem(key.getString("item"));
			mat = Material.getMaterial(itm[0]);
			dat = itm[1];
			amt = key.getInt("amount", 1);
			if(amt < 1) {
				amt = 1;
			}
		}
		catch (final Exception e) {
			return null;
		}
		return new ItemCondition(mat, amt, dat, invert, quest);
	}
}
