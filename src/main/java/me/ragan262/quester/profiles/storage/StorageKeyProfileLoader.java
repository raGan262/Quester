package me.ragan262.quester.profiles.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import me.ragan262.quester.Quester;
import me.ragan262.quester.profiles.ProfileImage;
import me.ragan262.quester.profiles.ProfileImage.ProfileImageBuilder;
import me.ragan262.quester.profiles.QuestProgress.ProgressImage;
import me.ragan262.quester.quests.Quest;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.Ql;
import me.ragan262.quester.utils.Util;

public abstract class StorageKeyProfileLoader {
	
	// never going to be serialized
	@SuppressWarnings("serial")
	private static final Map<Integer, StorageKeyProfileLoader> versions = new HashMap<Integer, StorageKeyProfileLoader>() {
		
		{
			put(0, new Version0Loader());
			put(1, new Version1Loader());
		}
	};
	
	public static final int CURRENT_VERSION = 1;
	
	public static final StorageKeyProfileLoader CURRENT_LOADER = versions.get(CURRENT_VERSION);
	
	public static StorageKeyProfileLoader version(final int version) {
		if(!versions.containsKey(version)) {
			throw new IllegalArgumentException("Unknown profile version.");
		}
		return versions.get(version);
	}
	
	public static ProfileImage loadProfile(final StorageKey key) {
		StorageKeyProfileLoader loader = versions.get(key.getInt("version", 0));
		if(loader == null) {
			loader = versions.get(0);
		}
		return loader.load(key);
	}
	
	public static void saveProfile(final ProfileImage image, final StorageKey key) {
		CURRENT_LOADER.save(image, key);
	}
	
	private StorageKeyProfileLoader() {
		// we don't want anyone else to extend this
	}
	
	public abstract ProfileImage load(StorageKey key);
	
	public abstract void save(ProfileImage image, StorageKey key);
	
	private static class Version0Loader extends StorageKeyProfileLoader {
		
		@Override
		public ProfileImage load(final StorageKey key) {
			OfflinePlayer player = null;
			try {
				player = Bukkit.getOfflinePlayer(UUID.fromString(key.getName()));
			}
			catch(final Exception e) {
				// OLD FORMAT (0.6.3)
				if(key.getString("name") != null) {
					player = Bukkit.getOfflinePlayer(key.getString("name"));
				}
			}
			
			if(player == null) {
				Ql.verbose("Could not retrieve player from profile '" + key.getName() + "'.");
				return null;
			}
			
			final ProfileImageBuilder builder = new ProfileImageBuilder(player);
			builder.setLanguage(key.getString("language"));
			builder.putReputation("default", key.getInt("points", 0));
			if(key.getSubKey("completed").hasSubKeys()) {
				for(final StorageKey subKey : key.getSubKey("completed").getSubKeys()) {
					builder.putCompleted(subKey.getName().replaceAll("#%#", "."), subKey.getInt("", 0));
				}
			}
			
			if(key.getSubKey("quests").hasSubKeys()) {
				for(final StorageKey subKey : key.getSubKey("quests").getSubKeys()) {
					int quest = -1;
					try {
						quest = Integer.parseInt(subKey.getName());
					}
					catch(final NumberFormatException e) {
						// OLD FORMAT (0.6.3)
						final Quest q = Quester.getInstance().getQuestManager().getQuest(subKey.getName().replaceAll("#%#", "."));
						if(q != null) {
							quest = q.getID();
						}
					}
					if(quest < 0) {
						continue;
					}
					
					String progressString = null;
					if(!subKey.getSubKeys().isEmpty()) {
						progressString = key.getString("progress", "");
					}
					else if(subKey.getString("") != null) {
						// OLD FORMAT (0.6.3)
						progressString = key.getString("");
					}
					
					try {
						final List<Integer> prog = new ArrayList<>();
						String[] strs = progressString.split("\\|");
						if(strs[0].isEmpty()) {
							strs = new String[0];
						}
						
						for(final String s : strs) {
							prog.add(Integer.parseInt(s));
						}
						
						builder.putProgress(quest, new ProgressImage(prog));
					}
					catch(final Exception e) {
						Ql.info("Invalid or missing progress for quest " + quest + " in profile.");
						Ql.debug("Exception", e);
					}
				}
			}
			
			builder.setActive(key.getInt("active", 0));
			return builder.build();
		}
		
