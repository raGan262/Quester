package com.gmail.molnardad.quester;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.exceptions.HolderException;
import com.gmail.molnardad.quester.utils.Util;

public class QuestHolder {
	
	private List<Integer> heldQuests = new ArrayList<Integer>();
	private int selected = -1;
	private String name;
	private long lastAction = 0;
	
	public QuestHolder(String name) {
		this.name = name;
	}
	
	public boolean canInteract() {
		if(System.currentTimeMillis() - lastAction < 500) {
			lastAction = System.currentTimeMillis();
			return true;
		}
		return false;
	}
	
	public void setname(String newName) {
		name = newName;
	}
	
	public String getName() {
		return name;
	}
	
	public List<Integer> getQuests() {
		return heldQuests;
	}
	
	public int getSelected() {
		if(heldQuests.size() > selected && selected >= 0)
			return heldQuests.get(selected);
		else
			return -1;
	}
	
	public int getSelectedIndex() {
		return selected;
	}
	
	public boolean selectNext() throws HolderException {
		lastAction = System.currentTimeMillis();
		if(heldQuests.isEmpty())
			throw new HolderException(LanguageManager.getInstance().getDefaultLang().ERROR_Q_NONE);
		if(getSelected() == -1) {
			selected = 0;
			if(QuestManager.getInstance().isQuestActive(heldQuests.get(0)))
				return true;
		}
		int i = selected;
		boolean notChosen = true;
		while(notChosen) {
			if(i < heldQuests.size()-1)
				i++;
			else
				i = 0;
			if(QuestManager.getInstance().isQuestActive(heldQuests.get(i))) {
				selected = i;
				notChosen = false;
			} else if(i == selected) {
				throw new HolderException(LanguageManager.getInstance().getDefaultLang().ERROR_Q_NONE_ACTIVE);
			}
		}
		return true;
	}
	
	private void checkQuests() {
		for(int i=heldQuests.size()-1; i>=0; i--) {
			if(!QuestManager.getInstance().isQuest(heldQuests.get(i))) {
				heldQuests.remove(i);
			}
		}
	}
	
	public void addQuest(int questID) {
		if(!heldQuests.contains(questID)) {
			heldQuests.add(questID);
			checkQuests();
		}
	}
	
	public void removeQuest(int questID) {
		for(int i=0; i<heldQuests.size(); i++) {
			if(questID == heldQuests.get(i)) {
				heldQuests.remove(i);
				break;
			}
		}
		checkQuests();
	}
	
	public void moveQuest(int from, int to) throws HolderException, IndexOutOfBoundsException {
		heldQuests.get(from);
		heldQuests.get(to);
		Util.moveListUnit(heldQuests, from, to);
	}
	
	public void showQuestsUse(Player player) {
		for(int i=0; i<heldQuests.size(); i++) {
			if(QuestManager.getInstance().isQuestActive(heldQuests.get(i))) {
				player.sendMessage((i == selected ? ChatColor.GREEN : ChatColor.BLUE) + " - "
						+ QuestManager.getInstance().getQuestNameByID(heldQuests.get(i)));
			}
		}
	}
	
	public void showQuestsModify(CommandSender sender){
		sender.sendMessage(ChatColor.GOLD + "Holder name: " + ChatColor.RESET + name);
		for(int i=0; i<heldQuests.size(); i++) {
			ChatColor col = QuestManager.getInstance().isQuestActive(heldQuests.get(i)) ? ChatColor.BLUE : ChatColor.RED;
			
			sender.sendMessage(i + ". " + (i == selected ? ChatColor.GREEN : ChatColor.BLUE) + "["
					+ heldQuests.get(i) + "] " + col + QuestManager.getInstance().getQuestNameByID(heldQuests.get(i)));
		}
	}
	
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		String str = "";
		boolean first = true;
		for(int i : heldQuests) {
			if(first) {
				str += String.valueOf(i);
				first = false;
			} else 
				str += "," + i;
		}
		map.put("name", name);
		map.put("quests", str);
		
		return map;
	}
	
	public static QuestHolder deserialize(ConfigurationSection section) {
		QuestHolder qHolder = null;
		try{
			if(section == null)
				return null;
			String name = section.getString("name", "QuestHolder");
			String str = section.getString("quests", "");
			
			qHolder = new QuestHolder(name);
			String[] split = str.split(",");
			
			int i;
			for(String s : split) {
				try {
					i = Integer.parseInt(s);
					qHolder.addQuest(i);
				} catch (NumberFormatException f) {
				}
			}
			
		} catch (Exception e) {
		}
		
		if(qHolder != null)
			qHolder.checkQuests();
		return qHolder;
	}
}
