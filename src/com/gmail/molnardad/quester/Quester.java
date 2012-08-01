package com.gmail.molnardad.quester;

import java.util.logging.Logger;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitFactory;
import net.citizensnpcs.api.trait.TraitInfo;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.molnardad.quester.listeners.*;
import com.gmail.molnardad.quester.objectives.*;
import com.gmail.molnardad.quester.rewards.*;

public class Quester extends JavaPlugin {
	
		public static Quester plugin = null;
		public static Logger log = null;
		public static Permission perms = null;
		public static Economy econ = null;
		public static QuestManager qMan = null;
		public static boolean citizens2 = false;
		public static ProfileConfig profileConfig;
		public static QuestConfig questConfig;
		private boolean loaded = false;
		private int saveID = 0;

		public YamlConfiguration config = null;
		
		public static final String LABEL = ChatColor.BLUE + "[" + ChatColor.GOLD + "Quester" + ChatColor.BLUE + "] ";
		
		public Quester() {
			super();
			plugin = this;
			ConfigurationSerialization.registerClass(PlayerProfile.class);
			ConfigurationSerialization.registerClass(Quest.class);
			ConfigurationSerialization.registerClass(BreakObjective.class);
			ConfigurationSerialization.registerClass(DeathObjective.class);
			ConfigurationSerialization.registerClass(ExpObjective.class);
			ConfigurationSerialization.registerClass(ItemObjective.class);
			ConfigurationSerialization.registerClass(LocObjective.class);
			ConfigurationSerialization.registerClass(MobKillObjective.class);
			ConfigurationSerialization.registerClass(PlayerKillObjective.class);
			ConfigurationSerialization.registerClass(WorldObjective.class);
			ConfigurationSerialization.registerClass(EffectReward.class);
			ConfigurationSerialization.registerClass(ExpReward.class);
			ConfigurationSerialization.registerClass(ItemReward.class);
			ConfigurationSerialization.registerClass(MoneyReward.class);
			ConfigurationSerialization.registerClass(TeleportReward.class);
		}
		
		@Override
		public void onEnable() {
			
			log = this.getLogger();
			qMan = new QuestManager();
			profileConfig = new ProfileConfig("profiles.yml");
			questConfig = new QuestConfig("quests.yml");
			
			if(!this.setupEconomy()) {
				return;
			}
			
			this.setupPerms();
			
			this.initializeConfig();
			
			if(this.setupCitizens()) {
				log.info("Citizens 2 found and hooked...");
			}
			
			QuestData.loadQuests();
			QuestData.loadProfiles();
			
			
			this.setupListeners();
			
			QuesterCommandExecutor cmdExecutor = new QuesterCommandExecutor();
			getCommand("quest").setExecutor(cmdExecutor);
			
			startSaving();
			loaded = true;
		}

		@Override
		public void onDisable() {
			if(loaded) {
				stopSaving();
				QuestData.saveQuests();
				QuestData.saveProfiles();
				if(QuestData.verbose) {
					log.info("Quester data saved.");
				}
			}
			QuestData.wipeData();
			plugin = null;
			log = null;
			perms = null;
			econ = null;
			qMan = null;
		}

		private void setupPerms() {
			RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
	        perms = rsp.getProvider();
	        if(perms == null){
	            log.info("Permissions hook failed, disabling Quester.");
	            getServer().getPluginManager().disablePlugin(this);
	        }
		}

		private boolean setupEconomy() {
			if (getServer().getPluginManager().getPlugin("Vault") == null) {
	            log.info("Vault not found, disabling Quester.");
	            getServer().getPluginManager().disablePlugin(this);
	            return false;
	        }
	        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
	        if (rsp == null) {
	            log.info("Economy plugin not found, disabling Quester.");
	            getServer().getPluginManager().disablePlugin(this);
	            return false;
	        }
	        econ = rsp.getProvider();
	        return true;
		}
		
		private boolean setupCitizens() {
			try{
				Class.forName("net.citizensnpcs.api.CitizensAPI");
			} catch(Exception e) {
				return false;
			}
			TraitFactory factory = CitizensAPI.getTraitFactory();
		    TraitInfo info = TraitInfo.create(QuesterTrait.class).withName("quester");
		    factory.registerTrait(info);
		    citizens2 = true;
		    return true;
		}

		public void initializeConfig() {
			config = (new BaseConfig("config.yml")).getConfig();
			if(QuestData.verbose) {
				log.info("Config loaded.");
			}
		}
		
		private void setupListeners() {
			getServer().getPluginManager().registerEvents(new BreakListener(), this);
			getServer().getPluginManager().registerEvents(new MoveListener(), this);
			getServer().getPluginManager().registerEvents(new DeathListener(), this);
			getServer().getPluginManager().registerEvents(new MobKillListener(), this);
			if(citizens2) {
				getServer().getPluginManager().registerEvents(new Citizens2Listener(), this);
			}
		}
		
		public boolean startSaving() {
			if(saveID == 0) {
				if(QuestData.saveInterval > 0) {
					saveID = getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
						
						@Override
						public void run() {
							QuestData.saveProfiles();
						}
					}, QuestData.saveInterval * 20L * 60L, QuestData.saveInterval * 20L * 60L);
				}
				return true;
			}
			return false;
		}
		
		public boolean stopSaving() {
			if(saveID != 0) {
				getServer().getScheduler().cancelTask(saveID);
				saveID = 0;
				return true;
			}
			return false;
		}
}
