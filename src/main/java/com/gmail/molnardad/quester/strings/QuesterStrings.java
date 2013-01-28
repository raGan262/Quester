package com.gmail.molnardad.quester.strings;

public class QuesterStrings {

	public String MSG_ONLY_PLAYER = "This command can only be run by player.";
	public String MSG_PROFILES_SAVE = "Profiles saved.";
	public String MSG_AUTOSAVE_DISABLED = "AutoSaving is disabled in config.";
	public String MSG_AUTOSAVE_STARTED = "Saving started. Interval: %intervalm";// %interval = autosave interval;
	public String MSG_AUTOSAVE_RUNNING = "Saving already running.";
	public String MSG_AUTOSAVE_STOPPED = "Saving Stopped.";
	public String MSG_AUTOSAVE_NOT_RUNNING = "Saving not running.";
	public String MSG_CONFIG_RELOADED = "Quest configs reloaded.";
	public String MSG_PERMS = "You don't have permission for this.";
	public String MSG_Q_STARTED = "You have started quest %q";// %q = quest name;
	public String MSG_Q_COMPLETED = "Quest %q completed.";// %q = quest name;
	public String MSG_Q_CANCELLED = "Quest %q cancelled.";// %q = quest name;
	public String MSG_Q_DEACTIVATED = "One of your quests has been deactivated.";
	public String MSG_OBJ_COMPLETED = "You completed a quest objective.";

	
	public String INFO_NAME = "Name";
	public String INFO_DESCRIPTION = "Description";
	public String INFO_LOCATION = "Location";
	public String INFO_WORLDS = "Worlds";
	public String INFO_FLAGS = "Flags";
	public String INFO_CONDITIONS = "Conditions";
	public String INFO_OBJECTIVES = "Objectives";
	public String INFO_EVENTS = "Events";
	public String INFO_FIRST_OBJECTIVE = "First objective";
	public String INFO_QUEST_LIST = "Quest list";
	public String INFO_QUEST_INFO = "Quest info";
	public String INFO_PROGRESS = "%q progress";
	public String INFO_PROGRESS_HIDDEN = "Quest progress hidden";
	public String INFO_PROGRESS_COMPLETED = "Completed";
	public String INFO_PROFILE_POINTS = "Quest points";
	public String INFO_PROFILE_RANK = "Quester rank";
	public String INFO_PROFILE_COMPLETED = "Completed quests";
	public String INFO_PROFILE_NOT_EXIST = "%p does not have profile.";// %p = player name;
	public String INFO_HOLDER_LIST = "Holder list";

	public String HELP_SECTION_USE = "Quester help";
	public String HELP_SECTION_MODIFY = "Modify help";
	public String HELP_SECTION_MODIFY_SELECTED = "Applies only to selected quest";
	public String HELP_SECTION_MODIFY_HOLDER_SELECTED = "Applies only to selected quest holder";
	public String HELP_SECTION_ADMIN = "Admin help";
	public String HELP_VER = "version info";
	public String HELP_HELP = "this";
	public String HELP_LIST = "displays quest list";
	public String HELP_SHOW = "shows info about quest";
	public String HELP_START_PICK = "starts a quest";
	public String HELP_START_RANDOM = "starts random quest";
	public String HELP_CANCEL = "cancels current quest";
	public String HELP_DONE = "completes current objective";
	public String HELP_SWITCH = "switches current quest";
	public String HELP_PROGRESS = "shows quest progress";
	public String HELP_QUESTS = "shows accepted quests";
	public String HELP_PROFILE = "shows player's profile";
	public String HELP_CREATE = "creates a quest";
	public String HELP_REMOVE = "removes the quest";
	public String HELP_SELECT = "selects the quest";
	public String HELP_TOGGLE = "toggles state of the quest";
	public String HELP_INFO = "shows detailed info about the quest";
	public String HELP_NAME = "changes the name";
	public String HELP_DESC = "quest description manipulation";
	public String HELP_WORLD = "world restriction manipulation";
	public String HELP_MODIFIER = "quest modifier manipulation";
	public String HELP_CONDITION = "condition manipulation";
	public String HELP_OBJECTIVE = "objective manipulation";
	public String HELP_EVENT = "event manipulation";
	public String HELP_STARTSAVE = "starts scheduled profile saving";
	public String HELP_STOPSAVE = "stops scheduled profile saving";
	public String HELP_SAVE = "saves profiles";
	public String HELP_RELOAD = "reloads config and local file";
	public String HELP_LOCATION = "set the quest location";
	public String HELP_HOL_CREATE = "creates a holder";
	public String HELP_HOL_DELETE = "deletes a holder";
	public String HELP_HOL_ADD = "adds quest to holder";
	public String HELP_HOL_REMOVE = "removes quest from holder";
	public String HELP_HOL_MOVE = "moves quest in holder";
	public String HELP_HOL_LIST = "lists quest holders";
	public String HELP_HOL_INFO = "shows info about holder";
	public String HELP_HOL_SELECT = "selects holder";
	
