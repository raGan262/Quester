package me.ragan262.quester.lang;

import java.util.HashMap;
import java.util.Map;

class MessageRegistry {
	
	static final int INITIAL_CAPACITY = 200;
	
	final Map<String, String> messages = new HashMap<>(INITIAL_CAPACITY);
	
	final Map<String, String> customMessages = new HashMap<>();
	
	MessageRegistry() {
		messages.put("MSG_UNKNOWN_MESSAGE", "UNKNOWN MESSAGE");
		
		messages.put("MSG_ONLY_PLAYER", "This command can only be run by player.");
		messages.put("MSG_DATA_SAVE", "Quester data saved.");
		messages.put("MSG_AUTOSAVE_DISABLED", "AutoSaving is disabled in config.");
		messages.put("MSG_AUTOSAVE_STARTED", "Saving started. Interval: %intervalm");// %interval = autosave interval
		messages.put("MSG_AUTOSAVE_RUNNING", "Saving already running.");
		messages.put("MSG_AUTOSAVE_STOPPED", "Saving Stopped.");
		messages.put("MSG_AUTOSAVE_NOT_RUNNING", "Saving not running.");
		messages.put("MSG_DATA_RELOADED", "Quester data reloaded.");
		messages.put("MSG_PERMS", "You don't have permission for this.");
		messages.put("MSG_Q_STARTED", "You have started quest %q");// %q = quest name
		messages.put("MSG_Q_COMPLETED", "Quest %q completed.");// %q = quest name
		messages.put("MSG_Q_CANCELLED", "Quest %q cancelled.");// %q = quest name
		messages.put("MSG_Q_DEACTIVATED", "One of your quests has been deactivated.");
		messages.put("MSG_Q_SOME_CANCELLED", "Some of your quests have been cancelled.");
		messages.put("MSG_OBJ_COMPLETED", "You completed a quest objective.");
		messages.put("MSG_LANG_SET", "Language set.");
		
		messages.put("INFO_NAME", "Name");
		messages.put("INFO_DESCRIPTION", "Description");
		messages.put("INFO_LOCATION", "Location");
		messages.put("INFO_WORLDS", "Worlds");
		messages.put("INFO_FLAGS", "Flags");
		messages.put("INFO_CONDITIONS", "Conditions");
		messages.put("INFO_OBJECTIVES", "Objectives");
		messages.put("INFO_EVENTS", "Events");
		messages.put("INFO_TRIGGERS", "Triggers");
		messages.put("INFO_QUEST_LIST", "Quest list");
		messages.put("INFO_QUEST_INFO", "Quest info");
		messages.put("INFO_PROGRESS", "%q progress");
		messages.put("INFO_PROGRESS_HIDDEN", "Quest progress hidden");
		messages.put("INFO_PROGRESS_COMPLETED", "Completed");
		messages.put("INFO_PROFILE_POINTS", "Quest points");
		messages.put("INFO_PROFILE_RANK", "Quester rank");
		messages.put("INFO_PROFILE_COMPLETED", "%p's completed quests");// %p = player name
		messages.put("INFO_PROFILE_NOT_EXIST", "%p does not have profile.");// %p = player name
		messages.put("INFO_HOLDER_LIST", "Holder list");
		messages.put("INFO_QUESTS", "Quests");
		messages.put("INFO_QUESTS_OTHER", "%p's quests");
		messages.put("INFO_LIMIT", "Limit");
		messages.put("AVAILABLE_LANGS", "Available languages");
		
		messages.put("HELP_SECTION_USE", "Quester help");
		messages.put("HELP_SECTION_MODIFY", "Modify help");
		messages.put("HELP_SECTION_MODIFY_SELECTED", "Applies only to selected quest");
		messages.put("HELP_SECTION_MODIFY_HOLDER_SELECTED", "Applies only to selected quest holder");
		messages.put("HELP_SECTION_ADMIN", "Admin help");
		messages.put("HELP_SECTION_OTHER", "Other");
		
		messages.put("USAGE_LABEL", "Usage: ");
		messages.put("USAGE_MOD_AVAIL", "Available modifiers: ");
		
		messages.put("PROF_COMP_ADDED", "Completed quest added.");
		messages.put("PROF_COMP_REMOVED", "Completed quest removed.");
		messages.put("PROF_QUEST_STARTED", "Quest started.");
		messages.put("PROF_QUEST_CANCELLED", "Quest cancelled.");
		messages.put("PROF_QUEST_COMPLETED", "Quest completed.");
		messages.put("PROF_REPUTATION_SET", "Reputation points set.");
		messages.put("PROF_REPUTATION_ADDED", "Reputation points added.");
		messages.put("PROF_PROGRESS", "%p's progress with %q");
		messages.put("PROF_PROGRESS_SET", "Progress set.");
		messages.put("PROF_LANGUAGE", "%p's language");
		messages.put("PROF_LANGUAGE_SET", "%p's language set.");
		
		messages.put("Q_CREATED", "Quest created and selected.");
		messages.put("Q_REMOVED", "Quest removed.");
		messages.put("Q_SELECTED", "Quest selected.");
		messages.put("Q_RENAMED", "Quest name changed to '%q'."); // %q = quest name
		messages.put("Q_DESC_SET", "Quest description set.");
		messages.put("Q_DESC_ADDED", "Quest description added.");
		messages.put("Q_LOC_SET", "Quest location set.");
		messages.put("Q_LOC_REMOVED", "Quest location removed.");
		messages.put("Q_MOD_ADDED", "Modifiers added.");
		messages.put("Q_MOD_REMOVED", "Modifiers removed.");
		messages.put("Q_ACTIVATED", "Quest activated.");
		messages.put("Q_DEACTIVATED", "Quest deactivated.");
		messages.put("Q_WORLD_ADDED", "Quest world added.");
		messages.put("Q_WORLD_REMOVED", "Quest world removed.");
		messages.put("Q_SWITCHED", "Quest switched.");
		
		messages.put("HOL_CREATED", "Holder created and selected.");
		messages.put("HOL_REMOVED", "Holder deleted.");
		messages.put("HOL_SELECTED", "Holder selected.");
		messages.put("HOL_ASSIGNED", "Holder assigned.");
		messages.put("HOL_UNASSIGNED", "Holder unassigned.");
		messages.put("HOL_Q_ADDED", "Quest added to holder.");
		messages.put("HOL_Q_REMOVED", "Quest removed from holder.");
		messages.put("HOL_Q_MOVED", "Quest in holder moved.");
		
		messages.put("SIGN_HEADER", "Sign quests");
		messages.put("SIGN_REGISTERED", "Sign registered.");
		messages.put("SIGN_UNREGISTERED", "Sign unregistered.");
		
		messages.put("OBJ_ADD", "%type objective added."); // %type = objective type
		messages.put("OBJ_SET", "%type objective set."); // %type = objective type
		messages.put("OBJ_LIST", "Available objective types");
		messages.put("OBJ_REMOVE", "Objective %id removed."); // %id = objective ID
		messages.put("OBJ_SWAP", "Objectives %id1 and %id2 swapped."); // %id1 = objective ID 1; %id2 = objective ID 2
		messages.put("OBJ_MOVE", "Objective moved from %id1 to %id2."); // %id1 = objective ID 1; %id2 = objective ID 2
		messages.put("OBJ_DESC_ADD", "Description to objective %id added."); // %id = objective ID
		messages.put("OBJ_DESC_REMOVE", "Description of objective %id removed."); // %id = objective ID;
		messages.put("OBJ_PREREQ_ADD", "Prerequisites to objective %id added."); // %id = objective ID
		messages.put("OBJ_PREREQ_REMOVE", "Prerequisites of objective %id removed."); // %id = objective ID
		messages.put("OBJ_TRIG_ADD", "Triggers of objective %id added."); // %id = objective ID
		messages.put("OBJ_TRIG_REMOVE", "Triggers of objective %id removed."); // %id = objective ID
		
		messages.put("CON_ADD", "%type condition added."); // %type = condition type
		messages.put("CON_SET", "%type condition set."); // %type = condition type
		messages.put("CON_LIST", "Available condition types");
		messages.put("CON_REMOVE", "Condition %id removed."); // %id = condition ID
		messages.put("CON_DESC_ADD", "Description to condition %id added."); // %id = condition ID
		messages.put("CON_DESC_REMOVE", "Description of condition %id removed."); // %id = condition ID
		
		messages.put("EVT_ADD", "%type event added."); // %type = event type
		messages.put("EVT_SET", "%type event set."); // %type = event type
		messages.put("EVT_LIST", "Available event types");
		messages.put("EVT_REMOVE", "Event %id removed."); // %id = event ID
		messages.put("EVT_SPECIFY", "Specify occasion and delay.");
		
		messages.put("TRIG_ADD", "%type trigger added."); // %type = event type
		messages.put("TRIG_SET", "%type trigger set."); // %type = event type
		messages.put("TRIG_LIST", "Available trigger types");
		messages.put("TRIG_REMOVE", "Trigger %id removed."); // %id = event ID
		
		messages.put("ERROR_CUSTOM", "Something is wrong.");
		messages.put("ERROR_CMD_BAD_ID", "ID must be non-negative number.");
		messages.put("ERROR_CMD_RANGE_INVALID", "Invalid range.");
		messages.put("ERROR_CMD_WORLD_THIS", "World '%this' requires player context."); // %this = world label
		messages.put("ERROR_CMD_WORLD_INVALID", "Invalid world.");
		messages.put("ERROR_CMD_ITEM_UNKNOWN", "Unknown item.");
		messages.put("ERROR_CMD_BLOCK_UNKNOWN", "Unknown block.");
		messages.put("ERROR_CMD_ITEM_NUMBERS", "Amount must be > 0. Data must be >= 0.");
		messages.put("ERROR_CMD_ENCH_LEVEL", "Enchantment level must be > 0.");
		messages.put("ERROR_CMD_ENCH_INVALID", "Invalid enchantment.");
		messages.put("ERROR_CMD_EFFECT_UNKNOWN", "Unknown effect.");
		messages.put("ERROR_CMD_EFFECT_DURATION", "Duration must be positive number.");
		messages.put("ERROR_CMD_EFFECT_AMPLIFIER", "Amplifier must be non-negative number.");
		messages.put("ERROR_CMD_AMOUNT_GENERAL", "Amount must be number.");
		messages.put("ERROR_CMD_AMOUNT_POSITIVE", "Amount must be positive number.");
		messages.put("ERROR_CMD_AMOUNT_NONZERO", "Amount must not be zero.");
		messages.put("ERROR_CMD_LOC_INVALID", "Invalid location.");
		messages.put("ERROR_CMD_LOC_HERE", "Location '%here' requires player context."); // %here = location label
		messages.put("ERROR_CMD_LOC_BLOCK", "Location '%block' requires player context."); // %block = location label
		messages.put("ERROR_CMD_LOC_NOBLOCK", "No block targeted.");
		messages.put("ERROR_CMD_COORDS_INVALID", "Invalid coordinates.");
		messages.put("ERROR_CMD_ENTITY_UNKNOWN", "Unknown entity.");
		messages.put("ERROR_CMD_SOUND_UNKNOWN", "Unknown sound.");
		messages.put("ERROR_CMD_VOL_PIT", "Volume and pitch must be greater than 0.");
		messages.put("ERROR_CMD_COLOR_UNKNOWN", "Unknown color.");
		messages.put("ERROR_CMD_ARG_CANT_PARSE", "Could not parse argument '%arg'.");
		messages.put("ERROR_CMD_ARGS_UNKNOWN", "Unknown arguments.");
		messages.put("ERROR_CMD_ARGS_NOT_ENOUGH", "Not enough arguments.");
		messages.put("ERROR_CMD_ARGS_TOO_MANY", "Too many arguments.");
		messages.put("ERROR_CMD_ID_OUT_OF_BOUNDS", "Index does not exist.");
		messages.put("ERROR_CMD_OCC_INCORRECT", "Incorrect ocasion.");
		messages.put("ERROR_CMD_OCC_INCORRECT_FORM", "Incorrect occasion format.");
		messages.put("ERROR_CMD_REGION_INVALID", "Invalid region.");
		messages.put("ERROR_CMD_PLAYER_OFFLINE", "Player %p is not online.");
		messages.put("ERROR_CMD_LANG_INVALID", "Invalid language.");
		
		messages.put("ERROR_Q_EXIST", "Quest already exists.");
		messages.put("ERROR_Q_NOT_EXIST", "Quest does not exist.");
		messages.put("ERROR_Q_NOT_SELECTED", "No quest selected.");
		messages.put("ERROR_Q_CANT_MODIFY", "Modification of active quests is not allowed.");
		messages.put("ERROR_Q_NONE", "No quest available.");
		messages.put("ERROR_Q_NONE_ACTIVE", "No quest active.");
		messages.put("ERROR_Q_ASSIGNED", "Quest is already assigned.");
		messages.put("ERROR_Q_NOT_ASSIGNED", "Quest not assigned.");
		messages.put("ERROR_Q_CANT_CANCEL", "This quest cannot be cancelled.");
		messages.put("ERROR_Q_NOT_COMPLETED", "One or more objectives are not completed.");
		messages.put("ERROR_Q_BAD_WORLD", "Quest cannot be completed in this world.");
		messages.put("ERROR_Q_NOT_CMD", "Quest cannot be started or completed by command.");
		messages.put("ERROR_Q_MAX_AMOUNT", "Maximum quest amount reached.");
		messages.put("ERROR_Q_NOT_HERE", "You can't complete your quest here.");
		messages.put("ERROR_ELEMENT_FAIL", "Failed to create element, check console for more info.");
		messages.put("ERROR_MOD_UNKNOWN", "Unknown modifiers.");
		messages.put("ERROR_WORLD_NOT_ASSIGNED", "That world is not assigned.");
		messages.put("ERROR_HOL_NOT_EXIST", "Holder does not exist.");
		messages.put("ERROR_HOL_NOT_SELECTED", "No holder selected.");
		messages.put("ERROR_HOL_NOT_ASSIGNED", "No holder assigned.");
		messages.put("ERROR_HOL_INTERACT", "You can't interact that fast.");
		messages.put("ERROR_CON_NOT_EXIST", "Condition does not exist.");
		messages.put("ERROR_OBJ_NOT_EXIST", "Objective does not exist.");
		messages.put("ERROR_OBJ_CANT_DO", "Not enough resources to complete objective.");
		messages.put("ERROR_OCC_NOT_EXIST", "Occasion does not exist.");
		messages.put("ERROR_REW_NOT_EXIST", "Reward does not exist.");
		messages.put("ERROR_REW_CANT_DO", "Not enough space to recieve quest rewards.");
		messages.put("ERROR_EVT_NOT_EXIST", "Event does not exist.");
		messages.put("ERROR_TRIG_NOT_EXIST", "Trigger does not exist.");
		messages.put("ERROR_PROF_Q_ALREADY_DONE", "Player already completed this quest.");
		messages.put("ERROR_WHY", "Why would you want to do this ?");
		messages.put("ERROR_INTERESTING", "Interesting error, you should definitely notify Quester developer.");
	}
	
	boolean registerMessage(final String key, final String message) {
		final String upperKey = key.toUpperCase();
		if(messages.containsKey(upperKey)) {
			return false;
		}
		else {
			messages.put(upperKey, message);
			return true;
		}
	}
	
	boolean registerCustomMessage(final String key, final String message) {
		final String upperKey = key.toUpperCase();
		if(customMessages.containsKey(upperKey)) {
			return false;
		}
		else {
			customMessages.put(upperKey, message);
			return true;
		}
	}
}
