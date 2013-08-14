package com.gmail.molnardad.quester.holder;

import java.io.File;
import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.exceptions.CustomException;
import com.gmail.molnardad.quester.exceptions.HolderException;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.lang.QuesterLang;
import com.gmail.molnardad.quester.profiles.ProfileManager;
import com.gmail.molnardad.quester.quests.QuestManager;
import com.gmail.molnardad.quester.storage.ConfigStorage;
import com.gmail.molnardad.quester.storage.Storage;
import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.utils.Ql;
import com.gmail.molnardad.quester.utils.Util;

public class QuestHolderManager {
	
	private ProfileManager profMan = null;
	private QuestManager qMan = null;
	
	private Storage holderStorage = null;
	
	private final Map<Integer, QuestHolder> holderIds = new HashMap<Integer, QuestHolder>();
	private final Map<Location, QuesterSign> signs = new HashMap<Location, QuesterSign>();
	
	private int holderID = -1;
	
	public QuestHolderManager(final Quester plugin) {
		qMan = plugin.getQuestManager();
		profMan = plugin.getProfileManager();
		final File file = new File(plugin.getDataFolder(), "holders.yml");
		holderStorage = new ConfigStorage(file, plugin.getLogger(), null);
	}
	
	public Map<Integer, QuestHolder> getHolders() {
		return holderIds;
	}
	
	public QuestHolder getHolder(final int ID) {
		return holderIds.get(ID);
	}
	
	public int getLastHolderID() {
		return holderID;
	}
	
	public int getNewHolderID() {
		holderID++;
		return holderID;
	}
	
	public void setHolderID(final int newID) {
		holderID = newID;
	}
	
	public void adjustHolderID() {
		int newID = -1;
		for(final int i : holderIds.keySet()) {
			if(i > newID) {
				newID = i;
			}
		}
		holderID = newID;
	}
	
	// SIGN MANIPULATION
	
	public QuesterSign getSign(final Location loc) {
		return signs.get(loc);
	}
	
	public void addSign(final QuesterSign sign) {
		if(sign != null && sign.getLocation() != null) {
			signs.put(sign.getLocation(), sign);
		}
	}
	
	public boolean removeSign(final Location location) {
		return signs.remove(location) != null;
	}
	
	// HOLDER MANIPULATION
	
	public int createHolder(final String name) {
		final QuestHolder qh = new QuestHolder(name);
		final int id = getNewHolderID();
		holderIds.put(id, qh);
		return id;
	}
	
	public void removeHolder(final int ID) {
		holderIds.remove(ID);
	}
	
	public void addHolderQuest(final String issuer, final int questID, final QuesterLang lang) throws QuesterException {
		final QuestHolder qh = getHolder(profMan.getProfile(issuer).getHolderID());
		if(qh == null) {
			throw new HolderException(lang.get("ERROR_HOL_NOT_EXIST"));
		}
		qh.addQuest(questID);
	}
	
	public void removeHolderQuest(final String issuer, final int questID, final QuesterLang lang) throws QuesterException {
		final QuestHolder qh = getHolder(profMan.getProfile(issuer).getHolderID());
		if(qh == null) {
			throw new HolderException(lang.get("ERROR_HOL_NOT_EXIST"));
		}
		qh.removeQuest(questID);
	}
	
	public void moveHolderQuest(final String issuer, final int which, final int where, final QuesterLang lang) throws QuesterException {
		final QuestHolder qh = getHolder(profMan.getProfile(issuer).getHolderID());
		if(qh == null) {
			throw new HolderException(lang.get("ERROR_HOL_NOT_SELECTED"));
		}
		try {
			qh.moveQuest(which, where);
		}
		catch (final IndexOutOfBoundsException e) {
			throw new CustomException(lang.get("ERROR_CMD_ID_OUT_OF_BOUNDS"));
		}
	}
	
	public int getOne(final QuestHolder holder) {
		int one = -1;
		final List<Integer> heldQuests = holder.getQuests();
		if(heldQuests.isEmpty()) {
			return -1;
		}
		for(final int q : heldQuests) {
			if(qMan.isQuestActive(q)) {
				if(one >= 0) {
					return -1;
				}
				else {
					one = q;
				}
			}
		}
		return one;
	}
	
	public boolean selectNext(final String selecter, final QuestHolder holder, final QuesterLang lang) throws HolderException {
		if(holder == null) {
			return false;
		}
		final List<Integer> heldQuests = holder.getQuests();
		if(heldQuests.isEmpty()) {
			throw new HolderException(lang.get("ERROR_Q_NONE"));
		}
		if(holder.getSelected(selecter) == -1) {
			holder.setSelected(selecter, 0);
			if(qMan.isQuestActive(heldQuests.get(0))) {
				return true;
			}
		}
		int i = holder.getSelected(selecter);
		final int selected = i;
		boolean notChosen = true;
		while(notChosen) {
			if(i < heldQuests.size() - 1) {
				i++;
			}
			else {
				i = 0;
			}
			if(qMan.isQuestActive(heldQuests.get(i))) {
				holder.setSelected(selecter, i);
				notChosen = false;
			}
			else if(i == selected) {
				throw new HolderException(lang.get("ERROR_Q_NONE_ACTIVE"));
			}
		}
		return true;
	}
	
