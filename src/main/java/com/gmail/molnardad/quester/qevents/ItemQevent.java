package com.gmail.molnardad.quester.qevents;

import static com.gmail.molnardad.quester.utils.Util.parseEnchants;
import static com.gmail.molnardad.quester.utils.Util.parseItem;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.elements.Qevent;
import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.utils.Util;

@QElement("ITEM")
public final class ItemQevent extends Qevent {

	public final Material material;
	public final short data;
	private final int amount;
	private final Map<Integer, Integer> enchants;
	
	public ItemQevent(Material mat, int dat, int amt, Map<Integer, Integer> enchs) {
		this.material = mat;
		this.data = (short) dat;
		this.amount = amt;
		if(enchs != null)
			this.enchants = enchs;
		else
			this.enchants = new HashMap<Integer, Integer>();
	}
	
	@Override
	public String info() {
		String itm = material.name()+"["+material.getId()+":"+data+"]; AMT: "+amount;
		String enchs = enchants.isEmpty() ? "" : "\n -- ENCH:";
		for(Integer e : enchants.keySet()) {
			enchs = enchs + " " + Enchantment.getById(e).getName() + ":" + enchants.get(e);
		}
		return itm + enchs;
	}

	@Override
	protected void run(Player player, Quester plugin) {
		int maxSize = material.getMaxStackSize();
        int toGive = amount;
        int numSpaces = 0;
        int given = 0;
        ItemStack[] contents = player.getInventory().getContents();
        for (ItemStack i : contents) {
            if (i == null) {
                numSpaces += maxSize;
            } 
            else if (i.getType().equals(material) && enchants.isEmpty() && i.getDurability() == data) {
                   numSpaces += (maxSize - i.getAmount());
            }
        }
        given = Math.min(toGive, numSpaces);
        toGive -= given;
        numSpaces = (int) Math.ceil((double)given / (double)maxSize);
        int round;
        ItemStack item;
        PlayerInventory inv = player.getInventory();
        for(int k=0; k<numSpaces; k++) {
        	round = Math.min(maxSize, given);
	        item = new ItemStack(material, round, data);
	        for(Integer j : enchants.keySet()) {
				item.addUnsafeEnchantment(Enchantment.getById(j), enchants.get(j));
			}
	        inv.addItem(item);
	        given -= round;
        }

        if(toGive > 0) {
            numSpaces = (int) Math.ceil((double)toGive / (double)maxSize);
        	for(int k=0; k<numSpaces; k++) {
        		given = Math.min(toGive, maxSize);
	        	item = new ItemStack(material, given, data);
	        	for(Integer j : enchants.keySet()) {
        			item.addEnchantment(Enchantment.getById(j), enchants.get(j));
        		}
	        	player.getWorld().dropItem(player.getLocation(), item);
	        	toGive -= given;
        	}
        }
	}

	@QCommand(
			min = 1,
			max = 3,
			usage = "{<item>} [amount] {[enchants]}")
	public static Qevent fromCommand(QCommandContext context) throws QCommandException {
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
		
		return new ItemQevent(mat, dat, amt, enchs);
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
	}
	
	protected static Qevent load(StorageKey key) {
		Material mat = null;
		int dat = 0, amt = 1;
		Map<Integer, Integer> enchs = null;
		try {
			int[] itm = Util.parseItem(key.getString("item"));
			mat = Material.getMaterial(itm[0]);
			dat = itm[1];
			if(dat < 0) {
				dat = 0;
			}
			amt = key.getInt("amount", 1);
			if(amt < 1) {
				amt = 1;
			}
			try {
				enchs = Util.parseEnchants(key.getString("enchants"));
			} catch (IllegalArgumentException e) {
				enchs = null;
			}
		} catch (Exception e) {
			return null;
		}
		
		return new ItemQevent(mat, dat, amt, enchs);
	}
}
