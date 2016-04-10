package me.ragan262.quester.quests;

import me.ragan262.quester.elements.Condition;
import me.ragan262.quester.elements.Objective;
import me.ragan262.quester.elements.Qevent;
import me.ragan262.quester.elements.Trigger;
import me.ragan262.quester.lang.LanguageManager;
import me.ragan262.quester.lang.QuesterLang;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.QLocation;
import me.ragan262.quester.utils.Ql;
import me.ragan262.quester.utils.SerUtils;
import me.ragan262.quester.utils.Util;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Quest {
	
	private final List<Objective> objectives = new ArrayList<>();
	private final List<Condition> conditions = new ArrayList<>();
	private final List<Qevent> qevents = new ArrayList<>();
	private final List<Trigger> triggers = new ArrayList<>();
	private final Set<String> worlds = new HashSet<>();
	private final Set<QuestFlag> flags = new HashSet<>();
	private String description = "";
	private boolean isCustomMessage = false;
	private String name = null;
	private QLocation location = null;
	private int range = 1;
	private int ID = -1;
	
	boolean error = false;
	
	public Quest(final String name) {
		this.name = name;
	}
	
	public boolean hasID() {
		return ID >= 0;
	}
	
	public int getID() {
		return ID;
	}
	
	void setID(final int newID) {
		ID = newID;
	}
	
	public boolean hasFlag(final QuestFlag flag) {
		return flags.contains(flag);
	}
	
	public Set<QuestFlag> getFlags() {
		return new HashSet<>(flags);
	}
	
	void addFlag(final QuestFlag flag) {
		flags.add(flag);
	}
	
	void removeFlag(final QuestFlag flag) {
		flags.remove(flag);
	}
	
	public String getName() {
		return name;
	}
	
	void setName(final String newName) {
		name = newName;
	}
	
	public String getRawDescription() {
		if(isCustomMessage) {
			return "LANG:" + description;
		}
		else {
			return description;
		}
	}
	
	public String getDescription(final String playerName, final QuesterLang lang) {
		final String localDesc = isCustomMessage ? lang.getCustom(description) : description;
		return ChatColor.translateAlternateColorCodes('&', localDesc).replaceAll("%p", playerName);
	}
	
	void setDescription(final String newDescription) {
		description = newDescription;
		customMessageCheck();
	}
	
	void addDescription(final String toAdd) {
		final boolean doCheck = description.isEmpty();
		description = (description + " " + toAdd).trim();
		if(doCheck) {
			customMessageCheck();
		}
	}
	
	private void customMessageCheck() {
		final String langMsg = LanguageManager.getCustomMessageKey(description);
		if(langMsg != null) {
			description = langMsg;
			isCustomMessage = true;
		}
		else {
			isCustomMessage = false;
		}
	}
	
	public boolean hasLocation() {
		return location != null;
	}
	
	public QLocation getLocation() {
		return location;
	}
	
	void setLocation(final QLocation loc) {
		location = loc;
	}
	
	public int getRange() {
		return range;
	}
	
	void setRange(final int rng) {
		range = rng;
	}
	
	/* OBJECTIVE METHODS */
	
	public Objective getObjective(final int id) {
		if(id < objectives.size() && id >= 0) {
			return objectives.get(id);
		}
		return null;
	}
	
	public List<Objective> getObjectives() {
		return new ArrayList<>(objectives);
	}
	
	public List<Objective> getObjectives(final String type) {
		final List<Objective> result = new ArrayList<>();
		for(final Objective o : objectives) {
			if(o.getType().equalsIgnoreCase(type)) {
				result.add(o);
			}
		}
		return result;
	}
	
	boolean removeObjective(final int id) {
		if(id < objectives.size()) {
			objectives.remove(id);
			return true;
		}
		return false;
	}
	
	void addObjective(final Objective newObjective) {
		objectives.add(newObjective);
	}
	
	void setObjective(final int objectiveID, final Objective newObjective) {
		objectives.set(objectiveID, newObjective);
	}
	
	/* ----------------- */
	
	/* CONDITION METHODS */
	
	public Condition getCondition(final int id) {
		if(id < conditions.size()) {
			return conditions.get(id);
		}
		return null;
	}
	
	public List<Condition> getConditions() {
		return new ArrayList<>(conditions);
	}
	
	boolean removeCondition(final int id) {
		if(id < conditions.size() && id >= 0) {
			conditions.remove(id);
			return true;
		}
		return false;
	}
	
	void addCondition(final Condition newCondition) {
		conditions.add(newCondition);
	}
	
	void setCondition(final int conditionID, final Condition newCondition) {
		conditions.set(conditionID, newCondition);
	}
	
	/* ----------------- */
	
	/* TRIGGER METHODS */
	
	public Trigger getTrigger(final int id) {
		if(id < triggers.size()) {
			return triggers.get(id);
		}
		return null;
	}
	
	public List<Trigger> getTriggers() {
		return new ArrayList<>(triggers);
	}
	
	Trigger removeTrigger(final int id) {
		if(id < triggers.size() && id >= 0) {
			return triggers.remove(id);
		}
		return null;
	}
	
	void addTrigger(final Trigger newTrigger) {
		triggers.add(newTrigger);
	}
	
	void setTrigger(final int triggerID, final Trigger newTrigger) {
		triggers.set(triggerID, newTrigger);
	}
	
	/* ----------------- */
	
	/* QEVENT METHODS */
	
	public Qevent getQevent(final int id) {
		if(id < qevents.size()) {
			return qevents.get(id);
		}
		return null;
	}
	
	public List<Qevent> getQevents() {
		return new ArrayList<>(qevents);
	}
	
	public Map<Integer, Map<Integer, Qevent>> getQeventMap() {
		return getQeventMap(null);
	}
	
	public Map<Integer, Map<Integer, Qevent>> getQeventMap(final String type) {
		final Map<Integer, Map<Integer, Qevent>> result = new HashMap<>();
		Qevent q = null;
		int occ = 0;
		for(int i = 0; i < qevents.size(); i++) {
			q = qevents.get(i);
			if(type == null || type.equalsIgnoreCase(q.getType())) {
				occ = q.getOccasion();
				if(result.get(occ) == null) {
					result.put(occ, new TreeMap<Integer, Qevent>());
				}
				result.get(occ).put(i, q);
			}
		}
		return result;
	}
	
	boolean removeQevent(final int id) {
		if(id < qevents.size() && id >= 0) {
			qevents.remove(id);
			return true;
		}
		return false;
	}
	
	void addQevent(final Qevent newQevent) {
		qevents.add(newQevent);
	}
	
	void setQevent(final int qeventID, final Qevent newQevent) {
		qevents.set(qeventID, newQevent);
	}
	
	/* ----------------- */
	
	public String getWorldNames() {
		return Util.implode(worlds.toArray(new String[worlds.size()]), ',');
	}
	
	public Set<String> getWorlds() {
		return new HashSet<>(worlds);
	}
	
	void addWorld(final String worldName) {
		worlds.add(worldName.toLowerCase());
	}
	
	boolean removeWorld(final String worldName) {
		if(!worlds.contains(worldName.toLowerCase())) {
			return false;
		}
		worlds.remove(worldName.toLowerCase());
		return true;
	}
	
	public boolean allowedWorld(final String worldName) {
		return worlds.contains(worldName.toLowerCase()) || worlds.isEmpty();
	}
	
	void serialize(final StorageKey key) {
		
		key.setString("name", name);
		if(!description.isEmpty()) {
			final String prefix = isCustomMessage ? "LANG:" : "";
			key.setString("description", prefix + description);
		}
		if(location != null) {
			key.setString("location", SerUtils.serializeLocString(location));
			if(range > 1) {
				key.setInt("range", range);
			}
		}
		if(!worlds.isEmpty()) {
			key.setRaw("worlds", worlds.toArray(new String[worlds.size()]));
		}
		if(!flags.isEmpty()) {
			key.setString("flags", QuestFlag.serialize(flags));
		}
		
		if(!objectives.isEmpty()) {
			final StorageKey subKey = key.getSubKey("objectives");
			for(int i = 0; i < objectives.size(); i++) {
				objectives.get(i).serialize(subKey.getSubKey(String.valueOf(i)));
			}
		}
		if(!conditions.isEmpty()) {
			final StorageKey subKey = key.getSubKey("conditions");
			for(int i = 0; i < conditions.size(); i++) {
				conditions.get(i).serialize(subKey.getSubKey(String.valueOf(i)));
			}
		}
		if(!qevents.isEmpty()) {
			final StorageKey subKey = key.getSubKey("events");
			for(int i = 0; i < qevents.size(); i++) {
				qevents.get(i).serialize(subKey.getSubKey(String.valueOf(i)));
			}
		}
		if(!triggers.isEmpty()) {
			final StorageKey subKey = key.getSubKey("triggers");
			for(int i = 0; i < triggers.size(); i++) {
				triggers.get(i).serialize(subKey.getSubKey(String.valueOf(i)));
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	static Quest deserialize(final StorageKey key) {
		Quest quest;
		try {
			String name;
			if(key.getString("name") != null) {
				name = key.getString("name");
				quest = new Quest(name);
			}
			else {
				return null;
			}
			if(key.getString("description") != null) {
				quest.setDescription(key.getString("description"));
			}
			
			if(key.getString("location") != null) {
				quest.setLocation(SerUtils.deserializeLocString(key.getString("location")));
				if(key.getInt("range", 1) > 1) {
					quest.setRange(key.getInt("range"));
				}
			}
			
			int id = key.getInt("ID", -1);
			if(id < 0) {
				try {
					id = Integer.parseInt(key.getName());
				}
				catch(final NumberFormatException ignore) {}
			}
			if(id >= 0) {
				quest.setID(id);
			}
			
			if(key.getString("flags") != null) {
				final Set<QuestFlag> flags = QuestFlag.deserialize(key.getString("flags"));
				for(final QuestFlag f : flags) {
					quest.addFlag(f);
				}
			}
			
			if(key.getRaw("worlds") instanceof List) {
				final List<String> strs = (List<String>)key.getRaw("worlds", new ArrayList<String>());
				for(final String s : strs) {
					if(s != null) {
						quest.addWorld(s);
					}
				}
			}
			
			Objective obj = null;
			if(key.getSubKey("objectives").hasSubKeys()) {
				final StorageKey subKey = key.getSubKey("objectives");
				final List<StorageKey> keys = subKey.getSubKeys();
				for(int i = 0; i < keys.size(); i++) {
					obj = Objective.deserialize(subKey.getSubKey(String.valueOf(i)));
					if(obj != null) {
						quest.addObjective(obj);
					}
					else {
						quest.error = true;
						Ql.severe("Error occured when deserializing objective ID " + i
								+ " in quest '" + quest.getName() + "'.");
					}
				}
			}
			
			Condition con = null;
			if(key.getSubKey("conditions").hasSubKeys()) {
				final StorageKey subKey = key.getSubKey("conditions");
				final List<StorageKey> keys = subKey.getSubKeys();
				for(int i = 0; i < keys.size(); i++) {
					con = Condition.deserialize(subKey.getSubKey(String.valueOf(i)));
					if(con != null) {
						quest.addCondition(con);
					}
					else {
						quest.error = true;
						Ql.severe("Error occured when deserializing condition ID " + i
								+ " in quest '" + quest.getName() + "'.");
					}
				}
			}
			
			Qevent qvt = null;
			if(key.getSubKey("events").hasSubKeys()) {
				final StorageKey subKey = key.getSubKey("events");
				final List<StorageKey> keys = subKey.getSubKeys();
				for(int i = 0; i < keys.size(); i++) {
					qvt = Qevent.deserialize(subKey.getSubKey(String.valueOf(i)));
					if(qvt != null) {
						quest.addQevent(qvt);
					}
					else {
						quest.error = true;
						Ql.severe("Error occured when deserializing event ID:" + i + " in quest '"
								+ quest.getName() + "'.");
					}
				}
			}
			
			Trigger trig = null;
			if(key.getSubKey("triggers").hasSubKeys()) {
				final StorageKey subKey = key.getSubKey("triggers");
				final List<StorageKey> keys = subKey.getSubKeys();
				for(int i = 0; i < keys.size(); i++) {
					trig = Trigger.deserialize(subKey.getSubKey(String.valueOf(i)));
					if(trig != null) {
						quest.addTrigger(trig);
					}
					else {
						quest.error = true;
						Ql.severe("Error occured when deserializing trigger ID:" + i
								+ " in quest '" + quest.getName() + "'.");
					}
				}
			}
			
		}
		catch(final Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return quest;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if(obj != null && obj instanceof Quest) {
			final Quest other = (Quest)obj;
			return name.equals(other.name) && ID == other.ID && flags.equals(other.flags);
		}
		return false;
	}
}
