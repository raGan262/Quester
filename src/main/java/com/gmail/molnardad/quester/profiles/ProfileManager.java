package com.gmail.molnardad.quester.profiles;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.management.InstanceNotFoundException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.ActionSource;
import com.gmail.molnardad.quester.QConfiguration;
import com.gmail.molnardad.quester.QConfiguration.StorageType;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.elements.Condition;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.elements.Qevent;
import com.gmail.molnardad.quester.events.ObjectiveCompleteEvent;
import com.gmail.molnardad.quester.events.QuestCancelEvent;
import com.gmail.molnardad.quester.events.QuestCompleteEvent;
import com.gmail.molnardad.quester.events.QuestStartEvent;
import com.gmail.molnardad.quester.exceptions.CustomException;
import com.gmail.molnardad.quester.exceptions.ObjectiveException;
import com.gmail.molnardad.quester.exceptions.QuestException;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.lang.LanguageManager;
import com.gmail.molnardad.quester.lang.QuesterLang;
import com.gmail.molnardad.quester.profiles.PlayerProfile.SerializedPlayerProfile;
import com.gmail.molnardad.quester.profiles.QuestProgress.ObjectiveStatus;
import com.gmail.molnardad.quester.quests.Quest;
import com.gmail.molnardad.quester.quests.QuestFlag;
import com.gmail.molnardad.quester.quests.QuestManager;
import com.gmail.molnardad.quester.storage.ConfigStorage;
import com.gmail.molnardad.quester.storage.Storage;
import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.utils.CaseAgnosticSet;
import com.gmail.molnardad.quester.utils.DatabaseConnection;
import com.gmail.molnardad.quester.utils.Util;

public class ProfileManager {
	
	private Storage profileStorage = null;
	private QuestManager qMan = null;
	private LanguageManager langMan = null;
	private Quester plugin = null;
	private final Random randGen = new Random();
	
	private final Map<String, PlayerProfile> profiles = new HashMap<String, PlayerProfile>();
	private Map<Integer, String> ranks = new HashMap<Integer, String>();
	private List<Integer> sortedRanks = new ArrayList<Integer>();
	
	public ProfileManager(final Quester plugin) {
		this.plugin = plugin;
		qMan = plugin.getQuestManager();
		langMan = plugin.getLanguageManager();
		final File file = new File(plugin.getDataFolder(), "profiles.yml");
		profileStorage = new ConfigStorage(file, Quester.log, null);
	}
	
	private PlayerProfile createProfile(final String playerName) {
		final PlayerProfile prof = new PlayerProfile(playerName);
		final Player player = Bukkit.getPlayerExact(playerName);
		if(player == null || !Util.isPlayer(player)) {
			Quester.log.warning("Smeone/Something tried to get profile of a non-player.");
			new CustomException("Contact Quester author and show him this exception.")
					.printStackTrace();
		}
		else {
			profiles.put(playerName.toLowerCase(), prof);
		}
		return prof;
	}
	
	private void updateRank(final PlayerProfile prof) {
		final int pts = prof.getPoints();
		String lastRank = "";
		for(final int i : sortedRanks) {
			if(pts >= i) {
				lastRank = ranks.get(i);
			}
			else {
				break;
			}
		}
		prof.setRank(lastRank);
	}
	
	public PlayerProfile[] getProfiles() {
		return profiles.values().toArray(new PlayerProfile[0]);
	}
	
	public PlayerProfile getProfile(final String playerName) {
		if(playerName == null) {
			return null;
		}
		PlayerProfile prof = profiles.get(playerName.toLowerCase());
		if(prof == null) {
			prof = createProfile(playerName);
			prof.setChanged();
		}
		return prof;
	}
	
	public boolean hasProfile(final String playerName) {
		return profiles.containsKey(playerName.toLowerCase());
	}
	
	public Map<Integer, String> getRanks() {
		return ranks;
	}
	
	public Quest getSelectedQuest(final String playerName) {
		if(playerName == null) {
			return null;
		}
		return getProfile(playerName).getSelected();
	}
	
	public boolean setProgress(final String playerName, final int objective, final int value) {
		return setProgress(playerName, getProfile(playerName).getQuestProgressIndex(), objective,
				value);
	}
	
