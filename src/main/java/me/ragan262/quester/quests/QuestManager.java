package me.ragan262.quester.quests;

import me.ragan262.quester.QConfiguration;
import me.ragan262.quester.Quester;
import me.ragan262.quester.elements.Condition;
import me.ragan262.quester.elements.Objective;
import me.ragan262.quester.elements.Qevent;
import me.ragan262.quester.elements.Trigger;
import me.ragan262.quester.exceptions.ConditionException;
import me.ragan262.quester.exceptions.CustomException;
import me.ragan262.quester.exceptions.ObjectiveException;
import me.ragan262.quester.exceptions.QeventException;
import me.ragan262.quester.exceptions.QuestException;
import me.ragan262.quester.exceptions.QuesterException;
import me.ragan262.quester.lang.LanguageManager;
import me.ragan262.quester.lang.QuesterLang;
import me.ragan262.quester.profiles.PlayerProfile;
import me.ragan262.quester.profiles.ProfileManager;
import me.ragan262.quester.storage.ConfigStorage;
import me.ragan262.quester.storage.Storage;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.QLocation;
import me.ragan262.quester.utils.Ql;
import me.ragan262.quester.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

public class QuestManager {
	
	private LanguageManager langMan = null;
	private final File dataFolder;
	private final Logger logger;
	private Storage questStorage = null;
	
	private final Map<Integer, Quest> quests = new TreeMap<>();
	private final Map<String, Integer> questNames = new HashMap<>();
	public final Map<Integer, QLocation> questLocations = new HashMap<>();
	
	private int questID = -1;
	
	public QuestManager(final Quester plugin) {
		this(plugin.getLanguageManager(), plugin.getDataFolder(), plugin.getLogger());
	}
	
	public QuestManager(final LanguageManager langMan, final File dataFolder, final Logger logger) {
		this.langMan = langMan;
		this.dataFolder = dataFolder;
		this.logger = logger;
		final File file = new File(dataFolder, "quests.yml");
		questStorage = new ConfigStorage(file, logger, null);
	}
	
	// QUEST ID MANIPULATION
	
	public int getLastQuestID() {
		return questID;
	}
	
	public void assignQuestID(final Quest qst) {
		questID++;
		qst.setID(questID);
	}
	
	public void adjustQuestID() {
		int newID = -1;
		for(final int i : quests.keySet()) {
			if(i > newID) {
				newID = i;
			}
		}
		questID = newID;
	}
	
	// QUEST MANIPULATION
	
	public void modifyCheck(final Quest quest, final QuesterLang lang) throws QuesterException {
		if(quest == null) {
			throw new QuestException(lang.get("ERROR_Q_NOT_SELECTED"));
		}
		if(quest.hasFlag(QuestFlag.ACTIVE)) {
			throw new QuestException(lang.get("ERROR_Q_CANT_MODIFY"));
		}
	}
	
	public Set<Integer> getQuestIds() {
		return quests.keySet();
	}
	
	public Collection<Quest> getQuests() {
		return quests.values();
	}
	
	public Quest getQuest(final String questName) {
		final Integer id = questNames.get(questName.toLowerCase());
		if(id == null) {
			return null;
		}
		return quests.get(id);
	}
	
	public Quest getQuest(final Integer questID) {
		return quests.get(questID);
	}
	
	public String getQuestName(final int id) {
		final Quest q = getQuest(id);
		if(q == null) {
			return "non-existant";
		}
		else {
			return q.getName();
		}
	}
	
	public boolean isQuest(final int questID) {
		
		return quests.containsKey(questID);
	}
	
	public boolean isQuest(final String questName) {
		
		return questNames.containsKey(questName.toLowerCase());
	}
	
	public boolean isQuestActive(final String questName) {
		
		return isQuestActive(getQuest(questName));
	}
	
	public boolean isQuestActive(final int questID) {
		
		return isQuestActive(getQuest(questID));
	}
	
	public boolean isQuestActive(final Quest q) {
		
		if(q == null) {
			return false;
		}
		return q.hasFlag(QuestFlag.ACTIVE);
	}
	
