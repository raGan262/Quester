package com.gmail.molnardad.quester;

import java.io.IOException;
import java.util.Random;
import java.util.logging.Logger;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitFactory;
import net.citizensnpcs.api.trait.TraitInfo;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import com.gmail.molnardad.quester.listeners.*;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.commandbase.exceptions.QPermissionException;
import com.gmail.molnardad.quester.commandbase.exceptions.QUsageException;
import com.gmail.molnardad.quester.commands.AdminCommands;
import com.gmail.molnardad.quester.commands.ModificationCommands;
import com.gmail.molnardad.quester.commands.UserCommands;
import com.gmail.molnardad.quester.conditions.*;
import com.gmail.molnardad.quester.objectives.*;
import com.gmail.molnardad.quester.qevents.*;
import com.gmail.molnardad.quester.config.*;
import com.gmail.molnardad.quester.elements.Element;
import com.gmail.molnardad.quester.exceptions.*;

public class Quester extends JavaPlugin {

		public static Logger log = null;
		public static Random randGen = new Random();
		public static Economy econ = null;
		
		public ProfileConfig profileConfig = null;
		public QuestConfig questConfig = null;
		public HolderConfig holderConfig = null;
		public YamlConfiguration config = null;
		
		private DataManager data = null;
		private LanguageManager langMan = null;
		private ElementManager elements = null;
		private QCommandManager commands = null;
		
		private boolean loaded = false;
		private int saveID = 0;
		
		public static boolean citizens2 = false;
		public static boolean epicboss = false;
		public static boolean vault = false;
		public static boolean denizen = false;

		
		public static final String LABEL = ChatColor.BLUE
				+ "[" + ChatColor.GOLD + "Quester" + ChatColor.BLUE + "] ";
		
		@Override
		public void onEnable() {
			
			log = this.getLogger();
			//Data first
			data = new DataManager(this);
			DataManager.setInstance(data);
			this.initializeConfig();
			//Load languages
			langMan = new LanguageManager(this);
			LanguageManager.setInstance(langMan);
			this.loadLocal();
			
			QuestManager.setInstance(new QuestManager(this));
			
			elements = new ElementManager();
			
			registerElements();
			
			//Load configs TODO load after all other plugins
			profileConfig = new ProfileConfig(this, "profiles.yml");
			questConfig = new QuestConfig(this, "quests.yml");
			holderConfig = new HolderConfig(this, "holders.yml");
			data.loadQuests();
			data.loadProfiles();
			data.loadHolders();	
			
			try {
			    Metrics metrics = new Metrics(this);
			    metrics.start();
			} catch (IOException e) {
			    // Failed to submit the stats :-(
			}
			
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
			
			this.setupListeners();
			
			commands = new QCommandManager(this);
			
			commands.register(UserCommands.class);
			commands.register(AdminCommands.class);
			commands.register(ModificationCommands.class);
			
			startSaving();
			loaded = true;
		}

