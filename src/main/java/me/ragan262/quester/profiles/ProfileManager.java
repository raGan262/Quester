package me.ragan262.quester.profiles;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.management.InstanceNotFoundException;
import me.ragan262.quester.ActionSource;
import me.ragan262.quester.QConfiguration;
import me.ragan262.quester.Quester;
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
import me.ragan262.quester.exceptions.ProfileException;
import me.ragan262.quester.exceptions.QuestException;
import me.ragan262.quester.exceptions.QuesterException;
import me.ragan262.quester.lang.LanguageManager;
import me.ragan262.quester.lang.QuesterLang;
import me.ragan262.quester.profiles.QuestProgress.ObjectiveStatus;
import me.ragan262.quester.profiles.storage.ProfileStorage;
import me.ragan262.quester.profiles.storage.YamlProfileStorage;
import me.ragan262.quester.quests.Quest;
import me.ragan262.quester.quests.QuestFlag;
import me.ragan262.quester.quests.QuestManager;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.Ql;
import me.ragan262.quester.utils.Util;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ProfileManager {
	
	// unfortunately, non-player senders share one profile 
	//(CB always provides fake offline players with the same UUID)
	private static final String CONSOLE_NAME = "Quester:Console";
	private final OfflinePlayer senderPlayer;
	private final PlayerProfile senderProfile;
	
	private QuestManager qMan = null;
	private LanguageManager langMan = null;
	private Quester plugin = null;
	private final Random randGen = new Random();
	
	private final Map<UUID, PlayerProfile> profiles = new HashMap<UUID, PlayerProfile>();
	private Map<Integer, String> ranks = new HashMap<Integer, String>();
	private List<Integer> sortedRanks = new ArrayList<Integer>();
	
	private final ExecutorService storageExecutor = Executors.newSingleThreadExecutor();
	private final ProfileStorage profileStorage;
	private BukkitTask saveTask = null;
	
	public ProfileManager(final Quester plugin, final File profileFolder) {
		this.plugin = plugin;
		qMan = plugin.getQuestManager();
		langMan = plugin.getLanguageManager();
		senderPlayer = Bukkit.getOfflinePlayer(CONSOLE_NAME);
		senderProfile = new PlayerProfile(senderPlayer);
		profileStorage = new YamlProfileStorage(profileFolder, plugin.getLogger());
	}
	
	private PlayerProfile createProfile(final OfflinePlayer player) {
		if(player instanceof Player && !Util.isPlayer((Player)player)) {
			Ql.warning("Smeone/Something tried to get profile of a non-player.");
			Ql.debug("Contact Quester author and show him this exception.", new CustomException("player name: "
					+ player.getName()));
			return null;
		}
		final PlayerProfile prof = new PlayerProfile(player);
		profiles.put(player.getUniqueId(), prof);
		return prof;
	}
	
	private PlayerProfile loadProfile(final ProfileImage image) {
		if(image == null) {
			return null;
		}
		final PlayerProfile prof = new PlayerProfile(image, qMan);
		updateRank(prof);
		profiles.put(prof.getId(), prof);
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
	
	public PlayerProfile getSenderProfile(final CommandSender sender) {
		if(sender instanceof Player) {
			return getProfile((Player)sender);
		}
		else {
			return senderProfile;
		}
	}
	
	public PlayerProfile getProfile(final OfflinePlayer player) {
		if(player == null) {
			return null;
		}
		PlayerProfile prof = profiles.get(player.getUniqueId());
		if(prof == null) {
			// CURRENTLY ONLY SYNC
			prof = loadProfile(profileStorage.retrieve(player.getUniqueId()));;
		}
		if(prof == null && player instanceof Player) {
			prof = createProfile(player);
		}
		return prof;
	}
	
	public PlayerProfile getProfileSafe(final String player, final QuesterLang lang) throws ProfileException {
		if(CONSOLE_NAME.equalsIgnoreCase(player)) {
			return senderProfile;
		}
		final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);
		final PlayerProfile prof = getProfile(offlinePlayer);
		if(prof == null) {
			throw new ProfileException(lang.get("INFO_PROFILE_NOT_EXIST").replaceAll("%p", player));
		}
		return prof;
	}
	
	public boolean hasProfile(final OfflinePlayer player) {
		return profiles.containsKey(player.getUniqueId());
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
		profile.addCompleted(questName, (int)(timeInMillis / 1000));
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
		return profile.getProgress().getObjectiveStatus(id) == ObjectiveStatus.ACTIVE;
	}
	
	public boolean setProfileLanguage(final PlayerProfile profile, final String language) {
		Validate.notNull(profile, "Profile can't be null.");
		if(language == null) {
			profile.setLanguage("");
			return true;
		}
		if(langMan.hasLang(language)) {
			profile.setLanguage(language);
			return true;
		}
		return false;
	}
	
	// QUEST PROGRESS METHODS
	
	public void startQuest(final Player player, final Quest quest, final ActionSource as, final QuesterLang senderLang) throws QuesterException {
		startQuest(player, quest, as, senderLang, false);
	}
	
	public void startQuest(final Player player, final Quest quest, final ActionSource as, final QuesterLang senderLang, final boolean disableAdminCheck) throws QuesterException {
		if(quest == null) {
			throw new QuestException(senderLang.get("ERROR_Q_NOT_EXIST"));
		}
		final PlayerProfile prof = getProfile(player);
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
		final QuesterLang playerLang = langMan.getLang(prof.getLanguage());
		if(QConfiguration.progMsgStart) {
			player.sendMessage(Quester.LABEL
					+ playerLang.get("MSG_Q_STARTED").replaceAll("%q", ChatColor.GOLD
							+ quest.getName() + ChatColor.BLUE));
		}
		final String description = quest.getDescription(player.getName(), playerLang);
		if(!description.isEmpty() && !quest.hasFlag(QuestFlag.NODESC)) {
			player.sendMessage(description);
		}
		Ql.verbose(player.getName() + " started quest '" + quest.getName() + "'.");
		for(final Qevent qv : quest.getQevents()) {
			if(qv.getOccasion() == -1) {
				qv.execute(player, plugin);
			}
		}
		
		if(quest.getObjectives().isEmpty()) {
			forceCompleteQuest(player, as, senderLang);
		}
	}
	
	public void startRandomQuest(final Player player, final ActionSource as, final QuesterLang lang) throws QuesterException {
		Collection<Quest> allQuests = qMan.getQuests();
		final ArrayList<Quest> chosenQuests = new ArrayList<Quest>();
		for(final Quest quest : allQuests) {
			if(quest.hasFlag(QuestFlag.ACTIVE) && !quest.hasFlag(QuestFlag.HIDDEN)
					&& !getProfile(player).hasQuest(quest)
					&& qMan.areConditionsMet(player, quest, lang)) {
				chosenQuests.add(quest);
			}
		}
		allQuests = null;
		if(chosenQuests.isEmpty()) {
			throw new QuestException(lang.get("ERROR_Q_NONE_ACTIVE"));
		}
		final int id = randGen.nextInt(chosenQuests.size());
		startQuest(player, chosenQuests.get(id), as, lang);
	}
	
	public void cancelQuest(final Player player, final ActionSource as, final QuesterLang lang) throws QuesterException {
		cancelQuest(player, -1, as, lang);
	}
	
	public void cancelQuest(final Player player, final int index, ActionSource as, final QuesterLang lang) throws QuesterException {
		Quest quest = null;
		final PlayerProfile prof = getProfile(player);
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
					+ langMan.getLang(prof.getLanguage()).get("MSG_Q_CANCELLED").replaceAll("%q", ChatColor.GOLD
							+ quest.getName() + ChatColor.BLUE));
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
		final PlayerProfile prof = getProfile(player);
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
			forceCompleteQuest(player, as, lang);
		}
		else if(error) {
			throw new ObjectiveException(lang.get("ERROR_OBJ_CANT_DO"));
		}
	}
	
	private boolean completeObjective(final Player player, final ActionSource as, final QuesterLang lang) throws QuesterException {
		final PlayerProfile prof = getProfile(player);
		final Quest quest = prof.getQuest();
		final List<Objective> objs = quest.getObjectives();
		
		int i = 0;
		boolean completed = false;
		while(i < objs.size() && !completed) {
			final Objective obj = objs.get(i);
			if(isObjectiveActive(prof, i) && obj.tryToComplete(player)) {
				final int increment = obj.getTargetAmount()
						- prof.getProgress().getProgress().get(i);
				incProgress(player, as, i, increment, false);
				completed = true;
			}
			i++;
		}
		
		return completed || i == 0;
	}
	
	public void forceCompleteQuest(final Player player, final ActionSource as, final QuesterLang lang) throws QuesterException {
		final PlayerProfile prof = getProfile(player);
		final Quest quest = prof.getQuest();
		
		unassignQuest(prof);
		addCompletedQuest(prof, quest.getName());
		if(QConfiguration.progMsgDone) {
			player.sendMessage(Quester.LABEL
					+ langMan.getLang(prof.getLanguage()).get("MSG_Q_COMPLETED").replaceAll("%q", ChatColor.GOLD
							+ quest.getName() + ChatColor.BLUE));
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
			qMan.deactivateQuest(quest, this);
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
		final PlayerProfile prof = getProfile(player);
		final QuesterLang lang = langMan.getLang(prof.getLanguage());
		final QuestProgress prog = prof.getProgress();
		if(prog == null || objectiveId < 0 || objectiveId >= prog.getSize()) {
			return;
		}
		final int newValue = prog.getProgress().get(objectiveId) + amount;
		final Quest q = prof.getQuest();
		final Objective obj = q.getObjectives().get(objectiveId);
		setProgress(prof, objectiveId, newValue);
		
		if(obj.getTargetAmount() <= newValue) {
			if(QConfiguration.progMsgObj && !obj.isHidden()) {
				player.sendMessage(Quester.LABEL + lang.get("MSG_OBJ_COMPLETED"));
			}
			/* ObjectiveCompleteEvent */
			final ObjectiveCompleteEvent event = new ObjectiveCompleteEvent(as, player, q, objectiveId);
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
				catch(final QuesterException ignore) {}
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
	
	// STORAGE
	
	public void loadRanks() {
		final Map<Integer, String> rankMap = new HashMap<Integer, String>();
		final List<Integer> sortedList = new ArrayList<Integer>();
		
		StorageKey rankKey = null;
		try {
			rankKey = QConfiguration.getConfigKey("ranks");
		}
		catch(final InstanceNotFoundException e) {
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
			catch(final InstanceNotFoundException ignore) {}
		}
		Collections.sort(sortedList);
		ranks = rankMap;
		sortedRanks = sortedList;
	}
	
	public void saveProfiles() {
		for(final PlayerProfile prof : profiles.values()) {
			if(prof.isDirty()) {
				saveProfile(prof);
			}
		}
	}
	
	public void saveProfile(final PlayerProfile prof) {
		if(prof == null) {
			return;
		}
		storageExecutor.execute(new StoreProfile(prof.getProfileImage()));
		prof.setDirty(false);
	}
	
	class StoreProfile implements Runnable {
		
		final ProfileImage[] images;
		
		public StoreProfile(final ProfileImage... images) {
			this.images = images;
		}
		
		@Override
		public void run() {
			for(final ProfileImage i : images) {
				profileStorage.store(i);
			}
		}
	}
	
	public boolean startSaving() {
		if(saveTask != null) {
			return false;
		}
		
		saveTask = new BukkitRunnable() {
			
			@Override
			public void run() {
				saveProfiles();
			}
		}.runTaskTimer(plugin, 60 * 20L, 60 * 20L);
		
		return true;
	}
	
	public boolean stopSaving() {
		if(saveTask == null) {
			return false;
		}
		
		saveTask.cancel();
		return true;
	}
	
	public boolean waitForSaving() {
		storageExecutor.shutdown();
		while(true) {
			try {
				return storageExecutor.awaitTermination(1, TimeUnit.MINUTES);
			}
			catch(final InterruptedException ignore) {}
		}
	}
	
	public void loadProfilesFromFile(final File file) {
		if(file == null) {
			return;
		}
		if(file.isFile()) {
			Ql.verbose("Loading profiles...");
			final List<ProfileImage> profs = ((YamlProfileStorage)profileStorage).retrieveAllFromFile(file);
			Ql.verbose("Processing loaded profiles...");
			for(final ProfileImage image : profs) {
				loadProfile(image);
			}
		}
	}
	
	//	public void loadProfiles() {
	//		loadProfiles(QConfiguration.profileStorageType, true);
	//	}
	//	
	//	public void loadProfiles(final StorageType loadFrom, final boolean async) {
	//		switch(loadFrom) {
	//			case MYSQL: {
	//				loadFromDatabase(async);
	//				break;
	//			}
	//			default: {
	//				final Storage profileStorage = new ConfigStorage(profileStorageFile, plugin.getLogger(), null);
	//				profileStorage.load();
	//				final StorageKey mainKey = profileStorage.getKey("");
	//				PlayerProfile prof;
	//				int counter = 0;
	//				for(final StorageKey subKey : mainKey.getSubKeys()) {
	//					counter++;
	//					prof = PlayerProfile.deserialize(subKey, qMan);
	//					if(prof != null) {
	//						loadProfile(prof);
	//					}
	//					else {
	//						Ql.info("Invalid key in profiles.yml: " + subKey.getName());
	//					}
	//					if(counter % 10 == 0) {
	//						Ql.debug(counter + " profiles processed.");
	//					}
	//				}
	//				Ql.verbose(profiles.size() + " profiles loaded.");
	//			}
	//		}
	//	}
	//	
	//	private void loadFromDatabase(final boolean async) {
	//		final Runnable loadTask = new Runnable() {
	//			
	//			@Override
	//			public void run() {
	//				Connection conn = null;
	//				PreparedStatement stmt = null;
	//				ResultSet rs = null;
	//				try {
	//					Ql.debug("Loading profiles...");
	//					final List<SerializedPlayerProfile> serps = new ArrayList<PlayerProfile.SerializedPlayerProfile>();
	//					conn = DatabaseConnection.getConnection();
	//					stmt = conn.prepareStatement("SELECT * FROM `quester-profiles`");
	//					rs = stmt.executeQuery();
	//					while(rs.next()) {
	//						try {
	//							serps.add(new SerializedPlayerProfile(rs));
	//						}
	//						catch(final SQLException ignore) {}
	//					}
	//					rs.close();
	//					stmt.close();
	//					Ql.debug("Loaded " + serps.size() + " profiles.");
	//					final Runnable deserialization = new Runnable() {
	//						
	//						@Override
	//						public void run() {
	//							int count = 0;
	//							for(final SerializedPlayerProfile sp : serps) {
	//								final PlayerProfile prof = PlayerProfile.deserialize(sp.getStoragekey(), qMan);
	//								if(prof != null) {
	//									updateRank(prof);
	//									profiles.put(prof.getId(), prof);
	//									count++;
	//								}
	//								else {
	//									Ql.info("Invalid profile '" + sp.uid.toString() + "'");
	//								}
	//							}
	//							Ql.debug("Deserialized " + count + " profiles.");
	//						}
	//					};
	//					
	//					if(async) {
	//						Bukkit.getScheduler().runTask(plugin, deserialization);
	//					}
	//					else {
	//						deserialization.run();
	//					}
	//				}
	//				catch(final SQLException e) {
	//					e.printStackTrace();
	//				}
	//				finally {
	//					if(conn != null) {
	//						try {
	//							conn.close();
	//						}
	//						catch(final SQLException ignore) {}
	//					}
	//					if(stmt != null) {
	//						try {
	//							stmt.close();
	//						}
	//						catch(final SQLException ignore) {}
	//					}
	//					if(rs != null) {
	//						try {
	//							rs.close();
	//						}
	//						catch(final SQLException ignore) {}
	//					}
	//				}
	//			}
	//		};
	//		
	//		if(async) {
	//			new Thread(loadTask, "Quester Profile Loading Thread").start();
	//		}
	//		else {
	//			loadTask.run();
	//		}
	//	}
	//	
	//	public void saveProfiles() {
	//		saveProfiles(QConfiguration.profileStorageType, true);
	//	}
	//	
	//	
	//	private void saveToDatabase(final boolean async) {
	//		final List<SerializedPlayerProfile> serps = new ArrayList<PlayerProfile.SerializedPlayerProfile>();
	//		
	//		for(final PlayerProfile prof : profiles.values()) {
	//			serps.add(new PlayerProfile.SerializedPlayerProfile(prof));
	//			prof.setUnchanged();
	//		}
	//		
	//		final Runnable saveTask = new Runnable() {
	//			
	//			@Override
	//			public void run() {
	//				Connection conn = null;
	//				PreparedStatement stmt = null;
	//				ResultSet rs = null;
	//				try {
	//					final Set<String> stored = new HashSet<String>();
	//					conn = DatabaseConnection.getConnection();
	//					stmt = conn.prepareStatement("SELECT `name` FROM `quester-profiles`");
	//					rs = stmt.executeQuery();
	//					int saved = 0;
	//					while(rs.next()) {
	//						stored.add(rs.getString("name"));
	//					}
	//					rs.close();
	//					stmt.close();
	//					for(final SerializedPlayerProfile sp : serps) {
	//						final boolean isStored = stored.contains(sp.uid.toString());
	//						try {
	//							if(!isStored || sp.changed) { // only save if it has changed, or is not stored
	//								stmt = conn.prepareStatement(isStored
	//										? sp.getUpdateQuerry("quester-profiles")
	//										: sp.getInsertQuerry("quester-profiles"));
	//								stmt.execute();
	//								stmt.close();
	//								saved++;
	//							}
	//						}
	//						catch(final SQLException e) {
	//							System.out.println("Failed to save profile " + sp.uid.toString());
	//							if(QConfiguration.debug) {
	//								e.printStackTrace();
	//							}
	//						}
	//						finally {
	//							if(stmt != null) {
	//								try {
	//									stmt.close();
	//								}
	//								catch(final SQLException ignore) {}
	//							}
	//						}
	//					}
	//					if(QConfiguration.verbose) {
	//						System.out.println(saved + " profiles saved.");
	//					}
	//				}
	//				catch(final SQLException e) {
	//					e.printStackTrace();
	//				}
	//				finally {
	//					if(conn != null) {
	//						try {
	//							conn.close();
	//						}
	//						catch(final SQLException ignore) {}
	//					}
	//					if(stmt != null) {
	//						try {
	//							stmt.close();
	//						}
	//						catch(final SQLException ignore) {}
	//					}
	//					if(rs != null) {
	//						try {
	//							rs.close();
	//						}
	//						catch(final SQLException ignore) {}
	//					}
	//				}
	//			}
	//		};
	//		
	//		if(async) {
	//			new Thread(saveTask, "Quester Profile Saving Thread").start();
	//		}
	//		else {
	//			saveTask.run();
	//		}
	//	}
}
