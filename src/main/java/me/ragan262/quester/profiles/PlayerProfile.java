package me.ragan262.quester.profiles;

import java.lang.ref.WeakReference;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.regex.Pattern;

import me.ragan262.quester.quests.Quest;
import me.ragan262.quester.quests.QuestManager;
import me.ragan262.quester.storage.MemoryStorageKey;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.Ql;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class PlayerProfile {
	
	private final String name;
	private final UUID uid;
	private final Map<String, Integer> completed;
	private WeakReference<Quest> selected;
	private int holder;
	private QuestProgress current;
	private final List<QuestProgress> progresses;
	private int points;
	private String rank;
	private boolean changed;
	private String language;
	
	PlayerProfile(final OfflinePlayer player) {
		name = player.getName();
		uid = player.getUniqueId();
		completed = new HashMap<String, Integer>();
		current = null;
		progresses = new ArrayList<QuestProgress>();
		selected = new WeakReference<Quest>(null);
		holder = -1;
		points = 0;
		rank = "";
		changed = false;
		language = "";
	}
	
	public String getName() {
		return name;
	}
	
	public UUID getId() {
		return uid;
	}
	
	void addCompleted(final String questName) {
		addCompleted(questName.toLowerCase(), 0);
	}
	
	void addCompleted(final String questName, final int time) {
		completed.put(questName.toLowerCase(), time);
		setChanged();
	}
	
	void removeCompleted(final String questName) {
		if(completed.remove(questName.toLowerCase()) != null) {
			setChanged();
		}
	}
	
	public String[] getCompletedQuests() {
		return completed.keySet().toArray(new String[0]);
	}
	
	public boolean isCompleted(final String questName) {
		return completed.containsKey(questName.toLowerCase());
	}
	
	public int getCompletionTime(final String questName) {
		final Integer time = completed.get(questName.toLowerCase());
		if(time == null) {
			return 0;
		}
		return time;
	}
	
	public int getQuestAmount() {
		return progresses.size();
	}
	
	public boolean hasQuest(final String questName) {
		return getQuestProgressIndex(questName) != -1;
	}
	
	public boolean hasQuest(final Quest quest) {
		return getQuestProgressIndex(quest) != -1;
	}
	
	boolean setActiveQuest(final int index) {
		try {
			current = progresses.get(index);
			setChanged();
		}
		catch (final Exception e) {
			return false;
		}
		return true;
	}
	
	void refreshActive() {
		if(current == null) {
			setActiveQuest(0);
		}
	}
	
	public int getQuestProgressIndex() {
		return progresses.indexOf(current);
	}
	
	public int getQuestProgressIndex(final Quest quest) {
		for(int i = 0; i < progresses.size(); i++) {
			if(progresses.get(i).quest.equals(quest)) {
				return i;
			}
		}
		return -1;
	}
	
	public int getQuestProgressIndex(final String questName) {
		for(int i = 0; i < progresses.size(); i++) {
			if(progresses.get(i).quest.getName().equalsIgnoreCase(questName)) {
				return i;
			}
		}
		return -1;
	}
	
	void setSelected(final Quest newSelected) {
		selected = new WeakReference<Quest>(newSelected);
	}
	
	public Quest getSelected() {
		return selected.get();
	}
	
	void setHolderID(final int newID) {
		holder = newID;
	}
	
	public int getHolderID() {
		return holder;
	}
	
	int addPoints(final int pts) {
		setChanged();
		points += pts;
		return points;
	}
	
	public int getPoints() {
		return points;
	}
	
	void setRank(final String newRank) {
		rank = newRank;
	}
	
	public String getRank() {
		return rank;
	}
	
	void addQuest(final Quest quest) {
		final QuestProgress prg = new QuestProgress(quest);
		if(!progresses.contains(prg)) {
			progresses.add(prg);
			setActiveQuest(progresses.size() - 1);
			setChanged();
		}
	}
	
	void unsetQuest() {
		try {
			progresses.remove(current);
			current = null;
			setChanged();
		}
		catch (final Exception ignore) {}
	}
	
	void unsetQuest(final int index) {
		try {
			if(progresses.get(index).equals(current)) {
				current = null;
			}
			progresses.remove(index);
			setChanged();
		}
		catch (final Exception ignore) {}
	}
	
	public Quest getQuest() {
		if(current == null) {
			return null;
		}
		return current.quest;
	}
	
	public Quest getQuest(final int index) {
		if(getProgress(index) == null) {
			return null;
		}
		return progresses.get(index).quest;
	}
	
	public QuestProgress getProgress() {
		return current;
	}
	
	public QuestProgress getProgress(final int index) {
		if(index >= 0 && index < progresses.size()) {
			return progresses.get(index);
		}
		return null;
	}
	
	public QuestProgress[] getProgresses() {
		return progresses.toArray(new QuestProgress[0]);
	}
	
	private void addQuestProgress(final QuestProgress prg) {
		if(!progresses.contains(prg)) {
			progresses.add(prg);
			setChanged();
		}
	}
	
	public String getLanguage() {
		return language;
	}
	
	void setLanguage(final String newLang) {
		if(newLang != null) {
			setChanged();
			language = newLang;
		}
	}
	
	public boolean isChanged() {
		return changed;
	}
	
	void setChanged() {
		changed = true;
	}
	
	void setUnchanged() {
		changed = false;
	}
	
	void serialize(final StorageKey key) {
		
		key.removeKey("points");
		if(points != 0) {
			key.setInt("points", points);
		}
		
		key.removeKey("language");
		if(!language.isEmpty()) {
			key.setString("language", language);
		}
		
		key.removeKey("completed");
		if(!completed.isEmpty()) {
			final StorageKey subKey = key.getSubKey("completed");
			for(final String name : completed.keySet()) {
				subKey.setInt(name.replaceAll("\\.", "#%#"), completed.get(name));
			}
		}
		
		key.removeKey("active");
		if(current != null) {
			final int index = progresses.indexOf(current);
			if(index > -1) {
				key.setInt("active", index);
			}
		}
		
		key.removeKey("quests");
		if(!progresses.isEmpty()) {
			final StorageKey subKey = key.getSubKey("quests");
			for(final QuestProgress prg : progresses) {
				if(prg != null) {
					// subKey.setString(prg.quest.getName().replaceAll("\\.", "#%#"),
					// Util.implodeInt(prg.progress.toArray(new Integer[0]), "|"));
					prg.serialize(subKey.getSubKey(String.valueOf(prg.quest.getID())));
				}
			}
		}
	}
	
	static PlayerProfile deserialize(final StorageKey key, final QuestManager qMan) {
		PlayerProfile prof = null;
		OfflinePlayer player = null;
		
		try {
			player = Bukkit.getOfflinePlayer(UUID.fromString(key.getName()));
		}
		catch (final Exception ignore) {
			// compatibility
			if(key.getString("name") != null) {
				player = Bukkit.getOfflinePlayer(key.getString("name"));
			}
		}
		if(player == null) {
			Ql.verbose("Could not retrieve player.");
			return null;
		}
		
		prof = new PlayerProfile(player);
		
		prof.addPoints(key.getInt("points", 0));
		
		prof.setLanguage(key.getString("language"));
		
		if(key.getSubKey("completed").hasSubKeys()) {
			for(final StorageKey subKey : key.getSubKey("completed").getSubKeys()) {
				prof.addCompleted(subKey.getName().replaceAll("#%#", "."), subKey.getInt("", 0));
			}
		}
		
		//		OLD OLD FORMAT
		//		if(key.getString("quest") != null) {
		//			if(key.getString("progress") != null) {
		//				try {
		//					final QuestProgress prg = prof.new QuestProgress(key.getString("quest"));
		//					final String[] strs = key.getString("progress", "").split("\\|");
		//					for(int i = 0; i < strs.length; i++) {
		//						prg.addToProgress(Integer.parseInt(strs[i]));
		//					}
		//					prof.addQuestProgress(prg);
		//					prof.setActiveQuest(0);
		//				}
		//				catch (final Exception e) {
		//					if(QConfiguration.verbose) {
		//						Quester.log.info("Invalid progress in profile.");
		//					}
		//				}
		//			}
		//			else {
		//				if(QConfiguration.verbose) {
		//					Quester.log.info("Invalid or missing progress for quest '" + key.getString("quest") + "' in profile.");
		//				}
		//			}
		//		}
		
		if(key.getSubKey("quests").hasSubKeys()) {
			for(final StorageKey subKey : key.getSubKey("quests").getSubKeys()) {
				Quest quest = null;
				try {
					quest = qMan.getQuest(Integer.parseInt(subKey.getName()));
				}
				catch (final NumberFormatException e) {
					// compatibility with older format
					quest = qMan.getQuest(subKey.getName().replaceAll("#%#", "."));
				}
				if(quest == null) {
					continue;
				}
				final QuestProgress prog = QuestProgress.deserialize(subKey, quest);
				if(prog == null) {
					Ql.info("Invalid quest progress in profile '" + prof.getName() + "'.");
				}
				else {
					prof.addQuestProgress(prog);
				}
			}
		}
		
		prof.setActiveQuest(key.getInt("active", 0));
		
		prof.setChanged();
		
		return prof;
	}
	
	// is used to serialize profiles into database
	static class SerializedPlayerProfile {
		
		static final String delimiter1 = "~~";
		static final String delimiter2 = "|";
		
		final UUID uid;
		final int current;
		final String progresses;
		final String completed;
		final String reputation;
		final boolean changed;
		
		private String insertQuerry = null;
		private String updateQuerry = null;
		
		SerializedPlayerProfile(final PlayerProfile prof) {
			uid = prof.getId();
			current = prof.getQuestProgressIndex();
			StringBuilder sb = new StringBuilder();
			boolean run = false;
			for(final QuestProgress progress : prof.progresses) {
				sb.append(progress.quest.getID());
				for(final int p : progress.progress) {
					sb.append(delimiter2).append(p);
				}
				sb.append(delimiter1);
				run = true;
			}
			progresses = run ? sb.substring(0, sb.length() - delimiter1.length()) : "";
			
			run = false;
			sb = new StringBuilder();
			for(final Entry<String, Integer> entry : prof.completed.entrySet()) {
				sb.append(entry.getKey()).append(delimiter2).append(entry.getValue())
						.append(delimiter1);
				run = true;
			}
			completed = run ? sb.substring(0, sb.length() - delimiter1.length()) : "";
			
			sb = new StringBuilder();
			// until reputation is implemented
			sb.append("default").append(delimiter2).append(prof.points);
			reputation = sb.toString();
			changed = prof.changed;
		}
		
		SerializedPlayerProfile(final ResultSet rs) throws SQLException {
			final String id = rs.getString("name");
			UUID tempId = null;
			try {
				tempId = UUID.fromString(id);
			}
			catch (final IllegalArgumentException e) {
				// backwards compatibility
				tempId = Bukkit.getOfflinePlayer(id).getUniqueId();
			}
			uid = tempId;
			current = rs.getInt("current");
			progresses = rs.getString("quests");
			completed = rs.getString("completed");
			reputation = rs.getString("reputation");
			changed = false;
		}
		
		StorageKey getStoragekey() {
			boolean err = false;
			final StorageKey result = new MemoryStorageKey(uid.toString());
			
			if(current >= 0) {
				result.setInt("active", current);
			}
			
			StorageKey temp = result.getSubKey("completed");
			for(final String s : completed.split(Pattern.quote(delimiter1))) {
				if(!s.isEmpty()) {
					final String[] split = s.split(Pattern.quote(delimiter2));
					try {
						temp.setInt(split[0].replaceAll("\\.", "#%#"), Integer.valueOf(split[1]));
					}
					catch (final Exception e) {
						err = true;
						Ql.debug("Error in completed '" + s + "'", e);
					}
				}
			}
			
			temp = result.getSubKey("quests");
			for(final String s : progresses.split(Pattern.quote(delimiter1))) {
				try {
					if(!s.isEmpty()) {
						final int pos = s.indexOf('|');
						if(pos < 0) {
							temp.setString(s, "");
						}
						else {
							temp.setString(s.substring(0, pos), s.substring(pos + 1));
						}
					}
				}
				catch (final Exception e) {
					err = true;
					Ql.debug("Error in progress '" + s + "'", e);
				}
			}
			
			try {
				result.setInt("points",
						Integer.valueOf(reputation.split(Pattern.quote(delimiter2))[1]));
			}
			catch (final Exception e) {
				err = true;
				Ql.debug("Error in reputation '" + reputation + "'", e);
			}
			
			if(err) {
				Ql.warning("Error occurred when loading profile " + uid.toString()
						+ ". Switch to debug mode for more details.");
			}
			
			return result;
		}
		
		String getInsertQuerry(final String tableName) {
			if(insertQuerry == null) {
				insertQuerry =
						String.format(
								"INSERT INTO `%s`(`name`, `completed`, `current`, `quests`, `reputation`) VALUES ('%s','%s',%d,'%s','%s')",
								tableName, uid.toString(), completed.replaceAll("'", "\\\\'"),
								current, progresses, reputation.replaceAll("'", "\\\\'"));
			}
			return insertQuerry;
		}
		
		String getUpdateQuerry(final String tableName) {
			if(updateQuerry == null) {
				updateQuerry =
						String.format(
								"UPDATE `%s` SET `completed`='%s',`current`=%d,`quests`='%s',`reputation`='%s' WHERE `name`='%s'",
								tableName, completed.replaceAll("'", "\\\\'"), current, progresses,
								reputation.replaceAll("'", "\\\\'"), uid.toString());
			}
			return updateQuerry;
		}
	}
}