	public boolean setProgress(final String playerName, final int index, final int objective, final int value) {
		final PlayerProfile prof = getProfile(playerName);
		final QuestProgress prog = prof.getProgress(index);
		if(prog != null) {
			prog.setProgress(objective, value);
			prof.setChanged();
			return true;
		}
		return false;
	}
	
	public void assignQuest(final String playerName, final Quest quest) {
		getProfile(playerName).addQuest(quest);
	}
	
	public void unassignQuest(final String playerName) {
		unassignQuest(playerName, -1);
	}
	
	public void unassignQuest(final String playerName, final int index) {
		final PlayerProfile prof = getProfile(playerName);
		if(index < 0) {
			prof.unsetQuest();
		}
		else {
			prof.unsetQuest(index);
		}
		prof.refreshActive();
	}
	
	public void addCompletedQuest(final String playerName, final String questName) {
		final PlayerProfile prof = getProfile(playerName);
		prof.addCompleted(questName, (int) (System.currentTimeMillis() / 1000));
	}
	
	public void selectQuest(final String changer, final Quest newSelected) throws QuesterException {
		getProfile(changer).setSelected(newSelected);
	}
	
	public void clearSelectedQuest(final String playerName) {
		getProfile(playerName).setSelected(null);
	}
	
	public void selectHolder(final String changer, final int id) throws QuesterException {
		getProfile(changer).setHolderID(id);
	}
	
	public void clearSelectedHolder(final String playerName) {
		getProfile(playerName).setHolderID(-1);
	}
	
	public boolean switchQuest(final String playerName, final int id) {
		return getProfile(playerName).setActiveQuest(id);
	}
	
	public int addPoints(final String playerName, final int amount) {
		return getProfile(playerName).addPoints(amount);
	}
	
	public boolean areObjectivesCompleted(final Player player) {
		
		for(final ObjectiveStatus status : getProfile(player.getName()).getProgress()
				.getObjectiveStatuses()) {
			if(status != ObjectiveStatus.COMPLETED) {
				return false;
			}
		}
		return true;
	}
	
	public int getCurrentObjective(final Player player) {
		
		return getProfile(player.getName()).getProgress().getCurrentObjectiveID();
	}
	
	public boolean isObjectiveActive(final Player player, final int id) {
		
		final QuestProgress progress = getProfile(player.getName()).getProgress();
		return progress.getObjectiveStatus(id) == ObjectiveStatus.ACTIVE;
	}
	
	// QUEST PROGRESS METHODS
	
	public void startQuest(final Player player, final int questID, final ActionSource as, final QuesterLang lang) throws QuesterException {
		final Quest qst = qMan.getQuest(questID);
		startQuest(player, qst, as, lang);
	}
	
	public void startQuest(final Player player, final String questName, final ActionSource as, final QuesterLang lang) throws QuesterException {
		final Quest qst = qMan.getQuest(questName);
		startQuest(player, qst, as, lang);
	}
	
	public void startQuest(final Player player, final Quest quest, final ActionSource as, final QuesterLang lang) throws QuesterException {
		final String playerName = player.getName();
		if(quest == null) {
			throw new QuestException(lang.ERROR_Q_NOT_EXIST);
		}
		final PlayerProfile prof = getProfile(playerName);
		if(prof.hasQuest(quest)) {
			throw new QuestException(lang.ERROR_Q_ASSIGNED);
		}
		if(prof.getQuestAmount() >= QConfiguration.maxQuests) {
			throw new QuestException(lang.ERROR_Q_MAX_AMOUNT);
		}
		if(!quest.hasFlag(QuestFlag.ACTIVE)) {
			throw new QuestException(lang.ERROR_Q_NOT_EXIST);
		}
		if(as.is(ActionSource.COMMAND) && quest.hasFlag(QuestFlag.HIDDEN)) {
			throw new QuestException(lang.ERROR_Q_NOT_CMD);
		}
		if(!Util.permCheck(player, QConfiguration.PERM_ADMIN, false, null)) {
			for(final Condition con : quest.getConditions()) {
				if(!con.isMet(player, plugin)) {
					player.sendMessage(ChatColor.RED + con.inShow());
					return;
				}
			}
		}
		/* QuestStartEvent */
		final QuestStartEvent event = new QuestStartEvent(as, player, quest);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if(event.isCancelled()) {
			if(QConfiguration.verbose) {
				Quester.log.info("QuestStart event cancelled. (" + player.getName() + "; '"
						+ quest.getName() + "')");
			}
			return;
		}
		
		assignQuest(playerName, quest);
		if(QConfiguration.progMsgStart) {
			player.sendMessage(Quester.LABEL
					+ lang.MSG_Q_STARTED.replaceAll("%q", ChatColor.GOLD + quest.getName()
							+ ChatColor.BLUE));
		}
		final String description = quest.getDescription(playerName);
		if(!description.isEmpty() && !quest.hasFlag(QuestFlag.NODESC)) {
			player.sendMessage(description);
		}
		if(QConfiguration.verbose) {
			Quester.log.info(playerName + " started quest '" + quest.getName() + "'.");
		}
		for(final Qevent qv : quest.getQevents()) {
			if(qv.getOccasion() == -1) {
				qv.execute(player, plugin);
			}
		}
	}
	
