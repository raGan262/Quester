package me.ragan262.quester;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import javax.management.InstanceNotFoundException;
import me.ragan262.commandmanager.CommandManager;
import me.ragan262.commandmanager.context.ContextFactory;
import me.ragan262.quester.QConfiguration.StorageType;
import me.ragan262.quester.commandmanager.QuesterCommandExceptionHandler;
import me.ragan262.quester.commandmanager.QuesterContextFactory;
import me.ragan262.quester.commands.AdminCommands;
import me.ragan262.quester.commands.ModificationCommands;
import me.ragan262.quester.commands.UserCommands;
import me.ragan262.quester.conditions.ExperienceCondition;
import me.ragan262.quester.conditions.ItemCondition;
import me.ragan262.quester.conditions.MoneyCondition;
import me.ragan262.quester.conditions.PermissionCondition;
import me.ragan262.quester.conditions.PointCondition;
import me.ragan262.quester.conditions.QuestCondition;
import me.ragan262.quester.conditions.QuestNotCondition;
import me.ragan262.quester.conditions.SlotCondition;
import me.ragan262.quester.conditions.TimeCondition;
import me.ragan262.quester.elements.Element;
import me.ragan262.quester.elements.ElementManager;
import me.ragan262.quester.exceptions.ElementException;
import me.ragan262.quester.holder.QuestHolderManager;
import me.ragan262.quester.holder.SignHolderActionHandler;
import me.ragan262.quester.lang.LanguageManager;
import me.ragan262.quester.lang.Messenger;
import me.ragan262.quester.listeners.ActionListener;
import me.ragan262.quester.listeners.BreakListener;
import me.ragan262.quester.listeners.ChatListener;
import me.ragan262.quester.listeners.CollectListener;
import me.ragan262.quester.listeners.CraftSmeltListener;
import me.ragan262.quester.listeners.DeathListener;
import me.ragan262.quester.listeners.DropListener;
import me.ragan262.quester.listeners.DyeListener;
import me.ragan262.quester.listeners.EnchantListener;
import me.ragan262.quester.listeners.FishListener;
import me.ragan262.quester.listeners.MilkListener;
import me.ragan262.quester.listeners.MobKillListener;
import me.ragan262.quester.listeners.PlaceListener;
import me.ragan262.quester.listeners.PositionListener;
import me.ragan262.quester.listeners.QuestItemListener;
import me.ragan262.quester.listeners.ShearListener;
import me.ragan262.quester.listeners.TameListener;
import me.ragan262.quester.objectives.ActionObjective;
import me.ragan262.quester.objectives.BreakObjective;
import me.ragan262.quester.objectives.ChatObjective;
import me.ragan262.quester.objectives.CollectObjective;
import me.ragan262.quester.objectives.CraftObjective;
import me.ragan262.quester.objectives.DeathObjective;
import me.ragan262.quester.objectives.DropObjective;
import me.ragan262.quester.objectives.DummyObjective;
import me.ragan262.quester.objectives.DyeObjective;
import me.ragan262.quester.objectives.EnchantObjective;
import me.ragan262.quester.objectives.ExpObjective;
import me.ragan262.quester.objectives.FishObjective;
import me.ragan262.quester.objectives.ItemObjective;
import me.ragan262.quester.objectives.LocObjective;
import me.ragan262.quester.objectives.MilkObjective;
import me.ragan262.quester.objectives.MobKillObjective;
import me.ragan262.quester.objectives.MoneyObjective;
import me.ragan262.quester.objectives.NpcKillObjective;
import me.ragan262.quester.objectives.NpcObjective;
import me.ragan262.quester.objectives.PlaceObjective;
import me.ragan262.quester.objectives.PlayerKillObjective;
import me.ragan262.quester.objectives.RegionObjective;
import me.ragan262.quester.objectives.ShearObjective;
import me.ragan262.quester.objectives.SmeltObjective;
import me.ragan262.quester.objectives.TameObjective;
import me.ragan262.quester.objectives.WorldObjective;
import me.ragan262.quester.profiles.ProfileListener;
import me.ragan262.quester.profiles.ProfileManager;
import me.ragan262.quester.qevents.CancelQevent;
import me.ragan262.quester.qevents.CommandQevent;
import me.ragan262.quester.qevents.EffectQevent;
import me.ragan262.quester.qevents.ExperienceQevent;
import me.ragan262.quester.qevents.ExplosionQevent;
import me.ragan262.quester.qevents.ItemQevent;
import me.ragan262.quester.qevents.LightningQevent;
import me.ragan262.quester.qevents.MessageQevent;
import me.ragan262.quester.qevents.MoneyQevent;
import me.ragan262.quester.qevents.ObjectiveCompleteQevent;
import me.ragan262.quester.qevents.PointQevent;
import me.ragan262.quester.qevents.ProgressQevent;
import me.ragan262.quester.qevents.QuestQevent;
import me.ragan262.quester.qevents.SetBlockQevent;
import me.ragan262.quester.qevents.SoundQevent;
import me.ragan262.quester.qevents.SpawnQevent;
import me.ragan262.quester.qevents.TeleportQevent;
import me.ragan262.quester.qevents.ToggleQevent;
import me.ragan262.quester.quests.QuestManager;
import me.ragan262.quester.triggers.NpcTrigger;
import me.ragan262.quester.triggers.RegionTrigger;
import me.ragan262.quester.utils.DatabaseConnection;
import me.ragan262.quester.utils.Ql;
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
	
	public static final String LABEL = ChatColor.BLUE + "[" + ChatColor.GOLD + "Quester"
			+ ChatColor.BLUE + "] ";
	public static boolean vault = false;
	public static Economy econ = null;
	
	private LanguageManager langs = null;
	private Messenger messages = null;
	private QuestManager quests = null;
	private ProfileManager profiles = null;
	private QuestHolderManager holders = null;
	private ElementManager elements = null;
	private CommandManager commands = null;
	
	private boolean enabled = false;
	private int saveID = 0;
	
	public Quester() {
		instance = this;
	}
	
	@Override
	public void onLoad() {
		try {
			langs = new LanguageManager(this, new File(getDataFolder() + File.separator + "local"
					+ File.separator));
		}
		catch(final Exception e) {
			Ql.severe("Failed to load languages.");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		
		langs.loadCustomMessages(new File(getDataFolder(), "messages.yml"));
		
		elements = new ElementManager();
		ElementManager.setInstance(elements);
		registerElements();
	}
	
	@Override
	public void onEnable() {
		// initialize and load configuration
		Ql.init(this);
		QConfiguration.createInstance(this);
		try {
			QConfiguration.loadData();
		}
		catch(final InstanceNotFoundException e1) {
			Ql.severe("DataManager instance exception. Disabling quester...");
			getPluginLoader().disablePlugin(this);
			return;
		}
		
		// load languages
		final int langCount = langs.loadLangs();
		Ql.info(langCount + (langCount == 1 ? " language" : " languages") + " loaded.");
		langs.setDefaultLang(QConfiguration.defaultLang);
		Ql.info("Default language is " + langs.getDefaultLangName() + ".");
		langs.saveLanguages();
		
		// create managers
		messages = new Messenger(langs);
		quests = new QuestManager(this);
		profiles = new ProfileManager(this);
		holders = new QuestHolderManager(quests, getDataFolder(), getLogger());
		
		// set up command manager
		final ContextFactory cf = new QuesterContextFactory(langs, profiles);
		commands = new CommandManager(cf, getLogger(), QConfiguration.displayedCmd, this);
		commands.setExceptionHandler(new QuesterCommandExceptionHandler(getLogger()));
		
		// metrics
		if(QConfiguration.useMetrics) {
			try {
				final Metrics metrics = new Metrics(this);
				metrics.start();
			}
			catch(final IOException e) {
				// Failed to submit the statistics :-(
			}
		}
		
		// hooks
		if(setupEconomy()) {
			Ql.info("Vault found and hooked...");
		}
		
		// listeners and commands
		setupListeners();
		
		commands.register(UserCommands.class);
		commands.register(AdminCommands.class);
		commands.register(ModificationCommands.class);
		
		// mysql initialization
		if(QConfiguration.profileStorageType == StorageType.MYSQL) {
			Connection conn = null;
			Statement stmt = null;
			try {
				DatabaseConnection.initialize(QConfiguration.mysqlUrl, QConfiguration.mysqlUser, QConfiguration.mysqlPass);
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
			catch(final Exception e) {
				Ql.severe("Failed to connect to the database, falling back to config...");
				Ql.debug("Error report: ", e);
				QConfiguration.profileStorageType = StorageType.CONFIG;
			}
			finally {
				if(conn != null) {
					try {
						conn.close();
					}
					catch(final SQLException ignore) {}
				}
				if(stmt != null) {
					try {
						stmt.close();
					}
					catch(final SQLException ignore) {}
				}
			}
		}
		
		// load data
		if(QConfiguration.useRank) {
			profiles.loadRanks();
		}
		holders.loadHolders();
		quests.loadQuests();
		profiles.loadProfiles(QConfiguration.profileStorageType, false);
		holders.checkHolders();
		
		// saving task
		startSaving();
		enabled = true;
	}
	
	@Override
	public void onDisable() {
		if(enabled) {
			stopSaving();
			quests.saveQuests();
			profiles.saveProfiles(QConfiguration.profileStorageType, false);
			holders.saveHolders();
			Ql.verbose("Quester data saved.");
		}
		DatabaseConnection.close();
		econ = null;
		vault = false;
	}
	
	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		commands.handleCommand(args, sender);
		return true;
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
	
	public Messenger getMessenger() {
		return messages;
	}
	
	public QuestHolderManager getHolderManager() {
		return holders;
	}
	
	private boolean setupEconomy() {
		if(getServer().getPluginManager().getPlugin("Vault") == null) {
			Ql.warning("Vault not found, economy support disabled.");
			return false;
		}
		final RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if(rsp == null) {
			Ql.warning("Economy plugin not found, economy support disabled.");
			return false;
		}
		econ = rsp.getProvider();
		vault = true;
		return true;
	}
	
	private void setupListeners() {
		// POSIITON CHECKER
		final PositionListener posCheck = new PositionListener(this);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, posCheck, 20, 20);
		
		getServer().getPluginManager().registerEvents(new SignHolderActionHandler(this), this);
		
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
		getServer().getPluginManager().registerEvents(new ActionListener(this), this);
		getServer().getPluginManager().registerEvents(new DyeListener(this), this);
		getServer().getPluginManager().registerEvents(new ChatListener(this), this);
		getServer().getPluginManager().registerEvents(new QuestItemListener(), this);
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
			catch(final ElementException e) {
				Ql.warning("(" + clss.getSimpleName() + ") Failed to register quester element: "
						+ e.getMessage());
			}
		}
	}
	
	public boolean startSaving() {
		if(saveID == 0) {
			if(QConfiguration.saveInterval > 0) {
				saveID = getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
					
					@Override
					public void run() {
						profiles.saveProfiles();
					}
				}, QConfiguration.saveInterval * 20L * 60L, QConfiguration.saveInterval * 20L * 60L);
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