	public Quest createQuest(final PlayerProfile issuer, final String questName, final QuesterLang lang) throws QuesterException {
		if(isQuest(questName)) {
			throw new QuestException(lang.get("ERROR_Q_EXIST"));
		}
		final Quest quest = new Quest(questName);
		assignQuestID(quest);
		quests.put(quest.getID(), quest);
		questNames.put(questName.toLowerCase(), quest.getID());
		return quest;
	}
	
	public Quest removeQuest(final PlayerProfile issuer, final int questID, final QuesterLang lang) throws QuesterException {
		final Quest q = getQuest(questID);
		modifyCheck(q, lang);
		questNames.remove(q.getName().toLowerCase());
		questLocations.remove(q.getID());
		quests.remove(q.getID());
		questStorage.getKey(q.getName().toLowerCase()).removeKey("");
		adjustQuestID();
		return q;
	}
	
	public void activateQuest(final Quest q) {
		q.addFlag(QuestFlag.ACTIVE);
	}
	
	public void deactivateQuest(final Quest quest, final ProfileManager profMan) {
		quest.removeFlag(QuestFlag.ACTIVE);
		for(final PlayerProfile prof : profMan.getProfiles()) {
			if(prof.hasQuest(quest)) {
				profMan.unassignQuest(prof, prof.getQuestProgressIndex(quest));
				final Player player = Bukkit.getServer().getPlayerExact(prof.getName());
				if(player != null) {
					player.sendMessage(Quester.LABEL
							+ langMan.getLang(prof.getLanguage()).get("MSG_Q_DEACTIVATED"));
				}
			}
		}
		profMan.saveProfiles();
	}
	
	public boolean toggleQuest(final Quest quest, final QuesterLang lang, final ProfileManager profMan) throws QuesterException {
		if(quest == null) {
			throw new QuestException(lang.get("ERROR_Q_NOT_EXIST"));
		}
		if(quest.hasFlag(QuestFlag.ACTIVE)) {
			deactivateQuest(quest, profMan);
			return false;
		}
		else {
			activateQuest(quest);
			return true;
		}
	}
	
	public void changeQuestName(final PlayerProfile issuer, final String newName, final QuesterLang lang) throws QuesterException {
		final Quest quest = issuer.getSelected();
		if(isQuest(newName)) {
			throw new QuestException(lang.get("ERROR_Q_EXIST"));
		}
		modifyCheck(quest, lang);
		questNames.remove(quest.getName().toLowerCase());
		quest.setName(newName);
		questNames.put(newName.toLowerCase(), quest.getID());
	}
	
	public void setQuestDescription(final PlayerProfile issuer, final String newDesc, final QuesterLang lang) throws QuesterException {
		final Quest quest = issuer.getSelected();
		modifyCheck(quest, lang);
		quest.setDescription(newDesc);
	}
	
	public void addQuestDescription(final PlayerProfile issuer, final String descToAdd, final QuesterLang lang) throws QuesterException {
		final Quest quest = issuer.getSelected();
		modifyCheck(quest, lang);
		quest.addDescription(descToAdd);
	}
	
	public void setQuestLocation(final PlayerProfile issuer, final QLocation location, final int range, final QuesterLang lang) throws QuesterException {
		final Quest quest = issuer.getSelected();
		modifyCheck(quest, lang);
		quest.setLocation(location);
		quest.setRange(range);
		questLocations.put(quest.getID(), location);
	}
	
	public void removeQuestLocation(final PlayerProfile issuer, final QuesterLang lang) throws QuesterException {
		final Quest quest = issuer.getSelected();
		modifyCheck(quest, lang);
		quest.setLocation(null);
		quest.setRange(1);
		questLocations.remove(quest.getID());
	}
	
	public void addQuestWorld(final PlayerProfile issuer, final String worldName, final QuesterLang lang) throws QuesterException {
		final Quest quest = issuer.getSelected();
		modifyCheck(quest, lang);
		quest.addWorld(worldName);
	}
	
	public boolean removeQuestWorld(final PlayerProfile issuer, final String worldName, final QuesterLang lang) throws QuesterException {
		final Quest quest = issuer.getSelected();
		modifyCheck(quest, lang);
		final boolean result = quest.removeWorld(worldName);
		return result;
	}
	
