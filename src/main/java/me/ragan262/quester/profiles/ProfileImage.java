package me.ragan262.quester.profiles;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import me.ragan262.quester.profiles.QuestProgress.ProgressImage;
import org.apache.commons.lang.Validate;
import org.bukkit.OfflinePlayer;

public final class ProfileImage {
	
	public final String name;
	public final UUID uid;
	public final int active;
	public final String language;
	public final Map<String, Integer> reputation;
	public final Map<String, Integer> completed;
	public final Map<Integer, ProgressImage> progresses;
	
	private ProfileImage(final String name, final UUID uid, final int active, final String language, final Map<String, Integer> reputation, final Map<String, Integer> completed, final Map<Integer, ProgressImage> progresses) {
		this.name = name;
		this.uid = uid;
		this.active = active;
		this.language = language;
		this.reputation = reputation;
		this.completed = completed;
		this.progresses = progresses;
	}
	
	public static final class ProfileImageBuilder {
		
		private UUID uid;
		private String name = "";
		private int active = -1;
		private String language = "";
		private final Map<String, Integer> reputation = new TreeMap<>();
		private final Map<String, Integer> completed = new TreeMap<>();
		private final Map<Integer, ProgressImage> progresses = new TreeMap<>();
		
		public ProfileImageBuilder(final UUID uid) {
			Validate.notNull(uid, "UUID can't be null.");
			this.uid = uid;
		}
		
		public ProfileImageBuilder(final OfflinePlayer player) {
			Validate.notNull(player, "OfflinePlayer can't be null.");
			name = player.getName();
			uid = player.getUniqueId();
		}
		
		public void setName(final String name) {
			if(name == null) {
				return;
			}
			this.name = name;
		}
		
		public void setUid(final UUID uid) {
			if(uid == null) {
				return;
			}
			this.uid = uid;
		}
		
		public void setActive(final int active) {
			if(!progresses.containsKey(active)) {
				this.active = -1;
			}
			this.active = active;
		}
		
		public void setLanguage(final String language) {
			if(language == null) {
				return;
			}
			this.language = language;
		}
		
		public void putReputation(final String group, final int time) {
			if(group == null) {
				return;
			}
			reputation.put(group, time);
		}
		
		public void putCompleted(final String completed, final int time) {
			if(completed == null) {
				return;
			}
			this.completed.put(completed, time);
		}
		
		public void putProgress(final int id, final ProgressImage progress) {
			if(progress == null) {
				return;
			}
			progresses.put(id, progress);
		}
		
		public ProfileImage build() {
			final Map<String, Integer> rep = Collections.unmodifiableMap(new TreeMap<>(reputation));
			final Map<String, Integer> compl = Collections.unmodifiableMap(new TreeMap<>(completed));
			final Map<Integer, ProgressImage> prog = Collections.unmodifiableMap(new TreeMap<>(progresses));
			return new ProfileImage(name, uid, active, language, rep, compl, prog);
		}
	}
}