	public void startRandomQuest(final Player player, final ActionSource as, final QuesterLang lang) throws QuesterException {
		Collection<Quest> allQuests = qMan.getQuests();
		final ArrayList<Quest> chosenQuests = new ArrayList<Quest>();
		for(final Quest quest : allQuests) {
			if(quest.hasFlag(QuestFlag.ACTIVE) && !quest.hasFlag(QuestFlag.HIDDEN)
					&& !getProfile(player.getName()).hasQuest(quest)
					&& qMan.areConditionsMet(player, quest, lang)) {
				chosenQuests.add(quest);
			}
		}
		allQuests = null;
		if(chosenQuests.isEmpty()) {
			throw new QuestException(lang.ERROR_Q_NONE_ACTIVE);
		}
		final int id = randGen.nextInt(chosenQuests.size());
		startQuest(player, chosenQuests.get(id).getName(), as, lang);
	}
	
	public void cancelQuest(final Player player, final ActionSource as, final QuesterLang lang) throws QuesterException {
		cancelQuest(player, -1, as, lang);
	}
	
	public void cancelQuest(final Player player, final int index, ActionSource as, final QuesterLang lang) throws QuesterException {
		Quest quest = null;
		final PlayerProfile prof = getProfile(player.getName());
		if(index < 0) {
			if(prof.getProgress() != null) {
				quest = prof.getProgress().getQuest();
			}
		}
		else {
			if(prof.getProgress(index) != null) {
				quest = prof.getProgress(index).getQuest();
			}
		}
		if(quest == null) {
			throw new QuestException(lang.ERROR_Q_NOT_ASSIGNED);
		}
		if(as == null) {
			as = ActionSource.BLANKSOURCE;
		}
		if(as.is(ActionSource.COMMAND) && quest.hasFlag(QuestFlag.UNCANCELLABLE)) {
			throw new QuestException(lang.ERROR_Q_CANT_CANCEL);
		}
		/* QuestCancelEvent */
		final QuestCancelEvent event = new QuestCancelEvent(as, player, quest);
		Bukkit.getServer().getPluginManager().callEvent(event);
		
		unassignQuest(player.getName(), index);
		if(QConfiguration.progMsgCancel) {
			player.sendMessage(Quester.LABEL
					+ lang.MSG_Q_CANCELLED.replaceAll("%q", ChatColor.GOLD + quest.getName()
							+ ChatColor.BLUE));
		}
		if(QConfiguration.verbose) {
			Quester.log.info(player.getName() + " cancelled quest '" + quest.getName() + "'.");
		}
		for(final Qevent qv : quest.getQevents()) {
			if(qv.getOccasion() == -2) {
				qv.execute(player, plugin);
			}
		}
	}
	
	public void complete(final Player player, final ActionSource as, final QuesterLang lang) throws QuesterException {
		complete(player, as, lang, true);
	}
	
