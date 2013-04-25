package com.gmail.molnardad.quester.strings;

import java.io.File;

public class QuesterLang {

	/**
	 * @uml.property  name="mSG_ONLY_PLAYER"
	 */
	public String MSG_ONLY_PLAYER = "This command can only be run by player.";
	/**
	 * @uml.property  name="mSG_PROFILES_SAVE"
	 */
	public String MSG_PROFILES_SAVE = "Profiles saved.";
	/**
	 * @uml.property  name="mSG_AUTOSAVE_DISABLED"
	 */
	public String MSG_AUTOSAVE_DISABLED = "AutoSaving is disabled in config.";
	/**
	 * @uml.property  name="mSG_AUTOSAVE_STARTED"
	 */
	public String MSG_AUTOSAVE_STARTED = "Saving started. Interval: %intervalm";// %interval = autosave interval;
	/**
	 * @uml.property  name="mSG_AUTOSAVE_RUNNING"
	 */
	public String MSG_AUTOSAVE_RUNNING = "Saving already running.";
	/**
	 * @uml.property  name="mSG_AUTOSAVE_STOPPED"
	 */
	public String MSG_AUTOSAVE_STOPPED = "Saving Stopped.";
	/**
	 * @uml.property  name="mSG_AUTOSAVE_NOT_RUNNING"
	 */
	public String MSG_AUTOSAVE_NOT_RUNNING = "Saving not running.";
	/**
	 * @uml.property  name="mSG_CONFIG_RELOADED"
	 */
	public String MSG_CONFIG_RELOADED = "Quest configs reloaded.";
	/**
	 * @uml.property  name="mSG_PERMS"
	 */
	public String MSG_PERMS = "You don't have permission for this.";
	/**
	 * @uml.property  name="mSG_Q_STARTED"
	 */
	public String MSG_Q_STARTED = "You have started quest %q";// %q = quest name;
	/**
	 * @uml.property  name="mSG_Q_COMPLETED"
	 */
	public String MSG_Q_COMPLETED = "Quest %q completed.";// %q = quest name;
	/**
	 * @uml.property  name="mSG_Q_CANCELLED"
	 */
	public String MSG_Q_CANCELLED = "Quest %q cancelled.";// %q = quest name;
	/**
	 * @uml.property  name="mSG_Q_DEACTIVATED"
	 */
	public String MSG_Q_DEACTIVATED = "One of your quests has been deactivated.";
	/**
	 * @uml.property  name="mSG_OBJ_COMPLETED"
	 */
	public String MSG_OBJ_COMPLETED = "You completed a quest objective.";

	
	/**
	 * @uml.property  name="iNFO_NAME"
	 */
	public String INFO_NAME = "Name";
	/**
	 * @uml.property  name="iNFO_DESCRIPTION"
	 */
	public String INFO_DESCRIPTION = "Description";
	/**
	 * @uml.property  name="iNFO_LOCATION"
	 */
	public String INFO_LOCATION = "Location";
	/**
	 * @uml.property  name="iNFO_WORLDS"
	 */
	public String INFO_WORLDS = "Worlds";
	/**
	 * @uml.property  name="iNFO_FLAGS"
	 */
	public String INFO_FLAGS = "Flags";
	/**
	 * @uml.property  name="iNFO_CONDITIONS"
	 */
	public String INFO_CONDITIONS = "Conditions";
	/**
	 * @uml.property  name="iNFO_OBJECTIVES"
	 */
	public String INFO_OBJECTIVES = "Objectives";
	/**
	 * @uml.property  name="iNFO_EVENTS"
	 */
	public String INFO_EVENTS = "Events";
	/**
	 * @uml.property  name="iNFO_FIRST_OBJECTIVE"
	 */
	public String INFO_FIRST_OBJECTIVE = "First objective";
	/**
	 * @uml.property  name="iNFO_QUEST_LIST"
	 */
	public String INFO_QUEST_LIST = "Quest list";
	/**
	 * @uml.property  name="iNFO_QUEST_INFO"
	 */
	public String INFO_QUEST_INFO = "Quest info";
	/**
	 * @uml.property  name="iNFO_PROGRESS"
	 */
	public String INFO_PROGRESS = "%q progress";
	/**
	 * @uml.property  name="iNFO_PROGRESS_HIDDEN"
	 */
	public String INFO_PROGRESS_HIDDEN = "Quest progress hidden";
	/**
	 * @uml.property  name="iNFO_PROGRESS_COMPLETED"
	 */
	public String INFO_PROGRESS_COMPLETED = "Completed";
	/**
	 * @uml.property  name="iNFO_PROFILE_POINTS"
	 */
	public String INFO_PROFILE_POINTS = "Quest points";
	/**
	 * @uml.property  name="iNFO_PROFILE_RANK"
	 */
	public String INFO_PROFILE_RANK = "Quester rank";
	/**
	 * @uml.property  name="iNFO_PROFILE_COMPLETED"
	 */
	public String INFO_PROFILE_COMPLETED = "Completed quests";
	/**
	 * @uml.property  name="iNFO_PROFILE_NOT_EXIST"
	 */
	public String INFO_PROFILE_NOT_EXIST = "%p does not have profile.";// %p = player name;
	/**
	 * @uml.property  name="iNFO_HOLDER_LIST"
	 */
	public String INFO_HOLDER_LIST = "Holder list";

