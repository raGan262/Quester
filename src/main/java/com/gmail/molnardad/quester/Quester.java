package com.gmail.molnardad.quester;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Logger;

import javax.management.InstanceNotFoundException;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitFactory;
import net.citizensnpcs.api.trait.TraitInfo;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import com.gmail.molnardad.quester.listeners.*;
import com.gmail.molnardad.quester.managers.DataManager;
import com.gmail.molnardad.quester.managers.ElementManager;
import com.gmail.molnardad.quester.managers.LanguageManager;
import com.gmail.molnardad.quester.managers.CommandManager;
import com.gmail.molnardad.quester.managers.ProfileManager;
import com.gmail.molnardad.quester.managers.QuestHolderManager;
import com.gmail.molnardad.quester.managers.QuestManager;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.commandbase.exceptions.QPermissionException;
import com.gmail.molnardad.quester.commandbase.exceptions.QUsageException;
import com.gmail.molnardad.quester.commands.AdminCommands;
import com.gmail.molnardad.quester.commands.ModificationCommands;
import com.gmail.molnardad.quester.commands.UserCommands;
import com.gmail.molnardad.quester.conditions.*;
import com.gmail.molnardad.quester.objectives.*;
import com.gmail.molnardad.quester.qevents.*;
import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.elements.Element;
import com.gmail.molnardad.quester.exceptions.*;

public class Quester extends JavaPlugin {

		public static Logger log = null;
		public static Random randGen = new Random();
		public static Economy econ = null;
		
		private LanguageManager langs = null;
		private QuestManager quests = null;
		private ProfileManager profiles = null;
		private QuestHolderManager holders = null;
		private ElementManager elements = null;
		private CommandManager commands = null;
		
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
			
			DataManager.createInstance(this);
			try {
				DataManager.loadData();
			}
			catch (InstanceNotFoundException e1) {
				log.severe("DataManager instance exception. Disabling quester...");
				this.getPluginLoader().disablePlugin(this);
				return;
			}
			
			//Managers
			langs = new LanguageManager();
			elements = new ElementManager();
			ElementManager.setInstance(elements);
			quests = new QuestManager(this);
			profiles = new ProfileManager(this);
			quests.setProfileManager(profiles); // loading conflicts...
			holders = new QuestHolderManager(this);
			commands = new CommandManager(langs, log, DataManager.displayedCmd, this);

			this.loadLocal();
			registerElements();
			if(DataManager.useRank) {
				profiles.loadRanks();
			}
			holders.loadHolders();
			