	public String USAGE_LABEL = "Usage: ";
	public String USAGE_SHOW = "\"<quest name>\"";
	public String USAGE_INFO = "[quest_ID]";
	public String USAGE_START = "\"[quest name]\"";
	public String USAGE_CANCEL = "[quest_ID]";
	public String USAGE_SWITCH = "<index>";
	public String USAGE_QUESTS = "[player]";
	public String USAGE_PROFILE = "[player]";
	public String USAGE_CREATE = "\"<quest name>\"";
	public String USAGE_REMOVE = "<quest_ID>";
	public String USAGE_SELECT = "<quest_ID>";
	public String USAGE_NAME = "\"<new name>\"";
	public String USAGE_DESC_SET = "[description]";
	public String USAGE_DESC_ADD = "<description>";
	public String USAGE_LOC_SET = "{<location>} [range]";
	public String USAGE_MOD_AVAIL = "Available modifiers: ";
	public String USAGE_MOD_ADD = "<modifier1> ...";
	public String USAGE_MOD_REMOVE = "<modifier1> ...";
	public String USAGE_WORLD_ADD = "{<world>}";
	public String USAGE_WORLD_REMOVE = "{<world>}";
	
	// QUEST STRINGs
	
	public String Q_CREATED = "Quest created and selected.";
	public String Q_REMOVED = "Quest removed.";
	public String Q_SELECTED = "Quest selected.";
	public String Q_RENAMED = "Quest name changed to '%q'."; // %q = quest name;
	public String Q_DESC_SET = "Quest description set.";
	public String Q_DESC_ADDED = "Quest description added.";
	public String Q_LOC_SET = "Quest location set.";
	public String Q_LOC_REMOVED = "Quest location removed.";
	public String Q_MOD_ADDED = "Modifiers added.";
	public String Q_MOD_REMOVED = "Modifiers removed.";
	public String Q_ACTIVATED = "Quest activated.";
	public String Q_DEACTIVATED = "Quest deactivated.";
	public String Q_WORLD_ADDED = "Quest world added.";
	public String Q_WORLD_REMOVED = "Quest world removed.";
	public String Q_SWITCHED = "Quest switched.";
	
	// HOLDER STRINGS
	
	public String HOL_CREATED = "Holder created and selected.";
	public String HOL_REMOVED = "Holder deleted.";
	public String HOL_SELECTED = "Holder selected.";
	public String HOL_ASSIGNED = "Holder assigned.";
	public String HOL_UNASSIGNED = "Holder unassigned.";
	public String HOL_Q_ADDED = "Quest added to holder.";
	public String HOL_Q_REMOVED = "Quest removed from holder.";
	public String HOL_Q_MOVED = "Quest in holder moved.";
	public String HOL_CREATE_USAGE = "<holder name>";
	public String HOL_DELETE_USAGE = "<holder ID>";
	public String HOL_ADD_USAGE = "<quest ID>";
	public String HOL_REMOVE_USAGE = "<quest ID>";
	public String HOL_MOVE_USAGE = "<from> <to>";
	public String HOL_INFO_USAGE = "[holder ID]";
	public String HOL_SELECT_USAGE = "<holder ID>";