	public void complete(final Player player, ActionSource as, final QuesterLang lang, final boolean checkObjs) throws QuesterException {
		final Quest quest = getProfile(player.getName()).getQuest();
		if(quest == null) {
			throw new QuestException(lang.ERROR_Q_NOT_ASSIGNED);
		}
		if(as == null) {
			as = ActionSource.BLANKSOURCE;
		}
		
		if(as.is(ActionSource.COMMAND) && quest.hasFlag(QuestFlag.HIDDEN)) {
			throw new QuestException(lang.ERROR_Q_NOT_CMD);
		}
		
		if(!quest.allowedWorld(player.getWorld().getName())) {
			throw new QuestException(lang.ERROR_Q_BAD_WORLD);
		}
		
		boolean error = false;
		if(checkObjs) {
			error = !completeObjective(player, as, lang);
		}
		
		if(areObjectivesCompleted(player)) {
			completeQuest(player, as, lang);
		}
		else if(error) {
			throw new ObjectiveException(lang.ERROR_OBJ_CANT_DO);
		}
	}
	
	private boolean completeObjective(final Player player, final ActionSource as, final QuesterLang lang) throws QuesterException {
		final Quest quest = getProfile(player.getName()).getQuest();
		final List<Objective> objs = quest.getObjectives();
		
		int i = 0;
		boolean completed = false;
		while(i < objs.size() && !completed) {
			if(isObjectiveActive(player, i)) {
				if(objs.get(i).tryToComplete(player)) {
					incProgress(player, as, i, false);
					completed = true;
				}
			}
			i++;
		}
		
		return completed || i == 0;
	}
	
	public void completeQuest(final Player player, final ActionSource as, final QuesterLang lang) throws QuesterException {
		final Quest quest = getProfile(player.getName()).getQuest();
		
		unassignQuest(player.getName());
		addCompletedQuest(player.getName(), quest.getName());
		if(QConfiguration.progMsgDone) {
			player.sendMessage(Quester.LABEL
					+ lang.MSG_Q_COMPLETED.replaceAll("%q", ChatColor.GOLD + quest.getName()
							+ ChatColor.BLUE));
		}
		if(QConfiguration.verbose) {
			Quester.log.info(player.getName() + " completed quest '" + quest.getName() + "'.");
		}
		/* QuestCompleteEvent */
		final QuestCompleteEvent event = new QuestCompleteEvent(as, player, quest);
		Bukkit.getServer().getPluginManager().callEvent(event);
		
		for(final Qevent qv : quest.getQevents()) {
			if(qv.getOccasion() == -3) {
				qv.execute(player, plugin);
			}
		}
		if(quest.hasFlag(QuestFlag.ONLYFIRST)) {
			qMan.deactivateQuest(quest);
		}
	}
	
	public void incProgress(final Player player, final ActionSource as, final int objectiveId) {
		incProgress(player, as, objectiveId, 1, true);
	}
	
	public void incProgress(final Player player, final ActionSource as, final int objectiveId, final boolean checkAll) {
		incProgress(player, as, objectiveId, 1, checkAll);
	}
	
	public void incProgress(final Player player, final ActionSource as, final int objectiveId, final int amount) {
		incProgress(player, as, objectiveId, amount, true);
	}
	
	public void incProgress(final Player player, final ActionSource as, final int objectiveId, final int amount, final boolean checkAll) {
		final QuesterLang lang = langMan.getPlayerLang(player.getName());
		final PlayerProfile prof = getProfile(player.getName());
		final QuestProgress prog = prof.getProgress();
		if(prog == null || objectiveId < 0 || objectiveId >= prog.getSize()) {
			return;
		}
		final int newValue = prog.getProgress()[objectiveId] + amount;
		final Quest q = prof.getQuest();
		final Objective obj = q.getObjectives().get(objectiveId);
		setProgress(prof.getName(), objectiveId, newValue);
		
		// TODO add progress update message
		if(obj.getTargetAmount() <= newValue) {
			if(QConfiguration.progMsgObj && !obj.isHidden()) {
				player.sendMessage(Quester.LABEL + lang.MSG_OBJ_COMPLETED);
			}
			/* ObjectiveCompleteEvent */
			final ObjectiveCompleteEvent event =
					new ObjectiveCompleteEvent(as, player, q, objectiveId);
			Bukkit.getServer().getPluginManager().callEvent(event);
			
			for(final Qevent qv : q.getQevents()) {
				if(qv.getOccasion() == objectiveId) {
					qv.execute(player, plugin);
				}
			}
			if(checkAll) {
				try {
					complete(player, as, lang, false);
				}
				catch (final QuesterException ignore) {}
			}
		}
	}
	