	/**
	 * @uml.property  name="hELP_SECTION_USE"
	 */
	public String HELP_SECTION_USE = "Quester help";
	/**
	 * @uml.property  name="hELP_SECTION_MODIFY"
	 */
	public String HELP_SECTION_MODIFY = "Modify help";
	/**
	 * @uml.property  name="hELP_SECTION_MODIFY_SELECTED"
	 */
	public String HELP_SECTION_MODIFY_SELECTED = "Applies only to selected quest";
	/**
	 * @uml.property  name="hELP_SECTION_MODIFY_HOLDER_SELECTED"
	 */
	public String HELP_SECTION_MODIFY_HOLDER_SELECTED = "Applies only to selected quest holder";
	/**
	 * @uml.property  name="hELP_SECTION_ADMIN"
	 */
	public String HELP_SECTION_ADMIN = "Admin help";
	/**
	 * @uml.property  name="hELP_SECTION_OTHER"
	 */
	public String HELP_SECTION_OTHER = "Other";
	
	/**
	 * @uml.property  name="uSAGE_LABEL"
	 */
	public String USAGE_LABEL = "Usage: ";
	/**
	 * @uml.property  name="uSAGE_MOD_AVAIL"
	 */
	public String USAGE_MOD_AVAIL = "Available modifiers: ";
	
	// QUEST STRINGs
	
	/**
	 * @uml.property  name="q_CREATED"
	 */
	public String Q_CREATED = "Quest created and selected.";
	/**
	 * @uml.property  name="q_REMOVED"
	 */
	public String Q_REMOVED = "Quest removed.";
	/**
	 * @uml.property  name="q_SELECTED"
	 */
	public String Q_SELECTED = "Quest selected.";
	/**
	 * @uml.property  name="q_RENAMED"
	 */
	public String Q_RENAMED = "Quest name changed to '%q'."; // %q = quest name;
	/**
	 * @uml.property  name="q_DESC_SET"
	 */
	public String Q_DESC_SET = "Quest description set.";
	/**
	 * @uml.property  name="q_DESC_ADDED"
	 */
	public String Q_DESC_ADDED = "Quest description added.";
	/**
	 * @uml.property  name="q_LOC_SET"
	 */
	public String Q_LOC_SET = "Quest location set.";
	/**
	 * @uml.property  name="q_LOC_REMOVED"
	 */
	public String Q_LOC_REMOVED = "Quest location removed.";
	/**
	 * @uml.property  name="q_MOD_ADDED"
	 */
	public String Q_MOD_ADDED = "Modifiers added.";
	/**
	 * @uml.property  name="q_MOD_REMOVED"
	 */
	public String Q_MOD_REMOVED = "Modifiers removed.";
	/**
	 * @uml.property  name="q_ACTIVATED"
	 */
	public String Q_ACTIVATED = "Quest activated.";
	/**
	 * @uml.property  name="q_DEACTIVATED"
	 */
	public String Q_DEACTIVATED = "Quest deactivated.";
	/**
	 * @uml.property  name="q_WORLD_ADDED"
	 */
	public String Q_WORLD_ADDED = "Quest world added.";
	/**
	 * @uml.property  name="q_WORLD_REMOVED"
	 */
	public String Q_WORLD_REMOVED = "Quest world removed.";
	/**
	 * @uml.property  name="q_SWITCHED"
	 */
	public String Q_SWITCHED = "Quest switched.";
	