	// SIGN STRINGS
	
	public String SIGN_HEADER = "Sign quests";
	public String SIGN_REGISTERED = "Sign registered.";
	public String SIGN_UNREGISTERED = "Sign unregistered.";
	
	// OBJECTIVE STRINGS
	
	public String OBJ_ADD = "%type objective added."; // %type = objective type;
	public String OBJ_ADD_USAGE = "<objective type>";
	public String OBJ_LIST = "Available objective types";
	public String OBJ_REMOVE = "Objective %id removed."; // %id = objective ID;
	public String OBJ_REMOVE_USAGE = "<objective ID>.";
	
	public String OBJ_SWAP = "Objectives %id1 and %id2 swapped.";// %id1 = objective ID 1; %id2 = objective ID 2;
	public String OBJ_SWAP_USAGE = "<obj ID 1> <obj ID 2>";
	
	public String OBJ_MOVE = "Objective moved from %id1 to %id2.";// %id1 = objective ID 1; %id2 = objective ID 2;
	public String OBJ_MOVE_USAGE = "<ID from> <ID to>";
	
	public String OBJ_DESC_ADD = "Description to objective %id added."; // %id = objective ID;
	public String OBJ_DESC_ADD_USAGE = "<objective ID> <description>";
	public String OBJ_DESC_REMOVE = "Description of objective %id removed."; // %id = objective ID;
	
	public String OBJ_PREREQ_ADD = "Prerequisites to objective %id added."; // %id = objective ID;
	public String OBJ_PREREQ_ADD_USAGE = "<objective ID> <prerequisite1>...";
	public String OBJ_PREREQ_REMOVE = "Prerequisites of objective %id removed."; // %id = objective ID;
	public String OBJ_PREREQ_REMOVE_USAGE = "<objective ID> <prerequisite1>...";
	
	public String OBJ_BREAK_USAGE = "{<item>} <amount> [hand]";
	
	public String OBJ_PLACE_USAGE = "{<item>} <amount>";
	
	public String OBJ_ITEM_USAGE = "{<item>} [amount] {[ench1]}...";
	
	public String OBJ_COLLECT_USAGE = "{<item>} <amount>";
	
	public String OBJ_ENCHANT_USAGE = "{<item>} [amount] {[enchants]}";
	
	public String OBJ_EXP_USAGE = "<amount>";
	
	public String OBJ_LOC_USAGE = "{<location>} [range]";
	
	public String OBJ_DEATH_USAGE = "<amount> {[location]} [range]";
	
	public String OBJ_WORLD_USAGE = "{<world>}";
	
	public String OBJ_MOBKILL_USAGE = "<amount> {[entity]}";
	
	public String OBJ_KILL_USAGE = "<amount> [player] (-p)";
	
	public String OBJ_CRAFT_USAGE = "{<item>} <amount>";
	
	public String OBJ_SMELT_USAGE = "{<item>} <amount>";
	
	public String OBJ_SHEAR_USAGE = "<amount> {[color]}";
	
	public String OBJ_FISH_USAGE = "<amount>";
	
	public String OBJ_MILK_USAGE = "<amount>";
	
	public String OBJ_TAME_USAGE = "<amount> {[entity]}";
	
	public String OBJ_MONEY_USAGE = "<amount>";

	public String OBJ_ACTION_USAGE = "{<click>} {[block]} {[item]} {[location]} [range]";

	public String OBJ_NPC_USAGE = "<id> (-c)";
	
	public String OBJ_DYE_USAGE = "<amount> {[color]}";
	