	public void addQuestFlag(final PlayerProfile issuer, final QuestFlag[] flags, final QuesterLang lang) throws QuesterException {
		final Quest quest = issuer.getSelected();
		modifyCheck(quest, lang);
		for(final QuestFlag f : flags) {
			quest.addFlag(f);
		}
	}
	
	public void removeQuestFlag(final PlayerProfile issuer, final QuestFlag[] flags, final QuesterLang lang) throws QuesterException {
		final Quest quest = issuer.getSelected();
		modifyCheck(quest, lang);
		for(final QuestFlag f : flags) {
			quest.removeFlag(f);
		}
	}
	
	public void addQuestObjective(final PlayerProfile issuer, final Objective newObjective, final QuesterLang lang) throws QuesterException {
		final Quest quest = issuer.getSelected();
		modifyCheck(quest, lang);
		quest.addObjective(newObjective);
		for(int i = 0; i < quest.getTriggers().size(); i++) {
			if(quest.getTrigger(i).isGlobal()) {
				newObjective.addTrigger(i);
			}
		}
	}
	
	public void setQuestObjective(final PlayerProfile issuer, final int id, final Objective newObjective, final QuesterLang lang) throws QuesterException {
		final Quest quest = issuer.getSelected();
		modifyCheck(quest, lang);
		final Objective oldObj = quest.getObjective(id);
		if(oldObj == null) {
			throw new ObjectiveException(lang.get("ERROR_OBJ_NOT_EXIST"));
		}
		quest.setObjective(id, newObjective);
		for(final int prerequisity : oldObj.getPrerequisites()) {
			newObjective.addPrerequisity(prerequisity);
		}
		for(int i = 0; i < quest.getTriggers().size(); i++) {
			if(quest.getTrigger(i).isGlobal()) {
				newObjective.addTrigger(i);
			}
		}
	}
	
	public void removeQuestObjective(final PlayerProfile issuer, final int id, final QuesterLang lang) throws QuesterException {
		final Quest quest = issuer.getSelected();
		modifyCheck(quest, lang);
		if(!quest.removeObjective(id)) {
			throw new ObjectiveException(lang.get("ERROR_OBJ_NOT_EXIST"));
		}
	}
	
	public void addObjectiveDescription(final PlayerProfile issuer, final int id, final String desc, final QuesterLang lang) throws QuesterException {
		final Quest quest = issuer.getSelected();
		modifyCheck(quest, lang);
		final List<Objective> objs = quest.getObjectives();
		if(id >= objs.size() || id < 0) {
			throw new ObjectiveException(lang.get("ERROR_OBJ_NOT_EXIST"));
		}
		objs.get(id).addDescription(desc);
	}
	
	public void removeObjectiveDescription(final PlayerProfile issuer, final int id, final QuesterLang lang) throws QuesterException {
		final Quest quest = issuer.getSelected();
		modifyCheck(quest, lang);
		final List<Objective> objs = quest.getObjectives();
		if(id >= objs.size() || id < 0) {
			throw new ObjectiveException(lang.get("ERROR_OBJ_NOT_EXIST"));
		}
		objs.get(id).removeDescription();
	}
	
	public void swapQuestObjectives(final PlayerProfile issuer, final int first, final int second, final QuesterLang lang) throws QuesterException {
		final Quest quest = issuer.getSelected();
		if(first == second) {
			throw new CustomException(lang.get("ERROR_WHY"));
		}
		modifyCheck(quest, lang);
		
		if(quest.getObjective(first) == null || quest.getObjective(second) == null) {
			throw new ObjectiveException(lang.get("ERROR_OBJ_NOT_EXIST"));
		}
		final List<Objective> objs = quest.getObjectives();
		final Objective obj = objs.get(first);
		objs.set(first, objs.get(second));
		objs.set(second, obj);
		final List<Qevent> evts = quest.getQevents();
		for(final Qevent e : evts) {
			if(e.getOccasion() == first) {
				e.setOccasion(second);
			}
			else if(e.getOccasion() == second) {
				e.setOccasion(first);
			}
		}
	}
	