			try {
			    Metrics metrics = new Metrics(this);
			    metrics.start();
			} catch (IOException e) {
			    // Failed to submit the statistics :-(
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
			
			commands.register(UserCommands.class);
			commands.register(AdminCommands.class);
			commands.register(ModificationCommands.class);
			
			if (getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
				public void run() {
					Quester.this.quests.loadQuests();
					Quester.this.profiles.loadProfiles();
					Quester.this.holders.checkHolders();
				}
			}, 1L) == -1) {
				Quester.log.severe("Failed to schedule loading task. Disabling Quester...");
				getServer().getPluginManager().disablePlugin(this);
				return;
			}
			
			startSaving();
			loaded = true;
		}

		@Override
		public void onDisable() {
			if(loaded) {
				stopSaving();
				// TODO SAVING
				// profiles
				// holders, signs
				// quests
				if(DataManager.verbose) {
					log.info("Quester data saved.");
				}
			}
			log = null;
			econ = null;
			citizens2 = false;
			epicboss = false;
			vault = false;
			denizen = false;
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
						sender.sendMessage(ChatColor.RED + langs.getPlayerLang(sender.getName()).USAGE_LABEL
								+ ((QUsageException) e).getUsage());
					}
					else if(e instanceof QPermissionException) {
						sender.sendMessage(ChatColor.RED + langs.getDefaultLang().MSG_PERMS);
					}
					else {
						sender.sendMessage(ChatColor.RED + e.getMessage());
					}
				}
				catch (NumberFormatException e) {
					sender.sendMessage(ChatColor.RED + "Number expected, but " + e.getMessage().replaceFirst(".+ \"", "\"") + " found. ");
				}
				catch (IllegalArgumentException e) {
					sender.sendMessage(ChatColor.RED + "Invalid argument: '" + e.getMessage() + "'");
				}
				return true;
			}
			return false;
		}
		
		public CommandManager getCommandManager() {
			return commands;
		}
		
		public ElementManager getElementManager() {
			return elements;
		}
		
		public QuestManager getQuestManager() {
			return quests;
		}
		
		public ProfileManager getProfileManager() {
			return profiles;
		}
		
		public LanguageManager getLanguageManager() {
			return langs;
		}
		
		public QuestHolderManager getHolderManager() {
			return holders;
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
			epicboss = (getServer().getPluginManager().getPlugin("EpicBossRecoded") != null);
		    return epicboss;
		}
		
		private boolean setupDenizen() {
			if(citizens2) {
				denizen = (getServer().getPluginManager().getPlugin("Denizen") != null);
			}
			if(denizen) {
				try {
					denizen = Class.forName("net.aufdemrand.denizen.npc.dNPC") != null;
				} catch (Exception e) {
					denizen = false;
				}
				if(!denizen) {
					log.info("Incorrect denizen version found. Supported version is 0.8.8 or newer.");
				} 
			}
			return denizen;
		}
		
		private void loadLocal() {
			if(langs == null) {
				log.info("Failed to load languages: LanguageManager null");
			}
			langs.loadLang("english", new File(getDataFolder(), "langEN.yml"));
			int i = 1;
			try {
				if(DataManager.getConfigKey("languges").hasSubKeys()) {
					for(StorageKey subKey : DataManager.getConfigKey("languges").getSubKeys()) {
						if(subKey.getString("") != null) {
							langs.loadLang(subKey.getName(), new File(getDataFolder(), subKey.getString("") + ".yml"));
							i++;
						}
					}
				}
			}
			catch (InstanceNotFoundException e) {
				log.severe("DataManager instance exception occured while loading languages.");
			}
			log.info("Languages loaded. (" + i + ")");
		}
		
		public void reloadLocal() {
			if(langs == null) {
				log.info("Failed to reload languages: LanguageManager null");
			}
			int i = 0;
			for(String lang : langs.getLangSet()) {
				langs.reloadLang(lang);
				i++;
			}
			log.info("Languages reloaded. (" + i + ")");
		}
		
		private void setupListeners() {
			// OLD LISTENER
			// getServer().getPluginManager().registerEvents(new MoveListener(), this);
			
			// NEW CHECKER
			PositionListener posCheck = new PositionListener(this);
			Bukkit.getScheduler().scheduleSyncRepeatingTask(this, posCheck, 20, 20);
			
			getServer().getPluginManager().registerEvents(new BreakListener(this), this);
			getServer().getPluginManager().registerEvents(new DeathListener(this), this);
			getServer().getPluginManager().registerEvents(new MobKillListener(this), this);
			getServer().getPluginManager().registerEvents(new PlaceListener(this), this);
			getServer().getPluginManager().registerEvents(new CraftSmeltListener(this), this);
			getServer().getPluginManager().registerEvents(new EnchantListener(this), this);
			getServer().getPluginManager().registerEvents(new ShearListener(this), this);
			getServer().getPluginManager().registerEvents(new FishListener(this), this);
			getServer().getPluginManager().registerEvents(new MilkListener(this), this);
			getServer().getPluginManager().registerEvents(new CollectListener(this), this);
			getServer().getPluginManager().registerEvents(new DropListener(this), this);
			getServer().getPluginManager().registerEvents(new TameListener(this), this);
			getServer().getPluginManager().registerEvents(new SignListeners(this), this);
			getServer().getPluginManager().registerEvents(new ActionListener(this), this);
			getServer().getPluginManager().registerEvents(new DyeListener(this), this);
			if(citizens2) {
				getServer().getPluginManager().registerEvents(new Citizens2Listener(this), this);
			}
			if(epicboss) {
				getServer().getPluginManager().registerEvents(new BossDeathListener(this), this);
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
					TimeCondition.class,
					
					// events
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
				if(DataManager.saveInterval > 0) {
					saveID = getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
						
						@Override
						public void run() {
							profiles.saveProfiles();
						}
					}, DataManager.saveInterval * 20L * 60L, DataManager.saveInterval * 20L * 60L);
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
