package com.gmail.molnardad.quester.strings;

import java.io.File;

public class QuesterLang {
	
	public static QuesterLang defaultLang = new QuesterLang(null);

	public String MSG_ONLY_PLAYER = "This command can only be run by player.";
	public String MSG_DATA_SAVE = "Quester data saved.";
	public String MSG_AUTOSAVE_DISABLED = "AutoSaving is disabled in config.";
	public String MSG_AUTOSAVE_STARTED = "Saving started. Interval: %intervalm";// %interval = autosave interval;
	public String MSG_AUTOSAVE_RUNNING = "Saving already running.";
	public String MSG_AUTOSAVE_STOPPED = "Saving Stopped.";
	public String MSG_AUTOSAVE_NOT_RUNNING = "Saving not running.";
	public String MSG_DATA_RELOADED = "Quester data reloaded.";
	public String MSG_PERMS = "You don't have permission for this.";
	public String MSG_Q_STARTED = "You have started quest %q";// %q = quest name;
	public String MSG_Q_COMPLETED = "Quest %q completed.";// %q = quest name;
	public String MSG_Q_CANCELLED = "Quest %q cancelled.";// %q = quest name;
	public String MSG_Q_DEACTIVATED = "One of your quests has been deactivated.";
	public String MSG_Q_SOME_CANCELLED = "Some of your quests have been cancelled.";
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
	public String INFO_QUESTS = "Quests";
	public String INFO_QUESTS_OTHER = "%p's quests";
	public String INFO_LIMIT = "Limit";

	public String HELP_SECTION_USE = "Quester help";
	public String HELP_SECTION_MODIFY = "Modify help";
	public String HELP_SECTION_MODIFY_SELECTED = "Applies only to selected quest";
	public String HELP_SECTION_MODIFY_HOLDER_SELECTED = "Applies only to selected quest holder";
	public String HELP_SECTION_ADMIN = "Admin help";
	public String HELP_SECTION_OTHER = "Other";
	
	public String USAGE_LABEL = "Usage: ";
	public String USAGE_MOD_AVAIL = "Available modifiers: ";
	
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

	// SIGN STRINGS
	
	public String SIGN_HEADER = "Sign quests";
	public String SIGN_REGISTERED = "Sign registered.";
	public String SIGN_UNREGISTERED = "Sign unregistered.";
	
	// OBJECTIVE STRINGS
	
	public String OBJ_ADD = "%type objective added."; // %type = objective type;
	public String OBJ_LIST = "Available objective types";
	public String OBJ_REMOVE = "Objective %id removed."; // %id = objective ID;
	
	public String OBJ_SWAP = "Objectives %id1 and %id2 swapped.";// %id1 = objective ID 1; %id2 = objective ID 2;
	
	public String OBJ_MOVE = "Objective moved from %id1 to %id2.";// %id1 = objective ID 1; %id2 = objective ID 2;
	
	public String OBJ_DESC_ADD = "Description to objective %id added."; // %id = objective ID;
	public String OBJ_DESC_REMOVE = "Description of objective %id removed."; // %id = objective ID;
	
	public String OBJ_PREREQ_ADD = "Prerequisites to objective %id added."; // %id = objective ID;
	public String OBJ_PREREQ_REMOVE = "Prerequisites of objective %id removed."; // %id = objective ID;
	
	// CONDITION STRINGS

	public String CON_ADD = "%type condition added."; // %type = condition type;
	public String CON_LIST = "Available condition types";
	public String CON_REMOVE = "Condition %id removed."; // %id = condition ID;
	
	public String CON_DESC_ADD = "Description to condition %id added."; // %id = condition ID;
	public String CON_DESC_REMOVE = "Description of condition %id removed."; // %id = condition ID;
	
	// EVENT STRINGS
	
	public String EVT_ADD = "%type event added."; // %type = event type;
	public String EVT_LIST = "Available event types";
	public String EVT_REMOVE = "Event %id removed.";// %id = event ID;
	public String EVT_SPECIFY = "Specify occasion and delay.";
	
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
	public String ERROR_CMD_SOUND_UNKNOWN = "Unknown sound.";
	public String ERROR_CMD_VOL_PIT = "Volume and pitch must be greater than 0.";
	public String ERROR_CMD_COLOR_UNKNOWN = "Unknown color.";
	public String ERROR_CMD_ARG_CANT_PARSE = "Could not parse argument '%arg'.";
	public String ERROR_CMD_ARGS_UNKNOWN = "Unknown arguments.";
	public String ERROR_CMD_ARGS_NOT_ENOUGH = "Not enough arguments.";
	public String ERROR_CMD_ARGS_TOO_MANY = "Too many arguments.";
	public String ERROR_CMD_ID_OUT_OF_BOUNDS = "Index does not exist.";
	public String ERROR_CMD_OCC_INCORRECT = "Incorrect ocasion.";
	public String ERROR_CMD_OCC_INCORRECT_FORM = "Incorrect occasion format.";
	public String ERROR_CMD_REGION_INVALID = "Invalid region.";
	
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

	
	
	private File file = null;
	
	public QuesterLang(File file) {
		this.file = file;
	}
	
	public File getFile() {
		return this.file;
	}
}
