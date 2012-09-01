package com.gmail.molnardad.quester;

import java.lang.reflect.Field;
import java.util.Random;
import java.util.logging.Logger;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitFactory;
import net.citizensnpcs.api.trait.TraitInfo;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.molnardad.quester.listeners.*;
import com.gmail.molnardad.quester.objectives.*;
import com.gmail.molnardad.quester.qevents.*;
import com.gmail.molnardad.quester.rewards.*;
import com.gmail.molnardad.quester.conditions.*;
import com.gmail.molnardad.quester.config.*;

public class Quester extends JavaPlugin {
	
		public static Quester plugin = null;
		public static Logger log = null;
		public static Random randGen = new Random();
		public static Permission perms = null;
		public static Economy econ = null;
		public static QuestManager qMan = null;
		public static boolean citizens2 = false;
		public static ProfileConfig profileConfig;
		public static QuestConfig questConfig;
		public static HolderConfig holderConfig;
		public static QuesterStrings strings;
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
			ConfigurationSerialization.registerClass(PlaceObjective.class);
			ConfigurationSerialization.registerClass(DeathObjective.class);
			ConfigurationSerialization.registerClass(ExpObjective.class);
			ConfigurationSerialization.registerClass(ItemObjective.class);
			ConfigurationSerialization.registerClass(LocObjective.class);
			ConfigurationSerialization.registerClass(MobKillObjective.class);
			ConfigurationSerialization.registerClass(PlayerKillObjective.class);
			ConfigurationSerialization.registerClass(WorldObjective.class);
			ConfigurationSerialization.registerClass(CraftObjective.class);
			ConfigurationSerialization.registerClass(EnchantObjective.class);
			ConfigurationSerialization.registerClass(ShearObjective.class);
			ConfigurationSerialization.registerClass(FishObjective.class);
			ConfigurationSerialization.registerClass(MilkObjective.class);
			ConfigurationSerialization.registerClass(SmeltObjective.class);
			ConfigurationSerialization.registerClass(CollectObjective.class);
			ConfigurationSerialization.registerClass(TameObjective.class);
			ConfigurationSerialization.registerClass(MoneyObjective.class);
			
			ConfigurationSerialization.registerClass(EffectReward.class);
			ConfigurationSerialization.registerClass(ExpReward.class);
			ConfigurationSerialization.registerClass(ItemReward.class);
			ConfigurationSerialization.registerClass(MoneyReward.class);
			ConfigurationSerialization.registerClass(TeleportReward.class);
			ConfigurationSerialization.registerClass(CommandReward.class);
			ConfigurationSerialization.registerClass(PointReward.class);
			
			ConfigurationSerialization.registerClass(QuestCondition.class);
			ConfigurationSerialization.registerClass(QuestNotCondition.class);
			ConfigurationSerialization.registerClass(PermissionCondition.class);
			ConfigurationSerialization.registerClass(MoneyCondition.class);
			ConfigurationSerialization.registerClass(ItemCondition.class);
			ConfigurationSerialization.registerClass(PointCondition.class);

			ConfigurationSerialization.registerClass(MessageQevent.class);
			ConfigurationSerialization.registerClass(ExplosionQevent.class);
			ConfigurationSerialization.registerClass(SetBlockQevent.class);
			ConfigurationSerialization.registerClass(TeleportQevent.class);
			ConfigurationSerialization.registerClass(CommandQevent.class);
			ConfigurationSerialization.registerClass(LightningQevent.class);
			ConfigurationSerialization.registerClass(QuestQevent.class);
			ConfigurationSerialization.registerClass(CancelQevent.class);
		}
		
		@Override
		public void onEnable() {
			
			log = this.getLogger();
			qMan = new QuestManager();
			profileConfig = new ProfileConfig("profiles.yml");
			questConfig = new QuestConfig("quests.yml");
			holderConfig = new HolderConfig("holders.yml");
			
			if(!this.setupEconomy()) {
				return;
			}
			
			this.setupPerms();
		
			if(this.setupCitizens()) {
				log.info("Citizens 2 found and hooked...");
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
					} catch (IllegalArgumentException | IllegalAccessException e) {
						Quester.log.info("Error occured while setting values in local file.");
						if(QuestData.debug) {
							e.printStackTrace();
						}
					}
				} else {
					try {
						f.set(strings, (String) val.replaceAll("%n", "\n"));
					} catch (IllegalArgumentException | IllegalAccessException e) {
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
