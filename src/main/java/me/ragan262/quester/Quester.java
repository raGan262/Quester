package me.ragan262.quester;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.management.InstanceNotFoundException;

import me.ragan262.quester.QConfiguration.StorageType;
import me.ragan262.quester.commandbase.CommandManager;
import me.ragan262.quester.commandbase.exceptions.QCommandException;
import me.ragan262.quester.commandbase.exceptions.QPermissionException;
import me.ragan262.quester.commandbase.exceptions.QUsageException;
import me.ragan262.quester.commands.AdminCommands;
import me.ragan262.quester.commands.ModificationCommands;
import me.ragan262.quester.commands.UserCommands;
import me.ragan262.quester.conditions.*;
import me.ragan262.quester.elements.Element;
import me.ragan262.quester.elements.ElementManager;
import me.ragan262.quester.exceptions.*;
import me.ragan262.quester.holder.QuestHolderManager;
import me.ragan262.quester.holder.QuesterTrait;
import me.ragan262.quester.lang.LanguageManager;
import me.ragan262.quester.listeners.*;
import me.ragan262.quester.objectives.*;
import me.ragan262.quester.profiles.ProfileListener;
import me.ragan262.quester.profiles.ProfileManager;
import me.ragan262.quester.qevents.*;
import me.ragan262.quester.quests.QuestManager;
import me.ragan262.quester.triggers.*;
import me.ragan262.quester.utils.DatabaseConnection;
import me.ragan262.quester.utils.Ql;
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

public class Quester extends JavaPlugin {
	
	private static Quester instance = null;
	
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
	public static boolean vault = false;
	public static boolean denizen = false;
	
	public static final String LABEL = ChatColor.BLUE + "[" + ChatColor.GOLD + "Quester"
			+ ChatColor.BLUE + "] ";
	
	public Quester() {
		instance = this;
	}
	