	// HOLDER STRINGS
	
	/**
	 * @uml.property  name="hOL_CREATED"
	 */
	public String HOL_CREATED = "Holder created and selected.";
	/**
	 * @uml.property  name="hOL_REMOVED"
	 */
	public String HOL_REMOVED = "Holder deleted.";
	/**
	 * @uml.property  name="hOL_SELECTED"
	 */
	public String HOL_SELECTED = "Holder selected.";
	/**
	 * @uml.property  name="hOL_ASSIGNED"
	 */
	public String HOL_ASSIGNED = "Holder assigned.";
	/**
	 * @uml.property  name="hOL_UNASSIGNED"
	 */
	public String HOL_UNASSIGNED = "Holder unassigned.";
	/**
	 * @uml.property  name="hOL_Q_ADDED"
	 */
	public String HOL_Q_ADDED = "Quest added to holder.";
	/**
	 * @uml.property  name="hOL_Q_REMOVED"
	 */
	public String HOL_Q_REMOVED = "Quest removed from holder.";
	/**
	 * @uml.property  name="hOL_Q_MOVED"
	 */
	public String HOL_Q_MOVED = "Quest in holder moved.";

	// SIGN STRINGS
	
	/**
	 * @uml.property  name="sIGN_HEADER"
	 */
	public String SIGN_HEADER = "Sign quests";
	/**
	 * @uml.property  name="sIGN_REGISTERED"
	 */
	public String SIGN_REGISTERED = "Sign registered.";
	/**
	 * @uml.property  name="sIGN_UNREGISTERED"
	 */
	public String SIGN_UNREGISTERED = "Sign unregistered.";
	
	// OBJECTIVE STRINGS
	
	/**
	 * @uml.property  name="oBJ_ADD"
	 */
	public String OBJ_ADD = "%type objective added."; // %type = objective type;
	/**
	 * @uml.property  name="oBJ_LIST"
	 */
	public String OBJ_LIST = "Available objective types";
	/**
	 * @uml.property  name="oBJ_REMOVE"
	 */
	public String OBJ_REMOVE = "Objective %id removed."; // %id = objective ID;
	
	/**
	 * @uml.property  name="oBJ_SWAP"
	 */
	public String OBJ_SWAP = "Objectives %id1 and %id2 swapped.";// %id1 = objective ID 1; %id2 = objective ID 2;
	
	/**
	 * @uml.property  name="oBJ_MOVE"
	 */
	public String OBJ_MOVE = "Objective moved from %id1 to %id2.";// %id1 = objective ID 1; %id2 = objective ID 2;
	
	/**
	 * @uml.property  name="oBJ_DESC_ADD"
	 */
	public String OBJ_DESC_ADD = "Description to objective %id added."; // %id = objective ID;
	/**
	 * @uml.property  name="oBJ_DESC_REMOVE"
	 */
	public String OBJ_DESC_REMOVE = "Description of objective %id removed."; // %id = objective ID;
	
	/**
	 * @uml.property  name="oBJ_PREREQ_ADD"
	 */
	public String OBJ_PREREQ_ADD = "Prerequisites to objective %id added."; // %id = objective ID;
	/**
	 * @uml.property  name="oBJ_PREREQ_REMOVE"
	 */
	public String OBJ_PREREQ_REMOVE = "Prerequisites of objective %id removed."; // %id = objective ID;
	
	// CONDITION STRINGS

	/**
	 * @uml.property  name="cON_ADD"
	 */
	public String CON_ADD = "%type condition added."; // %type = condition type;
	/**
	 * @uml.property  name="cON_LIST"
	 */
	public String CON_LIST = "Available condition types";
	/**
	 * @uml.property  name="cON_REMOVE"
	 */
	public String CON_REMOVE = "Condition %id removed."; // %id = condition ID;
	
