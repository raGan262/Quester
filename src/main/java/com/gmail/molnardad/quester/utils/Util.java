package com.gmail.molnardad.quester.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.managers.DataManager;
import com.gmail.molnardad.quester.strings.QuesterLang;

public class Util {
	
	private static Random randGen = new Random();
	
	// LINE
	public static String line(ChatColor lineColor) {
		return line(lineColor, "", lineColor);
	}
	
	public static String line(ChatColor lineColor, String label) {
		return line(lineColor, label, lineColor);
	}
	
	public static String line(ChatColor lineColor, String label, ChatColor labelColor) {
		String temp;
		if(!label.isEmpty()) {
			temp = "[" + labelColor + label.trim() + lineColor + "]";
		} else {
			temp = "" + lineColor + lineColor;
		}
		String line1 = "-------------------------".substring((int) Math.ceil((temp.trim().length()-2)/2));
		String line2 = "-------------------------".substring((int) Math.floor((temp.trim().length()-2)/2));
		return lineColor + line1 + temp + line2;
	}
	
	public static Location getLoc(CommandSender sender, String arg) throws IllegalArgumentException {
		return getLoc(sender, arg, new QuesterLang(null));
	}
	
	public static Location getLoc(CommandSender sender, String arg, QuesterLang lang) throws IllegalArgumentException {
		
		String args[] = arg.split(";");
		Location loc;
		if(args.length < 1)
			throw new IllegalArgumentException(ChatColor.RED + lang.ERROR_CMD_LOC_INVALID);
		
		if(args[0].equalsIgnoreCase(DataManager.locLabelHere)) {
			if(sender instanceof Player) {
				return ((Player) sender).getLocation();
			}
			else {
				throw new IllegalArgumentException(ChatColor.RED + lang.ERROR_CMD_LOC_HERE
						.replaceAll("%here", DataManager.locLabelHere));
			}
		}
		else if(args[0].equalsIgnoreCase(DataManager.locLabelBlock)) {
			if(sender instanceof Player) {
				Block block = ((Player) sender).getTargetBlock(null, 5);
				if(block == null) {
					throw new IllegalArgumentException(ChatColor.RED + lang.ERROR_CMD_LOC_NOBLOCK);
				}
				return block.getLocation();
			}
			else {
				throw new IllegalArgumentException(ChatColor.RED + lang.ERROR_CMD_LOC_BLOCK
						.replaceAll("%block", DataManager.locLabelBlock));
			}
		}
		else if(args[0].equalsIgnoreCase(DataManager.locLabelPlayer)) {
			return null;
		}
		
		if(args.length > 3){
			double x, y, z;
			try {
				x = Double.parseDouble(args[0]);
				y = Double.parseDouble(args[1]);
				z = Double.parseDouble(args[2]);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException(ChatColor.RED + lang.ERROR_CMD_COORDS_INVALID);
			}
			if(y < 0) {
				throw new IllegalArgumentException(ChatColor.RED + lang.ERROR_CMD_COORDS_INVALID);
			}
			if(sender instanceof Player && args[3].equalsIgnoreCase(DataManager.worldLabelThis)) {
				loc = new Location(((Player)sender).getWorld(), x, y, z);
			} else {
				World world = Bukkit.getServer().getWorld(args[3]);
				if(world == null) {
					throw new IllegalArgumentException(ChatColor.RED + lang.ERROR_CMD_WORLD_INVALID);
				}
				loc = new Location(world, x, y, z);
			}
			return loc;
		}
		
		throw new IllegalArgumentException(ChatColor.RED + lang.ERROR_CMD_LOC_INVALID);
	}
	
	public static String implode(String[] strs) {
		return implode(strs, ' ', 0);
	}
	
	public static String implode(String[] strs, char glue) {
		return implode(strs, glue, 0);
	}
	
	public static String implode(String[] strs, int start) {
		return implode(strs, ' ', start);
	}
	
	public static String implode(String[] strs, char glue, int start) {
		StringBuilder result = new StringBuilder();
		String gl = " ";
		if(glue != ' ')
			gl = glue + gl;
		boolean first = true;
		for(int i = start; i < strs.length; i++) {
			if(first) {
				first = false;
			} else {
				result.append(gl);
			}
			result.append(strs[i]);
		}
		return result.toString();
	}
	
	public static String implodeInt(Integer[] ints, String glue) {
		StringBuilder result = new StringBuilder();
		boolean first = true;
		for(int i = 0; i < ints.length; i++) {
			if(first) {
				first = false;
			} else {
				result.append(glue);
			}
			result.append(ints[i]);
		}
		return result.toString();
	}
	