	@Override
	public void onEnable() {
		
		Ql.init(this);
		
		QConfiguration.createInstance(this);
		try {
			QConfiguration.loadData();
		}
		catch (final InstanceNotFoundException e1) {
			Ql.severe("DataManager instance exception. Disabling quester...");
			getPluginLoader().disablePlugin(this);
			return;
		}
		
		// Managers
		loadLangs();
		
		elements = new ElementManager();
		ElementManager.setInstance(elements);
		quests = new QuestManager(this);
		profiles = new ProfileManager(this);
		quests.setProfileManager(profiles); // loading conflicts...
		holders = new QuestHolderManager(this);
		commands = new CommandManager(langs, getLogger(), QConfiguration.displayedCmd, this);
		
		registerElements();
		if(QConfiguration.useRank) {
			profiles.loadRanks();
		}
		holders.loadHolders();
		
		if(QConfiguration.useMetrics) {
			try {
				final Metrics metrics = new Metrics(this);
				metrics.start();
			}
			catch (final IOException e) {
				// Failed to submit the statistics :-(
			}
		}
		
		if(setupEconomy()) {
			Ql.info("Vault found and hooked...");
		}
		
		if(setupCitizens()) {
			Ql.info("Citizens 2 found and hooked...");
		}
		if(setupDenizen()) {
			Ql.info("Denizen found and hooked...");
		}
		
		setupListeners();
		
		commands.register(UserCommands.class);
		commands.register(AdminCommands.class);
		commands.register(ModificationCommands.class);
		
		if(QConfiguration.profileStorageType == StorageType.MYSQL) {
			Connection conn = null;
			Statement stmt = null;
			try {
				DatabaseConnection.initialize(QConfiguration.mysqlUrl, QConfiguration.mysqlUser,
						QConfiguration.mysqlPass);
				Ql.info("Successfully connected to the database...");
				conn = DatabaseConnection.getConnection();
				final DatabaseMetaData dmd = conn.getMetaData();
				if(!dmd.getTables(null, null, "quester-profiles", null).next()) {
					Ql.verbose("Creating table quester-profiles...");
					stmt = conn.createStatement();
					stmt.execute("CREATE TABLE `quester-profiles` ( name VARCHAR(50) NOT NULL, completed TEXT, current SMALLINT(6), quests TEXT, reputation TEXT, PRIMARY KEY (name) );");
					if(!dmd.getTables(null, null, "quester-profiles", null).next()) {
						throw new SQLException("Table creation failed.");
					}
					Ql.verbose("Table created.");
				}
			}
			catch (final Exception e) {
				Ql.severe("Failed to connect to the database, falling back to config...");
				Ql.debug("Error report: ", e);
				QConfiguration.profileStorageType = StorageType.CONFIG;
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
			}
		}
		
		if(getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			@Override
			public void run() {
				langs.saveLanguages();
				quests.loadQuests();
				profiles.loadProfiles(QConfiguration.profileStorageType, false);
				holders.checkHolders();
				
			}
		}, 1L) == -1) {
			Ql.severe("Failed to schedule loading task. Disabling Quester...");
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
			quests.saveQuests();
			profiles.saveProfiles(QConfiguration.profileStorageType, false);
			holders.saveHolders();
			Ql.verbose("Quester data saved.");
		}
		DatabaseConnection.close();
		econ = null;
		citizens2 = false;
		vault = false;
		denizen = false;
	}
	
	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		if(label.equalsIgnoreCase("q") || label.equalsIgnoreCase("quest")
				|| label.equalsIgnoreCase("quester")) {
			try {
				commands.execute(args, sender);
			}
			catch (final QuesterException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
			}
			catch (final QCommandException e) {
				if(e instanceof QUsageException) {
					sender.sendMessage(ChatColor.RED + e.getMessage());
					sender.sendMessage(ChatColor.RED
							+ langs.getPlayerLang(sender.getName()).get("USAGE_LABEL")
							+ ((QUsageException) e).getUsage());
				}
				else if(e instanceof QPermissionException) {
					sender.sendMessage(ChatColor.RED + langs.getDefaultLang().get("MSG_PERMS"));
				}
				else {
					sender.sendMessage(ChatColor.RED + e.getMessage());
				}
			}
			catch (final NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + "Number expected, but "
						+ e.getMessage().replaceFirst(".+ \"", "\"") + " found. ");
			}
			catch (final IllegalArgumentException e) {
				sender.sendMessage(ChatColor.RED + "Invalid argument: '" + e.getMessage() + "'");
			}
			return true;
		}
		return false;
	}
	
	public static Quester getInstance() {
		return instance;
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
	
	private void loadLangs() {
		try {
			langs =
					new LanguageManager(this, new File(getDataFolder() + File.separator + "local"
							+ File.separator), QConfiguration.defaultLang);
		}
		catch (final Exception e) {
			Ql.severe("Failed to load languages.");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		langs.loadCustomMessages(new File(getDataFolder(), "messages.yml"));
		
		final int langCount = langs.loadLangs();
		Ql.info(langCount + (langCount == 1 ? " language" : " languages") + " loaded.");
	}
	
	private boolean setupEconomy() {
		if(getServer().getPluginManager().getPlugin("Vault") == null) {
			Ql.warning("Vault not found, economy support disabled.");
			return false;
		}
		final RegisteredServiceProvider<Economy> rsp =
				getServer().getServicesManager().getRegistration(Economy.class);
		if(rsp == null) {
			Ql.warning("Economy plugin not found, economy support disabled.");
			return false;
		}
		econ = rsp.getProvider();
		vault = true;
		return true;
	}
	
	private boolean setupCitizens() {
		try {
			Class.forName("net.citizensnpcs.api.CitizensAPI");
		}
		catch (final Exception e) {
			return false;
		}
		final TraitFactory factory = CitizensAPI.getTraitFactory();
		final TraitInfo info = TraitInfo.create(QuesterTrait.class).withName("quester");
		factory.registerTrait(info);
		citizens2 = true;
		return true;
	}
	
	private boolean setupDenizen() {
		if(citizens2) {
			denizen = getServer().getPluginManager().getPlugin("Denizen") != null;
		}
		if(denizen) {
			try {
				denizen = Class.forName("net.aufdemrand.denizen.npc.dNPC") != null;
			}
			catch (final Exception e) {
				denizen = false;
			}
			if(!denizen) {
				Ql.warning("Incorrect denizen version found. Supported version is 0.8.8 or newer.");
			}
		}
		return denizen;
	}
	
	private void setupListeners() {
		// OLD LISTENER
		// getServer().getPluginManager().registerEvents(new MoveListener(), this);
		
		// NEW CHECKER
		final PositionListener posCheck = new PositionListener(this);
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
		getServer().getPluginManager().registerEvents(new ChatListener(this), this);
		getServer().getPluginManager().registerEvents(new QuestItemListener(), this);
		if(citizens2) {
			getServer().getPluginManager().registerEvents(new Citizens2Listener(this), this);
		}
		getServer().getPluginManager().registerEvents(new ProfileListener(this), this);
	}
	
	private void registerElements() {
		@SuppressWarnings("unchecked")
		final Class<? extends Element>[] classes = new Class[] {
				// conditions
				ItemCondition.class,
				MoneyCondition.class,
				PermissionCondition.class,
				PointCondition.class,
				QuestCondition.class,
				QuestNotCondition.class,
				TimeCondition.class,
				SlotCondition.class,
				ExperienceCondition.class,
				
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
				SoundQevent.class,
				ProgressQevent.class,
				
				// objectives
				BreakObjective.class,
				CollectObjective.class,
				CraftObjective.class,
				DeathObjective.class,
				EnchantObjective.class,
				ExpObjective.class,
				FishObjective.class,
				ItemObjective.class,
				RegionObjective.class,
				MilkObjective.class,
				MobKillObjective.class,
				MoneyObjective.class,
				PlaceObjective.class,
				PlayerKillObjective.class,
				ShearObjective.class,
				SmeltObjective.class,
				TameObjective.class,
				LocObjective.class,
				WorldObjective.class,
				ActionObjective.class,
				NpcObjective.class,
				DyeObjective.class,
				NpcKillObjective.class,
				DropObjective.class,
				ChatObjective.class,
				DummyObjective.class,
				
				// triggers
				NpcTrigger.class,
				RegionTrigger.class };
		for(final Class<? extends Element> clss : classes) {
			try {
				elements.register(clss);
			}
			catch (final ElementException e) {
				Ql.warning("(" + clss.getSimpleName() + ") Failed to register quester element: "
						+ e.getMessage());
			}
		}
	}
	
	public boolean startSaving() {
		if(saveID == 0) {
			if(QConfiguration.saveInterval > 0) {
				saveID =
						getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
							
							@Override
							public void run() {
								profiles.saveProfiles();
							}
						}, QConfiguration.saveInterval * 20L * 60L,
								QConfiguration.saveInterval * 20L * 60L);
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