	public String OBJ_BOSS_USAGE = "<name> [amount]";
	
	public String OBJ_NPCKILL_USAGE = "<name> [amount]";
	
	// CONDITION STRINGS

	public String CON_ADD = "%type condition added."; // %type = condition type;
	public String CON_ADD_USAGE = "<condition type> [args]";
	public String CON_LIST = "Available condition types";
	public String CON_REMOVE = "Condition %id removed."; // %id = condition ID;
	public String CON_REMOVE_USAGE = "<condition ID>";
	
	public String CON_DESC_ADD = "Description to condition %id added."; // %id = condition ID;
	public String CON_DESC_ADD_USAGE = "<condition ID> <description>";
	public String CON_DESC_REMOVE = "Description of condition %id removed."; // %id = condition ID;
	public String CON_DESC_REMOVE_USAGE = "<condition ID>";
	
	public String CON_QUEST_USAGE = "<quest_name> [time in seconds]";
	
	public String CON_QUESTNOT_USAGE = "<quest_name> [time in seconds]";
	
	public String CON_PERM_USAGE = "<quest_name>";
	
	public String CON_MONEY_USAGE = "<amount>";
	
	public String CON_ITEM_USAGE = "{<item>} <amount>";
	
	public String CON_POINT_USAGE = "<amount>";
	
	// EVENT STRINGS
	
	public String EVT_ADD = "%type event added."; // %type = event type;
	public String EVT_LIST = "Available event types";
	public String EVT_ADD_USAGE = "{<occasion>} <event_type> [args]";
	public String EVT_REMOVE = "Event %id removed.";// %id = event ID;
	public String EVT_REMOVE_USAGE = "<event ID>";
	public String EVT_SPECIFY = "Specify occasion and delay.";
	
	public String EVT_MSG_USAGE = "<message>";
	
	public String EVT_QUEST_USAGE = "<quest ID>";
	
	public String EVT_TOGGLE_USAGE = "<quest ID>";
	
	public String EVT_OBJCOM_USAGE = "<objective ID>";
	
	public String EVT_CMD_USAGE = "<command>";
	
	public String EVT_EXPLOSION_USAGE = "{<location>} [range] (-d)";
	
	public String EVT_LIGHTNING_USAGE = "{<location>} [range] (-d)";
	
	public String EVT_TELE_USAGE = "{<location>}";
	
	public String EVT_BLOCK_USAGE = "{<block>} {<location>}";
	
	public String EVT_SPAWN_USAGE = "{<entity>} <amount> {<location>} [range]";

	public String EVT_EFF_USAGE = "{<potion effect>}";
	
	public String EVT_ITEM_USAGE = "{<item>} [amount] {[enchants]}";
	
	public String EVT_MONEY_USAGE = "<amount>";
	
	public String EVT_EXP_USAGE = "<amount>";
	
	public String EVT_POINT_USAGE = "<amount>";
	
	public String EVT_DSCRIPT_USAGE = "<script> [npc ID] (-cn)";
	
	// ERROR STRINGS

