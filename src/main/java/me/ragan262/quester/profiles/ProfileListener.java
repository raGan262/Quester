package me.ragan262.quester.profiles;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import me.ragan262.quester.QConfiguration;
import me.ragan262.quester.QConfiguration.StorageType;
import me.ragan262.quester.Quester;
import me.ragan262.quester.profiles.PlayerProfile.SerializedPlayerProfile;
import me.ragan262.quester.quests.QuestManager;
import me.ragan262.quester.utils.DatabaseConnection;
import me.ragan262.quester.utils.Ql;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ProfileListener implements Listener {
	
	private final ProfileManager profMan;
	private final QuestManager qMan;
	private final Quester plugin;
	
	public ProfileListener(final Quester plugin) {
		profMan = plugin.getProfileManager();
		qMan = plugin.getQuestManager();
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(final PlayerJoinEvent event) {
		if(QConfiguration.profileStorageType != StorageType.MYSQL) {
			return;
		}
		final String playerName = event.getPlayer().getName();
		final Runnable loadTask = new Runnable() {
			
			@Override
			public void run() {
				Connection conn = null;
				PreparedStatement stmt = null;
				ResultSet rs = null;
				try {
					final SerializedPlayerProfile serp;
					conn = DatabaseConnection.getConnection();
					stmt =
							conn.prepareStatement("SELECT * FROM `quester-profiles` WHERE `name`='"
									+ playerName + "'");
					rs = stmt.executeQuery();
					if(rs.next()) {
						serp = new SerializedPlayerProfile(rs);
					}
					else {
						return;
					}
					
					final Runnable deserialization = new Runnable() {
						
						@Override
						public void run() {
							final PlayerProfile prof =
									PlayerProfile.deserialize(serp.getStoragekey(), qMan);
							if(prof != null) {
								profMan.loadProfile(prof);
							}
							else {
								Ql.info("Invalid profile '" + serp.uid + "'");
							}
						}
					};
					
					Bukkit.getScheduler().runTask(plugin, deserialization);
				}
				catch (final SQLException e) {
					Ql.info("Failed to fetch " + playerName + "'s profile from database.");
					Ql.debug("Exception", e);
					return;
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
		
		new Thread(loadTask, "Quester - " + playerName + "'s Profile Loading").start();
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onLeave(final PlayerQuitEvent event) {
		if(QConfiguration.profileStorageType != StorageType.MYSQL) {
			return;
		}
		final String playerName = event.getPlayer().getName();
		final SerializedPlayerProfile serp =
				new SerializedPlayerProfile(profMan.getProfile(event.getPlayer()));
		
		final Runnable saveTask = new Runnable() {
			
			@Override
			public void run() {
				Connection conn = null;
				PreparedStatement stmt = null;
				ResultSet rs = null;
				try {
					final boolean stored;
					conn = DatabaseConnection.getConnection();
					stmt =
							conn.prepareStatement("SELECT * FROM `quester-profiles` WHERE `name`='"
									+ playerName + "'");
					rs = stmt.executeQuery();
					stored = rs.next();
					rs.close();
					stmt.close();
					if(!stored || serp.changed) { // only save if it has changed, or is not stored
						stmt =
								conn.prepareStatement(stored ? serp
										.getUpdateQuerry("quester-profiles") : serp
										.getInsertQuerry("quester-profiles"));
						stmt.execute();
					}
				}
				catch (final SQLException e) {
					Ql.warning("Failed to save " + playerName + "'s profile to database.");
					Ql.debug("Exception", e);
					return;
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
		
		new Thread(saveTask, "Quester - " + playerName + "'s Profile Saving").start();
	}
}