	public void checkHolders() {
		for(final QuestHolder hol : holderIds.values()) {
			checkQuests(hol);
		}
	}
	
	private void checkQuests(final QuestHolder holder) {
		if(holder == null) {
			return;
		}
		final Iterator<Integer> iterator = holder.getQuests().iterator();
		while(iterator.hasNext()) {
			if(!qMan.isQuest(iterator.next())) {
				iterator.remove();
			}
		}
	}
	
	public void showHolderList(final CommandSender sender, final QuesterLang lang) {
		sender.sendMessage(Util.line(ChatColor.BLUE, lang.get("INFO_HOLDER_LIST"), ChatColor.GOLD));
		for(final int id : getHolders().keySet()) {
			sender.sendMessage(ChatColor.BLUE + "[" + id + "]" + ChatColor.GOLD + " "
					+ getHolder(id).getName());
		}
	}
	
	public void showHolderInfo(final CommandSender sender, final int holderID, final QuesterLang lang) throws QuesterException {
		QuestHolder qh;
		int id;
		if(holderID < 0) {
			id = profMan.getProfile(sender.getName()).getHolderID();
		}
		else {
			id = holderID;
		}
		qh = getHolder(id);
		if(qh == null) {
			if(holderID < 0) {
				throw new HolderException(lang.get("ERROR_HOL_NOT_SELECTED"));
			}
			else {
				throw new HolderException(lang.get("ERROR_HOL_NOT_EXIST"));
			}
		}
		sender.sendMessage(ChatColor.GOLD + "Holder ID: " + ChatColor.RESET + id);
		showQuestsModify(qh, sender);
	}
	
	public boolean showQuestsUse(final QuestHolder holder, final Player player) {
		if(holder == null) {
			return false;
		}
		final List<Integer> heldQuests = holder.getQuests();
		final int selected = holder.getSelected(player.getName());
		for(int i = 0; i < heldQuests.size(); i++) {
			if(qMan.isQuestActive(heldQuests.get(i))) {
				player.sendMessage((i == selected ? ChatColor.GREEN : ChatColor.BLUE) + " - "
						+ qMan.getQuestName(heldQuests.get(i)));
			}
		}
		return true;
	}
	
	public boolean showQuestsModify(final QuestHolder holder, final CommandSender sender) {
		if(holder == null) {
			return false;
		}
		sender.sendMessage(ChatColor.GOLD + "Holder name: " + ChatColor.RESET + holder.getName());
		final List<Integer> heldQuests = holder.getQuests();
		final int selected = holder.getSelected(sender.getName());
		for(int i = 0; i < heldQuests.size(); i++) {
			final ChatColor col =
					qMan.isQuestActive(heldQuests.get(i)) ? ChatColor.BLUE : ChatColor.RED;
			
			sender.sendMessage(i + ". " + (i == selected ? ChatColor.GREEN : ChatColor.BLUE) + "["
					+ heldQuests.get(i) + "] " + col + qMan.getQuestName(heldQuests.get(i)));
		}
		return true;
	}
	
	public void saveHolders() {
		final StorageKey pKey = holderStorage.getKey("");
		pKey.removeKey("holders");
		pKey.removeKey("signs");
		final StorageKey holKey = pKey.getSubKey("holders");
		for(final int i : holderIds.keySet()) {
			holderIds.get(i).serialize(holKey.getSubKey(String.valueOf(i)));
		}
		final StorageKey signKey = pKey.getSubKey("signs");
		int i = 0;
		for(final QuesterSign sign : signs.values()) {
			sign.serialize(signKey.getSubKey(String.valueOf(i)));
			i++;
		}
		holderStorage.save();
	}
	
	public void loadHolders() {
		// HOLDERS
		holderStorage.load();
		final StorageKey holderKey = holderStorage.getKey("holders");
		QuestHolder qh;
		if(holderKey.hasSubKeys()) {
			for(final StorageKey subKey : holderKey.getSubKeys()) {
				try {
					final int id = Integer.parseInt(subKey.getName());
					qh = QuestHolder.deserialize(subKey);
					if(qh == null) {
						throw new InvalidKeyException();
					}
					if(holderIds.get(id) != null) {
						Ql.info("Duplicate holder index: '" + subKey.getName() + "'");
					}
					holderIds.put(id, qh);
				}
				catch (final NumberFormatException e) {
					Ql.info("Not numeric holder index: '" + subKey.getName() + "'");
				}
				catch (final Exception e) {
					Ql.info("Invalid holder: '" + subKey.getName() + "'");
				}
			}
		}
		adjustHolderID();
		
		// SIGNS
		final StorageKey signKey = holderStorage.getKey("signs");
		for(final StorageKey k : signKey.getSubKeys()) {
			final QuesterSign sign = QuesterSign.deserialize(k);
			if(sign == null) {
				Ql.info("Failed to deserialize sign under key '" + k.getName() + "'");
				continue;
			}
			signs.put(sign.getLocation(), sign);
		}
		Ql.verbose(holderIds.size() + " holders loaded.");
		Ql.verbose(signs.size() + " signs loaded.");
	}
}
