package me.ragan262.quester.objectives;

import java.util.HashMap;
import java.util.Map;
import me.ragan262.commandmanager.annotations.Command;
import me.ragan262.commandmanager.exceptions.CommandException;
import me.ragan262.quester.commandmanager.QuesterCommandContext;
import me.ragan262.quester.elements.Objective;
import me.ragan262.quester.elements.QElement;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.SerUtils;
import me.ragan262.quester.utils.Util;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

@QElement("ENCHANT")
public final class EnchantObjective extends Objective {
	
	private final Material material;
	private final int amount;
	private final Map<Integer, Integer> enchants;
	
	public EnchantObjective(final Material mat, final int amt, final Map<Integer, Integer> enchs) {
		material = mat;
		amount = amt;
		if(enchs != null) {
			enchants = enchs;
		}
		else {
			enchants = new HashMap<Integer, Integer>();
		}
	}
	
	@Override
	public int getTargetAmount() {
		return amount;
	}
	
	@Override
	protected String show(final int progress) {
		final String pcs = amount == 1 ? " piece of " : " pieces of ";
		String enchs = "\n -- Required enchants:";
		if(enchants.isEmpty()) {
			enchs = "";
		}
		for(final Integer i : enchants.keySet()) {
			enchs = enchs + " " + Util.enchantName(i, enchants.get(i)) + ";";
		}
		return "Enchant " + (amount - progress) + pcs
				+ (material == null ? "any item" : material.name()) + "." + enchs;
	}
	
	@Override
	protected String info() {
		final String mat = material == null ? "ANY ITEM" : material.name() + "[" + material.getId()
				+ "]";
		final String itm = mat + "; AMT: " + amount;
		String enchs = enchants.isEmpty() ? "" : "\n -- ENCH:";
		for(final Integer e : enchants.keySet()) {
			enchs = enchs + " " + Enchantment.getById(e).getName() + ":" + enchants.get(e);
		}
		return itm + enchs;
	}
	
	@Command(min = 1, max = 3, usage = "{<item>} [amount] {[enchants]}")
	public static Objective fromCommand(final QuesterCommandContext context) throws CommandException {
		Map<Integer, Integer> enchs = null;
		int amt = 1;
		final int[] itm = SerUtils.parseItem(context.getString(0));
		final Material mat = Material.getMaterial(itm[0]);
		if(context.length() > 1) {
			amt = context.getInt(1);
			if(amt < 1) {
				throw new CommandException(context.getSenderLang().get("ERROR_CMD_ENCH_LEVEL"));
			}
			if(context.length() > 2) {
				enchs = SerUtils.parseEnchants(context.getString(2));
			}
		}
		return new EnchantObjective(mat, amt, enchs);
	}
	
	@Override
	protected void save(final StorageKey key) {
		key.setString("enchants", SerUtils.serializeEnchants(enchants));
		if(material != null) {
			key.setString("item", SerUtils.serializeItem(material, -1));
		}
		if(amount > 1) {
			key.setInt("amount", amount);
		}
	}
	
	protected static Objective load(final StorageKey key) {
		Material mat = null;
		int amt = 1;
		Map<Integer, Integer> enchs = null;
		try {
			try {
				mat = Material.getMaterial(SerUtils.parseItem(key.getString("item"))[0]);
			}
			catch(final Exception ignore) {}
			amt = key.getInt("amount", 1);
			if(amt < 1) {
				amt = 1;
			}
			enchs = SerUtils.parseEnchants(key.getString("enchants", ""));
		}
		catch(final Exception e) {
			return null;
		}
		
		return new EnchantObjective(mat, amt, enchs);
	}
	
	// Custom methods
	
	public boolean check(final ItemStack item, final Map<Enchantment, Integer> enchs) {
		if(item.getTypeId() != material.getId()) {
			return false;
		}
		for(final int i : enchants.keySet()) {
			if(enchs.get(Enchantment.getById(i)) == null) {
				return false;
			}
			else if(enchs.get(Enchantment.getById(i)) < enchants.get(i)) {
				return false;
			}
		}
		return true;
	}
}