		@Override
		public void save(final ProfileImage image, final StorageKey key) {
			
			key.setInt("version", 0);
			
			key.removeKey("points");
			if(image.reputation.containsKey("default")) {
				key.setInt("points", image.reputation.get("default"));
			}
			
			key.removeKey("language");
			if(!image.language.isEmpty()) {
				key.setString("language", image.language);
			}
			
			key.removeKey("completed");
			for(final Entry<String, Integer> e : image.completed.entrySet()) {
				key.setInt("completed." + e.getKey().replaceAll("\\.", "#%#"), e.getValue());
			}
			
			key.removeKey("quests");
			key.removeKey("active");
			int i = 0;
			for(final Entry<Integer, ProgressImage> e : image.progresses.entrySet()) {
				if(image.active == e.getKey()) {
					key.setInt("active", i);
				}
				key.setString("quests." + e.getKey() + ".progress", Util.implodeIterable(e.getValue().asList(), "|"));
				i++;
			}
		}
	}
	
	private static class Version1Loader extends StorageKeyProfileLoader {
		
		@Override
		public ProfileImage load(final StorageKey key) {
			OfflinePlayer player = null;
			try {
				player = Bukkit.getOfflinePlayer(UUID.fromString(key.getName()));
			}
			catch(final Exception e) {
				Ql.verbose("Could not retrieve player from profile '" + key.getName() + "'.");
				return null;
			}
			
			final ProfileImageBuilder builder = new ProfileImageBuilder(player);
			builder.setLanguage(key.getString("language"));
			if(key.getSubKey("reputation").hasSubKeys()) {
				for(final StorageKey subKey : key.getSubKey("reputation").getSubKeys()) {
					builder.putReputation(subKey.getName(), subKey.getInt("", 0));
				}
			}
			
			if(key.getSubKey("completed").hasSubKeys()) {
				for(final StorageKey subKey : key.getSubKey("completed").getSubKeys()) {
					builder.putCompleted(subKey.getName().replaceAll("#%#", "."), subKey.getInt("", 0));
				}
			}
			
			if(key.getSubKey("quests").hasSubKeys()) {
				for(final StorageKey subKey : key.getSubKey("quests").getSubKeys()) {
					int quest = -1;
					try {
						quest = Integer.parseInt(subKey.getName());
					}
					catch(final NumberFormatException ignore) {}

					if(quest < 0) {
						Ql.severe("Invalid quest ID '" + subKey.getName() + "' in profile "
								+ player.getUniqueId().toString() + ". (" + player.getName()
								+ ")");
						continue;
					}
					
					try {
						final List<Integer> prog = new ArrayList<>();
						String[] strs = subKey.getString("progress", null).split("\\|");
						if(strs[0].isEmpty()) {
							strs = new String[0];
						}
						
						for(final String s : strs) {
							prog.add(Integer.parseInt(s));
						}
						
						builder.putProgress(quest, new ProgressImage(prog));
					}
					catch(final Exception e) {
						Ql.info("Invalid or missing progress for quest " + quest + " in profile.");
					}
				}
			}
			
			builder.setActive(key.getInt("active", 0));
			return builder.build();
		}
		
		@Override
		public void save(final ProfileImage image, final StorageKey key) {
			
			key.setInt("version", 1);
			
			key.removeKey("reputation");
			for(final Entry<String, Integer> e : image.reputation.entrySet()) {
				key.setInt("reputation." + e.getKey(), e.getValue());
			}
			
			key.removeKey("language");
			if(!image.language.isEmpty()) {
				key.setString("language", image.language);
			}
			
			key.removeKey("completed");
			for(final Entry<String, Integer> e : image.completed.entrySet()) {
				key.setInt("completed." + e.getKey().replaceAll("\\.", "#%#"), e.getValue());
			}
			
			key.removeKey("quests");
			key.removeKey("active");
			for(final Entry<Integer, ProgressImage> e : image.progresses.entrySet()) {
				if(image.active == e.getKey()) {
					key.setInt("active", image.active);
				}
				key.setString("quests." + e.getKey() + ".progress", Util.implodeIterable(e.getValue().asList(), "|"));
			}
		}
	}
}