	public static boolean permCheck(CommandSender sender, String perm, boolean message, QuesterLang lang) {
		if(perm.isEmpty()) {
			return true;
		}
		if(sender.isOp() || sender.hasPermission(DataManager.PERM_ADMIN)) {
			return true;
		}
		for(String s : perm.split("\\|\\|")) {
			Quester.log.info(s);
			if(sender.hasPermission(s)) {
				return true;
			}
		}
		if(message)
			sender.sendMessage(ChatColor.RED + lang.MSG_PERMS);
		return false;
	}
	
	public static String serializeEnchants(Map<Integer, Integer> enchs) {
		String result = "";
		boolean first = true;
		for(int key : enchs.keySet()) {
			if(!first)
				result += ",";
			else
				first = false;
			result += key + ":" + enchs.get(key);
		}
		if(result.isEmpty())
			return null;
		return result;
	}
	
	public static Map<Integer, Integer> parseEnchants(String arg) throws IllegalArgumentException {
		return parseEnchants(arg, new QuesterLang(null));
	}
	
	public static Map<Integer, Integer> parseEnchants(String arg, QuesterLang lang) throws IllegalArgumentException {
		
		Map<Integer, Integer> enchs = new HashMap<Integer, Integer>();
		String[] args = arg.split(",");
		for(int i = 0; i < args.length; i++) {
			Enchantment en = null;
			int lvl = 0;
			String[] s = args[i].split(":");
			if(s.length != 2) {
				throw new IllegalArgumentException(lang.ERROR_CMD_ENCH_INVALID);
			}
			en = Enchantment.getByName(s[0].toUpperCase());
			if(en == null) {
				en = Enchantment.getById(Integer.parseInt(s[0]));
			}
			if(en == null)
				throw new IllegalArgumentException(lang.ERROR_CMD_ENCH_INVALID);
			lvl = Integer.parseInt(s[1]);
			if(lvl < 1) {
				throw new IllegalArgumentException(lang.ERROR_CMD_ENCH_LEVEL);
			}
			
			enchs.put(en.getId(), lvl);
		}
			
		return enchs;
	}
	
	public static String serializeItem(Material mat, int data) {
		if(mat == null)
			return null;
		
		return serializeItem(mat.getId(), data);
	}
	
	public static String serializeItem(int mat, int data) {
		String str = "";
		if(data >= 0)
			str = ":" + (short)data;
		return (mat + str);
	}
	
	public static int[] parseItem(String arg) throws IllegalArgumentException {
		return parseItem(arg, new QuesterLang(null));
	}
	
	public static int[] parseItem(String arg, QuesterLang lang) throws IllegalArgumentException {
		
		int[] itm = new int[2];
		String[] s = arg.split(":");
		if(s.length > 2) {
			throw new IllegalArgumentException(lang.ERROR_CMD_ITEM_UNKNOWN);
		}
		Material mat = Material.getMaterial(s[0].toUpperCase());
		if(mat == null) {
			try {
				itm[0] = Integer.parseInt(s[0]);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException(lang.ERROR_CMD_ITEM_UNKNOWN);
			}
			if(Material.getMaterial(itm[0]) == null)
				throw new IllegalArgumentException(lang.ERROR_CMD_ITEM_UNKNOWN);
		} else {
			itm[0] = mat.getId();
		}
		if(s.length < 2) {
			itm[1] = -1;
		} else {
			try {
				itm[1] = Integer.parseInt(s[1]);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException(lang.ERROR_CMD_ITEM_UNKNOWN);
			}
		}
		return itm;
	}
	
	public static String serializeColor(DyeColor col) {
		if(col == null)
			return null;
		return "" + col.getDyeData();
	}
	
	public static DyeColor parseColor(String arg) {
		DyeColor col = null;
		try {
			col = DyeColor.valueOf(arg.toUpperCase());
		} catch (Exception ignore) {}
		if(col == null) {
			try {
				col = DyeColor.getByDyeData(Byte.parseByte(arg));
			} catch (NumberFormatException ignore) {}
		}
		return col;
	}
	
	public static String serializeEffect(PotionEffect eff) {
		if(eff == null)
			return null;
		return eff.getType().getId() + ";" + eff.getDuration()/20.0 + ";" + eff.getAmplifier();
	}
	
	public static PotionEffect parseEffect(String arg) throws IllegalArgumentException {
		return parseEffect(arg, new QuesterLang(null));
	}
	
