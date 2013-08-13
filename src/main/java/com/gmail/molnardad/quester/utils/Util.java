package com.gmail.molnardad.quester.utils;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.gmail.molnardad.quester.QConfiguration;
import com.gmail.molnardad.quester.lang.QuesterLang;

public class Util {
	
	private static Random randGen = new Random();
	
	// LINE
	public static String line(final ChatColor lineColor) {
		return line(lineColor, "", lineColor);
	}
	
	public static String line(final ChatColor lineColor, final String label) {
		return line(lineColor, label, lineColor);
	}
	
	public static String line(final ChatColor lineColor, final String label, final ChatColor labelColor) {
		String temp;
		if(!label.isEmpty()) {
			temp = "[" + labelColor + label.trim() + lineColor + "]";
		}
		else {
			temp = "" + lineColor + lineColor;
		}
		final String line1 =
				"-------------------------".substring((int) Math
						.ceil((temp.trim().length() - 2) / 2));
		final String line2 =
				"-------------------------".substring((int) Math
						.floor((temp.trim().length() - 2) / 2));
		return lineColor + line1 + temp + line2;
	}
	
	public static String implode(final String[] strs) {
		return implode(strs, ' ', 0);
	}
	
	public static String implode(final String[] strs, final char glue) {
		return implode(strs, glue, 0);
	}
	
	public static String implode(final String[] strs, final int start) {
		return implode(strs, ' ', start);
	}
	
	public static String implode(final String[] strs, final char glue, final int start) {
		final StringBuilder result = new StringBuilder();
		String gl = " ";
		if(glue != ' ') {
			gl = glue + gl;
		}
		boolean first = true;
		for(int i = start; i < strs.length; i++) {
			if(first) {
				first = false;
			}
			else {
				result.append(gl);
			}
			result.append(strs[i]);
		}
		return result.toString();
	}
	
	// whatever
	public static String implodeInt(final int[] ints, final String glue) {
		final StringBuilder result = new StringBuilder();
		boolean first = true;
		for(int i = 0; i < ints.length; i++) {
			if(first) {
				first = false;
			}
			else {
				result.append(glue);
			}
			result.append(ints[i]);
		}
		return result.toString();
	}
	
	public static String implodeInt(final Integer[] ints, final String glue) {
		final StringBuilder result = new StringBuilder();
		boolean first = true;
		for(int i = 0; i < ints.length; i++) {
			if(first) {
				first = false;
			}
			else {
				result.append(glue);
			}
			result.append(ints[i]);
		}
		return result.toString();
	}
	
	public static boolean permCheck(final CommandSender sender, final String perm, final boolean message, final QuesterLang lang) {
		if(perm.isEmpty()) {
			return true;
		}
		if(sender.isOp() || sender.hasPermission(QConfiguration.PERM_ADMIN)) {
			return true;
		}
		for(final String s : perm.split("\\|\\|")) {
			if(sender.hasPermission(s)) {
				return true;
			}
		}
		if(message) {
			sender.sendMessage(ChatColor.RED + lang.MSG_PERMS);
		}
		return false;
	}
	
	public static String enchantName(final int id, final int lvl) {
		String result = "Unknown";
		switch(id) {
			case 0:
				result = "Protection";
				break;
			case 1:
				result = "Fire Protection";
				break;
			case 2:
				result = "Feather Falling";
				break;
			case 3:
				result = "Blast Protection";
				break;
			case 4:
				result = "Projectile Protection";
				break;
			case 5:
				result = "Respiration";
				break;
			case 6:
				result = "Aqua Affinity";
				break;
			case 7:
				result = "Thorns";
				break;
			case 16:
				result = "Sharpness";
				break;
			case 17:
				result = "Smite";
				break;
			case 18:
				result = "Bane of Arthropods";
				break;
			case 19:
				result = "Knockback";
				break;
			case 20:
				result = "Fire Aspect";
				break;
			case 21:
				result = "Looting";
				break;
			case 32:
				result = "Efficiency";
				break;
			case 33:
				result = "Silk Touch";
				break;
			case 34:
				result = "Unbreaking";
				break;
			case 35:
				result = "Fortune";
				break;
			case 48:
				result = "Power";
				break;
			case 49:
				result = "Punch";
				break;
			case 50:
				result = "Flame";
				break;
			case 51:
				result = "Infinity";
				break;
		}
		switch(lvl) {
			case 1:
				result = result + " I";
				break;
			case 2:
				result = result + " II";
				break;
			case 3:
				result = result + " III";
				break;
			case 4:
				result = result + " IV";
				break;
			case 5:
				result = result + " V";
				break;
			case 6:
				result = result + " VI";
				break;
			case 7:
				result = result + " VII";
				break;
			case 8:
				result = result + " VIII";
				break;
			case 9:
				result = result + " IX";
				break;
			case 10:
				result = result + " X";
				break;
			default:
				result = result + " " + lvl;
		}
		return result;
	}
	
	public static boolean isQuestItem(final ItemStack item) {
		try {
			final List<String> lore = item.getItemMeta().getLore();
			return ChatColor.stripColor(lore.get(0)).equalsIgnoreCase("Quest Item")
					|| ChatColor.stripColor(lore.get(lore.size() - 1)).equalsIgnoreCase(
							"Quest Item");
		}
		catch (final Exception e) {
			return false;
		}
	}
	
	public static boolean isPlayer(final Player player) {
		return !player.hasMetadata("NPC");
	}
	
	// LOCATION SERIALIZATION
	
	
	
	// MOVE LOCATION
	
	public static Location move(final Location loc, final double d) {
		if(d == 0) {
			return loc;
		}
		final Location newLoc = loc.clone();
		final Vector v =
				new Vector(randGen.nextDouble() * d * 2 - d, 0, randGen.nextDouble() * d * 2 - d);
		newLoc.add(v);
		
		return newLoc;
	}
	
	// MOVE LIST UNIT
	
	public static <T> void moveListUnit(final List<T> list, final int which, final int where) {
		final T temp = list.get(which);
		final int increment = which > where ? -1 : 1;
		for(int i = which; i != where; i += increment) {
			list.set(i, list.get(i + increment));
		}
		list.set(where, temp);
	}
	
	// INVENTORY
	
	public static Inventory createInventory(final Player player) {
		
		final Inventory inv = Bukkit.getServer().createInventory(null, InventoryType.PLAYER);
		final ItemStack[] contents = player.getInventory().getContents();
		
		for(int i = 0; i < contents.length; i++) {
			if(contents[i] != null) {
				inv.setItem(i, contents[i].clone());
			}
		}
		return inv;
	}
}
