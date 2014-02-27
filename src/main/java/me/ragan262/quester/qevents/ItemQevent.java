package me.ragan262.quester.qevents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import me.ragan262.quester.Quester;
import me.ragan262.quester.commandbase.QCommand;
import me.ragan262.quester.commandbase.QCommandContext;
import me.ragan262.quester.commandbase.exceptions.QCommandException;
import me.ragan262.quester.elements.QElement;
import me.ragan262.quester.elements.Qevent;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.SerUtils;
import me.ragan262.quester.utils.Util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

@QElement("ITEM")
public final class ItemQevent extends Qevent {
	
	private final Material material;
	private final short data;
	private final int amount;
	private final Map<Integer, Integer> enchants;
	private final boolean inverted;
	private final boolean quest;
	private final boolean armor;
	
	public ItemQevent(final Material mat, final int dat, final int amt, final Map<Integer, Integer> enchs, final boolean invert, final boolean quest, final boolean armor) {
		inverted = invert;
		if(inverted) {
			this.armor = armor;
		}
		else {
			this.armor = false;
		}
		material = mat;
		this.quest = quest;
		if(!invert && dat < 0) {
			data = 0;
		}
		else {
			data = (short) dat;
		}
		amount = amt;
		if(enchs != null) {
			enchants = enchs;
		}
		else {
			enchants = new HashMap<Integer, Integer>();
		}
	}
	
	@Override
	public String info() {
		final String itm =
				material.name() + "[" + material.getId() + ":" + data + "]; AMT: " + amount;
		String enchs = enchants.isEmpty() ? "" : "\n -- ENCH:";
		for(final Integer e : enchants.keySet()) {
			enchs = enchs + " " + Enchantment.getById(e).getName() + ":" + enchants.get(e);
		}
		final StringBuilder flag = new StringBuilder();
		if(inverted || quest) {
			flag.append(" (-");
			if(quest) {
				flag.append('q');
			}
			if(inverted) {
				flag.append('i');
			}
			flag.append(')');
		}
		return itm + flag.toString() + enchs;
	}
	
	@Override
	protected void run(final Player player, final Quester plugin) {
		if(inverted) { // remove if inverted
			int toRemove = amount;
			final PlayerInventory inv = player.getInventory();
			// ARMOR
			if(armor) {
				final ItemStack[] armor = inv.getArmorContents();
				toRemove = removeItems(armor, toRemove);
				inv.setArmorContents(armor);
			}
			// INVENTORY
			final ItemStack[] contents = inv.getContents();
			toRemove = removeItems(contents, toRemove);
			inv.setContents(contents);
		}
		else {
			final int maxSize = material.getMaxStackSize();
			int toGive = amount;
			int numSpaces = 0;
			int given = 0;
			final ItemStack[] contents = player.getInventory().getContents();
			ItemStack item = new ItemStack(material, 1, data);
			if(quest) {
				final List<String> lore = new ArrayList<String>();
				lore.add(ChatColor.BLUE + "Quest Item");
				final ItemMeta im = item.getItemMeta();
				im.setLore(lore);
				item.setItemMeta(im);
			}
			for(final Integer j : enchants.keySet()) {
				item.addUnsafeEnchantment(Enchantment.getById(j), enchants.get(j));
			}
			for(final ItemStack i : contents) {
				if(i == null) {
					numSpaces += maxSize;
				}
				else if(i.isSimilar(item)) {
					numSpaces += maxSize - i.getAmount();
				}
			}
			given = Math.min(toGive, numSpaces);
			toGive -= given;
			numSpaces = (int) Math.ceil((double) given / (double) maxSize);
			int round;
			final PlayerInventory inv = player.getInventory();
			for(int k = 0; k < numSpaces; k++) {
				round = Math.min(maxSize, given);
				item.setAmount(round);
				inv.addItem(item.clone());
				given -= round;
			}
			
			if(toGive > 0) {
				numSpaces = (int) Math.ceil((double) toGive / (double) maxSize);
				for(int k = 0; k < numSpaces; k++) {
					given = Math.min(toGive, maxSize);
					item = new ItemStack(material, given, data);
					for(final Integer j : enchants.keySet()) {
						item.addUnsafeEnchantment(Enchantment.getById(j), enchants.get(j));
					}
					player.getWorld().dropItem(player.getLocation(), item);
					toGive -= given;
				}
			}
		}
	}
	