	/**
	 * @uml.property  name="cON_DESC_ADD"
	 */
	public String CON_DESC_ADD = "Description to condition %id added."; // %id = condition ID;
	/**
	 * @uml.property  name="cON_DESC_REMOVE"
	 */
	public String CON_DESC_REMOVE = "Description of condition %id removed."; // %id = condition ID;
	
	// EVENT STRINGS
	
	/**
	 * @uml.property  name="eVT_ADD"
	 */
	public String EVT_ADD = "%type event added."; // %type = event type;
	/**
	 * @uml.property  name="eVT_LIST"
	 */
	public String EVT_LIST = "Available event types";
	/**
	 * @uml.property  name="eVT_REMOVE"
	 */
	public String EVT_REMOVE = "Event %id removed.";// %id = event ID;
	/**
	 * @uml.property  name="eVT_SPECIFY"
	 */
	public String EVT_SPECIFY = "Specify occasion and delay.";
	
	// ERROR STRINGS

	/**
	 * @uml.property  name="eRROR_CUSTOM"
	 */
	public String ERROR_CUSTOM = "Something is wrong.";
	/**
	 * @uml.property  name="eRROR_CMD_BAD_ID"
	 */
	public String ERROR_CMD_BAD_ID = "ID must be non-negative number.";
	/**
	 * @uml.property  name="eRROR_CMD_RANGE_INVALID"
	 */
	public String ERROR_CMD_RANGE_INVALID = "Invalid range.";
	/**
	 * @uml.property  name="eRROR_CMD_WORLD_THIS"
	 */
	public String ERROR_CMD_WORLD_THIS = "World '%this' requires player context."; // %this = world label;
	/**
	 * @uml.property  name="eRROR_CMD_WORLD_INVALID"
	 */
	public String ERROR_CMD_WORLD_INVALID = "Invalid world.";
	/**
	 * @uml.property  name="eRROR_CMD_ITEM_UNKNOWN"
	 */
	public String ERROR_CMD_ITEM_UNKNOWN = "Unknown item.";
	/**
	 * @uml.property  name="eRROR_CMD_BLOCK_UNKNOWN"
	 */
	public String ERROR_CMD_BLOCK_UNKNOWN = "Unknown block.";
	/**
	 * @uml.property  name="eRROR_CMD_ITEM_NUMBERS"
	 */
	public String ERROR_CMD_ITEM_NUMBERS = "Amount must be > 0. Data must be >= 0.";
	/**
	 * @uml.property  name="eRROR_CMD_ENCH_LEVEL"
	 */
	public String ERROR_CMD_ENCH_LEVEL = "Enchantment level must be > 0.";
	/**
	 * @uml.property  name="eRROR_CMD_ENCH_INVALID"
	 */
	public String ERROR_CMD_ENCH_INVALID = "Invalid enchantment.";
	/**
	 * @uml.property  name="eRROR_CMD_ENCH_CANT"
	 */
	public String ERROR_CMD_ENCH_CANT = "One or more enchantments cannot be applied to specified item.";
	/**
	 * @uml.property  name="eRROR_CMD_EFFECT_UNKNOWN"
	 */
	public String ERROR_CMD_EFFECT_UNKNOWN = "Unknown effect.";
	/**
	 * @uml.property  name="eRROR_CMD_EFFECT_DURATION"
	 */
	public String ERROR_CMD_EFFECT_DURATION = "Duration must be positive number.";
	/**
	 * @uml.property  name="eRROR_CMD_EFFECT_AMPLIFIER"
	 */
	public String ERROR_CMD_EFFECT_AMPLIFIER = "Amplifier must be non-negative number.";
	/**
	 * @uml.property  name="eRROR_CMD_AMOUNT_GENERAL"
	 */
	public String ERROR_CMD_AMOUNT_GENERAL = "Amount must be number.";
	/**
	 * @uml.property  name="eRROR_CMD_AMOUNT_POSITIVE"
	 */
	public String ERROR_CMD_AMOUNT_POSITIVE = "Amount must be positive number.";
	/**
	 * @uml.property  name="eRROR_CMD_LOC_INVALID"
	 */
	public String ERROR_CMD_LOC_INVALID = "Invalid location.";
	/**
	 * @uml.property  name="eRROR_CMD_LOC_HERE"
	 */
	public String ERROR_CMD_LOC_HERE = "Location '%here' requires player context."; // %here = location label;
	/**
	 * @uml.property  name="eRROR_CMD_LOC_BLOCK"
	 */
	public String ERROR_CMD_LOC_BLOCK = "Location '%block' requires player context."; // %block = location label;
	/**
	 * @uml.property  name="eRROR_CMD_LOC_NOBLOCK"
	 */
	public String ERROR_CMD_LOC_NOBLOCK = "No block targeted.";
	/**
	 * @uml.property  name="eRROR_CMD_COORDS_INVALID"
	 */
	public String ERROR_CMD_COORDS_INVALID = "Invalid coordinates.";
	/**
	 * @uml.property  name="eRROR_CMD_ENTITY_UNKNOWN"
	 */
	public String ERROR_CMD_ENTITY_UNKNOWN = "Unknown entity.";
	/**
	 * @uml.property  name="eRROR_CMD_SOUND_UNKNOWN"
	 */
	public String ERROR_CMD_SOUND_UNKNOWN = "Unknown sound.";
	/**
	 * @uml.property  name="eRROR_CMD_VOL_PIT"
	 */
	public String ERROR_CMD_VOL_PIT = "Volume and pitch must be greater than 0.";
	/**
	 * @uml.property  name="eRROR_CMD_COLOR_UNKNOWN"
	 */
	public String ERROR_CMD_COLOR_UNKNOWN = "Unknown color.";
	/**
	 * @uml.property  name="eRROR_CMD_ARG_CANT_PARSE"
	 */
	public String ERROR_CMD_ARG_CANT_PARSE = "Could not parse argument '%arg'.";
	/**
	 * @uml.property  name="eRROR_CMD_ARGS_UNKNOWN"
	 */
	public String ERROR_CMD_ARGS_UNKNOWN = "Unknown arguments.";
	/**
	 * @uml.property  name="eRROR_CMD_ARGS_NOT_ENOUGH"
	 */
	public String ERROR_CMD_ARGS_NOT_ENOUGH = "Not enough arguments.";
	/**
	 * @uml.property  name="eRROR_CMD_ARGS_TOO_MANY"
	 */
	public String ERROR_CMD_ARGS_TOO_MANY = "Too many arguments.";
	/**
	 * @uml.property  name="eRROR_CMD_ID_OUT_OF_BOUNDS"
	 */
	public String ERROR_CMD_ID_OUT_OF_BOUNDS = "Index does not exist.";
	/**
	 * @uml.property  name="eRROR_CMD_OCC_INCORRECT"
	 */
	public String ERROR_CMD_OCC_INCORRECT = "Incorrect ocasion.";
	/**
	 * @uml.property  name="eRROR_CMD_OCC_INCORRECT_FORM"
	 */
	public String ERROR_CMD_OCC_INCORRECT_FORM = "Incorrect occasion format.";
	
