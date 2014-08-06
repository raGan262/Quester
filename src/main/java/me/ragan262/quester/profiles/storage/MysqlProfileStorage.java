package me.ragan262.quester.profiles.storage;

import java.util.UUID;
import me.ragan262.quester.profiles.ProfileImage;

public class MysqlProfileStorage implements ProfileStorage {
	
	public MysqlProfileStorage() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public ProfileImage retrieve(final UUID uid) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void store(final ProfileImage image) {
		// TODO Auto-generated method stub
		
	}
	
	// change that into mysql loaders
	
	//	// is used to serialize profiles into database
	//	static class SerializedPlayerProfile {
	//		
	//		static final String delimiter1 = "~~";
	//		static final String delimiter2 = "|";
	//		
	//		final UUID uid;
	//		final int current;
	//		final String progresses;
	//		final String completed;
	//		final String reputation;
	//		final boolean changed;
	//		
	//		private String insertQuerry = null;
	//		private String updateQuerry = null;
	//		
	//		SerializedPlayerProfile(final PlayerProfile prof) {
	//			uid = prof.getId();
	//			current = prof.getQuestProgressIndex();
	//			StringBuilder sb = new StringBuilder();
	//			boolean run = false;
	//			for(final QuestProgress progress : prof.progresses) {
	//				sb.append(progress.quest.getID());
	//				for(final int p : progress.progress) {
	//					sb.append(delimiter2).append(p);
	//				}
	//				sb.append(delimiter1);
	//				run = true;
	//			}
	//			progresses = run ? sb.substring(0, sb.length() - delimiter1.length()) : "";
	//			
	//			run = false;
	//			sb = new StringBuilder();
	//			for(final Entry<String, Integer> entry : prof.completed.entrySet()) {
	//				sb.append(entry.getKey()).append(delimiter2).append(entry.getValue()).append(delimiter1);
	//				run = true;
	//			}
	//			completed = run ? sb.substring(0, sb.length() - delimiter1.length()) : "";
	//			
	//			sb = new StringBuilder();
	//			// until reputation is implemented
	//			sb.append("default").append(delimiter2).append(prof.points);
	//			reputation = sb.toString();
	//			changed = prof.changed;
	//		}
	//		
	//		SerializedPlayerProfile(final ResultSet rs) throws SQLException {
	//			final String id = rs.getString("name");
	//			UUID tempId = null;
	//			try {
	//				tempId = UUID.fromString(id);
	//			}
	//			catch(final IllegalArgumentException e) {
	//				// backwards compatibility
	//				tempId = Bukkit.getOfflinePlayer(id).getUniqueId();
	//			}
	//			uid = tempId;
	//			current = rs.getInt("current");
	//			progresses = rs.getString("quests");
	//			completed = rs.getString("completed");
	//			reputation = rs.getString("reputation");
	//			changed = false;
	//		}
	//		
	//		StorageKey getStoragekey() {
	//			boolean err = false;
	//			final StorageKey result = new MemoryStorageKey(uid.toString());
	//			
	//			if(current >= 0) {
	//				result.setInt("active", current);
	//			}
	//			
	//			StorageKey temp = result.getSubKey("completed");
	//			for(final String s : completed.split(Pattern.quote(delimiter1))) {
	//				if(!s.isEmpty()) {
	//					final String[] split = s.split(Pattern.quote(delimiter2));
	//					try {
	//						temp.setInt(split[0].replaceAll("\\.", "#%#"), Integer.valueOf(split[1]));
	//					}
	//					catch(final Exception e) {
	//						err = true;
	//						Ql.debug("Error in completed '" + s + "'", e);
	//					}
	//				}
	//			}
	//			
	//			temp = result.getSubKey("quests");
	//			for(final String s : progresses.split(Pattern.quote(delimiter1))) {
	//				try {
	//					if(!s.isEmpty()) {
	//						final int pos = s.indexOf('|');
	//						if(pos < 0) {
	//							temp.setString(s, "");
	//						}
	//						else {
	//							temp.setString(s.substring(0, pos), s.substring(pos + 1));
	//						}
	//					}
	//				}
	//				catch(final Exception e) {
	//					err = true;
	//					Ql.debug("Error in progress '" + s + "'", e);
	//				}
	//			}
	//			
	//			try {
	//				result.setInt("points", Integer.valueOf(reputation.split(Pattern.quote(delimiter2))[1]));
	//			}
	//			catch(final Exception e) {
	//				err = true;
	//				Ql.debug("Error in reputation '" + reputation + "'", e);
	//			}
	//			
	//			if(err) {
	//				Ql.warning("Error occurred when loading profile " + uid.toString()
	//						+ ". Switch to debug mode for more details.");
	//			}
	//			
	//			return result;
	//		}
	//		
	//		String getInsertQuerry(final String tableName) {
	//			if(insertQuerry == null) {
	//				insertQuerry = String.format("INSERT INTO `%s`(`name`, `completed`, `current`, `quests`, `reputation`) VALUES ('%s','%s',%d,'%s','%s')", tableName, uid.toString(), completed.replaceAll("'", "\\\\'"), current, progresses, reputation.replaceAll("'", "\\\\'"));
	//			}
	//			return insertQuerry;
	//		}
	//		
	//		String getUpdateQuerry(final String tableName) {
	//			if(updateQuerry == null) {
	//				updateQuerry = String.format("UPDATE `%s` SET `completed`='%s',`current`=%d,`quests`='%s',`reputation`='%s' WHERE `name`='%s'", tableName, completed.replaceAll("'", "\\\\'"), current, progresses, reputation.replaceAll("'", "\\\\'"), uid.toString());
	//			}
	//			return updateQuerry;
	//		}
	//	}
}