	public String[] validateProgress(final PlayerProfile profile) {
		final QuestProgress[] progs = profile.getProgresses();
		boolean localUnset = false;
		final List<String> unset = new ArrayList<String>(QConfiguration.maxQuests);
		for(int i = progs.length - 1; i >= 0; i--) {
			localUnset = false;
			final Quest quest = progs[i].getQuest();
			if(!quest.equals(qMan.getQuest(quest.getID()))		// if ID or name
					// changed
					|| progs[i].getSize() != quest.getObjectives().size()) {	// if
																				// number
																				// of
																				// objectives
																				// changed
				localUnset = true;
			}
			
			if(localUnset) {
				profile.unsetQuest(i);
				unset.add(quest.getName());
			}
		}
		return unset.toArray(new String[0]);
	}
	
	// DISPLAY METHODS
	
	public void showProfile(final CommandSender sender) {
		showProfile(sender, sender.getName(), langMan.getPlayerLang(sender.getName()));
	}
	
	public void showProfile(final CommandSender sender, final String name, final QuesterLang lang) {
		if(!hasProfile(name)) {
			sender.sendMessage(ChatColor.RED + lang.INFO_PROFILE_NOT_EXIST.replaceAll("%p", name));
			return;
		}
		final PlayerProfile prof = getProfile(name);
		sender.sendMessage(ChatColor.BLUE + lang.INFO_NAME + ": " + ChatColor.GOLD + prof.getName());
		sender.sendMessage(ChatColor.BLUE + lang.INFO_PROFILE_POINTS + ": " + ChatColor.WHITE
				+ prof.getPoints());
		if(QConfiguration.useRank) {
			sender.sendMessage(ChatColor.BLUE + lang.INFO_PROFILE_RANK + ": " + ChatColor.GOLD
					+ prof.getRank());
		}
		sender.sendMessage(ChatColor.BLUE + lang.INFO_PROFILE_COMPLETED + ": " + ChatColor.WHITE
				+ Util.implode(prof.getCompletedQuests(), ','));
		
	}
	
	public void showProgress(final Player player, final QuesterLang lang) throws QuesterException {
		showProgress(player, -1, lang);
	}
	
	public void showProgress(final Player player, final int index, final QuesterLang lang) throws QuesterException {
		Quest quest = null;
		QuestProgress progress = null;
		final PlayerProfile prof = getProfile(player.getName());
		if(index < 0) {
			progress = prof.getProgress();
		}
		else {
			progress = prof.getProgress(index);
		}
		if(progress == null) {
			throw new QuestException(lang.ERROR_Q_NOT_ASSIGNED);
		}
		quest = progress.getQuest();
		
		if(!quest.hasFlag(QuestFlag.HIDDENOBJS)) {
			player.sendMessage(lang.INFO_PROGRESS.replaceAll("%q", ChatColor.GOLD + quest.getName()
					+ ChatColor.BLUE));
			final List<Objective> objs = quest.getObjectives();
			for(int i = 0; i < objs.size(); i++) {
				if(!objs.get(i).isHidden()) {
					if(progress.getObjectiveStatus(i) == ObjectiveStatus.COMPLETED) {
						player.sendMessage(ChatColor.GREEN + " - " + lang.INFO_PROGRESS_COMPLETED);
					}
					else {
						final boolean active =
								progress.getObjectiveStatus(i) == ObjectiveStatus.ACTIVE;
						if(active || !QConfiguration.ordOnlyCurrent) {
							final ChatColor col = active ? ChatColor.YELLOW : ChatColor.RED;
							player.sendMessage(col + " - "
									+ objs.get(i).inShow(progress.getProgress()[i]));
						}
					}
				}
			}
		}
		else {
			player.sendMessage(Quester.LABEL + lang.INFO_PROGRESS_HIDDEN);
		}
	}
	
	public void showTakenQuests(final CommandSender sender) {
		showTakenQuests(sender, sender.getName(), langMan.getPlayerLang(sender.getName()));
	}
	
