package com.gmail.molnardad.quester.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.bukkit.ChatColor;

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
