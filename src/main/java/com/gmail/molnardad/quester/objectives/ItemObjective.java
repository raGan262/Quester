package com.gmail.molnardad.quester.objectives;

import static com.gmail.molnardad.quester.utils.Util.parseEnchants;
import static com.gmail.molnardad.quester.utils.Util.parseItem;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.utils.Util;

@QElement("ITEM")
public final class ItemObjective extends Objective {

	private final Material material;
	private final short data;
	private final int amount;
	private final Map<Integer, Integer> enchants;
	private final boolean questItem;
	
	public ItemObjective(Material mat, int amt, int dat, Map<Integer, Integer> enchs, boolean questItem) {
		material = mat;
		amount = amt;
		data = (short)dat;
		if(enchs != null)
			this.enchants = enchs;
		else
			this.enchants = new HashMap<Integer, Integer>();
		this.questItem = questItem;
	}
	
	@Override
	public int getTargetAmount() {
		return 1;
	}
	
	@Override
	protected String show(int progress) {
		String datStr = data < 0 ? " (any) " : " (data " + data + ") ";
		String pcs = amount == 1 ? " piece of " : " pieces of ";
		String enchs = "\n -- Enchants:";
		String mat = material.getId() == 351 ? "dye" : material.name().toLowerCase();
		if(enchants.isEmpty()) {
			enchs = "";
		}
		for(Integer i : enchants.keySet()) {
			enchs = enchs + " " + Util.enchantName(i, enchants.get(i)) + ";";
		}
		return "Have " + amount + pcs + mat + datStr + "on completion." + enchs;
	}
	
	@Override
	protected String info() {
		String dataStr = (data < 0 ? "" : ":" + data);
		String itm = material.name() + "["+material.getId() + dataStr + "]; AMT: " + amount + "; QST: " + questItem;
		String enchs = enchants.isEmpty() ? "" : "\n -- ENCH:";
		for(Integer e : enchants.keySet()) {
			enchs = enchs + " " + Enchantment.getById(e).getName() + ":" + enchants.get(e);
		}
		return itm + enchs;
	}

	@Override
	public boolean tryToComplete(Player player) {
		Inventory newInv = Util.createInventory(player);
		if(takeInventory(newInv)) {
			takeInventory(player.getInventory());
			return true;
		}
		return false;
	}
	
	@QCommand(
			min = 1,
			max = 3,
			usage = "{<item>} [amount] {[enchants]} (-q)")
	public static Objective fromCommand(QCommandContext context) {
		Material mat = null;
		int dat;
		int amt = 1;
		Map<Integer, Integer> enchs = null;
		int[] itm = parseItem(context.getString(0), context.getSenderLang());
		mat = Material.getMaterial(itm[0]);
		dat = itm[1];
		if(context.length() > 1) {
			amt = context.getInt(1);
			if(context.length() > 2) {
				enchs = parseEnchants(context.getString(2));
			}
		}
		if(amt < 1 || dat < -1) {
			throw new IllegalArgumentException(context.getSenderLang().ERROR_CMD_ITEM_NUMBERS);
		}
		if(context.length() > 2) {
			enchs = parseEnchants(context.getString(2));
		}
		
		return new ItemObjective(mat, amt, dat, enchs, context.hasFlag('q'));
	}

	@Override
	protected void save(StorageKey key) {
		key.setString("item", Util.serializeItem(material, data));
		if(!enchants.isEmpty()) {
			key.setString("enchants", Util.serializeEnchants(enchants));
		}
		if(amount != 1) {
			key.setInt("amount", amount);
		}
		if(questItem) {
			key.setBoolean("questitem", questItem);
		}
	}
	
	protected static Objective load(StorageKey key) {
		Material mat = null;
		int dat = -1, amt = 1;
		Map<Integer, Integer> enchs = null;
		boolean qi = false;
		try {
			int[] itm = Util.parseItem(key.getString("item", ""));
			mat = Material.getMaterial(itm[0]);
			dat = itm[1];
			
			amt = key.getInt("amount", 1);
			if(amt < 1) {
				amt = 1;
			}
			try {
				enchs = Util.parseEnchants(key.getString("enchants", ""));
			} catch (IllegalArgumentException e) {
				enchs = null;
			}
			qi = key.getBoolean("questitem", false);
		} catch (Exception e) {
			return null;
		}
		
		return new ItemObjective(mat, amt, dat, enchs, qi);
	}
	
	//Custom methods
	
	public boolean takeInventory(Inventory inv) {
		int remain = amount;
		ItemStack[] contents = inv.getContents();
		for (int i = 0; i <contents.length; i++) {
	        if (contents[i] != null) {
	        	if(questItem != Util.isQuestItem(contents[i])) {
	        		continue;
	        	}
	        	boolean enchsOK = true;
	        	for(Integer e : enchants.keySet()) {
	        		if(enchants.get(e) != contents[i].getEnchantmentLevel(Enchantment.getById(e))) {
	        			enchsOK = false;
	        			break;
	        		}
	        	}
	        	if(enchsOK) {
		        	if (contents[i].getTypeId() == material.getId()) {
		        		if(data < 0 || contents[i].getDurability() == data) {
		        			if(remain >= contents[i].getAmount()) {
		        				remain -= contents[i].getAmount();
		        				contents[i] = null;
		        				inv.clear(i);
		        			} else {
		        				contents[i].setAmount(contents[i].getAmount() - remain);
		        				remain = 0;
		        				break;
		        			}
		        		}
		        	}
	        	}
	        }
	    }
		return remain == 0;
	}
}
