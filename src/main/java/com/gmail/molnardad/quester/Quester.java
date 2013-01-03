package com.gmail.molnardad.quester;

import java.lang.reflect.Field;
import java.util.Random;
import java.util.logging.Logger;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitFactory;
import net.citizensnpcs.api.trait.TraitInfo;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.molnardad.quester.listeners.*;
import com.gmail.molnardad.quester.config.*;

public class Quester extends JavaPlugin {
	
		public static Quester plugin = null;
		public static Logger log = null;
		public static Random randGen = new Random();
		public static Economy econ = null;
		public static QuestManager qMan = null;
		public static ProfileConfig profileConfig;
		public static QuestConfig questConfig;
		public static HolderConfig holderConfig;
		public static QuesterStrings strings;
		private boolean loaded = false;
		private int saveID = 0;
		public static boolean citizens2 = false;
		public static boolean epicboss = false;
		public static boolean vault = false;
		public static boolean denizen = false;

		public YamlConfiguration config = null;
		
		public static final String LABEL = ChatColor.BLUE + "[" + ChatColor.GOLD + "Quester" + ChatColor.BLUE + "] ";
		
		public Quester() {
			super();
			plugin = this;
		}
		
		@Override
		public void onEnable() {
			
			log = this.getLogger();
			qMan = new QuestManager();
			profileConfig = new ProfileConfig("profiles.yml");
			questConfig = new QuestConfig("quests.yml");
			holderConfig = new HolderConfig("holders.yml");
			
			if(this.setupEconomy()) {
				log.info("Vault found and hooked...");
			}
		
			if(this.setupCitizens()) {
				log.info("Citizens 2 found and hooked...");
			}
			if(this.setupDenizen()) {
				log.info("Denizen found and hooked...");
			}
			if(this.setupEpicBoss()) {
				log.info("EpicBoss found and hooked...");
			}
			
			this.initializeConfig();

			loadLocal();
			
			QuestData.loadQuests();
			QuestData.loadProfiles();
			QuestData.loadHolders();
			
			
			this.setupListeners();
			
			QuesterCommandExecutor cmdExecutor = new QuesterCommandExecutor();
			getCommand("q").setExecutor(cmdExecutor);
			
			startSaving();
			loaded = true;
		}

		@Override
		public void onDisable() {
			if(loaded) {
				stopSaving();
				QuestData.saveQuests();
				QuestData.saveProfiles();
				QuestData.saveHolders();
				if(QuestData.verbose) {
					log.info("Quester data saved.");
				}
			}
			QuestData.wipeData();
			plugin = null;
			log = null;
			econ = null;
			qMan = null;
			citizens2 = false;
			epicboss = false;
			vault = false;
			denizen = false;
		}

		private boolean setupEconomy() {
			if (getServer().getPluginManager().getPlugin("Vault") == null) {
				log.info("Vault not found, economy support disabled.");
	            return false;
	        }
	        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
	        if (rsp == null) {
				log.info("Economy plugin not found, economy support disabled.");
	            getServer().getPluginManager().disablePlugin(this);
	            return false;
	        }
	        econ = rsp.getProvider();
	        vault = true;
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
		
		private boolean setupEpicBoss() {
			epicboss = (getServer().getPluginManager().getPlugin("EpicBoss") != null);
		    return epicboss;
		}
		
		private boolean setupDenizen() {
			if(citizens2) {
				denizen = (getServer().getPluginManager().getPlugin("Denizen") != null);
			}
			if(denizen) {
				try {
					denizen = Class.forName("net.aufdemrand.denizen.scripts.ScriptBuilder")
							.getDeclaredMethod("runTaskScript", org.bukkit.entity.Player.class, String.class)
							.getReturnType().equals(boolean.class);
				} catch (Exception e) {
					denizen = false;
				}
				if(!denizen) {
					log.info("Incorrect denizen version found. 0.8-656 or higher supported.");
				} 
			}
			return denizen;
		}

		public void initializeConfig() {
			config = (new BaseConfig("config.yml")).getConfig();
			if(QuestData.verbose) {
				log.info("Config loaded.");
				log.info(QuestData.ranks.size() + " ranks loaded.");
			}
		}
		
		public void loadLocal() {
			strings = new QuesterStrings("local.yml");
			Class<? extends CustomConfig> qsclass = strings.getClass();
			YamlConfiguration conf = strings.getConfig();
			for(Field f : qsclass.getFields()) {
				String val = conf.getString(f.getName(), "");
				if(val.isEmpty()) {
					try {
						conf.set(f.getName(),((String)f.get(strings)).replaceAll("\\n", "%n"));
						if(QuestData.debug) {
							Quester.log.info(f.getName() + " reset to default.");
						}
					} catch (Exception e) {
						Quester.log.info("Error occured while setting values in local file.");
						if(QuestData.debug) {
							e.printStackTrace();
						}
					}
				} else {
					try {
						f.set(strings, (String) val.replaceAll("%n", "\n"));
					} catch (Exception e) {
						Quester.log.info("Error occured while setting values in local object.");
						if(QuestData.debug) {
							e.printStackTrace();
						}
					}
				}
			}
			strings.saveConfig();
			log.info("Local file loaded.");
		}
		
		private void setupListeners() {
			// OLD LISTENER
			// getServer().getPluginManager().registerEvents(new MoveListener(), this);
			
			// NEW CHECKER
			PositionListener posCheck = new PositionListener(qMan);
			Bukkit.getScheduler().scheduleSyncRepeatingTask(this, posCheck, 20, 20);
			
			getServer().getPluginManager().registerEvents(new BreakListener(), this);
			getServer().getPluginManager().registerEvents(new DeathListener(), this);
			getServer().getPluginManager().registerEvents(new MobKillListener(), this);
			getServer().getPluginManager().registerEvents(new PlaceListener(), this);
			getServer().getPluginManager().registerEvents(new CraftSmeltListener(), this);
			getServer().getPluginManager().registerEvents(new EnchantListener(), this);
			getServer().getPluginManager().registerEvents(new ShearListener(), this);
			getServer().getPluginManager().registerEvents(new FishListener(), this);
			getServer().getPluginManager().registerEvents(new MilkListener(), this);
			getServer().getPluginManager().registerEvents(new CollectListener(), this);
			getServer().getPluginManager().registerEvents(new DropListener(), this);
			getServer().getPluginManager().registerEvents(new TameListener(), this);
			getServer().getPluginManager().registerEvents(new SignListeners(), this);
			getServer().getPluginManager().registerEvents(new ActionListener(), this);
			getServer().getPluginManager().registerEvents(new DyeListener(), this);
			if(citizens2) {
				getServer().getPluginManager().registerEvents(new Citizens2Listener(), this);
			}
			if(epicboss) {
				getServer().getPluginManager().registerEvents(new BossDeathListener(), this);
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
