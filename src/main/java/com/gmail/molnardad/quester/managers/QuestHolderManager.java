package com.gmail.molnardad.quester.managers;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.gmail.molnardad.quester.QuestHolder;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.QuesterSign;
import com.gmail.molnardad.quester.exceptions.CustomException;
import com.gmail.molnardad.quester.exceptions.HolderException;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.strings.QuesterStrings;
import com.gmail.molnardad.quester.utils.Util;

public class QuestHolderManager {

	private Quester plugin = null;
	private ProfileManager profMan = null;
	
	public Map<Integer, QuestHolder> holderIds = new HashMap<Integer, QuestHolder>();
	public Map<String, QuesterSign> signs = new HashMap<String, QuesterSign>();
	
	private int holderID = -1;
	
	public QuestHolderManager(Quester plugin) {
		this.plugin = plugin;
		this.profMan = plugin.getProfileManager();
	}
	
	public Map<Integer, QuestHolder> getHolders() {
		return holderIds;
	}
	
	public QuestHolder getHolder(int ID) {
		return holderIds.get(ID);
	}
	
	public int getLastHolderID(){
		return holderID;
	}
	
	public int getNewHolderID() {
		holderID++;
		return holderID;
	}
	
	public void setHolderID(int newID) {
		holderID = newID;
	}
	
	public void adjustHolderID() {
		int newID = -1;
		for(int i : holderIds.keySet()) {
			if(i > newID)
				newID = i;
		}
		holderID = newID;
	}
	
	// HOLDER MANIPULATION
	
	public int createHolder(String name) {
		QuestHolder qh = new QuestHolder(name, plugin);
		int id = getNewHolderID();
		holderIds.put(id, qh);
		saveHolders();
		return id;
	}
	
	public void removeHolder(int ID) {
		holderIds.remove(ID);
		saveHolders();
	}
	
	public void addHolderQuest(String issuer, int questID, QuesterStrings lang) throws QuesterException {
		QuestHolder qh = getHolder(profMan.getProfile(issuer).getHolderID());
		if(qh == null) {
			throw new HolderException(lang.ERROR_HOL_NOT_EXIST);
		}
		qh.addQuest(questID);
		saveHolders();
	}
	
	public void removeHolderQuest(String issuer, int questID, QuesterStrings lang) throws QuesterException {
		QuestHolder qh = getHolder(profMan.getProfile(issuer).getHolderID());
		if(qh == null) {
			throw new HolderException(lang.ERROR_HOL_NOT_EXIST);
		}
		qh.removeQuest(questID);
		saveHolders();
	}
	
	public void moveHolderQuest(String issuer, int which, int where, QuesterStrings lang) throws QuesterException {
		QuestHolder qh = getHolder(profMan.getProfile(issuer).getHolderID());
		if(qh == null) {
			throw new HolderException(lang.ERROR_HOL_NOT_SELECTED);
		}
		try {
			qh.moveQuest(which, where);
		}
		catch (IndexOutOfBoundsException e) {
			throw new CustomException(lang.ERROR_CMD_ID_OUT_OF_BOUNDS);
		}
		saveHolders();
	}

	public void showHolderList(CommandSender sender, QuesterStrings lang) {
		sender.sendMessage(Util.line(ChatColor.BLUE, lang.INFO_HOLDER_LIST, ChatColor.GOLD));
		for(int id : getHolders().keySet()){
			sender.sendMessage(ChatColor.BLUE + "[" + id + "]" + ChatColor.GOLD + " " + getHolder(id).getName());
		}
	}

	public void showHolderInfo(CommandSender sender, int holderID, QuesterStrings lang) throws QuesterException {
		QuestHolder qh;
		int id;
		if(holderID < 0) {
			id = profMan.getProfile(sender.getName()).getHolderID();
		} else {
			id = holderID;
		}
		qh = getHolder(id);
		if(qh == null) {
			if(holderID < 0)
				throw new HolderException(lang.ERROR_HOL_NOT_SELECTED);
			else
				throw new HolderException(lang.ERROR_HOL_NOT_EXIST);
		}
		sender.sendMessage(ChatColor.GOLD + "Holder ID: " + ChatColor.RESET + id);
		qh.showQuestsModify(sender);
	}
	
	// TODO STORAGE METHODS

	public void saveHolders(){}
	
	public void loadHolders() {}
	
//	public void saveHolders(){
//		plugin.holderConfig.saveConfig();
//	}
//
//	@SuppressWarnings("unchecked")
//	public void loadHolders() {
//		try {
//
//			YamlConfiguration config = plugin.holderConfig.getConfig();
//			
//			// HOLDERS
//			ConfigurationSection holders = config.getConfigurationSection("holders");
//			QuestHolder qh;
//			if(holders != null) {
//				for(String key : holders.getKeys(false)) {
//					try {
//						int id = Integer.parseInt(key);
//						qh = QuestHolder.deserialize(holders.getConfigurationSection(key), plugin);
//						if(qh == null){
//							throw new InvalidKeyException();
//						}
//						if(holderIds.get(id) != null)
//							Quester.log.info("Duplicate holder index: '" + key + "'");
//						holderIds.put(id, qh);
//					} catch (NumberFormatException e) {
//						Quester.log.info("Not numeric holder index: '" + key + "'");
//					} catch (Exception e) {
//						Quester.log.info("Invalid holder: '" + key + "'");
//					}
//				}
//			}
//			adjustHolderID();
//			
//			// SIGNS
//			Object object = config.get("signs");
//			if(object != null) {
//				if(object instanceof List) {
//					List<Map<String, Object>> list = (List<Map<String, Object>>) object;
//					for(Map<String, Object> map : list) {
//						QuesterSign sign = QuesterSign.deserialize(map);
//						if(sign == null)
//							continue;
//						String s = sign.getLocation().getWorld().getName() + sign.getLocation().getBlockX() + sign.getLocation().getBlockY() + sign.getLocation().getBlockZ();
//						signs.put(s, sign);
//					}
//				} else {
//					Quester.log.info("Invalid sign list in holders.yml.");
//				}
//			}
//			
//			saveHolders();
//			if(verbose) {
//				Quester.log.info(holderIds.size() + " holders loaded.");
//				Quester.log.info(signs.size() + " signs loaded.");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//	}
}