		@Override
		public void onDisable() {
			if(loaded) {
				stopSaving();
				data.saveQuests();
				data.saveProfiles();
				data.saveHolders();
				if(data.verbose) {
					log.info("Quester data saved.");
				}
			}
			log = null;
			econ = null;
			citizens2 = false;
			epicboss = false;
			vault = false;
			denizen = false;
			QuestManager.setInstance(null);
			LanguageManager.setInstance(null);
			DataManager.setInstance(null);
		}
		
		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			if(label.equalsIgnoreCase("q")
					|| label.equalsIgnoreCase("quest")
					|| label.equalsIgnoreCase("quester")) {
				try {
					commands.execute(args, sender);
				}
				catch (QuesterException e) {
					sender.sendMessage(ChatColor.RED + e.getMessage());
				}
				catch (QCommandException e) {
					if(e instanceof QUsageException) {
						sender.sendMessage(ChatColor.RED + e.getMessage());
						sender.sendMessage(ChatColor.RED + langMan.getPlayerLang(sender.getName()).USAGE_LABEL
								+ ((QUsageException) e).getUsage());
					}
					else if(e instanceof QPermissionException) {
						sender.sendMessage(ChatColor.RED + langMan.getDefaultLang().MSG_PERMS);
					}
					else {
						sender.sendMessage(ChatColor.RED + e.getMessage());
					}
				}
				catch (NumberFormatException e) {
					sender.sendMessage(ChatColor.RED + "Number expected, but " + e.getMessage().replaceFirst(".+ \"", "\"") + " found. ");
				}
				return true;
			}
			return false;
		}
		
		public QCommandManager getCommandManager() {
			return commands;
		}
		
		public ElementManager getElementManager() {
			return elements;
		}
		
		private boolean setupEconomy() {
			if (getServer().getPluginManager().getPlugin("Vault") == null) {
				log.info("Vault not found, economy support disabled.");
	            return false;
	        }
	        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
	        if (rsp == null) {
				log.info("Economy plugin not found, economy support disabled.");
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
			epicboss = (getServer().getPluginManager().getPlugin("EpicBoss") != null); //TODO EpicBossRecoded
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
			config = (new BaseConfig(this, "config.yml")).getConfig();
			if(data.verbose) {
				log.info("Config loaded.");
				log.info(data.ranks.size() + " ranks loaded.");
			}
		}
		
		private void loadLocal() {
			if(langMan == null) {
				log.info("Failed to load languages: LanguageManager null");
			}
			langMan.loadLang("english", "langEN");
			int i = 1;
			if(config.isConfigurationSection("languges")) {
				ConfigurationSection langSection = config.getConfigurationSection("languages");
				for(String key : langSection.getKeys(false)) {
					if(langSection.isString(key)) {
						langMan.loadLang(key, langSection.getString(key));
						i++;
					}
				}
			}
			log.info("Languages loaded. (" + i + ")");
		}
		
		public void reloadLocal() {
			if(langMan == null) {
				log.info("Failed to reload languages: LanguageManager null");
			}
			int i = 0;
			for(String lang : langMan.getLangSet()) {
				langMan.reloadLang(lang);
				i++;
			}
			log.info("Languages reloaded. (" + i + ")");
		}
		
		private void setupListeners() {
			// OLD LISTENER
			// getServer().getPluginManager().registerEvents(new MoveListener(), this);
			
			// NEW CHECKER
			PositionListener posCheck = new PositionListener();
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
		
		private void registerElements() {
			@SuppressWarnings("unchecked")
			Class<? extends Element>[] classes = new Class[]{
					// conditions 
					ItemCondition.class,
					MoneyCondition.class,
					PermissionCondition.class,
					PointCondition.class,
					QuestCondition.class,
					QuestNotCondition.class,
					
					// qevents
					CancelQevent.class,
					CommandQevent.class,
					ExplosionQevent.class,
					LightningQevent.class,
					MessageQevent.class,
					ObjectiveCompleteQevent.class,
					QuestQevent.class,
					SetBlockQevent.class,
					SpawnQevent.class,
					TeleportQevent.class,
					ToggleQevent.class,
					EffectQevent.class,
					ExperienceQevent.class,
					MoneyQevent.class,
					PointQevent.class,
					ItemQevent.class,
					
					// objectives
					BreakObjective.class,
					CollectObjective.class,
					CraftObjective.class,
					DeathObjective.class,
					EnchantObjective.class,
					ExpObjective.class,
					FishObjective.class,
					ItemObjective.class,
					LocObjective.class,
					MilkObjective.class,
					MobKillObjective.class,
					MoneyObjective.class,
					PlaceObjective.class,
					PlayerKillObjective.class,
					ShearObjective.class,
					SmeltObjective.class,
					TameObjective.class,
					WorldObjective.class,
					ActionObjective.class,
					NpcObjective.class,
					DyeObjective.class,
					BossObjective.class,
					NpcKillObjective.class
			};
			for(Class<? extends Element> clss : classes) {
				try {
					elements.register(clss);
				}
				catch (ElementException e) {
					log.warning("(" + clss.getSimpleName() + ") Failed to register quester element: " + e.getMessage());
				}
			}
		}
		
		public boolean startSaving() {
			if(saveID == 0) {
				if(data.saveInterval > 0) {
					saveID = getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
						
						@Override
						public void run() {
							data.saveProfiles();
						}
					}, data.saveInterval * 20L * 60L, data.saveInterval * 20L * 60L);
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