	public void showTakenQuests(final CommandSender sender, final String name, final QuesterLang lang) {
		if(!hasProfile(name)) {
			sender.sendMessage(ChatColor.RED + lang.INFO_PROFILE_NOT_EXIST.replaceAll("%p", name));
			return;
		}
		final PlayerProfile prof = getProfile(name);
		sender.sendMessage(ChatColor.BLUE
				+ (sender.getName().equalsIgnoreCase(name) ? lang.INFO_QUESTS + ": "
						: lang.INFO_QUESTS_OTHER.replaceAll("%p", prof.getName()) + ": ") + "("
				+ lang.INFO_LIMIT + ": " + QConfiguration.maxQuests + ")");
		final int current = prof.getQuestProgressIndex();
		for(int i = 0; i < prof.getQuestAmount(); i++) {
			sender.sendMessage("[" + i + "] " + (current == i ? ChatColor.GREEN : ChatColor.YELLOW)
					+ prof.getProgress(i).getQuest().getName());
		}
		
	}
	
	// STORAGE
	
	public void loadRanks() {
		final Map<Integer, String> rankMap = new HashMap<Integer, String>();
		final List<Integer> sortedList = new ArrayList<Integer>();
		
		StorageKey rankKey = null;
		try {
			rankKey = QConfiguration.getConfigKey("ranks");
		}
		catch (final InstanceNotFoundException e) {
			Quester.log.severe("DataManager instance exception occured while acessing ranks.");
		}
		if(rankKey != null) {
			for(final StorageKey subKey : rankKey.getSubKeys()) {
				rankMap.put(subKey.getInt(""), subKey.getName().replace('-', ' '));
				sortedRanks.add(subKey.getInt(""));
			}
		}
		if(sortedRanks.size() == 0) {
			rankKey.setInt("Default-Rank", 0);
			rankMap.put(0, "Default-Rank");
			sortedList.add(0);
			Quester.log.info("No ranks found. Added default rank.");
			try {
				QConfiguration.saveData();
			}
			catch (final InstanceNotFoundException ignore) {}
		}
		Collections.sort(sortedList);
		ranks = rankMap;
		sortedRanks = sortedList;
	}
	
	void loadProfile(final PlayerProfile prof) {
		updateRank(prof);
		profiles.put(prof.getName().toLowerCase(), prof);
	}
	
	public void loadProfiles() {
		loadProfiles(QConfiguration.profileStorageType, true);
	}
	
	public void loadProfiles(final StorageType loadFrom, final boolean async) {
		switch(loadFrom) {
			case MYSQL: {
				loadFromDatabase(async);
				break;
			}
			default: {
				profileStorage.load();
				final StorageKey mainKey = profileStorage.getKey("");
				PlayerProfile prof;
				for(final StorageKey subKey : mainKey.getSubKeys()) {
					prof = PlayerProfile.deserialize(subKey, qMan);
					if(prof != null) {
						loadProfile(prof);
					}
					else {
						Quester.log.info("Invalid key in profiles.yml: " + subKey.getName());
					}
				}
				if(QConfiguration.verbose) {
					Quester.log.info(profiles.size() + " profiles loaded.");
				}
			}
		}
	}
	
