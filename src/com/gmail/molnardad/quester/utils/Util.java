package com.gmail.molnardad.quester.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import com.avaje.ebeaninternal.server.lib.util.InvalidDataException;
import com.gmail.molnardad.quester.Quester;

public class Util {
	
	
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
	
	public static Location getLoc(CommandSender sender, String[] args, int i) throws NumberFormatException {
		if(args.length > i+3){
			double x = Double.parseDouble(args[i]);
			double y = Double.parseDouble(args[i+1]);
			double z = Double.parseDouble(args[i+2]);
			if(y < 0) {
				throw new NumberFormatException();
			}
			Location loc;
			if(sender instanceof Player && args[i+3].equalsIgnoreCase("this")) {
				loc = new Location(((Player)sender).getWorld(), x, y, z);
			} else {
				World world = Quester.plugin.getServer().getWorld(args[i+3]);
				if(world == null) {
					return null;
				}
				loc = new Location(world, x, y, z);
			}
			return loc;
		} else 
			return null;
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
		String result = "";
		String gl = " ";
		if(glue != ' ')
			gl = glue + gl;
		boolean first = true;
		for(int i = start; i < strs.length; i++) {
			if(first) {
				result += strs[i];
				first = false;
			} else 
				result += gl + strs[i];
		}
		return result;
	}
	
	public static boolean permCheck(Player player, String perm, boolean message) {
		return permCheck((CommandSender) player, perm, message);
	}
	
	public static boolean permCheck(CommandSender sender, String perm, boolean message) {
		if(sender.isOp() || Quester.perms.has(sender, perm)) {
			return true;
		}
		if(message)
			sender.sendMessage(ChatColor.RED + "You don't have permission for this.");
		return false;
	}
	
	public static Map<Integer, Integer> parseEnchants(String[] args, int id) throws NumberFormatException, InvalidDataException {
		Map<Integer, Integer> enchs = new HashMap<Integer, Integer>();
		for(int i = id; i < args.length; i++) {
			Enchantment en = null;
			int lvl = 0;
			String[] s = args[i].split(":");
			if(s.length != 2) {
				throw new InvalidDataException("");
			}
			en = Enchantment.getByName(s[0].toUpperCase());
			if(en == null) {
				en = Enchantment.getById(Integer.parseInt(s[0]));
			}
			if(en == null)
				throw new InvalidDataException("");
			lvl = Integer.parseInt(s[1]);
			if(lvl < 1) {
				throw new NumberFormatException();
			}
			
			enchs.put(en.getId(), lvl);
		}
			
		return enchs;
	}
	
	public static int[] parseItem(String arg) throws NumberFormatException, InvalidDataException {
		int[] itm = new int[2];
		String[] s = arg.split(":");
		if(s.length > 2) {
			throw new InvalidDataException("");
		}
		Material mat = Material.getMaterial(s[0].toUpperCase());
		if(mat == null) {
			try {
				itm[0] = Integer.parseInt(s[0]);
			} catch (NumberFormatException e) {
				throw new InvalidDataException("");
			}
			if(Material.getMaterial(itm[0]) == null)
				throw new InvalidDataException("");
		} else {
			itm[0] = mat.getId();
		}
		if(s.length < 2) {
			itm[1] = -1;
		} else {
			itm[1] = Integer.parseInt(s[1]);
		}
		return itm;
	}
	
	public static byte parseColor(String arg) throws InvalidDataException {
		DyeColor col = null;
		col = DyeColor.valueOf(arg.toUpperCase());
		if(col == null) {
			try {
				col = DyeColor.getByData(Byte.parseByte(arg));
			} catch (NumberFormatException e) {
				throw new InvalidDataException("");
			}
		}
		if(col == null)
			throw new InvalidDataException("");
		return col.getData();
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
	
	public static Map<String, Object> serializeLocation(Location loc) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("x", loc.getX());
		map.put("y", loc.getY());
		map.put("z", loc.getZ());
		map.put("world", loc.getWorld().getName());
		map.put("yaw", loc.getYaw());
		map.put("pitch", loc.getPitch());
		
		return map;
	}
	
	public static Location deserializeLocation(Map<String, Object> map) {
		double x, y, z;
		double yaw = 0;
		double pitch = 0;
		World world = null;
		Location loc = null;
		
		try {
			x = (Double) map.get("x");
			y = (Double) map.get("y");
			z = (Double) map.get("z");
			world = Bukkit.getWorld((String) map.get("world"));
			if(world == null)
				throw new IllegalArgumentException();
			if(map.get("yaw") != null)
				yaw = (Double) map.get("yaw");
			if(map.get("pitch") != null)
				pitch = (Double) map.get("pitch");
			loc = new Location(world, x, y, z, (float) yaw, (float) pitch);
		} catch (Exception e) {}
		
		return loc;
	}
	
	// SAVE / LOAD OBJECT
	public static void saveObject(Object obj, File path, String fileName) throws IOException {
		File fajl = new File(path, fileName);
		if(!fajl.exists()) {
			fajl.createNewFile();
		}
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fajl));
		oos.writeObject(obj);
		oos.flush();
		oos.close();
	}
 
	public static Object loadObject(File path, String fileName) throws IOException, ClassNotFoundException{
		File fajl = new File(path, fileName);
		if(!fajl.exists()) {
			fajl.createNewFile();
		}
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fajl));
		Object result = ois.readObject();
		return result;
	}
}