	/**
	 * @uml.property  name="eRROR_Q_EXIST"
	 */
	public String ERROR_Q_EXIST = "Quest already exists.";
	/**
	 * @uml.property  name="eRROR_Q_NOT_EXIST"
	 */
	public String ERROR_Q_NOT_EXIST = "Quest does not exist.";
	/**
	 * @uml.property  name="eRROR_Q_NOT_SELECTED"
	 */
	public String ERROR_Q_NOT_SELECTED = "No quest selected.";
	/**
	 * @uml.property  name="eRROR_Q_CANT_MODIFY"
	 */
	public String ERROR_Q_CANT_MODIFY = "Modification of active quests is not allowed.";
	/**
	 * @uml.property  name="eRROR_Q_NONE"
	 */
	public String ERROR_Q_NONE = "No quest available.";
	/**
	 * @uml.property  name="eRROR_Q_NONE_ACTIVE"
	 */
	public String ERROR_Q_NONE_ACTIVE = "No quest active.";
	/**
	 * @uml.property  name="eRROR_Q_ASSIGNED"
	 */
	public String ERROR_Q_ASSIGNED = "Quest is already assigned.";
	/**
	 * @uml.property  name="eRROR_Q_NOT_ASSIGNED"
	 */
	public String ERROR_Q_NOT_ASSIGNED = "Quest not assigned.";
	/**
	 * @uml.property  name="eRROR_Q_CANT_CANCEL"
	 */
	public String ERROR_Q_CANT_CANCEL = "This quest cannot be cancelled.";
	/**
	 * @uml.property  name="eRROR_Q_NOT_COMPLETED"
	 */
	public String ERROR_Q_NOT_COMPLETED = "One or more objectives are not completed.";
	/**
	 * @uml.property  name="eRROR_Q_BAD_WORLD"
	 */
	public String ERROR_Q_BAD_WORLD = "Quest cannot be completed in this world.";
	/**
	 * @uml.property  name="eRROR_Q_NOT_CMD"
	 */
	public String ERROR_Q_NOT_CMD = "Quest cannot be started or completed by command.";
	/**
	 * @uml.property  name="eRROR_Q_MAX_AMOUNT"
	 */
	public String ERROR_Q_MAX_AMOUNT = "Maximum quest amount reached.";
	/**
	 * @uml.property  name="eRROR_Q_NOT_HERE"
	 */
	public String ERROR_Q_NOT_HERE = "You can't complete your quest here.";
	/**
	 * @uml.property  name="eRROR_ELEMENT_FAIL"
	 */
	public String ERROR_ELEMENT_FAIL = "Failed to create element, check console for more info.";
	/**
	 * @uml.property  name="eRROR_MOD_UNKNOWN"
	 */
	public String ERROR_MOD_UNKNOWN = "Unknown modifiers.";
	/**
	 * @uml.property  name="eRROR_WORLD_NOT_ASSIGNED"
	 */
	public String ERROR_WORLD_NOT_ASSIGNED = "That world is not assigned.";
	/**
	 * @uml.property  name="eRROR_HOL_NOT_EXIST"
	 */
	public String ERROR_HOL_NOT_EXIST = "Holder does not exist.";
	/**
	 * @uml.property  name="eRROR_HOL_NOT_SELECTED"
	 */
	public String ERROR_HOL_NOT_SELECTED = "No holder selected.";
	/**
	 * @uml.property  name="eRROR_HOL_NOT_ASSIGNED"
	 */
	public String ERROR_HOL_NOT_ASSIGNED = "No holder assigned.";
	/**
	 * @uml.property  name="eRROR_HOL_INTERACT"
	 */
	public String ERROR_HOL_INTERACT = "You can't interact that fast.";
	/**
	 * @uml.property  name="eRROR_CON_NOT_EXIST"
	 */
	public String ERROR_CON_NOT_EXIST = "Condition does not exist.";
	/**
	 * @uml.property  name="eRROR_OBJ_NOT_EXIST"
	 */
	public String ERROR_OBJ_NOT_EXIST = "Objective does not exist.";
	/**
	 * @uml.property  name="eRROR_OBJ_CANT_DO"
	 */
	public String ERROR_OBJ_CANT_DO = "Not enough resources to complete objective.";
	/**
	 * @uml.property  name="eRROR_OCC_NOT_EXIST"
	 */
	public String ERROR_OCC_NOT_EXIST = "Occasion does not exist.";
	/**
	 * @uml.property  name="eRROR_REW_NOT_EXIST"
	 */
	public String ERROR_REW_NOT_EXIST = "Reward does not exist.";
	/**
	 * @uml.property  name="eRROR_REW_CANT_DO"
	 */
	public String ERROR_REW_CANT_DO = "Not enough space to recieve quest rewards.";
	/**
	 * @uml.property  name="eRROR_EVT_NOT_EXIST"
	 */
	public String ERROR_EVT_NOT_EXIST = "Event does not exist.";
	/**
	 * @uml.property  name="eRROR_WHY"
	 */
	public String ERROR_WHY = "Why would you want to do this ?";
	/**
	 * @uml.property  name="eRROR_INTERESTING"
	 */
	public String ERROR_INTERESTING = "Interesting error, you should definitely notify Quester developer.";

	
	
	/**
	 * @uml.property  name="file"
	 */
	private File file = null;
	
	public QuesterLang(File file) {
		this.file = file;
	}
	
	/**
	 * @return
	 * @uml.property  name="file"
	 */
	public File getFile() {
		return this.file;
	}
}