	public static PotionEffect parseEffect(String arg, QuesterLang lang) throws IllegalArgumentException {
		
		PotionEffectType type = null;
		double dur = 0; 
		int amp = 0;
		String[] s = arg.split(";");
		if(s.length > 3 || s.length < 2) {
			throw new IllegalArgumentException(lang.ERROR_CMD_EFFECT_UNKNOWN + "1");
		}
		type = PotionEffectType.getByName(s[0]);
		if(type == null) {
			try {
				type = PotionEffectType.getById(Integer.parseInt(s[0]));
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException(lang.ERROR_CMD_EFFECT_UNKNOWN + "2");
			}
			if(type == null)
				throw new IllegalArgumentException(lang.ERROR_CMD_EFFECT_UNKNOWN + "3");
		}
		try {
			dur = Double.parseDouble(s[1]);
			if(dur < 1)
				throw new NumberFormatException();
			dur *= 20;
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(lang.ERROR_CMD_EFFECT_DURATION);
		}
		try {
			if(s.length > 2) {
				amp = Integer.parseInt(s[2]);
				if(amp < 0)
					throw new NumberFormatException();
			}
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(lang.ERROR_CMD_EFFECT_AMPLIFIER);
		}
		return new PotionEffect(type, (int)dur, amp);
	}
	
	public static String enchantName(int id, int lvl) {
		String result = "Unknown";
		switch(id) {
			case 0 : result = "Protection";
					break;
			case 1 : result = "Fire Protection";
					break;
			case 2 : result = "Feather Falling";
					break;
			case 3 : result = "Blast Protection";
					break;
			case 4 : result = "Projectile Protection";
					break;
			case 5 : result = "Respiration";
					break;
			case 6 : result = "Aqua Affinity";
					break;
			case 7 : result = "Thorns";
					break;
			case 16 : result = "Sharpness";
					break;
			case 17 : result = "Smite";
					break;
			case 18 : result = "Bane of Arthropods";
					break;
			case 19 : result = "Knockback";
					break;
			case 20 : result = "Fire Aspect";
					break;
			case 21 : result = "Looting";
					break;
			case 32 : result = "Efficiency";
					break;
			case 33 : result = "Silk Touch";
					break;
			case 34 : result = "Unbreaking";
					break;
			case 35 : result = "Fortune";
					break;
			case 48 : result = "Power";
					break;
			case 49 : result = "Punch";
					break;
			case 50 : result = "Flame";
					break;
			case 51 : result = "Infinity";
					break;
		} 
		switch(lvl) {
			case 1 : result = result + " I";
					break;
			case 2 : result = result + " II";
					break;
			case 3 : result = result + " III";
					break;
			case 4 : result = result + " IV";
					break;
			case 5 : result = result + " V";
					break;
		}
		return result;
	}
	
	public static String serializeEntity(EntityType ent) {
		if(ent == null)
			return null;
		return "" + ent.getTypeId();
	}
	
	public static EntityType parseEntity(String arg) throws IllegalArgumentException {
		return parseEntity(arg, new QuesterLang(null));
	}
	
	public static EntityType parseEntity(String arg, QuesterLang lang) throws IllegalArgumentException {
		EntityType ent = EntityType.fromName(arg.toUpperCase());
		if(ent == null) {
			ent = EntityType.fromId(Integer.parseInt(arg));
			if(ent == null || ent.getTypeId() < 50) {
				throw new IllegalArgumentException(lang.ERROR_CMD_ENTITY_UNKNOWN);
			}
		}
		return ent;
	}
	
	public static Sound parseSound(String arg) {
		Sound sound = null;
		try {
			sound = Sound.valueOf(arg.toUpperCase());
		}
		catch (Exception ignore) {}
		return sound;
	}
	
	public static Set<Integer> parsePrerequisites(String[] args, int from) {
		Set<Integer> result = new HashSet<Integer>();
		for(int i=from; i < args.length; i++) {
			try {
				result.add(Integer.parseInt(args[i]));
			} catch (Exception ignore) {}
		}
		return result;
	}
	
	public static Set<Integer> deserializePrerequisites(String arg) throws NumberFormatException {
		Set<Integer> result = new HashSet<Integer>();
		String[] args = arg.split(";");
		for(int i=0; i < args.length; i++) {
			result.add(Integer.parseInt(args[i]));
		}
		return result;
	}
	
