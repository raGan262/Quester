package com.gmail.molnardad.quester.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
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
	
	public static String sconcat(String[] strs, int start) {
		String result = "";
		for(int i = start; i < strs.length; i++) {
			result = result + " " + strs[i];
		}
		return result.trim();
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
			itm[0] = Integer.parseInt(s[0]);
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