	public void moveQuestObjective(final PlayerProfile issuer, final int which, final int where, final QuesterLang lang) throws QuesterException {
		final Quest quest = issuer.getSelected();
		if(which == where) {
			throw new CustomException(lang.get("ERROR_WHY"));
		}
		modifyCheck(quest, lang);
		
		if(quest.getObjective(which) == null || quest.getObjective(where) == null) {
			throw new CustomException(lang.get("ERROR_CMD_ID_OUT_OF_BOUNDS"));
		}
		Util.moveListUnit(quest.getObjectives(), which, where);
		final List<Qevent> evts = quest.getQevents();
		for(final Qevent e : evts) {
			final int occ = e.getOccasion();
			if(occ == which) {
				e.setOccasion(where);
			}
			else if(which < where) {
				if(occ > which && occ <= where) {
					e.setOccasion(occ - 1);
				}
			}
			else {
				if(occ < which && occ >= where) {
					e.setOccasion(occ + 1);
				}
			}
		}
	}
	
	public void addObjectivePrerequisites(final PlayerProfile issuer, final int id, final Set<Integer> prereq, final QuesterLang lang) throws QuesterException {
		final Quest quest = issuer.getSelected();
		modifyCheck(quest, lang);
		final List<Objective> objs = quest.getObjectives();
		if(id >= objs.size() || id < 0) {
			throw new ObjectiveException(lang.get("ERROR_OBJ_NOT_EXIST"));
		}
		for(final int i : prereq) {
			if(i < objs.size() && i >= 0 && i != id) {
				objs.get(id).addPrerequisity(i);
			}
		}
	}
	
	public void removeObjectiveTriggers(final PlayerProfile issuer, final int id, final Set<Integer> trigs, final QuesterLang lang) throws QuesterException {
		final Quest quest = issuer.getSelected();
		modifyCheck(quest, lang);
		final List<Objective> objs = quest.getObjectives();
		if(id >= objs.size() || id < 0) {
			throw new ObjectiveException(lang.get("ERROR_OBJ_NOT_EXIST"));
		}
		for(final int i : trigs) {
			objs.get(id).removeTrigger(i);
		}
	}
	
	public void addObjectiveTriggers(final PlayerProfile issuer, final int id, final Set<Integer> trigs, final QuesterLang lang) throws QuesterException {
		final Quest quest = issuer.getSelected();
		modifyCheck(quest, lang);
		final List<Objective> objs = quest.getObjectives();
		if(id >= objs.size() || id < 0) {
			throw new ObjectiveException(lang.get("ERROR_OBJ_NOT_EXIST"));
		}
		final int trigSize = quest.getTriggers().size();
		for(final int i : trigs) {
			if(i < trigSize && i >= 0) {
				objs.get(id).addTrigger(i);
			}
		}
	}
	
	public void removeObjectivePrerequisites(final PlayerProfile issuer, final int id, final Set<Integer> prereq, final QuesterLang lang) throws QuesterException {
		final Quest quest = issuer.getSelected();
		modifyCheck(quest, lang);
		final List<Objective> objs = quest.getObjectives();
		if(id >= objs.size() || id < 0) {
			throw new ObjectiveException(lang.get("ERROR_OBJ_NOT_EXIST"));
		}
		for(final int i : prereq) {
			objs.get(id).removePrerequisity(i);
		}
	}
	
	public void addQuestCondition(final PlayerProfile issuer, final Condition newCondition, final QuesterLang lang) throws QuesterException {
		final Quest quest = issuer.getSelected();
		modifyCheck(quest, lang);
		quest.addCondition(newCondition);
	}
	
	public void setQuestCondition(final PlayerProfile issuer, final int id, final Condition newCondition, final QuesterLang lang) throws QuesterException {
		final Quest quest = issuer.getSelected();
		modifyCheck(quest, lang);
		if(quest.getCondition(id) == null) {
			throw new ConditionException(lang.get("ERROR_CON_NOT_EXIST"));
		}
		quest.setCondition(id, newCondition);
	}
	
	public void removeQuestCondition(final PlayerProfile issuer, final int id, final QuesterLang lang) throws QuesterException {
		final Quest quest = issuer.getSelected();
		modifyCheck(quest, lang);
		if(!quest.removeCondition(id)) {
			throw new ConditionException(lang.get("ERROR_CON_NOT_EXIST"));
		}
	}
	
