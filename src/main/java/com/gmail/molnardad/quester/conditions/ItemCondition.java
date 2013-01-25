package com.gmail.molnardad.quester.conditions;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.Condition;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.utils.Util;

@QElement("ITEM")
public final class ItemCondition extends Condition {

	private final Material material;
	private final short data;
	private final int amount;
	
	private ItemCondition(Material mat, int amt, int dat) {
		this.material = mat;
		this.amount = amt;
		this.data = (short) dat;
	}

	@Override
	public boolean isMet(Player player) {
        int amt = 0;
        ItemStack[] contents = player.getInventory().getContents();
       
        for (ItemStack i : contents) {
        	if(i == null)
        		continue;
            if (i.getTypeId() == material.getId()) {
            	if(i.getDurability() == data || data < 0) {
                    amt += i.getAmount();
            	}
            	if(amt >= amount)
            		break;
            }
        }
       
        return (amt >= amount);
	}
	


	@Override
	protected String parseDescription(String description) {
		return description.replaceAll("%amt", amount+"").replaceAll("%data", data+"").replaceAll("%id", material.getId()+"");
	}
	
	@Override
	protected String show() {
		String datStr = data < 0 ? " (any) " : " (data " + data + ") ";
		String pcs = amount == 1 ? " piece of " : " pieces of ";
		String mat = material.getId() == 351 ? "dye" : material.name().toLowerCase();
		return "Must have " + amount + pcs + mat + datStr + ".";
	}

	@Override
	protected String info() {
		String dataStr = (data < 0 ? "ANY" : String.valueOf(data));
		return material.name() + "[" + material.getId() + "]; DMG: " + dataStr + "; AMT: " + amount;
	}
	
	@QCommand(
			min = 1,
			max = 2,
			desc = "requires player to have certain item",
			usage = "{<item>} <amount>")
	public static Condition fromCommand(QCommandContext context, CommandSender sender) throws QCommandException {
		int[] itm = Util.parseItem(context.getString(0));
		Material mat = Material.getMaterial(itm[0]);
		int dat = itm[1];
		int amt;
		try {
			amt = context.getInt(1);
			if(amt < 1 || dat < -1) {
				throw new NumberFormatException();
			}
		}
		catch (NumberFormatException e) {
			throw new QCommandException(context.getSenderLang().ERROR_CMD_ITEM_NUMBERS);
		}
		catch (IllegalArgumentException e) {
			throw new QCommandException(e.getMessage());
		}
		return new ItemCondition(mat, amt, dat);
	}
	
	// TODO serialization
	
	public void serialize(ConfigurationSection section) {
		section.set("item", Util.serializeItem(material.getId(), data));
		section.set("amount", amount);
	}

	public static ItemCondition deser(ConfigurationSection section) {
		int amt = 1, dat;
		Material mat;
		try {
			int[] itm = Util.parseItem(section.getString("item"));
			mat = Material.getMaterial(itm[0]);
			dat = itm[1];
			if(section.isInt("amount"))
				amt = section.getInt("amount");
			if(amt < 1)
				amt = 1;
		} catch (Exception e) {
			return null;
		}
		return new ItemCondition(mat, amt, dat);
	}
}