	private void loadFromDatabase(final boolean async) {
		final Runnable loadTask = new Runnable() {
			
			@Override
			public void run() {
				Connection conn = null;
				PreparedStatement stmt = null;
				ResultSet rs = null;
				try {
					if(QConfiguration.debug) {
						Quester.log.info("Loading profiles...");
					}
					final List<SerializedPlayerProfile> serps =
							new ArrayList<PlayerProfile.SerializedPlayerProfile>();
					conn = DatabaseConnection.getConnection();
					stmt = conn.prepareStatement("SELECT * FROM `quester-profiles`");
					rs = stmt.executeQuery();
					while(rs.next()) {
						try {
							serps.add(new SerializedPlayerProfile(rs));
						}
						catch (final SQLException ignore) {}
					}
					rs.close();
					stmt.close();
					if(QConfiguration.debug) {
						Quester.log.info("Loaded " + serps.size() + " profiles.");
					}
					final Runnable deserialization = new Runnable() {
						
						@Override
						public void run() {
							int count = 0;
							for(final SerializedPlayerProfile sp : serps) {
								final PlayerProfile prof =
										PlayerProfile.deserialize(sp.getStoragekey(), qMan);
								if(prof != null) {
									updateRank(prof);
									profiles.put(prof.getName().toLowerCase(), prof);
									count++;
								}
								else {
									Quester.log.info("Invalid profile '" + sp.name + "'");
								}
							}
							if(QConfiguration.debug) {
								Quester.log.info("Deserialized " + count + " profiles.");
							}
						}
					};
					
					if(async) {
						Bukkit.getScheduler().runTask(plugin, deserialization);
					}
					else {
						deserialization.run();
					}
				}
				catch (final SQLException e) {
					e.printStackTrace();
				}
				finally {
					if(conn != null) {
						try {
							conn.close();
						}
						catch (final SQLException ignore) {}
					}
					if(stmt != null) {
						try {
							stmt.close();
						}
						catch (final SQLException ignore) {}
					}
					if(rs != null) {
						try {
							rs.close();
						}
						catch (final SQLException ignore) {}
					}
				}
			}
		};
		
		if(async) {
			new Thread(loadTask, "Quester Profile Loading Thread").start();
		}
		else {
			loadTask.run();
		}
	}
	
	public void saveProfiles() {
		saveProfiles(QConfiguration.profileStorageType, true);
	}
	
	public void saveProfiles(final StorageType storeTo, final boolean async) {
		switch(storeTo) {
			case MYSQL: {
				saveToDatabase(async);
				break;
			}
			default: {
				final StorageKey pKey = profileStorage.getKey("");
				for(final String p : profiles.keySet()) {
					profiles.get(p).serialize(pKey.getSubKey(p));
				}
				profileStorage.save();
			}
		}
	}
	
	private void saveToDatabase(final boolean async) {
		final List<SerializedPlayerProfile> serps =
				new ArrayList<PlayerProfile.SerializedPlayerProfile>();
		
		for(final PlayerProfile prof : profiles.values()) {
			serps.add(new PlayerProfile.SerializedPlayerProfile(prof));
			prof.setUnchanged();
		}
		
		final Runnable saveTask = new Runnable() {
			
			@Override
			public void run() {
				Connection conn = null;
				PreparedStatement stmt = null;
				ResultSet rs = null;
				try {
					final CaseAgnosticSet stored = new CaseAgnosticSet();
					conn = DatabaseConnection.getConnection();
					stmt = conn.prepareStatement("SELECT `name` FROM `quester-profiles`");
					rs = stmt.executeQuery();
					int saved = 0;
					while(rs.next()) {
						stored.add(rs.getString("name"));
					}
					rs.close();
					stmt.close();
					for(final SerializedPlayerProfile sp : serps) {
						final boolean isStored = stored.contains(sp.name);
						try {
							if(!isStored || sp.changed) { // only save if it has changed, or is not stored
								stmt =
										conn.prepareStatement(isStored ? sp
												.getUpdateQuerry("quester-profiles") : sp
												.getInsertQuerry("quester-profiles"));
								stmt.execute();
								stmt.close();
								saved++;
							}
						}
						catch (final SQLException e) {
							System.out.println("Failed to save profile " + sp.name);
							if(QConfiguration.debug) {
								e.printStackTrace();
							}
						}
						finally {
							if(stmt != null) {
								try {
									stmt.close();
								}
								catch (final SQLException ignore) {}
							}
						}
					}
					if(QConfiguration.verbose) {
						System.out.println(saved + " profiles saved.");
					}
				}
				catch (final SQLException e) {
					e.printStackTrace();
				}
				finally {
					if(conn != null) {
						try {
							conn.close();
						}
						catch (final SQLException ignore) {}
					}
					if(stmt != null) {
						try {
							stmt.close();
						}
						catch (final SQLException ignore) {}
					}
					if(rs != null) {
						try {
							rs.close();
						}
						catch (final SQLException ignore) {}
					}
				}
			}
		};
		
		if(async) {
			new Thread(saveTask, "Quester Profile Saving Thread").start();
		}
		else {
			saveTask.run();
		}
	}
}