	public void addConditionDescription(final PlayerProfile issuer, final int id, final String desc, final QuesterLang lang) throws QuesterException {
		final Quest quest = issuer.getSelected();
		modifyCheck(quest, lang);
		final List<Condition> cons = quest.getConditions();
		if(id >= cons.size() || id < 0) {
			throw new ConditionException(lang.get("ERROR_CON_NOT_EXIST"));
		}
		cons.get(id).addDescription(desc);
	}
	
	public void removeConditionDescription(final PlayerProfile issuer, final int id, final QuesterLang lang) throws QuesterException {
		final Quest quest = issuer.getSelected();
		modifyCheck(quest, lang);
		final List<Condition> cons = quest.getConditions();
		if(id >= cons.size() || id < 0) {
			throw new ConditionException(lang.get("ERROR_CON_NOT_EXIST"));
		}
		cons.get(id).removeDescription();
	}
	
	public void addQuestQevent(final PlayerProfile issuer, final Qevent newQevent, final QuesterLang lang) throws QuesterException {
		final Quest quest = issuer.getSelected();
		modifyCheck(quest, lang);
		final int occasion = newQevent.getOccasion();
		if(occasion < -3 || occasion >= quest.getObjectives().size()) {
			throw new ConditionException(lang.get("ERROR_OCC_NOT_EXIST"));
		}
		quest.addQevent(newQevent);
	}
	
	public void setQuestQevent(final PlayerProfile issuer, final int qeventID, final Qevent newQevent, final QuesterLang lang) throws QuesterException {
		final Quest quest = issuer.getSelected();
		modifyCheck(quest, lang);
		final int occasion = newQevent.getOccasion();
		if(occasion < -3 || occasion >= quest.getObjectives().size()) {
			throw new ConditionException(lang.get("ERROR_OCC_NOT_EXIST"));
		}
		quest.setQevent(qeventID, newQevent);
	}
	
	public void removeQuestQevent(final PlayerProfile issuer, final int id, final QuesterLang lang) throws QuesterException {
		final Quest quest = issuer.getSelected();
		modifyCheck(quest, lang);
		if(!quest.removeQevent(id)) {
			throw new QeventException(lang.get("ERROR_EVT_NOT_EXIST"));
		}
	}
	
	public void addQuestTrigger(final PlayerProfile issuer, final Trigger newTrigger, final QuesterLang lang) throws QuesterException {
		final Quest quest = issuer.getSelected();
		modifyCheck(quest, lang);
		quest.addTrigger(newTrigger);
		if(newTrigger.isGlobal()) {
			final int id = quest.getTriggers().size() - 1;
			for(final Objective o : quest.getObjectives()) {
				o.addTrigger(id);
			}
		}
	}
	
	public void setQuestTrigger(final PlayerProfile issuer, final int triggerID, final Trigger newTrigger, final QuesterLang lang) throws QuesterException {
		final Quest quest = issuer.getSelected();
		modifyCheck(quest, lang);
		quest.setTrigger(triggerID, newTrigger);
		if(newTrigger.isGlobal()) {
			for(final Objective o : quest.getObjectives()) {
				o.addTrigger(triggerID);
			}
		}
	}
	
	public void removeQuestTrigger(final PlayerProfile issuer, final int id, final QuesterLang lang) throws QuesterException {
		final Quest quest = issuer.getSelected();
		modifyCheck(quest, lang);
		final Trigger t = quest.removeTrigger(id);
		if(t == null) {
			throw new QeventException(lang.get("ERROR_TRIG_NOT_EXIST"));
		}
		else {
			if(t.isGlobal()) {
				for(final Objective o : quest.getObjectives()) {
					o.removeTrigger(id);
				}
			}
		}
	}
	
	public boolean areConditionsMet(final Player player, final String questName, final QuesterLang lang) throws QuesterException {
		return areConditionsMet(player, getQuest(questName), lang);
	}
	
	public boolean areConditionsMet(final Player player, final Quest quest, final QuesterLang lang) throws QuesterException {
		if(quest == null) {
			throw new QuestException(lang.get("ERROR_Q_NOT_EXIST"));
		}
		for(final Condition c : quest.getConditions()) {
			if(!c.isMet(player)) {
				return false;
			}
		}
		
		return true;
	}
	