	private int removeItems(final ItemStack[] contents, final int amount) {
		int toRemove = amount;
		for(int i = 0; i < contents.length; i++) {
			final ItemStack is = contents[i];
			if(is != null && is.getTypeId() == material.getId() && Util.isQuestItem(is) == quest
					&& (data < 0 || is.getDurability() == data) && checkEnchants(is)) {
				if(is.getAmount() <= toRemove) {
					toRemove -= is.getAmount();
					contents[i] = null;
					if(toRemove == 0) {
						break;
					}
				}
				else {
					is.setAmount(is.getAmount() - toRemove);
					toRemove = 0;
					break;
				}
			}
		}
		return toRemove;
	}
	
	private boolean checkEnchants(final ItemStack is) {
		if(enchants.isEmpty()) {
			return true;
		}
		for(final Entry<Integer, Integer> e : enchants.entrySet()) {
			if(e.getValue() > is.getEnchantmentLevel(Enchantment.getById(e.getKey()))) {
				return false;
			}
		}
		return true;
	}
	
	@QCommand(min = 1, max = 3, usage = "{<item>} [amount] {[enchants]} (-i)")
	public static Qevent fromCommand(final QCommandContext context) throws QCommandException {
		Material mat = null;
		int dat;
		int amt = 1;
		Map<Integer, Integer> enchs = null;
		final int[] itm = SerUtils.parseItem(context.getString(0), context.getSenderLang());
		mat = Material.getMaterial(itm[0]);
		dat = itm[1];
		if(context.length() > 1) {
			amt = context.getInt(1);
			if(context.length() > 2) {
				enchs = SerUtils.parseEnchants(context.getString(2));
			}
		}
		if(amt < 1 || dat < -1) {
			throw new IllegalArgumentException(context.getSenderLang().get("ERROR_CMD_ITEM_NUMBERS"));
		}
		if(context.length() > 2) {
			enchs = SerUtils.parseEnchants(context.getString(2));
		}
		
		return new ItemQevent(mat, dat, amt, enchs, context.hasFlag('i'), context.hasFlag('q'),
				context.hasFlag('a'));
	}
	
	@Override
	protected void save(final StorageKey key) {
		key.setString("item", SerUtils.serializeItem(material, data));
		if(!enchants.isEmpty()) {
			key.setString("enchants", SerUtils.serializeEnchants(enchants));
		}
		if(amount != 1) {
			key.setInt("amount", amount);
		}
		if(inverted) {
			key.setBoolean("inverted", true);
			if(armor) {
				key.setBoolean("armor", armor);
			}
		}
		if(quest) {
			key.setBoolean("quest", true);
		}
	}
	
	protected static Qevent load(final StorageKey key) {
		Material mat = null;
		int dat;
		try {
			final int[] itm = SerUtils.parseItem(key.getString("item", ""));
			mat = Material.getMaterial(itm[0]);
			dat = itm[1];
		}
		catch (final Exception e) {
			return null;
		}
		
		int amt = key.getInt("amount", 1);
		if(amt < 1) {
			amt = 1;
		}
		
		Map<Integer, Integer> enchs = null;
		try {
			enchs = SerUtils.parseEnchants(key.getString("enchants", ""));
		}
		catch (final IllegalArgumentException ignore) {}
		
		return new ItemQevent(mat, dat, amt, enchs, key.getBoolean("inverted", false),
				key.getBoolean("quest", false), key.getBoolean("armor", false));
	}
}
