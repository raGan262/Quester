package me.ragan262.quester.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import me.ragan262.quester.QConfiguration;
import me.ragan262.quester.lang.LanguageManager;
import me.ragan262.quester.lang.QuesterLang;

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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SerUtils {
	
	public static Location getLoc(final CommandSender sender, final String arg) throws IllegalArgumentException {
		return getLoc(sender, arg, LanguageManager.defaultLang);
	}
	
	public static Location getLoc(final CommandSender sender, final String arg, final QuesterLang lang) throws IllegalArgumentException {
		
		final String args[] = arg.split(";");
		Location loc;
		if(args.length < 1) {
			throw new IllegalArgumentException(ChatColor.RED + lang.get("ERROR_CMD_LOC_INVALID"));
		}
		
		if(args[0].equalsIgnoreCase(QConfiguration.locLabelHere)) {
			if(sender instanceof Player) {
				return ((Player) sender).getLocation();
			}
			else {
				throw new IllegalArgumentException(ChatColor.RED
						+ lang.get("ERROR_CMD_LOC_HERE").replaceAll("%here",
								QConfiguration.locLabelHere));
			}
		}
		else if(args[0].equalsIgnoreCase(QConfiguration.locLabelBlock)) {
			if(sender instanceof Player) {
				final Block block = ((Player) sender).getTargetBlock(null, 5);
				if(block == null) {
					throw new IllegalArgumentException(ChatColor.RED
							+ lang.get("ERROR_CMD_LOC_NOBLOCK"));
				}
				return block.getLocation();
			}
			else {
				throw new IllegalArgumentException(ChatColor.RED
						+ lang.get("ERROR_CMD_LOC_BLOCK").replaceAll("%block",
								QConfiguration.locLabelBlock));
			}
		}
		else if(args[0].equalsIgnoreCase(QConfiguration.locLabelPlayer)) {
			return null;
		}
		
		if(args.length > 3) {
			double x, y, z;
			try {
				x = Double.parseDouble(args[0]);
				y = Double.parseDouble(args[1]);
				z = Double.parseDouble(args[2]);
			}
			catch (final NumberFormatException e) {
				throw new IllegalArgumentException(ChatColor.RED
						+ lang.get("ERROR_CMD_COORDS_INVALID"));
			}
			if(y < 0) {
				throw new IllegalArgumentException(ChatColor.RED
						+ lang.get("ERROR_CMD_COORDS_INVALID"));
			}
			if(sender instanceof Player && args[3].equalsIgnoreCase(QConfiguration.worldLabelThis)) {
				loc = new Location(((Player) sender).getWorld(), x, y, z);
			}
			else {
				final World world = Bukkit.getServer().getWorld(args[3]);
				if(world == null) {
					throw new IllegalArgumentException(ChatColor.RED
							+ lang.get("ERROR_CMD_WORLD_INVALID"));
				}
				loc = new Location(world, x, y, z);
			}
			return loc;
		}
		
		throw new IllegalArgumentException(ChatColor.RED + lang.get("ERROR_CMD_LOC_INVALID"));
	}
	
	public static String serializeEnchants(final Map<Integer, Integer> enchs) {
		String result = "";
		boolean first = true;
		for(final int key : enchs.keySet()) {
			if(!first) {
				result += ",";
			}
			else {
				first = false;
			}
			result += key + ":" + enchs.get(key);
		}
		if(result.isEmpty()) {
			return null;
		}
		return result;
	}
	
	public static Map<Integer, Integer> parseEnchants(final String arg) throws IllegalArgumentException {
		return parseEnchants(arg, LanguageManager.defaultLang);
	}
	
	public static Map<Integer, Integer> parseEnchants(final String arg, final QuesterLang lang) throws IllegalArgumentException {
		
		final Map<Integer, Integer> enchs = new HashMap<Integer, Integer>();
		final String[] args = arg.split(",");
		for(int i = 0; i < args.length; i++) {
			Enchantment en = null;
			int lvl = 0;
			final String[] s = args[i].split(":");
			if(s.length != 2) {
				throw new IllegalArgumentException(lang.get("ERROR_CMD_ENCH_INVALID"));
			}
			en = Enchantment.getByName(s[0].toUpperCase());
			if(en == null) {
				en = Enchantment.getById(Integer.parseInt(s[0]));
			}
			if(en == null) {
				throw new IllegalArgumentException(lang.get("ERROR_CMD_ENCH_INVALID"));
			}
			lvl = Integer.parseInt(s[1]);
			if(lvl < 1) {
				throw new IllegalArgumentException(lang.get("ERROR_CMD_ENCH_LEVEL"));
			}
			
			enchs.put(en.getId(), lvl);
		}
		
		return enchs;
	}
	
	public static String serializeItem(final Material mat, final int data) {
		if(mat == null) {
			return null;
		}
		
		return serializeItem(mat.getId(), data);
	}
	
	public static String serializeItem(final int mat, final int data) {
		String str = "";
		if(data >= 0) {
			str = ":" + (short) data;
		}
		return mat + str;
	}
	
	public static int[] parseItem(final String arg) throws IllegalArgumentException {
		return parseItem(arg, LanguageManager.defaultLang);
	}
	
	public static int[] parseItem(final String arg, final QuesterLang lang) throws IllegalArgumentException {
		
		final int[] itm = new int[2];
		final String[] s = arg.split(":");
		if(s.length > 2) {
			throw new IllegalArgumentException(lang.get("ERROR_CMD_ITEM_UNKNOWN"));
		}
		final Material mat = Material.getMaterial(s[0].toUpperCase());
		if(mat == null) {
			try {
				itm[0] = Integer.parseInt(s[0]);
			}
			catch (final NumberFormatException e) {
				throw new IllegalArgumentException(lang.get("ERROR_CMD_ITEM_UNKNOWN"));
			}
			if(Material.getMaterial(itm[0]) == null) {
				throw new IllegalArgumentException(lang.get("ERROR_CMD_ITEM_UNKNOWN"));
			}
		}
		else {
			itm[0] = mat.getId();
		}
		if(s.length < 2) {
			itm[1] = -1;
		}
		else {
			try {
				itm[1] = Integer.parseInt(s[1]);
			}
			catch (final NumberFormatException e) {
				throw new IllegalArgumentException(lang.get("ERROR_CMD_ITEM_UNKNOWN"));
			}
		}
		return itm;
	}
	
	public static String serializeColor(final DyeColor col) {
		if(col == null) {
			return null;
		}
		return "" + col.getDyeData();
	}
	
	public static DyeColor parseColor(final String arg) {
		DyeColor col = null;
		try {
			col = DyeColor.valueOf(arg.toUpperCase());
		}
		catch (final Exception ignore) {}
		if(col == null) {
			try {
				col = DyeColor.getByDyeData(Byte.parseByte(arg));
			}
			catch (final NumberFormatException ignore) {}
		}
		return col;
	}
	
	public static String serializeEffect(final PotionEffect eff) {
		if(eff == null) {
			return null;
		}
		return eff.getType().getId() + ";" + eff.getDuration() / 20.0 + ";" + eff.getAmplifier();
	}
	
	public static PotionEffect parseEffect(final String arg) throws IllegalArgumentException {
		return parseEffect(arg, LanguageManager.defaultLang);
	}
	
	public static PotionEffect parseEffect(final String arg, final QuesterLang lang) throws IllegalArgumentException {
		
		PotionEffectType type = null;
		double dur = 0;
		int amp = 0;
		final String[] s = arg.split(";");
		if(s.length > 3 || s.length < 2) {
			throw new IllegalArgumentException(lang.get("ERROR_CMD_EFFECT_UNKNOWN") + "1");
		}
		type = PotionEffectType.getByName(s[0]);
		if(type == null) {
			try {
				type = PotionEffectType.getById(Integer.parseInt(s[0]));
			}
			catch (final NumberFormatException e) {
				throw new IllegalArgumentException(lang.get("ERROR_CMD_EFFECT_UNKNOWN") + "2");
			}
			if(type == null) {
				throw new IllegalArgumentException(lang.get("ERROR_CMD_EFFECT_UNKNOWN") + "3");
			}
		}
		try {
			dur = Double.parseDouble(s[1]);
			if(dur < 1) {
				throw new NumberFormatException();
			}
			dur *= 20;
		}
		catch (final NumberFormatException e) {
			throw new IllegalArgumentException(lang.get("ERROR_CMD_EFFECT_DURATION"));
		}
		try {
			if(s.length > 2) {
				amp = Integer.parseInt(s[2]);
				if(amp < 0) {
					throw new NumberFormatException();
				}
			}
		}
		catch (final NumberFormatException e) {
			throw new IllegalArgumentException(lang.get("ERROR_CMD_EFFECT_AMPLIFIER"));
		}
		return new PotionEffect(type, (int) dur, amp);
	}
	
	public static String serializeEntity(final EntityType ent) {
		if(ent == null) {
			return null;
		}
		return "" + ent.getTypeId();
	}
	
	public static EntityType parseEntity(final String arg) throws IllegalArgumentException {
		return parseEntity(arg, LanguageManager.defaultLang);
	}
	
	public static EntityType parseEntity(final String arg, final QuesterLang lang) throws IllegalArgumentException {
		EntityType ent = EntityType.fromName(arg.toUpperCase());
		if(ent == null) {
			ent = EntityType.fromId(Integer.parseInt(arg));
			if(ent == null || ent.getTypeId() < 50) {
				throw new IllegalArgumentException(lang.get("ERROR_CMD_ENTITY_UNKNOWN"));
			}
		}
		return ent;
	}
	
	public static Sound parseSound(final String arg) {
		Sound sound = null;
		try {
			sound = Sound.valueOf(arg.toUpperCase());
		}
		catch (final Exception ignore) {}
		return sound;
	}
	
	public static Set<Integer> parseIntSet(final String[] args, final int from) {
		final Set<Integer> result = new HashSet<Integer>();
		for(int i = from; i < args.length; i++) {
			try {
				result.add(Integer.parseInt(args[i]));
			}
			catch (final Exception ignore) {}
		}
		return result;
	}
	
	public static Set<Integer> deserializeIntSet(final String arg) throws NumberFormatException {
		final Set<Integer> result = new HashSet<Integer>();
		final String[] args = arg.split(";");
		for(int i = 0; i < args.length; i++) {
			result.add(Integer.parseInt(args[i]));
		}
		return result;
	}
	
	public static String serializeIntSet(final Set<Integer> prereq, final String glue) {
		String result = "";
		boolean first = true;
		for(final int i : prereq) {
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
	
	public static String serializeIntSet(final Set<Integer> prereq) {
		return serializeIntSet(prereq, ";");
	}
	
	public static int[] deserializeOccasion(final String arg, final QuesterLang lang) throws IllegalArgumentException {
		final int[] arr = new int[2];
		arr[0] = -4;
		arr[1] = 0;
		final String[] s = arg.split(":");
		if(s.length > 2 || s.length < 1) {
			throw new IllegalArgumentException(lang.get("ERROR_CMD_OCC_INCORRECT_FORM"));
		}
		try {
			arr[0] = Integer.parseInt(s[0]);
		}
		catch (final NumberFormatException ignore) {}
		if(s.length > 1) {
			arr[1] = Integer.parseInt(s[1]);
		}
		if(arr[0] < -3 || arr[1] < 0) {
			throw new IllegalArgumentException(lang.get("ERROR_CMD_OCC_INCORRECT"));
		}
		return arr;
	}
	
	public static String serializeOccasion(final int occ, final int del) {
		if(del != 0) {
			return occ + ":" + del;
		}
		else {
			return "" + occ;
		}
	}
	
	public static int parseAction(final String arg) {
		try {
			final int i = Integer.parseInt(arg);
			if(i < 0 || i > 3) {
				return 0;
			}
			else {
				return i;
			}
		}
		catch (final NumberFormatException ignore) {}
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
	
	public static String displayLocation(final Location loc) {
		String str = "";
		
		if(loc == null) {
			return null;
		}
		
		str =
				String.format(Locale.ENGLISH, "%.1f;%.1f;%.1f;%s", loc.getX(), loc.getY(),
						loc.getZ(), loc.getWorld().getName());
		
		return str;
	}
	
	public static String serializeLocString(final Location loc) {
		String str = "";
		
		if(loc == null) {
			return null;
		}
		
		str =
				String.format(Locale.ENGLISH, "%.2f;%.2f;%.2f;%s;%.2f;%.2f;", loc.getX(),
						loc.getY(), loc.getZ(), loc.getWorld().getName(), loc.getYaw(),
						loc.getPitch());
		
		return str;
	}
	
	public static Location deserializeLocString(final String str) {
		double x, y, z;
		float yaw = 0;
		float pitch = 0;
		World world = null;
		Location loc = null;
		
		final String[] split = str.split(";");
		
		if(str.length() < 4) {
			return null;
		}
		
		try {
			x = Double.parseDouble(split[0]);
			y = Double.parseDouble(split[1]);
			z = Double.parseDouble(split[2]);
			world = Bukkit.getWorld(split[3]);
			if(world == null) {
				throw new IllegalArgumentException();
			}
			if(split.length > 4) {
				yaw = Float.parseFloat(split[4]);
			}
			if(split.length > 5) {
				pitch = Float.parseFloat(split[5]);
			}
			loc = new Location(world, x, y, z, yaw, pitch);
		}
		catch (final Exception e) {
			Ql.debug("Error when deserializing location.", e);
		}
		
		return loc;
	}
	
}
