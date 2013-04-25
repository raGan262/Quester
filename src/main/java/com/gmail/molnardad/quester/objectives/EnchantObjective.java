package com.gmail.molnardad.quester.objectives;

import static com.gmail.molnardad.quester.utils.Util.parseEnchants;
import static com.gmail.molnardad.quester.utils.Util.parseItem;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.utils.Util;

@QElement("ENCHANT")
public final class EnchantObjective extends Objective {

	private final Material material;
	private final int amount;
	private final Map<Integer, Integer> enchants;
	
	public EnchantObjective(Material mat, int amt, Map<Integer, Integer> enchs) {
		material = mat;
		amount = amt;
		if(enchs != null) {
			this.enchants = enchs;
		}
		else {
			this.enchants = new HashMap<Integer, Integer>();
		}
	}

	@Override
	public int getTargetAmount() {
		return amount;
	}
	
	@Override
	protected String show(int progress) {
		String pcs = amount == 1 ? " piece of " : " pieces of ";
		String enchs = "\n -- Required enchants:";
		if(enchants.isEmpty()) {
			enchs = "";
		}
		for(Integer i : enchants.keySet()) {
			enchs = enchs + " " + Util.enchantName(i, enchants.get(i)) + ";";
		}
		return "Enchant " + (amount - progress) + pcs + (material==null?"any item":material.name()) + "." + enchs;
	}
	
	@Override
	protected String info() {
		String mat = material==null?"ANY ITEM":material.name()+"["+material.getId()+"]";
		String itm = mat + "; AMT: "+amount;
		String enchs = enchants.isEmpty() ? "" : "\n -- ENCH:";
		for(Integer e : enchants.keySet()) {
			enchs = enchs + " " + Enchantment.getById(e).getName() + ":" + enchants.get(e);
		}
		return itm + enchs ;
	}
	
	@QCommand(
			min = 1,
			max = 3,
			usage = "{<item>} [amount] {[enchants]}")
	public static Objective fromCommand(QCommandContext context) throws QCommandException {
		Map<Integer, Integer> enchs = null;
		int amt = 1;
		int[] itm = parseItem(context.getString(0));
		Material mat = Material.getMaterial(itm[0]);
		if(context.length() > 1) {
			amt = context.getInt(1);
			if(amt < 1) {
				throw new QCommandException(context.getSenderLang().ERROR_CMD_ENCH_LEVEL);
			}
			if(context.length() > 2) {
				enchs = parseEnchants(context.getString(2));
			}
		}
		return new EnchantObjective(mat, amt, enchs);
	}

	@Override
	protected void save(StorageKey key) {
		key.setString("enchants", Util.serializeEnchants(enchants));
		if(material != null) {
			key.setString("item", Util.serializeItem(material, -1));
		}
		if(amount > 1) {
			key.setInt("amount", amount);
		}
	}
	
	protected static Objective load(StorageKey key) {
		Material mat = null;
		int amt = 1;
		Map<Integer, Integer> enchs = null;
		try {
			try {
				mat = Material.getMaterial(Util.parseItem(key.getString("item"))[0]);
			} catch (Exception ignore) {}
			amt = key.getInt("amount", 1);
			if(amt < 1) {
				amt = 1;
			}
			enchs = Util.parseEnchants(key.getString("enchants", ""));
		} catch (Exception e) {
			return null;
		}
		
		return new EnchantObjective(mat, amt, enchs);
	}
	
	// Custom methods
	
	public boolean check(ItemStack item, Map<Enchantment, Integer> enchs) {
		if(item.getTypeId() != material.getId())
			return false;
		for(int i : enchants.keySet()) {
			if(enchs.get(Enchantment.getById(i)) == null) {
				return false;
			} else if(enchs.get(Enchantment.getById(i)) < enchants.get(i)) {
				return false;
			}
		}
		return true;
	}
}
