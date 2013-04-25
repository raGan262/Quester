package com.gmail.molnardad.quester.conditions;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.Condition;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.utils.Util;

@QElement("ITEM")
public final class ItemCondition extends Condition {

	/**
	 * @uml.property  name="material"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private final Material material;
	/**
	 * @uml.property  name="data"
	 */
	private final short data;
	/**
	 * @uml.property  name="amount"
	 */
	private final int amount;
	/**
	 * @uml.property  name="inverted"
	 */
	private final boolean inverted;
	/**
	 * @uml.property  name="questItem"
	 */
	private final boolean questItem;
	
	private ItemCondition(Material mat, int amt, int dat, boolean invert, boolean quest) {
		this.material = mat;
		this.amount = amt;
		this.data = (short) dat;
		this.inverted = invert;
		this.questItem = quest;
	}

	@Override
	public boolean isMet(Player player, Quester plugin) {
        int amt = 0;
        ItemStack[] contents = player.getInventory().getContents();
       
        for (ItemStack i : contents) {
        	if(i == null)
        		continue;
        	if(Util.isQuestItem(i) != questItem) {
        		continue;
        	}
            if (i.getTypeId() == material.getId()) {
            	if(i.getDurability() == data || data < 0) {
                    amt += i.getAmount();
            	}
            	if(amt >= amount)
            		break;
            }
        }
       
        return (amt >= amount) != inverted;
	}
	


	@Override
	protected String parseDescription(String description) {
		return description.replaceAll("%amt", amount+"").replaceAll("%data", data+"").replaceAll("%id", material.getId()+"");
	}
	
	@Override
	protected String show() {
		String status = inverted ? "Must not have " : "Must have ";
		String datStr = data < 0 ? " (any)" : " (data " + data + ")";
		String spec = questItem ? " special" : "";
		String pcs = amount == 1 ? " piece of " : " pieces of ";
		String mat = material.getId() == 351 ? "dye" : material.name().toLowerCase();
		return status + amount + spec + pcs + mat + datStr + ".";
	}

	@Override
	protected String info() {
		String flags = "";
		if(questItem || inverted) {
			flags += "; (-" + (questItem ? "q":"") + (inverted ? "i":"") + ")";
		}
		String dataStr = (data < 0 ? "" : ":" + data);
		return material.name() + "[" + material.getId() + dataStr + "]; AMT: " + amount + flags;
	}
	
	@QCommand(
			min = 1,
			max = 2,
			usage = "{<item>} <amount> (-qi)")
	public static Condition fromCommand(QCommandContext context) throws QCommandException {
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
		return new ItemCondition(mat, amt, dat, context.hasFlag('i'), context.hasFlag('q'));
	}
	
	@Override
	protected void save(StorageKey key) {
		key.setString("item", Util.serializeItem(material.getId(), data));
		key.setInt("amount", amount);
		if(inverted) {
			key.setBoolean("inverted", inverted);
		}
		if(questItem) {
			key.setBoolean("questitem", questItem);
		}
	}

	protected static Condition load(StorageKey key) {
		int amt = 1, dat;
		Material mat;
		boolean invert = key.getBoolean("inverted", false);
		boolean quest = key.getBoolean("questitem", false);
		try {
			int[] itm = Util.parseItem(key.getString("item"));
			mat = Material.getMaterial(itm[0]);
			dat = itm[1];
			amt = key.getInt("amount", 1);
			if(amt < 1) {
				amt = 1;
			}
		} catch (Exception e) {
			return null;
		}
		return new ItemCondition(mat, amt, dat, invert, quest);
	}
}
