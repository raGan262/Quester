package me.ragan262.quester.profiles;

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

import me.ragan262.quester.ActionSource;
import me.ragan262.quester.QConfiguration;
import me.ragan262.quester.Quester;
import me.ragan262.quester.QConfiguration.StorageType;
import me.ragan262.quester.elements.Condition;
import me.ragan262.quester.elements.Objective;
import me.ragan262.quester.elements.Qevent;
import me.ragan262.quester.events.ObjectiveCompleteEvent;
import me.ragan262.quester.events.QuestCancelEvent;
import me.ragan262.quester.events.QuestCompleteEvent;
import me.ragan262.quester.events.QuestStartEvent;
import me.ragan262.quester.exceptions.ConditionException;
import me.ragan262.quester.exceptions.CustomException;
import me.ragan262.quester.exceptions.ObjectiveException;
import me.ragan262.quester.exceptions.QuestException;
import me.ragan262.quester.exceptions.QuesterException;
import me.ragan262.quester.lang.LanguageManager;
import me.ragan262.quester.lang.QuesterLang;
import me.ragan262.quester.profiles.PlayerProfile.SerializedPlayerProfile;
import me.ragan262.quester.profiles.QuestProgress.ObjectiveStatus;
import me.ragan262.quester.quests.Quest;
import me.ragan262.quester.quests.QuestFlag;
import me.ragan262.quester.quests.QuestManager;
import me.ragan262.quester.storage.ConfigStorage;
import me.ragan262.quester.storage.Storage;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.CaseAgnosticSet;
import me.ragan262.quester.utils.DatabaseConnection;
import me.ragan262.quester.utils.Ql;
import me.ragan262.quester.utils.Util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
		profileStorage = new ConfigStorage(file, plugin.getLogger(), null);
	}
	
	private PlayerProfile createProfile(final String playerName) {
		final PlayerProfile prof = new PlayerProfile(playerName);
		final Player player = Bukkit.getPlayerExact(playerName);
		if(!playerName.equalsIgnoreCase("console") && (player == null || !Util.isPlayer(player))) {
			Ql.warning("Smeone/Something tried to get profile of a non-player.");
			Ql.debug("Contact Quester author and show him this exception.", new CustomException(
					"player name: " + playerName));
		}
		else {
			profiles.put(playerName.toLowerCase(), prof);
		}
		return prof;
	}
	
	public void updateRank(final PlayerProfile prof) {
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
	
	public void updateRanks() {
		for(final PlayerProfile prof : profiles.values()) {
			updateRank(prof);
		}
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
	
	public boolean setProgress(final PlayerProfile profile, final int objective, final int value) {
		return setProgress(profile, profile.getQuestProgressIndex(), objective, value);
	}
	
	public boolean setProgress(final PlayerProfile profile, final int index, final int objective, final int value) {
		final QuestProgress prog = profile.getProgress(index);
		if(prog != null) {
			prog.setProgress(objective, value);
			profile.setChanged();
			return true;
		}
		return false;
	}
	
	public void assignQuest(final PlayerProfile profile, final Quest quest) {
		profile.addQuest(quest);
	}
	
	public void unassignQuest(final PlayerProfile profile) {
		unassignQuest(profile, -1);
	}
	
	public void unassignQuest(final PlayerProfile profile, final int index) {
		if(index < 0) {
			profile.unsetQuest();
		}
		else {
			profile.unsetQuest(index);
		}
		profile.refreshActive();
	}
	
	public void addCompletedQuest(final PlayerProfile profile, final String questName) {
		addCompletedQuest(profile, questName, System.currentTimeMillis());
	}
	
	public void addCompletedQuest(final PlayerProfile profile, final String questName, final long timeInMillis) {
		profile.addCompleted(questName, (int) (timeInMillis / 1000));
	}
	
	public void removeCompletedQuest(final PlayerProfile profile, final String questName) {
		profile.removeCompleted(questName);
	}
	
	public void selectQuest(final PlayerProfile profile, final Quest newSelected) throws QuesterException {
		profile.setSelected(newSelected);
	}
	
	public void clearSelectedQuest(final PlayerProfile profile) {
		profile.setSelected(null);
	}
	
	public void selectHolder(final PlayerProfile profile, final int id) throws QuesterException {
		profile.setHolderID(id);
	}
	
	public void clearSelectedHolder(final PlayerProfile profile) {
		profile.setHolderID(-1);
	}
	
	public boolean switchQuest(final PlayerProfile profile, final int id) {
		return profile.setActiveQuest(id);
	}
	
	public int addPoints(final PlayerProfile profile, final int amount) {
		final int result = profile.addPoints(amount);
		updateRank(profile);
		return result;
	}
	
	public boolean areObjectivesCompleted(final PlayerProfile profile) {
		
		for(final ObjectiveStatus status : profile.getProgress().getObjectiveStatuses()) {
			if(status != ObjectiveStatus.COMPLETED) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isObjectiveActive(final PlayerProfile profile, final int id) {
		
		final QuestProgress progress = profile.getProgress();
		return progress.getObjectiveStatus(id) == ObjectiveStatus.ACTIVE;
	}
	
	public boolean setProfileLanguage(final PlayerProfile profile, final String language) {
		if(langMan.setPlayerLang(profile.getName(), language)) {
			profile.setLanguage(language == null ? "" : language);
			return true;
		}
		return false;
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
	
	public void startQuest(final Player player, final Quest quest, final ActionSource as, final QuesterLang senderLang) throws QuesterException {
		startQuest(player, quest, as, senderLang, false);
	}
	
	public void startQuest(final Player player, final int questID, final ActionSource as, final QuesterLang lang, final boolean disableAdminCheck) throws QuesterException {
		final Quest qst = qMan.getQuest(questID);
		startQuest(player, qst, as, lang, disableAdminCheck);
	}
	
	public void startQuest(final Player player, final String questName, final ActionSource as, final QuesterLang lang, final boolean disableAdminCheck) throws QuesterException {
		final Quest qst = qMan.getQuest(questName);
		startQuest(player, qst, as, lang, disableAdminCheck);
	}
	
	public void startQuest(final Player player, final Quest quest, final ActionSource as, final QuesterLang senderLang, final boolean disableAdminCheck) throws QuesterException {
		final String playerName = player.getName();
		if(quest == null) {
			throw new QuestException(senderLang.get("ERROR_Q_NOT_EXIST"));
		}
		final PlayerProfile prof = getProfile(playerName);
		if(prof.hasQuest(quest)) {
			throw new QuestException(senderLang.get("ERROR_Q_ASSIGNED"));
		}
		if(prof.getQuestAmount() >= QConfiguration.maxQuests) {
			throw new QuestException(senderLang.get("ERROR_Q_MAX_AMOUNT"));
		}
		if(!quest.hasFlag(QuestFlag.ACTIVE)) {
			throw new QuestException(senderLang.get("ERROR_Q_NOT_EXIST"));
		}
		if(as.is(ActionSource.COMMAND) && quest.hasFlag(QuestFlag.HIDDEN)) {
			throw new QuestException(senderLang.get("ERROR_Q_NOT_CMD"));
		}
		if(!disableAdminCheck || !as.is(ActionSource.ADMIN)
				&& !Util.permCheck(player, QConfiguration.PERM_ADMIN, false, null)) {
			for(final Condition con : quest.getConditions()) {
				if(!con.isMet(player)) {
					throw new ConditionException(con.inShow(player, senderLang));
				}
			}
		}
		/* QuestStartEvent */
		final QuestStartEvent event = new QuestStartEvent(as, player, quest);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if(event.isCancelled()) {
			Ql.verbose("QuestStart event cancelled. (" + player.getName() + "; '" + quest.getName()
					+ "')");
			return;
		}
		
		assignQuest(prof, quest);
		final QuesterLang playerLang = langMan.getPlayerLang(playerName);
		if(QConfiguration.progMsgStart) {
			player.sendMessage(Quester.LABEL
					+ playerLang.get("MSG_Q_STARTED").replaceAll("%q",
							ChatColor.GOLD + quest.getName() + ChatColor.BLUE));
		}
		final String description = quest.getDescription(playerName, playerLang);
		if(!description.isEmpty() && !quest.hasFlag(QuestFlag.NODESC)) {
			player.sendMessage(description);
		}
		Ql.verbose(playerName + " started quest '" + quest.getName() + "'.");
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
			throw new QuestException(lang.get("ERROR_Q_NONE_ACTIVE"));
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
			throw new QuestException(lang.get("ERROR_Q_NOT_ASSIGNED"));
		}
		if(as == null) {
			as = ActionSource.BLANKSOURCE;
		}
		if(as.is(ActionSource.COMMAND) && quest.hasFlag(QuestFlag.UNCANCELLABLE)) {
			throw new QuestException(lang.get("ERROR_Q_CANT_CANCEL"));
		}
		/* QuestCancelEvent */
		final QuestCancelEvent event = new QuestCancelEvent(as, player, quest);
		Bukkit.getServer().getPluginManager().callEvent(event);
		
		unassignQuest(prof, index);
		if(QConfiguration.progMsgCancel) {
			player.sendMessage(Quester.LABEL
					+ langMan.getPlayerLang(player.getName()).get("MSG_Q_CANCELLED")
							.replaceAll("%q", ChatColor.GOLD + quest.getName() + ChatColor.BLUE));
		}
		Ql.verbose(player.getName() + "'s quest '" + quest.getName() + "' was cancelled. "
				+ "(ActionSource: " + as.getType() + ")");
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
		final PlayerProfile prof = getProfile(player.getName());
		final Quest quest = prof.getQuest();
		if(quest == null) {
			throw new QuestException(lang.get("ERROR_Q_NOT_ASSIGNED"));
		}
		if(as == null) {
			as = ActionSource.BLANKSOURCE;
		}
		
		if(as.is(ActionSource.COMMAND) && quest.hasFlag(QuestFlag.HIDDEN)) {
			throw new QuestException(lang.get("ERROR_Q_NOT_CMD"));
		}
		
		if(!quest.allowedWorld(player.getWorld().getName())) {
			throw new QuestException(lang.get("ERROR_Q_BAD_WORLD"));
		}
		
		boolean error = false;
		if(checkObjs) {
			error = !completeObjective(player, as, lang);
		}
		
		if(areObjectivesCompleted(prof)) {
			completeQuest(player, as, lang);
		}
		else if(error) {
			throw new ObjectiveException(lang.get("ERROR_OBJ_CANT_DO"));
		}
	}
	
	private boolean completeObjective(final Player player, final ActionSource as, final QuesterLang lang) throws QuesterException {
		final PlayerProfile prof = getProfile(player.getName());
		final Quest quest = prof.getQuest();
		final List<Objective> objs = quest.getObjectives();
		
		int i = 0;
		boolean completed = false;
		while(i < objs.size() && !completed) {
			if(isObjectiveActive(prof, i)) {
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
		final PlayerProfile prof = getProfile(player.getName());
		final Quest quest = prof.getQuest();
		
		unassignQuest(prof);
		addCompletedQuest(prof, quest.getName());
		if(QConfiguration.progMsgDone) {
			player.sendMessage(Quester.LABEL
					+ langMan.getPlayerLang(player.getName()).get("MSG_Q_COMPLETED")
							.replaceAll("%q", ChatColor.GOLD + quest.getName() + ChatColor.BLUE));
		}
		Ql.verbose(player.getName() + " completed quest '" + quest.getName() + "'.");
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
		setProgress(prof, objectiveId, newValue);
		
		if(obj.getTargetAmount() <= newValue) {
			if(QConfiguration.progMsgObj && !obj.isHidden()) {
				player.sendMessage(Quester.LABEL + lang.get("MSG_OBJ_COMPLETED"));
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
		else if(!obj.isHidden() && obj.shouldDisplayProgress() && QConfiguration.progMsg) {
			player.sendMessage(ChatColor.YELLOW + " - " + obj.inShow(newValue, lang));
		}
	}
	
	public String[] validateProgress(final PlayerProfile profile) {
		final QuestProgress[] progs = profile.getProgresses();
		boolean localUnset = false;
		final List<String> unset = new ArrayList<String>(QConfiguration.maxQuests);
		for(int i = progs.length - 1; i >= 0; i--) {
			localUnset = false;
			final Quest profileQuest = progs[i].getQuest();
			final Quest storedQuest = qMan.getQuest(profileQuest.getID());
			localUnset = !profileQuest.equals(storedQuest)		// if ID or name changed
					|| progs[i].getSize() != storedQuest.getObjectives().size();	// if number of objectives changed
			
			if(localUnset) {
				profile.unsetQuest(i);
				unset.add(profileQuest.getName());
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
			sender.sendMessage(ChatColor.RED
					+ lang.get("INFO_PROFILE_NOT_EXIST").replaceAll("%p", name));
			return;
		}
		final PlayerProfile prof = getProfile(name);
		sender.sendMessage(ChatColor.BLUE + lang.get("INFO_NAME") + ": " + ChatColor.GOLD
				+ prof.getName());
		sender.sendMessage(ChatColor.BLUE + lang.get("INFO_PROFILE_POINTS") + ": "
				+ ChatColor.WHITE + prof.getPoints());
		if(QConfiguration.useRank) {
			sender.sendMessage(ChatColor.BLUE + lang.get("INFO_PROFILE_RANK") + ": "
					+ ChatColor.GOLD + prof.getRank());
		}
		
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
			throw new QuestException(lang.get("ERROR_Q_NOT_ASSIGNED"));
		}
		quest = progress.getQuest();
		
		if(!quest.hasFlag(QuestFlag.HIDDENOBJS)) {
			player.sendMessage(lang.get("INFO_PROGRESS").replaceAll("%q",
					ChatColor.GOLD + quest.getName() + ChatColor.BLUE));
			final List<Objective> objs = quest.getObjectives();
			for(int i = 0; i < objs.size(); i++) {
				if(!objs.get(i).isHidden()) {
					if(progress.getObjectiveStatus(i) == ObjectiveStatus.COMPLETED) {
						player.sendMessage(ChatColor.GREEN + " - "
								+ lang.get("INFO_PROGRESS_COMPLETED"));
					}
					else {
						final boolean active =
								progress.getObjectiveStatus(i) == ObjectiveStatus.ACTIVE;
						if(active || !QConfiguration.ordOnlyCurrent) {
							final ChatColor col = active ? ChatColor.YELLOW : ChatColor.RED;
							player.sendMessage(col + " - "
									+ objs.get(i).inShow(progress.getProgress()[i], lang));
						}
					}
				}
			}
		}
		else {
			player.sendMessage(Quester.LABEL + lang.get("INFO_PROGRESS_HIDDEN"));
		}
	}
	
	public void showTakenQuests(final CommandSender sender) {
		showTakenQuests(sender, sender.getName(), langMan.getPlayerLang(sender.getName()));
	}
	
	public void showTakenQuests(final CommandSender sender, final String name, final QuesterLang lang) {
		if(!hasProfile(name)) {
			sender.sendMessage(ChatColor.RED
					+ lang.get("INFO_PROFILE_NOT_EXIST").replaceAll("%p", name));
			return;
		}
		final PlayerProfile prof = getProfile(name);
		sender.sendMessage(ChatColor.BLUE
				+ (sender.getName().equalsIgnoreCase(name) ? lang.get("INFO_QUESTS") + ": " : lang
						.get("INFO_QUESTS_OTHER").replaceAll("%p", prof.getName()) + ": ") + "("
				+ lang.get("INFO_LIMIT") + ": " + QConfiguration.maxQuests + ")");
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
			Ql.severe("DataManager instance exception occured while acessing ranks.");
		}
		if(rankKey != null) {
			for(final StorageKey subKey : rankKey.getSubKeys()) {
				rankMap.put(subKey.getInt(""), subKey.getName().replace('-', ' '));
				sortedList.add(subKey.getInt(""));
			}
		}
		if(sortedList.size() == 0) {
			rankKey.setInt("Default-Rank", 0);
			rankMap.put(0, "Default-Rank");
			sortedList.add(0);
			Ql.info("No ranks found. Added default rank.");
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
		if(!prof.getLanguage().isEmpty()) {
			if(!langMan.setPlayerLang(prof.getName(), prof.getLanguage())) {
				prof.setLanguage("");
			}
		}
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
						Ql.info("Invalid key in profiles.yml: " + subKey.getName());
					}
				}
				Ql.verbose(profiles.size() + " profiles loaded.");
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
					Ql.debug("Loading profiles...");
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
					Ql.debug("Loaded " + serps.size() + " profiles.");
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
									Ql.info("Invalid profile '" + sp.name + "'");
								}
							}
							Ql.debug("Deserialized " + count + " profiles.");
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