	public String ERROR_CUSTOM = "Something is wrong.";
	public String ERROR_CMD_BAD_ID = "ID must be non-negative number.";
	public String ERROR_CMD_RANGE_INVALID = "Invalid range.";
	public String ERROR_CMD_WORLD_THIS = "World '%this' requires player context."; // %this = world label;
	public String ERROR_CMD_WORLD_INVALID = "Invalid world.";
	public String ERROR_CMD_ITEM_UNKNOWN = "Unknown item.";
	public String ERROR_CMD_BLOCK_UNKNOWN = "Unknown block.";
	public String ERROR_CMD_ITEM_NUMBERS = "Amount must be > 0. Data must be >= 0.";
	public String ERROR_CMD_ENCH_LEVEL = "Enchantment level must be > 0.";
	public String ERROR_CMD_ENCH_INVALID = "Invalid enchantment.";
	public String ERROR_CMD_ENCH_CANT = "One or more enchantments cannot be applied to specified item.";
	public String ERROR_CMD_EFFECT_UNKNOWN = "Unknown effect.";
	public String ERROR_CMD_EFFECT_DURATION = "Duration must be positive number.";
	public String ERROR_CMD_EFFECT_AMPLIFIER = "Amplifier must be non-negative number.";
	public String ERROR_CMD_AMOUNT_GENERAL = "Amount must be number.";
	public String ERROR_CMD_AMOUNT_POSITIVE = "Amount must be positive number.";
	public String ERROR_CMD_LOC_INVALID = "Invalid location.";
	public String ERROR_CMD_LOC_HERE = "Location '%here' requires player context."; // %here = location label;
	public String ERROR_CMD_LOC_BLOCK = "Location '%block' requires player context."; // %block = location label;
	public String ERROR_CMD_LOC_NOBLOCK = "No block targeted.";
	public String ERROR_CMD_COORDS_INVALID = "Invalid coordinates.";
	public String ERROR_CMD_ENTITY_UNKNOWN = "Unknown entity.";
	public String ERROR_CMD_COLOR_UNKNOWN = "Unknown color.";
	public String ERROR_CMD_ARGUMENTS_UNKNOWN = "Unknown arguments.";
	public String ERROR_CMD_ID_OUT_OF_BOUNDS = "Index does not exist.";
	
	public String ERROR_Q_EXIST = "Quest already exists.";
	public String ERROR_Q_NOT_EXIST = "Quest does not exist.";
	public String ERROR_Q_NOT_SELECTED = "No quest selected.";
	public String ERROR_Q_CANT_MODIFY = "Modification of active quests is not allowed.";
	public String ERROR_Q_NONE = "No quest available.";
	public String ERROR_Q_NONE_ACTIVE = "No quest active.";
	public String ERROR_Q_ASSIGNED = "Quest is already assigned.";
	public String ERROR_Q_NOT_ASSIGNED = "Quest not assigned.";
	public String ERROR_Q_CANT_CANCEL = "This quest cannot be cancelled.";
	public String ERROR_Q_NOT_COMPLETED = "One or more objectives are not completed.";
	public String ERROR_Q_BAD_WORLD = "Quest cannot be completed in this world.";
	public String ERROR_Q_NOT_CMD = "Quest cannot be started or completed by command.";
	public String ERROR_Q_MAX_AMOUNT = "Maximum quest amount reached.";
	public String ERROR_Q_NOT_HERE = "You can't complete your quest here.";
	public String ERROR_ELEMENT_FAIL = "Failed to create element, check console for more info.";
	public String ERROR_MOD_UNKNOWN = "Unknown modifiers.";
	public String ERROR_WORLD_NOT_ASSIGNED = "That world is not assigned.";
	public String ERROR_HOL_NOT_EXIST = "Holder does not exist.";
	public String ERROR_HOL_NOT_SELECTED = "No holder selected.";
	public String ERROR_HOL_NOT_ASSIGNED = "No holder assigned.";
	public String ERROR_HOL_INTERACT = "You can't interact that fast.";
	public String ERROR_CON_NOT_EXIST = "Condition does not exist.";
	public String ERROR_OBJ_NOT_EXIST = "Objective does not exist.";
	public String ERROR_OBJ_CANT_DO = "Not enough resources to complete objective.";
	public String ERROR_OCC_NOT_EXIST = "Occasion does not exist.";
	public String ERROR_REW_NOT_EXIST = "Reward does not exist.";
	public String ERROR_REW_CANT_DO = "Not enough space to recieve quest rewards.";
	public String ERROR_EVT_NOT_EXIST = "Event does not exist.";
	public String ERROR_WHY = "Why would you want to do this ?";
	public String ERROR_INTERESTING = "Interesting error, you should definitely notify Quester developer.";
	
}