	public static String serializePrerequisites(Set<Integer> prereq, String glue) {
		String result = "";
		boolean first = true;
		for(int i : prereq) {
			if(first) {
				result += i;
				first = false;
			}
			else {
				result += glue + i;
			}
		}
		return result;
	}
	
	public static String serializePrerequisites(Set<Integer> prereq) {
		return serializePrerequisites(prereq, ";");
	}
	
	public static int[] deserializeOccasion(String arg, QuesterLang lang) throws IllegalArgumentException{
		int[] arr = new int[2];
		arr[0] = -4;
		arr[1] = 0;
		String[] s = arg.split(":");
		if(s.length > 2 || s.length < 1) {
			throw new IllegalArgumentException(lang.ERROR_CMD_OCC_INCORRECT_FORM);
		}
		try {
			arr[0] = Integer.parseInt(s[0]);
		}
		catch (NumberFormatException ignore) {}
		if(s.length > 1) {
			arr[1] = Integer.parseInt(s[1]);
		}
		if(arr[0] < -3 || arr[1] < 0)
			throw new IllegalArgumentException(lang.ERROR_CMD_OCC_INCORRECT);
		return arr;
	} 
	
	public static String serializeOccasion(int occ, int del) {
		if(del != 0) {
			return occ + ":" + del;
		}
		else {
			return "" + occ;
		}
	}
	
	public static int parseAction(String arg) {
		try {
			int i = Integer.parseInt(arg);
			if(i < 0 || i > 3) {
				return 0;
			}
			else {
				return i;
			}
		}
		catch (NumberFormatException ignore) {}
		if(arg.equalsIgnoreCase("left")) {
			return 1;
		}
		else if(arg.equalsIgnoreCase("right")) {
			return 2;
		}
		else if(arg.equalsIgnoreCase("push")) {
			return 3;
		}
		return 0;
	}
	
	public static String flagArgument(char flag, String arg) {
		if(arg.startsWith(flag + ":") && arg.length() > 2) {
			return arg.substring(2);
		}
		else {
			return "";
		}
	}
	
	public static boolean isQuestItem(ItemStack item) {
		try {
			return ChatColor.stripColor(item.getItemMeta().getLore().get(0)).equalsIgnoreCase("Quest Item");
		}
		catch (Exception e) {
			return false;
		}
	}
	
	public static boolean isPlayer(Player player) {
		return !player.hasMetadata("NPC");
	}
	
	// LOCATION SERIALIZATION
	
	public static String displayLocation(Location loc) {
		String str = "";
		
		if(loc == null)
			return null;
		
		str = String.format(Locale.ENGLISH, "%.1f;%.1f;%.1f;"+loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ());
		
		return str;
	}
	
	public static String serializeLocString(Location loc) {
		String str = "";
		
		if(loc == null)
			return null;
		
		str = String.format(Locale.ENGLISH, "%.2f;%.2f;%.2f;"+loc.getWorld().getName()+";%.2f;%.2f;", loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
		
		return str;
	}
	
	public static Location deserializeLocString(String str) {
		double x, y, z;
		float yaw = 0;
		float pitch = 0;
		World world = null;
		Location loc = null;
		
		String[] split = str.split(";");
		
		if(str.length() < 4) 
			return null;
		
		
		try {
			x = Double.parseDouble(split[0]);
			y = Double.parseDouble(split[1]);
			z = Double.parseDouble(split[2]);
			world = Bukkit.getWorld(split[3]);
			if(world == null)
				throw new IllegalArgumentException();
			if(split.length > 4)
				yaw = Float.parseFloat(split[4]);
			if(split.length > 5)
				pitch = Float.parseFloat(split[5]);
			loc = new Location(world, x, y, z, yaw, pitch);
		} catch (Exception e) {
			if(DataManager.debug) {
				Quester.log.severe("Error when deserializing location.");
			}
		}
		
		return loc;
	}
	
	// MOVE LOCATION
	
	public static Location move(Location loc, double d) {
		if(d == 0)
			return loc;
		Location newLoc = loc.clone();
		Vector v = new Vector(randGen.nextDouble()*d*2 - d, 0, randGen.nextDouble()*d*2 - d);
		newLoc.add(v);
		
		return newLoc;
	}

	// MOVE LIST UNIT
	
	public static <T> void moveListUnit(List<T> list, int which, int where) {
		T temp = list.get(which);
		int increment = (which > where ? -1 : 1);
		for(int i = which; i != where; i+=increment) {
			list.set(i, list.get(i+increment));
		}
		list.set(where, temp);
	}
}