	public void saveQuests() {
		for(final StorageKey subKey : questStorage.getKey("").getSubKeys()) {
			subKey.removeKey("");
		}
		for(final Quest q : quests.values()) {
			final StorageKey key = questStorage.getKey(String.valueOf(q.getID()));
			q.serialize(key);
		}
		questStorage.save();
	}
	
	public boolean loadQuests() {
		return loadQuests(null);
	}
	
	public boolean loadQuests(final String fileName) {
		Storage storage = questStorage;
		if(fileName != null) {
			final File storageFile = new File(dataFolder, fileName);
			if(storageFile.exists()) {
				storage = new ConfigStorage(storageFile, logger, null);
			}
			else {
				return false;
			}
		}
		if(!storage.load()) {
			return false;
		}
		
		if(fileName == null) {
			quests.clear();
			questNames.clear();
			questLocations.clear();
		}
		final List<Quest> onHold = new ArrayList<>();
		int lastGeneric = 0;
		boolean errorHappened = false;
		for(final StorageKey questKey : storage.getKey("").getSubKeys()) {
			if(questKey.hasSubKeys()) {
				Ql.debug("Deserializing quest " + questKey.getName() + ".");
				final Quest quest = Quest.deserialize(questKey);
				if(quest == null) {
					Ql.severe("Quest " + questKey.getName() + " is corrupted.");
					errorHappened = true;
					continue;
				}

				if(questNames.containsKey(quest.getName().toLowerCase())) { // duplicate name, generating new one
					quest.setName("");
					String name = "";
					while(quest.getName().isEmpty() || questNames.containsKey(quest.getName())) {
						name = "generic" + lastGeneric;
						quest.setName(name);
						lastGeneric++;
					}
					Ql.severe("Duplicate quest name in quest " + questKey.getName()
							+ " detected, generated new name '" + name + "'.");
					errorHappened = true;
				}

				checkPrerequisites(quest);
				if(quest.hasID()) {
					if(quests.get(quest.getID()) != null) { // duplicate ID
						Ql.severe("Duplicate quest ID in quest " + questKey.getName()
								+ " detected, new ID will be assigned.");
						quest.setID(-1);
						onHold.add(quest);
						errorHappened = true;
					}
					else { // everything all right
						quests.put(quest.getID(), quest);
						questNames.put(quest.getName().toLowerCase(), quest.getID());
						if(quest.hasLocation()) {
							questLocations.put(quest.getID(), quest.getLocation());
						}
					}
				}
				else { // quest has default ID, new needs to be generated
					onHold.add(quest);
				}

				if(quest.error) {
					errorHappened = true;
					quest.error = false;
				}
			}
		}
		adjustQuestID(); // get ID ready
		for(final Quest q : onHold) {
			assignQuestID(q);
			quests.put(q.getID(), q);
			questNames.put(q.getName().toLowerCase(), q.getID());
			if(q.hasLocation()) {
				questLocations.put(q.getID(), q.getLocation());
			}
		}

		Ql.verbose(quests.size() + " quests loaded.");

		if(QConfiguration.autoBackup && errorHappened) {
			try {
				final File backupDir = new File(dataFolder + File.separator + "backups");
				if(!backupDir.isDirectory()) {
					backupDir.mkdir();
				}
				final String date = new SimpleDateFormat("yy-MM-dd--HH-mm-ss").format(new Date());
				final File f = new File(backupDir, "quests-" + date + ".yml");
				f.createNewFile();
				((ConfigStorage)storage).saveToFile(f);
				Ql.info("Found errors in quests.yml, backup created. Backup name: " + f.getName());
			}
			catch(final Exception e) {
				Ql.severe("Failed to create quests backup.", e);
			}
		}
		return true;
	}

	private void checkPrerequisites(Quest quest) {
		List<Objective> objectives = quest.getObjectives();
		for(int o = 0; o<objectives.size(); o++) {
			for(int p : objectives.get(o).getPrerequisites()) {
				if(p < 0 || p >= objectives.size()) {
					Ql.warning("Invalid prerequisite " + p + " in objective " + o + " in quest " + quest.getName() + ".");
				}
			}
		}
	}
}
