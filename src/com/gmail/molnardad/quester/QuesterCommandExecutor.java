package com.gmail.molnardad.quester;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.avaje.ebeaninternal.server.lib.util.InvalidDataException;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.objectives.*;
import com.gmail.molnardad.quester.qevents.*;
import com.gmail.molnardad.quester.conditions.*;
import static com.gmail.molnardad.quester.Quester.strings;
import static com.gmail.molnardad.quester.utils.Util.*;

public class QuesterCommandExecutor implements CommandExecutor {

	Player player = null;
	QuestManager qm = null;
	
	private final String OBJECTIVES = "break, place, item, exp, loc, death, world, mobkill, kill, craft, ench, smelt, shear, fish, milk, collect, tame, money";
	private final String CONDITIONS = "quest, questnot, perm, money, item, point";
	private final String EVENTS = "msg, explosion, block, tele, lightning, cmd, quest, cancel, toggle, objcom, spawn, item, money, exp, effect, point";
	
	public QuesterCommandExecutor() {
		qm = Quester.qMan;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(label.equalsIgnoreCase("quester") || label.equalsIgnoreCase("quest") || label.equalsIgnoreCase("q")){
			
			if(sender instanceof Player)
				player = (Player) sender;
			else 
				player = null;
			
			if(args.length > 0){
				
				// QUEST HELP
				if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
					if(!permCheck(sender, QuestData.PERM_USE_HELP, true)) {
						return true;
					}

					String command = QuestData.displayedCmd;
					
					sender.sendMessage(line(ChatColor.BLUE, strings.HELP_SECTION_USE, ChatColor.GOLD));
					sender.sendMessage(ChatColor.GOLD + command + " help/? " + ChatColor.GRAY + strings.HELP_HELP);
					if(permCheck(sender, QuestData.PERM_USE_LIST, false))
						sender.sendMessage(ChatColor.GOLD + command + " list " + ChatColor.GRAY + strings.HELP_LIST);
					if(permCheck(sender, QuestData.PERM_USE_INFO, false)) {
						sender.sendMessage(ChatColor.GOLD + command + " show [name] " + ChatColor.GRAY + strings.HELP_SHOW);
					}
					if(permCheck(sender, QuestData.PERM_USE_START_PICK, false))
						sender.sendMessage(ChatColor.GOLD + command + " start [name] " + ChatColor.GRAY + strings.HELP_START_PICK);
					if(permCheck(sender, QuestData.PERM_USE_START_RANDOM, false))
						sender.sendMessage(ChatColor.GOLD + command + " start " + ChatColor.GRAY + strings.HELP_START_RANDOM);
					if(permCheck(sender, QuestData.PERM_USE_CANCEL, false))
						sender.sendMessage(ChatColor.GOLD + command + " cancel " + ChatColor.GRAY + strings.HELP_CANCEL);
					if(permCheck(sender, QuestData.PERM_USE_DONE, false))
						sender.sendMessage(ChatColor.GOLD + command + " done " + ChatColor.GRAY + strings.HELP_DONE);
					if(permCheck(sender, QuestData.PERM_USE_PROGRESS, false))
						sender.sendMessage(ChatColor.GOLD + command + " progress " + ChatColor.GRAY + strings.HELP_PROGRESS);
					if(permCheck(sender, QuestData.PERM_USE_PROFILE, false))
						sender.sendMessage(ChatColor.GOLD + command + " profile " + ChatColor.GRAY + strings.HELP_PROFILE_USE);
					if(permCheck(sender, QuestData.MODIFY_PERM, false)) {
						sender.sendMessage(line(ChatColor.BLUE, strings.HELP_SECTION_MODIFY, ChatColor.GOLD));
						sender.sendMessage(ChatColor.GOLD + command + " profile [name] " + ChatColor.GRAY + strings.HELP_PROFILE_MOD);
						sender.sendMessage(ChatColor.GOLD + command + " create [name] " + ChatColor.GRAY + strings.HELP_CREATE);
						sender.sendMessage(ChatColor.GOLD + command + " remove [name] " + ChatColor.GRAY + strings.HELP_REMOVE);
						sender.sendMessage(ChatColor.GOLD + command + " select [name] " + ChatColor.GRAY + strings.HELP_SELECT);
						sender.sendMessage(ChatColor.GOLD + command + " toggle [name*] " + ChatColor.GRAY + strings.HELP_TOGGLE);
						sender.sendMessage(ChatColor.GOLD + command + " info [name*] " + ChatColor.GRAY + strings.HELP_INFO);
						sender.sendMessage(line(ChatColor.DARK_GRAY, strings.HELP_SECTION_MODIFY_SELECTED));
						sender.sendMessage(ChatColor.GOLD + command + " name [newName] " + ChatColor.GRAY + strings.HELP_NAME);
						sender.sendMessage(ChatColor.GOLD + command + " desc set\\add " + ChatColor.GRAY + strings.HELP_DESC);
						sender.sendMessage(ChatColor.GOLD + command + " world add\\remove " + ChatColor.GRAY + strings.HELP_WORLD);
						sender.sendMessage(ChatColor.GOLD + command + " flag add\\remove " + ChatColor.GRAY + strings.HELP_FLAG);
						sender.sendMessage(ChatColor.GOLD + command + " condition add\\remove " + ChatColor.GRAY + strings.HELP_CONDITION);
						sender.sendMessage(ChatColor.GOLD + command + " objective add\\remove\\swap\\desc " + ChatColor.GRAY + strings.HELP_OBJECTIVE);
						sender.sendMessage(ChatColor.GOLD + command + " event add\\remove " + ChatColor.GRAY + strings.HELP_EVENT);
						sender.sendMessage(ChatColor.GOLD + command + " reward add\\remove " + ChatColor.GRAY + strings.HELP_REWARD);
					}
					if(permCheck(sender, QuestData.ADMIN_PERM, false)) {
						sender.sendMessage(line(ChatColor.BLUE, strings.HELP_SECTION_ADMIN, ChatColor.GOLD));
						sender.sendMessage(ChatColor.GOLD + command + " startsave " + ChatColor.GRAY + strings.HELP_STARTSAVE);
						sender.sendMessage(ChatColor.GOLD + command + " stopsave " + ChatColor.GRAY + strings.HELP_STOPSAVE);
						sender.sendMessage(ChatColor.GOLD + command + " save " + ChatColor.GRAY + strings.HELP_SAVE);
						sender.sendMessage(ChatColor.GOLD + command + " reload " + ChatColor.GRAY + strings.HELP_RELOAD);
					}
					sender.sendMessage(line(ChatColor.BLUE));
					return true;
				}
				
				// QUEST PROFILE
				if(args[0].equalsIgnoreCase("profile")) {
					if(args.length > 1){
						if(permCheck(sender, QuestData.MODIFY_PERM, false)) {
							String name = args[1];
							qm.showProfile(sender, name);
							return true;
						}
					}
					if(!permCheck(sender, QuestData.PERM_USE_PROFILE, true)) {
						return true;
					}
					qm.showProfile(sender);
					return true;
				}
				
				// QUEST SHOW
				if(args[0].equalsIgnoreCase("show")) {
					if(args.length > 1){
						String questName = implode(args, 1);
						try {
							qm.showQuest(sender, questName);
						} catch (QuesterException e) {
							sender.sendMessage(e.message());
						}
					} else {
						sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.USAGE_SHOW.replaceAll("%cmd", QuestData.displayedCmd));
					}
					return true;
				}
				
				// QUEST INFO
				if(args[0].equalsIgnoreCase("info")) {
					if(args.length > 1){
						String questName = implode(args, 1);
						try {
							if(permCheck(sender, QuestData.MODIFY_PERM, false)) {
								int id = Integer.parseInt(args[1]);
								qm.showQuestInfo(sender, id);
							} else {
								qm.showQuest(sender, questName);
							}
						} catch (QuesterException e) {
							sender.sendMessage(e.message());
						} catch (NumberFormatException e) {
							sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_BAD_ID);
						}
					} else {
						try {
							if(permCheck(sender, QuestData.MODIFY_PERM, false)) {
								qm.showQuestInfo(sender);
							} else {
								sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.USAGE_INFO_USER.replaceAll("%cmd", QuestData.displayedCmd));
							}
						} catch (QuesterException e) {
							sender.sendMessage(e.message());
							sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.USAGE_INFO_MOD.replaceAll("%cmd", QuestData.displayedCmd));
						}
					}
					return true;
				}
				
				// QUEST LIST
				if(args[0].equalsIgnoreCase("list")) {
					if(permCheck(sender, QuestData.MODIFY_PERM, false)) {
						qm.showFullQuestList(sender);
					} else if(permCheck(sender, QuestData.PERM_USE_LIST, true)) {
						qm.showQuestList(sender);
					}
					return true;
				}
				
				// QUEST CREATE
				if(args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("c")) {
					if(!permCheck(sender, QuestData.MODIFY_PERM, true)) {
						return true;
					}
					if(args.length > 1){
						try {
							String questName = implode(args, 1);
							qm.createQuest(sender.getName(), questName);
							sender.sendMessage(ChatColor.GREEN + strings.Q_CREATED);
							if(QuestData.verbose) {
								Quester.log.info(sender.getName() + " created quest '" + questName + "'.");
							}
						} catch (QuesterException e) {
							sender.sendMessage(e.message());
						}
					} else {
						sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.USAGE_CREATE.replaceAll("%cmd", QuestData.displayedCmd));
					}
					return true;
				}
				
				// QUEST REMOVE
				if(args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("r")) {
					if(!permCheck(sender, QuestData.MODIFY_PERM, true)) {
						return true;
					}
					if(args.length > 1){
						try {
							int id = Integer.parseInt(args[1]);
							String name = qm.getQuestNameByID(id);
							qm.removeQuest(sender.getName(), id);
							sender.sendMessage(ChatColor.GREEN + strings.Q_REMOVED);
							if(QuestData.verbose) {
								Quester.log.info(sender.getName() + " removed quest '" + name + "'.");
							}
						} catch (QuesterException e) {
							sender.sendMessage(e.message());
						} catch (NumberFormatException e) {
							sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_BAD_ID);
						}
					} else {
						sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.USAGE_REMOVE.replaceAll("%cmd", QuestData.displayedCmd));
					}
					return true;
				}
				
				// QUEST SELECT
				if(args[0].equalsIgnoreCase("select") || args[0].equalsIgnoreCase("sel")) {
					if(!permCheck(sender, QuestData.MODIFY_PERM, true)) {
						return true;
					}
					if(args.length > 1){
						try {
							int id = Integer.parseInt(args[1]);
							qm.selectQuest(sender.getName(), id);
							sender.sendMessage(ChatColor.GREEN + strings.Q_SELECTED);
						} catch (QuesterException e) {
							sender.sendMessage(e.message());
						} catch (NumberFormatException e) {
							sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_BAD_ID);
						}
					} else {
						sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.USAGE_SELECT.replaceAll("%cmd", QuestData.displayedCmd));
					}
					return true;
				}
				
				// QUEST NAME
				if(args[0].equalsIgnoreCase("name")) {
					if(!permCheck(sender, QuestData.MODIFY_PERM, true)) {
						return true;
					}
					if(args.length > 1){
						try {
							String questName = implode(args, 1);
							qm.changeQuestName(sender.getName(), questName);
							sender.sendMessage(ChatColor.GREEN + strings.Q_RENAMED.replaceAll("%q", questName));
						} catch (QuesterException e) {
							sender.sendMessage(e.message());
						}
					} else {
						sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.USAGE_NAME.replaceAll("%cmd", QuestData.displayedCmd));
					}
					return true;
				}
				
				// QUEST DESCRIPTION
				if(args[0].equalsIgnoreCase("desc")) {
					if(!permCheck(sender, QuestData.MODIFY_PERM, true)) {
						return true;
					}
					if(args.length > 1){
						//PARSE DESCRIPTION
						String questDesc = implode(args, 2).replaceAll("\\\\n", "\n");
						
						// SET DESCRIPTION
						if(args[1].equalsIgnoreCase("set") || args[1].equalsIgnoreCase("s")){
							try {
								qm.setQuestDescription(sender.getName(), questDesc);
								sender.sendMessage(ChatColor.GREEN + strings.Q_DESC_SET);
							} catch (QuesterException e) {
								sender.sendMessage(e.message());
							}
							return true;
						}
						
						// ADD DESCRIPTION
						if(args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("a")){
							try {
								qm.addQuestDescription(sender.getName(), questDesc);
								sender.sendMessage(ChatColor.GREEN + strings.Q_DESC_ADDED);
							} catch (QuesterException e) {
								sender.sendMessage(e.message());
							}
							return true;
						}
					}
					sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.USAGE_DESC.replaceAll("%cmd", QuestData.displayedCmd));
					return true;
				}
				
				//QUEST LOCATION
				if(args[0].equalsIgnoreCase("location") || args[0].equalsIgnoreCase("loc")) {
					if(!permCheck(sender, QuestData.MODIFY_PERM, true)) {
						return true;
					}

					if(args.length > 1) {
						
						if(args[1].equalsIgnoreCase("set") || args[1].equalsIgnoreCase("s")){
							if(args.length > 3) {
								try {
									int range = Integer.parseInt(args[3]);
									if(range < 1)
										throw new NumberFormatException();
									qm.setQuestLocation(sender.getName(), getLoc(sender, args[2]), range);
									sender.sendMessage(ChatColor.GREEN + strings.Q_LOC_SET);
								} catch (QuesterException e) {
									sender.sendMessage(e.message());
								} catch (NumberFormatException e) {
									sender.sendMessage(ChatColor.GREEN + strings.ERROR_CMD_RANGE_INVALID);
								}	
							} else {
								sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.USAGE_LOC_SET.replaceAll("%cmd", QuestData.displayedCmd));
								return true;
							}
							return true;
						}
						
						if(args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("r")){
							try{
								qm.removeQuestLocation(sender.getName());
								sender.sendMessage(ChatColor.GREEN + strings.Q_LOC_REMOVED);
							} catch (QuesterException e) {
								sender.sendMessage(e.message());
							}
							return true;
						}
					}
					
					sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.USAGE_LOC.replaceAll("%cmd", QuestData.displayedCmd));
					return true;
				}
				
				// QUEST FLAG
				if(args[0].equalsIgnoreCase("flag") || args[0].equalsIgnoreCase("f")) {
					if(!permCheck(sender, QuestData.MODIFY_PERM, true)) {
						return true;
					}

					if(args.length > 2) {
						
						Set<QuestFlag> flags = new HashSet<QuestFlag>();
						
						for(int i=2; i<args.length; i++) {
							QuestFlag flag = QuestFlag.getByName(args[i]);
							if(flag != null && flag != QuestFlag.ACTIVE)
								flags.add(flag);
						}
						
						if(flags.isEmpty()) {
							sender.sendMessage(ChatColor.RED + strings.USAGE_FLAG_AVAIL + ChatColor.WHITE + QuestFlag.stringize(QuestFlag.values()));
							return true;
						}
						
						if(args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("a")){
							try{
								qm.addQuestFlag(sender.getName(), flags.toArray(new QuestFlag[0]));
								sender.sendMessage(ChatColor.GREEN + strings.Q_FLGS_ADDED);
							} catch (QuesterException e) {
								sender.sendMessage(e.message());
							}
							return true;
						}
						
						if(args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("r")){
							try{
								qm.removeQuestFlag(sender.getName(), flags.toArray(new QuestFlag[0]));
								sender.sendMessage(ChatColor.GREEN + strings.Q_FLGS_REMOVED);
							} catch (QuesterException e) {
								sender.sendMessage(e.message());
							}
							return true;
						}
					}
					
					sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.USAGE_FLAG.replaceAll("%cmd", QuestData.displayedCmd));
					return true;
				}
				
				// QUEST TOGGLE
				if(args[0].equalsIgnoreCase("toggle")) {
					if(!permCheck(sender, QuestData.MODIFY_PERM, true)) {
						return true;
					}
					int id = -1;
					boolean active;
					try {
						if(args.length > 1)
							id = Integer.parseInt(args[1]);
						if(id < 0){
							qm.toggleQuest(sender);
							active = qm.isQuestActive(sender);
						} else {
							qm.toggleQuest(id);
							active = qm.isQuestActive(id);
						}
					} catch (QuesterException e) {
						sender.sendMessage(e.message());
						return true;
					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_BAD_ID);
						return true;
					}
					if(active){
						sender.sendMessage(ChatColor.GREEN + strings.Q_ACTIVATED);
					} else {
						sender.sendMessage(ChatColor.GREEN + strings.Q_DEACTIVATED);
					}
					return true;
				}
				
				// QUEST WORLD
				if(args[0].equalsIgnoreCase("world")) {
					if(!permCheck(sender, QuestData.MODIFY_PERM, true)) {
						return true;
					}
					if(args.length > 2){
						
						// ADD WORLD
						if(args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("a")){
							try {
								World world = null;
								if(args[2].equalsIgnoreCase(QuestData.worldLabelThis)) {
									if(player != null) {
										world = player.getWorld();
									} else {
										sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_WORLD_THIS.replaceAll("%this", QuestData.worldLabelThis));
										return true;
									}
								} else {
									world = sender.getServer().getWorld(args[2]);
								}
								if(world == null) {
									sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_WORLD_INVALID);
									return true;
								}
								qm.addQuestWorld(sender.getName(), world.getName());
								sender.sendMessage(ChatColor.GREEN + strings.Q_WORLD_ADDED);
							} catch (QuesterException e) {
								sender.sendMessage(e.message());
							}
							return true;
						}
						
						// REMOVE WORLD
						if(args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("r")){
							try {
								String wName = args[2];
								if(args[2].equalsIgnoreCase(QuestData.worldLabelThis)) {
									World world = null;
									if(player != null) {
										world = player.getWorld();
									} else {
										sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_WORLD_THIS.replaceAll("%this", QuestData.worldLabelThis));
										return true;
									}
									if(world != null) {
										wName = world.getName();
									}
								}
								
								qm.removeQuestWorld(sender.getName(), wName);
								sender.sendMessage(ChatColor.GREEN + strings.Q_WORLD_REMOVED);
							} catch (QuesterException e) {
								sender.sendMessage(e.message());
							}
							return true;
						}
					}
					
					sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.USAGE_WORLD.replaceAll("%this", QuestData.worldLabelThis).replaceAll("%cmd", QuestData.displayedCmd));
					return true;
				}
				
				// QUEST HOLDER
				if(args[0].equalsIgnoreCase("holder") || args[0].equalsIgnoreCase("hol")) {
					if(!permCheck(sender, QuestData.MODIFY_PERM, true)) {
						return true;
					}
					if(args.length > 1){
						
						if(args[1].equalsIgnoreCase("create") || args[1].equalsIgnoreCase("c")) {
							if(args.length < 3) {
								sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.HOL_CREATE_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
								return true;
							}
							qm.createHolder(implode(args, 2));
							sender.sendMessage(ChatColor.GREEN + strings.HOL_CREATED);
							return true;
						}
						
						if(args[1].equalsIgnoreCase("delete") || args[1].equalsIgnoreCase("d")) {
							if(args.length < 3) {
								sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.HOL_DELETE_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
								return true;
							}
							try {
								qm.removeHolder(Integer.parseInt(args[2]));
								sender.sendMessage(ChatColor.GREEN + strings.HOL_REMOVED);
							} catch (NumberFormatException e) {
								sender.sendMessage(ChatColor.RED + strings.ERROR_HOL_NOT_EXIST);
							}
							return true;
						}
						
						if(args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("a")) {
							if(args.length < 3) {
								sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.HOL_ADD_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
								return true;
							}
							try {
								qm.addHolderQuest(sender.getName(), Integer.parseInt(args[2]));
								sender.sendMessage(ChatColor.GREEN + strings.HOL_Q_ADDED);
							} catch (NumberFormatException e) {
								sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_BAD_ID);
							} catch (QuesterException e) {
								sender.sendMessage(e.message());
							}
							return true;
						}
						
						if(args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("r")) {
							if(args.length < 3) {
								sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.HOL_REMOVE_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
								return true;
							}
							try {
								qm.removeHolderQuest(sender.getName(), Integer.parseInt(args[2]));
								sender.sendMessage(ChatColor.GREEN + strings.HOL_Q_REMOVED);
							} catch (NumberFormatException e) {
								sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_BAD_ID);
							} catch (QuesterException e) {
								sender.sendMessage(e.message());
							}
							return true;
						}

						if(args[1].equalsIgnoreCase("move") || args[1].equalsIgnoreCase("m")) {
							if(args.length < 4) {
								sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.HOL_MOVE_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
								return true;
							}
							try {
								qm.moveHolderQuest(sender.getName(), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
								sender.sendMessage(ChatColor.GREEN + strings.HOL_Q_MOVED);
							} catch (NumberFormatException e) {
								sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_BAD_ID);
							} catch (QuesterException e) {
								sender.sendMessage(e.message());
							}
							return true;
						}
						
						if(args[1].equalsIgnoreCase("list")) {
							qm.showHolderList(sender);
							return true;
						}
						
						if(args[1].equalsIgnoreCase("info")) {
							if(args.length < 2) {
								sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.HOL_INFO_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
								return true;
							}
							try {
								int id = -1;
								if(args.length > 2)
									id = Integer.parseInt(args[2]);
								qm.showHolderInfo(sender, id);
							} catch (NumberFormatException e) {
								sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_BAD_ID);
							} catch (QuesterException e) {
								sender.sendMessage(e.message());
							}
							return true;
						}
						
						if(args[1].equalsIgnoreCase("select") || args[1].equalsIgnoreCase("sel")) {
							if(args.length < 3) {
								sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.HOL_SELECT_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
								return true;
							}
							try {
								qm.selectHolder(sender.getName(), Integer.parseInt(args[2]));
								sender.sendMessage(ChatColor.GREEN + strings.HOL_SELECTED);
							} catch (NumberFormatException e) {
								sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_BAD_ID);
							} catch (QuesterException e) {
								sender.sendMessage(e.message());
							}
							return true;
						}
					}
					
					sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.HOL_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
					return true;
				}
				
				// QUEST OBJECTIVE TODO
				if(args[0].equalsIgnoreCase("objective") || args[0].equalsIgnoreCase("obj")) {
					if(!permCheck(sender, QuestData.MODIFY_PERM, true)) {
						return true;
					}
					
					if(args.length > 2){
						
						// ADD OBJECTIVE
						if(args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("a")){
							
							// BREAK OBJECTIVE
							if(args[2].equalsIgnoreCase("break")) {
								if(args.length > 4) {
									Material mat;
									byte dat;
									int hnd = -1;
									try {
										int[] itm = parseItem(args[3]);
										mat = Material.getMaterial(itm[0]);
										dat = (byte)itm[1];
										if(mat.getId() > 255) {
											throw new InvalidDataException("");
										}
										int amt = Integer.parseInt(args[4]);
										if(amt < 1 || dat < -1) {
											throw new NumberFormatException();
										}
										if(args.length > 5) {
											itm = parseItem(args[5]);
											hnd = itm[0];
										}
										qm.addQuestObjective(sender.getName(), new BreakObjective(amt, mat, dat, hnd));
										sender.sendMessage(ChatColor.GREEN + strings.OBJ_ADD.replaceAll("%type", strings.OBJ_BREAK_TYPE));
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_ITEM_NUMBERS);
									} catch (InvalidDataException e) {
										sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_BLOCK_UNKNOWN);
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.OBJ_BREAK_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
								return true;
							}
							
							// PLACE OBJECTIVE
							if(args[2].equalsIgnoreCase("place")) {
								if(args.length > 4) {
									Material mat;
									byte dat;
									try {
										int[] itm = parseItem(args[3]);
										mat = Material.getMaterial(itm[0]);
										dat = (byte)itm[1];
										if(mat.getId() > 255) {
											throw new InvalidDataException("");
										}
										int amt = Integer.parseInt(args[4]);
										if(amt < 1 || dat < -1) {
											throw new NumberFormatException();
										}
										qm.addQuestObjective(sender.getName(), new PlaceObjective(amt, mat, dat));
										sender.sendMessage(ChatColor.GREEN + strings.OBJ_ADD.replaceAll("%type", strings.OBJ_PLACE_TYPE));
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_ITEM_NUMBERS);
									} catch (InvalidDataException e) {
										sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_BLOCK_UNKNOWN);
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.OBJ_PLACE_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
								return true;
							}
							
							// ITEM OBJECTIVE
							if(args[2].equalsIgnoreCase("item")) {
								if(args.length > 3) {
									Material mat;
									int dat;
									int amt = 1;
									try {
										int[] itm = parseItem(args[3]);
										mat = Material.getMaterial(itm[0]);
										dat = itm[1];
										if(args.length > 4) {
											amt = Integer.parseInt(args[4]);
										}
										if(amt < 1 || dat < -1) {
											throw new NumberFormatException();
										}
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_ITEM_NUMBERS);
										return true;
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
										return true;
									}
									try {
										Map<Integer, Integer> enchs = null;
										if(args.length > 5) {
											enchs = parseEnchants(args[5]);
											ItemStack test = new ItemStack(mat, amt, (short)dat);
											for(Integer i : enchs.keySet()) {
												test.addEnchantment(Enchantment.getById(i), enchs.get(i));
											}
										}
										qm.addQuestObjective(sender.getName(), new ItemObjective(mat, amt, dat, enchs));
										sender.sendMessage(ChatColor.GREEN + strings.OBJ_ADD.replaceAll("%type", strings.OBJ_ITEM_TYPE));
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									} catch (IllegalArgumentException e){
										sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_ENCH_CANT);
										return true;
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.OBJ_ITEM_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
								return true;
							}
							
							// COLLECT OBJECTIVE
							if(args[2].equalsIgnoreCase("collect")) {
								if(args.length > 4) {
									Material mat;
									int dat;
									try {
										int[] itm = parseItem(args[3]);
										mat = Material.getMaterial(itm[0]);
										dat = itm[1];
										int amt = Integer.parseInt(args[4]);
										if(amt < 1 || dat < -1) {
											throw new NumberFormatException();
										}
										qm.addQuestObjective(sender.getName(), new CollectObjective(amt, mat, dat));
										sender.sendMessage(ChatColor.GREEN + strings.OBJ_ADD.replaceAll("%type", strings.OBJ_COLLECT_TYPE));
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_ITEM_NUMBERS);
									} catch (InvalidDataException e) {
										sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_ITEM_UNKNOWN);
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.OBJ_COLLECT_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
								return true;
							}
							
							// ENCHANT OBJECTIVE
							if(args[2].equalsIgnoreCase("ench")) {
								if(args.length > 3) {
									Material mat;
									int amt = 1;
									try {
										int[] itm = parseItem(args[3]);
										mat = Material.getMaterial(itm[0]);
										if(args.length > 4) {
											amt = Integer.parseInt(args[4]);
										}
										if(amt < 1) {
											throw new NumberFormatException();
										}
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + strings.OBJ_ENCH_NUMBERS);
										return true;
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
										return true;
									}
									try {
										Map<Integer, Integer> enchs = null;
										if(args.length > 5) {
											enchs = parseEnchants(args[5]);
											ItemStack test = new ItemStack(mat);
											for(Integer i : enchs.keySet()) {
												test.addEnchantment(Enchantment.getById(i), enchs.get(i));
											}
										}
										qm.addQuestObjective(sender.getName(), new EnchantObjective(mat, amt, enchs));
										sender.sendMessage(ChatColor.GREEN + strings.OBJ_ADD.replaceAll("%type", strings.OBJ_ENCH_TYPE));
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									} catch (IllegalArgumentException e){
										sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_ENCH_CANT);
										return true;
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.OBJ_ENCH_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
								return true;
							}
							
							// EXPERIENCE OBJECTIVE
							if(args[2].equalsIgnoreCase("exp")) {
								if(args.length > 3) {
									try {
										int amt = Integer.parseInt(args[3]);
										if(amt < 1)
											throw new NumberFormatException();
										qm.addQuestObjective(sender.getName(), new ExpObjective(amt));
										sender.sendMessage(ChatColor.GREEN + strings.OBJ_ADD.replaceAll("%type", strings.OBJ_EXP_TYPE));
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_AMOUNT_POSITIVE);
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.OBJ_EXP_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
								return true;
							}
							
							// LOCATION OBJECTIVE
							if(args[2].equalsIgnoreCase("loc")) {
								if(args.length > 3) {
									Location loc = null;
									int rng = 3;				
									try {
										loc = getLoc(sender, args[3]);
										if(args.length > 4){
											rng = Integer.parseInt(args[4]);
											if(rng < 1) {
												throw new NumberFormatException();
											}
										}
										qm.addQuestObjective(sender.getName(), new LocObjective(loc, rng));
										sender.sendMessage(ChatColor.GREEN + strings.OBJ_ADD.replaceAll("%type", strings.OBJ_LOC_TYPE));
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_RANGE_INVALID);
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.OBJ_LOC_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
								return true;
							}
							
							// DEATH OBJECTIVE
							if(args[2].equalsIgnoreCase("death")) {
								if(args.length > 2) {
									int amt = 1;
									Location loc = null;
									int rng = 5;
									try {
										if(args.length > 3) {
											amt = Integer.parseInt(args[3]);
											if(amt < 1) {
												throw new QuesterException(ChatColor.RED + strings.ERROR_CMD_AMOUNT_POSITIVE);
											}
											if(args.length > 4) {
												loc = getLoc(sender, args[4]);
												
												if(args.length > 5) {
													rng = Integer.parseInt(args[5]);
													
													if(rng < 1) {
														throw new NumberFormatException();
													}
												}
											}
										}
										qm.addQuestObjective(sender.getName(), new DeathObjective(amt, loc, rng));
										sender.sendMessage(ChatColor.GREEN + strings.OBJ_ADD.replaceAll("%type", strings.OBJ_DEATH_TYPE));
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_RANGE_INVALID);
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.OBJ_DEATH_USAGE.replaceAll("%this", QuestData.worldLabelThis).replaceAll("%cmd", QuestData.displayedCmd));
								return true;
							}
							
							// WORLD OBJECTIVE
							if(args[2].equalsIgnoreCase("world")) {
								if(args.length > 3) {
									World world = null;
									if(args[3].equalsIgnoreCase(QuestData.worldLabelThis)) {
										if(player != null) {
											world = player.getWorld();
										} else {
											sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_WORLD_THIS.replaceAll("%this", QuestData.worldLabelThis));
											return true;
										}
									} else {
										world = sender.getServer().getWorld(args[3]);
									}
									if(world == null) {
										sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_WORLD_INVALID);
										return true;
									}
									try {
										qm.addQuestObjective(sender.getName(), new WorldObjective(world.getName()));
										sender.sendMessage(ChatColor.GREEN + strings.OBJ_ADD.replaceAll("%type", strings.OBJ_WORLD_TYPE));
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.OBJ_WORLD_USAGE.replaceAll("%this", QuestData.worldLabelThis).replaceAll("%cmd", QuestData.displayedCmd));
								return true;
							}
							
							// MOBKILL OBJECTIVE
							if(args[2].equalsIgnoreCase("mobkill")) {
								if(args.length > 3) {
									int amt = 1;
									EntityType ent = null;
									try {
										amt = Integer.parseInt(args[3]);
										if(amt < 1) {
											throw new NumberFormatException();
										}
										if(args.length > 4) {
											ent = parseEntity(args[4]);
										} 
										qm.addQuestObjective(sender.getName(), new MobKillObjective(amt, ent));
										sender.sendMessage(ChatColor.GREEN + strings.OBJ_ADD.replaceAll("%type", strings.OBJ_MOBKILL_TYPE));
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_ENTITY_NUMBERS);
										return true;
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.OBJ_MOBKILL_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
								return true;
							}
							
							// PLAYERKILL OBJECTIVE
							if(args[2].equalsIgnoreCase("kill")) {
								if(args.length > 3) {
									int amt = 1;
									String name = "";
									try {
										amt = Integer.parseInt(args[3]);
										if(amt < 1) {
											throw new NumberFormatException();
										}
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_AMOUNT_POSITIVE);
										return true;
									}
									if(args.length > 4) {
										name = args[4];
									}
									try {
										qm.addQuestObjective(sender.getName(), new PlayerKillObjective(amt, name));
										sender.sendMessage(ChatColor.GREEN + strings.OBJ_ADD.replaceAll("%type", strings.OBJ_KILL_TYPE));
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.OBJ_KILL_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
								return true;
							}
							
							// CRAFT OBJECTIVE
							if(args[2].equalsIgnoreCase("craft")) {
								if(args.length > 4) {
									Material mat;
									int dat;
									try {
										int[] itm = parseItem(args[3]);
										mat = Material.getMaterial(itm[0]);
										dat = itm[1];
										int amt = Integer.parseInt(args[4]);
										if(amt < 1 || dat < -1) {
											throw new NumberFormatException();
										}
										qm.addQuestObjective(sender.getName(), new CraftObjective(amt, mat, dat));
										sender.sendMessage(ChatColor.GREEN + strings.OBJ_ADD.replaceAll("%type", strings.OBJ_CRAFT_TYPE));
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_ITEM_NUMBERS);
									} catch (InvalidDataException e) {
										sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_ITEM_UNKNOWN);
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.OBJ_CRAFT_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
								return true;
							}
							
							// SMELT OBJECTIVE
							if(args[2].equalsIgnoreCase("smelt")) {
								if(args.length > 4) {
									Material mat;
									int dat;
									try {
										int[] itm = parseItem(args[3]);
										mat = Material.getMaterial(itm[0]);
										dat = itm[1];
										int amt = Integer.parseInt(args[4]);
										if(amt < 1 || dat < -1) {
											throw new NumberFormatException();
										}
										qm.addQuestObjective(sender.getName(), new SmeltObjective(amt, mat, dat));
										sender.sendMessage(ChatColor.GREEN + strings.OBJ_ADD.replaceAll("%type", strings.OBJ_SMELT_TYPE));
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_ITEM_NUMBERS);
									} catch (InvalidDataException e) {
										sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_ITEM_UNKNOWN);
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.OBJ_SMELT_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
								return true;
							}
							
							// SHEAR OBJECTIVE
							if(args[2].equalsIgnoreCase("shear")) {
								if(args.length > 3) {
									try {
										DyeColor col = null;
										int amt = Integer.parseInt(args[3]);
										if(args.length > 4)
											col = parseColor(args[4]);
										if(amt < 1)
											throw new NumberFormatException();
										qm.addQuestObjective(sender.getName(), new ShearObjective(amt, col));
										sender.sendMessage(ChatColor.GREEN + strings.OBJ_ADD.replaceAll("%type", strings.OBJ_SHEAR_TYPE));
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_AMOUNT_POSITIVE);
									} catch (InvalidDataException e) {
										sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_COLOR_UNKNOWN);
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.OBJ_SHEAR_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
								return true;
							}
							
							// FISH OBJECTIVE
							if(args[2].equalsIgnoreCase("fish")) {
								if(args.length > 3) {
									try {
										int amt = Integer.parseInt(args[3]);
										if(amt < 1)
											throw new NumberFormatException();
										qm.addQuestObjective(sender.getName(), new FishObjective(amt));
										sender.sendMessage(ChatColor.GREEN + strings.OBJ_ADD.replaceAll("%type", strings.OBJ_FISH_TYPE));
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_AMOUNT_POSITIVE);
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.OBJ_FISH_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
								return true;
							}
							
							// MILK OBJECTIVE
							if(args[2].equalsIgnoreCase("milk")) {
								if(args.length > 3) {
									try {
										int amt = Integer.parseInt(args[3]);
										if(amt < 1)
											throw new NumberFormatException();
										qm.addQuestObjective(sender.getName(), new MilkObjective(amt));
										sender.sendMessage(ChatColor.GREEN + strings.OBJ_ADD.replaceAll("%type", strings.OBJ_MILK_TYPE));
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_AMOUNT_POSITIVE);
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.OBJ_MILK_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
								return true;
							}
							
							// TAME OBJECTIVE
							if(args[2].equalsIgnoreCase("tame")) {
								if(args.length > 3) {
									int amt = 1;
									EntityType ent = null;
									try {
										amt = Integer.parseInt(args[3]);
										if(amt < 1) {
											throw new NumberFormatException();
										}
										if(args.length > 4) {
											ent = EntityType.fromName(args[4].toUpperCase());
											if(ent == null) {
												try {
													ent = EntityType.fromId(Integer.parseInt(args[4]));
												} catch (NumberFormatException e) {
													throw new InvalidDataException("");
												}
												if(ent == null) {
													throw new InvalidDataException("");
												}
											}
										}
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_ENTITY_NUMBERS);
										return true;
									} catch (InvalidDataException e){
										sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_ENTITY_UNKNOWN);
										return true;
									}
									try {
										qm.addQuestObjective(sender.getName(), new TameObjective(amt, ent));
										sender.sendMessage(ChatColor.GREEN + strings.OBJ_ADD.replaceAll("%type", strings.OBJ_TAME_TYPE));
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.OBJ_TAME_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
								return true;
							}
							
							// MONEY OBJECTIVE
							if(args[2].equalsIgnoreCase("money")) {
								if(args.length > 3) {
									try {
										double amt = Double.parseDouble(args[3]);
										if(amt <= 0)
											throw new NumberFormatException();
										qm.addQuestObjective(sender.getName(), new MoneyObjective(amt));
										sender.sendMessage(ChatColor.GREEN + strings.OBJ_ADD.replaceAll("%type", strings.OBJ_MONEY_TYPE));
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_AMOUNT_POSITIVE);
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.OBJ_MONEY_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
								return true;
							}
							
							sender.sendMessage(ChatColor.RED + strings.OBJ_ADD_AVAILABLE + ChatColor.WHITE + OBJECTIVES);
							return true;
						}
						
						// REMOVE OBJECTIVE
						if(args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("r")){
							try {
								int id = Integer.parseInt(args[2]);
								qm.removeQuestObjective(sender.getName(), id);
								sender.sendMessage(ChatColor.GREEN + strings.OBJ_REMOVE.replaceAll("%id", args[2]));
							} catch (NumberFormatException e) {
								sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.OBJ_REMOVE_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
							} catch (QuesterException e) {
								sender.sendMessage(e.message());
							}
							return true;
						}
						
						// SWAP OBJECTIVES
						if(args[1].equalsIgnoreCase("swap")){
							try {
								if(!(args.length > 3))
									throw new NumberFormatException();
								int first = Integer.parseInt(args[2]);
								int second = Integer.parseInt(args[3]);
								qm.swapQuestObjectives(sender.getName(), first, second);
								sender.sendMessage(ChatColor.GREEN + strings.OBJ_SWAP.replaceAll("%id1", args[2]).replaceAll("%id2", args[3]));
							} catch (NumberFormatException e) {
								sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.OBJ_SWAP_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
							} catch (QuesterException e) {
								sender.sendMessage(e.message());
							}
							return true;
						}
						
						// MOVE OBJECTIVES
						if(args[1].equalsIgnoreCase("move")){
							try {
								if(!(args.length > 3))
									throw new NumberFormatException();
								int first = Integer.parseInt(args[2]);
								int second = Integer.parseInt(args[3]);
								qm.moveQuestObjective(sender.getName(), first, second);
								sender.sendMessage(ChatColor.GREEN + strings.OBJ_MOVE.replaceAll("%id1", args[2]).replaceAll("%id2", args[3]));
							} catch (NumberFormatException e) {
								sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.OBJ_MOVE_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
							} catch (QuesterException e) {
								sender.sendMessage(e.message());
							}
							return true;
						}
						
						// OBJECTIVE DESCRIPTION
						if(args[1].equalsIgnoreCase("desc")){
							if(args.length > 3) {
								int obj;
								try {
									obj = Integer.parseInt(args[3]);
								} catch (NumberFormatException e) {
									sender.sendMessage(ChatColor.RED + strings.OBJ_BAD_ID);
									return true;
								}
								
								if(args[2].equalsIgnoreCase("add") || args[2].equalsIgnoreCase("a")) {
									
									if(args.length > 4) {
										String desc = implode(args, 4);
										try {
											qm.addObjectiveDescription(sender.getName(), obj, desc);
											sender.sendMessage(ChatColor.GREEN + strings.OBJ_DESC_ADD.replaceAll("%id", String.valueOf(obj)));
										} catch (QuesterException e) {
											sender.sendMessage(e.message());
										}
										return true;
									}
										
									sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.OBJ_DESC_ADD_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
									return true;	
								}
								
								if(args[2].equalsIgnoreCase("remove") || args[2].equalsIgnoreCase("r")) {
									try {
										qm.removeObjectiveDescription(sender.getName(), obj);
										sender.sendMessage(ChatColor.GREEN + strings.OBJ_DESC_REMOVE.replaceAll("%id", String.valueOf(obj)));
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								
								return true;
							}	
								
							sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.OBJ_DESC_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
							return true;
						}
						
						// OBJECTIVE PREREQUISITES
						if(args[1].equalsIgnoreCase("prereq")){
							if(args.length > 3) {
								int obj;
								try {
									obj = Integer.parseInt(args[3]);
								} catch (NumberFormatException e) {
									sender.sendMessage(ChatColor.RED + strings.OBJ_BAD_ID);
									return true;
								}
								
								if(args[2].equalsIgnoreCase("add") || args[2].equalsIgnoreCase("a")) {
									
									if(args.length > 4) {
										Set<Integer> prereq = parsePrerequisites(args, 4);
										try {
											qm.addObjectivePrerequisites(sender.getName(), obj, prereq);
											sender.sendMessage(ChatColor.GREEN + strings.OBJ_REQ_ADD.replaceAll("%id", String.valueOf(obj)));
										} catch (QuesterException e) {
											sender.sendMessage(e.message());
										}
										return true;
									}
										
									sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.OBJ_REQ_ADD_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
									return true;	
								}
								
								if(args[2].equalsIgnoreCase("remove") || args[2].equalsIgnoreCase("r")) {
									if(args.length > 4) {
										Set<Integer> prereq = parsePrerequisites(args, 4);
										try {
											qm.removeObjectivePrerequisites(sender.getName(), obj, prereq);
											sender.sendMessage(ChatColor.GREEN + strings.OBJ_REQ_REMOVE.replaceAll("%id", String.valueOf(obj)));
										} catch (QuesterException e) {
											sender.sendMessage(e.message());
										}
										return true;
									}
									
									sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.OBJ_REQ_REMOVE_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
									return true;
								}
								
							}	
								
							sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.OBJ_REQ_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
							return true;
						}
						
						sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.OBJ_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
						return true;
					}
					
					if(args.length > 1) {
						if(args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("a")){
							sender.sendMessage(ChatColor.RED + strings.OBJ_ADD_AVAILABLE + ChatColor.WHITE + OBJECTIVES);
							return true;
						}
						if(args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("r")) {
							sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.OBJ_REMOVE_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
							return true;
						}
						if(args[1].equalsIgnoreCase("desc")) {
							sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.OBJ_DESC_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
							return true;
						}
						if(args[1].equalsIgnoreCase("swap")) {
							sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.OBJ_SWAP_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
							return true;
						}
					}
					
					sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.OBJ_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
					return true;
				}
				
				// QUEST CONDITION TODO
				if(args[0].equalsIgnoreCase("condition") || args[0].equalsIgnoreCase("con")) {
					if(!permCheck(sender, QuestData.MODIFY_PERM, true)) {
						return true;
					}
					if(args.length > 2){
						
						// ADD CONDITION
						if(args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("a")){

							// QUEST CONDITION
							if(args[2].equalsIgnoreCase("quest")) {
								if(args.length > 3) {
									String questName = implode(args, 3);
									try {
										qm.addQuestCondition(sender.getName(), new QuestCondition(questName));
										sender.sendMessage(ChatColor.GREEN + strings.CON_ADD.replaceAll("%type", strings.CON_QUEST_TYPE));
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.CON_QUEST_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
								return true;
							}
							
							// QUESTNOT CONDITION
							if(args[2].equalsIgnoreCase("questnot")) {
								if(args.length > 3) {
									String questName = implode(args, 3);
									try {
										qm.addQuestCondition(sender.getName(), new QuestNotCondition(questName));
										sender.sendMessage(ChatColor.GREEN + strings.CON_ADD.replaceAll("%type", strings.CON_QUESTNOT_TYPE));
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.CON_QUESTNOT_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
								return true;
							}
							
							// PERMISSION CONDITION
							if(args[2].equalsIgnoreCase("perm")) {
								if(args.length > 3) {
									String perm = args[3];
									try {
										qm.addQuestCondition(sender.getName(), new PermissionCondition(perm));
										sender.sendMessage(ChatColor.GREEN + strings.CON_ADD.replaceAll("%type", strings.CON_PERM_TYPE));
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.CON_PERM_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
								return true;
							}
							
							// MONEY CONDITION
							if(args[2].equalsIgnoreCase("money")) {
								if(args.length > 3) {
									try {
										int amt = Integer.parseInt(args[3]);
										qm.addQuestCondition(sender.getName(), new MoneyCondition(amt));
										sender.sendMessage(ChatColor.GREEN + strings.CON_ADD.replaceAll("%type", strings.CON_MONEY_TYPE));
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_AMOUNT_GENERAL);
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.CON_MONEY_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
								return true;
							}
							
							// ITEM CONDITION
							if(args[2].equalsIgnoreCase("item")) {
								if(args.length > 4) {
									try {
										int amt = Integer.parseInt(args[4]);
										int[] itm = parseItem(args[3]);
										Material mat = Material.getMaterial(itm[0]);
										int dat = itm[1];
										if(amt < 1 || dat < -1) {
											throw new NumberFormatException();
										}
										qm.addQuestCondition(sender.getName(), new ItemCondition(mat, amt, dat));
										sender.sendMessage(ChatColor.GREEN + strings.CON_ADD.replaceAll("%type", strings.CON_ITEM_TYPE));
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_ITEM_NUMBERS);
									} catch (InvalidDataException e) {
										sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_ITEM_UNKNOWN);
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.CON_ITEM_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
								return true;
							}
							
							// POINT CONDITION
							if(args[2].equalsIgnoreCase("point")) {
								if(args.length > 3) {
									try {
										int amt = Integer.parseInt(args[3]);
										qm.addQuestCondition(sender.getName(), new PointCondition(amt));
										sender.sendMessage(ChatColor.GREEN + strings.CON_ADD.replaceAll("%type", strings.CON_POINT_TYPE));
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_AMOUNT_GENERAL);
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.CON_POINT_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
								return true;
							}
							
							sender.sendMessage(ChatColor.RED + strings.CON_ADD_AVAILABLE + ChatColor.WHITE + CONDITIONS);
							return true;
						}
						
						// REMOVE CONDITION
						if(args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("r")){
							try {
								int id = Integer.parseInt(args[2]);
								qm.removeQuestCondition(sender.getName(), id);
								sender.sendMessage(ChatColor.GREEN + strings.CON_REMOVE.replaceAll("%id", args[2]));
							} catch (NumberFormatException e) {
								sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.CON_REMOVE_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
							} catch (QuesterException e) {
								sender.sendMessage(e.message());
							}
							return true;
						}
						
						// CONDITION DESCRIPTION
						if(args[1].equalsIgnoreCase("desc")){
							if(args.length > 3) {
								int con;
								try {
									con = Integer.parseInt(args[3]);
								} catch (NumberFormatException e) {
									sender.sendMessage(ChatColor.RED + strings.CON_BAD_ID);
									return true;
								}
								
								if(args[2].equalsIgnoreCase("add") || args[2].equalsIgnoreCase("a")) {
									
									if(args.length > 4) {
										String desc = implode(args, 4);
										try {
											qm.addConditionDescription(sender.getName(), con, desc);
											sender.sendMessage(ChatColor.GREEN + strings.CON_DESC_ADD.replaceAll("%id", String.valueOf(con)));
										} catch (QuesterException e) {
											sender.sendMessage(e.message());
										}
										return true;
									}
										
									sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.CON_DESC_ADD_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
									return true;	
								}
								
								if(args[2].equalsIgnoreCase("remove") || args[2].equalsIgnoreCase("r")) {
									try {
										qm.removeConditionDescription(sender.getName(), con);
										sender.sendMessage(ChatColor.GREEN + strings.CON_DESC_REMOVE.replaceAll("%id", String.valueOf(con)));
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								
								return true;
							}	
								
							sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.CON_DESC_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
							return true;
						}
						
						sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.CON_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
						return true;
					}
					
					if(args.length > 1) {
						if(args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("a")){
							sender.sendMessage(ChatColor.RED + strings.CON_ADD_AVAILABLE + ChatColor.WHITE + CONDITIONS);
							return true;
						}
						if(args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("r")) {
							sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.CON_REMOVE_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
							return true;
						}
						if(args[1].equalsIgnoreCase("desc")) {
							sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.CON_DESC_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
							return true;
						}
					}
					
					sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.CON_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
					return true;
				}
				
				// QUEST EVENT TODO
				if(args[0].equalsIgnoreCase("event") || args[0].equalsIgnoreCase("evt")) {
					if(!permCheck(sender, QuestData.MODIFY_PERM, true)) {
						return true;
					}
					if(args.length > 2){
						
						// ADD EVENT
						if(args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("a")){
							
							if(args.length > 2){
								
								int occ;
								int del;
								try {
									int[] arr = deserializeOccasion(args[2]);
									occ = arr[0];
									del = arr[1];
								} catch (NumberFormatException e) {
									sender.sendMessage(ChatColor.RED + strings.EVT_NUMBERS);
									return true;
								}
								
								if(args.length > 3){
									
									// MESSAGE EVENT
									if(args[3].equalsIgnoreCase("msg")) {
										if(args.length > 4) {
											String msg = implode(args, 4);
											try {
												qm.addQevent(sender.getName(), new MessageQevent(occ, del, msg));
												sender.sendMessage(ChatColor.GREEN + strings.EVT_ADD.replaceAll("%type", strings.EVT_MSG_TYPE));
											} catch (QuesterException e) {
												sender.sendMessage(e.message());
											}
											return true;
										}
										sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.EVT_MSG_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
										return true;
									}
									
									// QUEST EVENT
									if(args[3].equalsIgnoreCase("quest")) {
										if(args.length > 4) {
											try {
												int qst = Integer.parseInt(args[4]);
												qm.addQevent(sender.getName(), new QuestQevent(occ, del, qst));
												sender.sendMessage(ChatColor.GREEN + strings.EVT_ADD.replaceAll("%type", strings.EVT_QUEST_TYPE));
											} catch (QuesterException e) {
												sender.sendMessage(e.message());
											} catch (NumberFormatException e) {
												sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_BAD_ID);
											}
											return true;
										}
										sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.EVT_QUEST_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
										return true;
									}
									
									// TOGGLE EVENT
									if(args[3].equalsIgnoreCase("toggle")) {
										if(args.length > 4) {
											try {
												int qst = Integer.parseInt(args[4]);
												qm.addQevent(sender.getName(), new ToggleQevent(occ, del, qst));
												sender.sendMessage(ChatColor.GREEN + strings.EVT_ADD.replaceAll("%type", strings.EVT_TOGGLE_TYPE));
											} catch (QuesterException e) {
												sender.sendMessage(e.message());
											} catch (NumberFormatException e) {
												sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_BAD_ID);
											}
											return true;
										}
										sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.EVT_TOGGLE_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
										return true;
									}
									
									// OBJECTIVE COMPLETE EVENT
									if(args[3].equalsIgnoreCase("objcom")) {
										if(args.length > 4) {
											try {
												int obj = Integer.parseInt(args[4]);
												qm.addQevent(sender.getName(), new ObjectiveCompleteQevent(occ, del, obj));
												sender.sendMessage(ChatColor.GREEN + strings.EVT_ADD.replaceAll("%type", strings.EVT_OBJCOM_TYPE));
											} catch (QuesterException e) {
												sender.sendMessage(e.message());
											} catch (NumberFormatException e) {
												sender.sendMessage(ChatColor.RED + strings.OBJ_BAD_ID);
											}
											return true;
										}
										sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.EVT_OBJCOM_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
										return true;
									}
									
									// CANCEL EVENT
									if(args[3].equalsIgnoreCase("cancel")) {
											try {
												qm.addQevent(sender.getName(), new CancelQevent(occ, del));
												sender.sendMessage(ChatColor.GREEN + strings.EVT_ADD.replaceAll("%type", strings.EVT_CANCEL_TYPE));
											} catch (QuesterException e) {
												sender.sendMessage(e.message());
											}
										return true;
									}
									
									// COMMAND EVENT
									if(args[3].equalsIgnoreCase("cmd")) {
										if(args.length > 4) {
											String comm = implode(args, 4);
											try {
												qm.addQevent(sender.getName(), new CommandQevent(occ, del, comm));
												sender.sendMessage(ChatColor.GREEN + strings.EVT_ADD.replaceAll("%type", strings.EVT_CMD_TYPE));
											} catch (QuesterException e) {
												sender.sendMessage(e.message());
											}
											return true;
										}
										sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.EVT_CMD_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
										return true;
									}
									
									// EXPLOSION EVENT
									if(args[3].equalsIgnoreCase("explosion")) {
										if(args.length > 4) {
											boolean damage = false;
											Location loc = null;
											int rng = 0;
											try {
												if(!args[5].equalsIgnoreCase(QuestData.locLabelPlayer))
													loc = getLoc(sender, args[4]);
												if(args.length > 5) {
													rng = Integer.parseInt(args[5]);
													if(rng < 0) {
														throw new NumberFormatException();
													}
													if(args.length > 6)
														damage = Boolean.parseBoolean(args[6]);
												}
												qm.addQevent(sender.getName(), new ExplosionQevent(occ, del, loc, rng, damage));
												sender.sendMessage(ChatColor.GREEN + strings.EVT_ADD.replaceAll("%type", strings.EVT_EXPL_TYPE));
											} catch (QuesterException e) {
												sender.sendMessage(e.message());
											} catch (NumberFormatException e) {
												sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_RANGE_INVALID);
											}
											return true;
										}
										sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.EVT_EXPL_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
										return true;
									}
									
									// LIGHTNING EVENT
									if(args[3].equalsIgnoreCase("lightning")) {
										if(args.length > 4) {
											boolean damage = false;
											Location loc = null;
											int rng = 0;
											try {
												if(!args[5].equalsIgnoreCase(QuestData.locLabelPlayer))
													loc = getLoc(sender, args[4]);
												if(args.length > 5) {
													rng = Integer.parseInt(args[5]);
													if(rng < 0) {
														throw new NumberFormatException();
													}
													if(args.length > 6)
														damage = Boolean.parseBoolean(args[6]);
												}
												qm.addQevent(sender.getName(), new LightningQevent(occ, del, loc, rng, damage));
												sender.sendMessage(ChatColor.GREEN + strings.EVT_ADD.replaceAll("%type", strings.EVT_LIGHT_TYPE));
											} catch (QuesterException e) {
												sender.sendMessage(e.message());
											} catch (NumberFormatException e) {
												sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_RANGE_INVALID);
											}
											return true;
										}
										sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.EVT_LIGHT_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
										return true;
									}
									
									// EXPLOSION EVENT
									if(args[3].equalsIgnoreCase("spawn")) {
										if(args.length > 6) {
											int amt;
											EntityType ent;
											Location loc = null;
											int rng = 0;
											try {
												ent = parseEntity(args[4]);
												try {
													amt = Integer.parseInt(args[5]);
													if(amt < 1)
														throw new NumberFormatException();
												} catch (NumberFormatException e) {
													throw new QuesterException(strings.ERROR_CMD_AMOUNT_POSITIVE);
												}
												if(!args[7].equalsIgnoreCase(QuestData.locLabelPlayer))
													loc = getLoc(sender, args[6]);
												if(args.length > 7) {
													rng = Integer.parseInt(args[7]);
													if(rng < 0) {
														throw new NumberFormatException();
													}
												}
												qm.addQevent(sender.getName(), new SpawnQevent(occ, del, loc, rng, ent, amt));
												sender.sendMessage(ChatColor.GREEN + strings.EVT_ADD.replaceAll("%type", strings.EVT_SPAWN_TYPE));
											} catch (QuesterException e) {
												sender.sendMessage(e.message());
											} catch (NumberFormatException e) {
												sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_RANGE_INVALID);
											}
											return true;
										}
										sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.EVT_SPAWN_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
										return true;
									}
									
									// TELEPORT EVENT
									if(args[3].equalsIgnoreCase("tele")) {
										if(args.length > 4) {
											Location loc = null;
											
											try {
												loc = getLoc(sender, args[4]);
												
												qm.addQevent(sender.getName(), new TeleportQevent(occ, del, loc));
												sender.sendMessage(ChatColor.GREEN + strings.EVT_ADD.replaceAll("%type", strings.EVT_TELE_TYPE));
											} catch (QuesterException e) {
												sender.sendMessage(e.message());
											}
											return true;
										}
										sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.EVT_TELE_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
										return true;
									}
									
									// SETBLOCK EVENT
									if(args[3].equalsIgnoreCase("block")) {
										if(args.length > 5) {
											Location loc = null;
											try {
												int[] itm = parseItem(args[4]);
												if(itm[0] > 255)
													throw new QuesterException(strings.ERROR_CMD_BLOCK_UNKNOWN);
												int dat = itm[1] < 0 ? 0 : itm[1];
												if(args[6].equalsIgnoreCase(QuestData.locLabelHere)) {
													if(player != null) {
														List<Block> blcks = player.getLastTwoTargetBlocks(null, 6);
														if(!blcks.isEmpty())
															loc = blcks.get(blcks.size()-1).getLocation();
														else {
															throw new QuesterException(strings.ERROR_CMD_BLOCK_LOOK);
														}
													} else {
														throw new QuesterException(strings.ERROR_CMD_LOC_HERE.replaceAll("%here", QuestData.locLabelHere));
													}
												} else
													loc = getLoc(sender, args[5]);
												qm.addQevent(sender.getName(), new SetBlockQevent(occ, del, itm[0], dat, loc));
												sender.sendMessage(ChatColor.GREEN + strings.EVT_ADD.replaceAll("%type", strings.EVT_BLOCK_TYPE));
											} catch (QuesterException e) {
												sender.sendMessage(e.message());
											}
											return true;
										}
										sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.EVT_BLOCK_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
										return true;
									}
									
									// EFFECT EVENT
									if(args[3].equalsIgnoreCase("effect")) {
										if(args.length > 4) {
											try {
												PotionEffect eff = parseEffect(args[4]);
												qm.addQevent(sender.getName(), new EffectQevent(occ, del, eff));
												sender.sendMessage(ChatColor.GREEN + strings.EVT_ADD.replaceAll("%type", strings.EVT_EFF_TYPE));
											} catch (QuesterException e) {
												sender.sendMessage(e.message());
											}
											return true;
										}
										sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.EVT_EFF_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
										return true;
									}
									
									// ITEM EVENT
									if(args[3].equalsIgnoreCase("item")) {
										if(args.length > 4) {
											Material mat;
											int dat;
											int amt = 1;
											try {
												int[] itm = parseItem(args[4]);
												mat = Material.getMaterial(itm[0]);
												dat = itm[1];
												if(args.length > 5) {
													amt = Integer.parseInt(args[5]);
												}
												if(amt < 1 || dat < -1) {
													throw new NumberFormatException();
												}
											} catch (NumberFormatException e) {
												sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_ITEM_NUMBERS);
												return true;
											} catch (QuesterException e) {
												sender.sendMessage(e.message());
												return true;
											}
											try {
												Map<Integer, Integer> enchs = null;
												if(args.length > 6) {
													enchs = parseEnchants(args[6]);
													ItemStack test = new ItemStack(mat, amt, (short)dat);
													for(Integer i : enchs.keySet()) {
														test.addEnchantment(Enchantment.getById(i), enchs.get(i));
													}
												}
												qm.addQevent(sender.getName(), new ItemQevent(occ, del, mat, dat, amt, enchs));
												sender.sendMessage(ChatColor.GREEN + strings.EVT_ADD.replaceAll("%type", strings.EVT_ITEM_TYPE));
											} catch (QuesterException e) {
												sender.sendMessage(e.message());
											} catch (IllegalArgumentException e){
												sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_ENCH_CANT);
												return true;
											}
											return true;
										}
										sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.EVT_ITEM_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
										return true;
									}
									
									// MONEY EVENT
									if(args[3].equalsIgnoreCase("money")) {
										if(args.length > 4) {
											try {
												double amt = Double.parseDouble(args[4]);
												qm.addQevent(sender.getName(), new MoneyQevent(occ, del, amt));
												sender.sendMessage(ChatColor.GREEN + strings.EVT_ADD.replaceAll("%type", strings.EVT_MONEY_TYPE));
											} catch (NumberFormatException e) {
												sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_AMOUNT_GENERAL);
											} catch (QuesterException e) {
												sender.sendMessage(e.message());
											}
											return true;
										}
										sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.EVT_MONEY_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
										return true;
									}
									
									// EXPERIENCE EVENT
									if(args[3].equalsIgnoreCase("exp")) {
										if(args.length > 4) {
											try {
												int amt = Integer.parseInt(args[4]);
												qm.addQevent(sender.getName(), new ExperienceQevent(occ, del, amt));
												sender.sendMessage(ChatColor.GREEN + strings.EVT_ADD.replaceAll("%type", strings.EVT_EXP_TYPE));
											} catch (NumberFormatException e) {
												sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_AMOUNT_GENERAL);
											} catch (QuesterException e) {
												sender.sendMessage(e.message());
											}
											return true;
										}
										sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.EVT_EXP_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
										return true;
									}
									
									// POINT EVENT
									if(args[3].equalsIgnoreCase("point")) {
										if(args.length > 4) {
											try {
												int amt = Integer.parseInt(args[4]);
												qm.addQevent(sender.getName(), new PointQevent(occ, del, amt));
												sender.sendMessage(ChatColor.GREEN + strings.EVT_ADD.replaceAll("%type", strings.EVT_POINT_TYPE));
											} catch (NumberFormatException e) {
												sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_AMOUNT_GENERAL);
											} catch (QuesterException e) {
												sender.sendMessage(e.message());
											}
											return true;
										}
										sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.EVT_POINT_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
										return true;
									}
								}
								
								sender.sendMessage(ChatColor.RED + strings.EVT_ADD_AVAILABLE + ChatColor.WHITE + EVENTS);
								return true;
							}
							
							sender.sendMessage(ChatColor.RED + strings.EVT_SPECIFY);
							return true;
						}
						
						// REMOVE EVENT
						if(args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("r")){
							try {
								int id = Integer.parseInt(args[2]);
								qm.removeQevent(sender.getName(), id);
								sender.sendMessage(ChatColor.GREEN + strings.EVT_REMOVE.replaceAll("%id", args[2]));
							} catch (NumberFormatException e) {
								sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.EVT_REMOVE_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
							} catch (QuesterException e) {
								sender.sendMessage(e.message());
							}
							return true;
						}
						
						sender.sendMessage(ChatColor.RED + strings.EVT_USAGE);
						return true;
					}
					
					if(args.length > 1) {
						if(args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("a")){
							sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.EVT_ADD_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
							sender.sendMessage(ChatColor.RED + strings.EVT_ADD_AVAILABLE + ChatColor.WHITE + EVENTS);
							return true;
						}
						if(args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("r")) {
							sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.EVT_REMOVE_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
							return true;
						}
					}
					
					sender.sendMessage(ChatColor.RED + strings.USAGE_LABEL + strings.EVT_USAGE.replaceAll("%cmd", QuestData.displayedCmd));
					return true;
				}
				
				// QUEST START TODO
				if(args[0].equalsIgnoreCase("start")) {
					if(player == null) {
						sender.sendMessage(ChatColor.RED + strings.MSG_ONLY_PLAYER);
						return true;
					}
					try {
						if(args.length > 1){
							if(!permCheck(sender, QuestData.PERM_USE_START_PICK, true)) {
								return true;
							}
							String questName = implode(args, 1);
							qm.startQuest(player, questName, true);
						} else {
							if(!permCheck(sender, QuestData.PERM_USE_START_RANDOM, true)) {
								return true;
							}
							qm.startRandomQuest(player);
						}
					} catch (QuesterException e) {
						sender.sendMessage(e.message());
					}
					return true;
				}
				
				// QUEST CANCEL
				if(args[0].equalsIgnoreCase("cancel")) {
					if(!permCheck(sender, QuestData.PERM_USE_CANCEL, true)) {
						return true;
					}
					if(player == null) {
						sender.sendMessage(ChatColor.RED + strings.MSG_ONLY_PLAYER);
					} else {
						try {
							qm.cancelQuest(player, true);
							if(QuestData.verbose) {
								Quester.log.info(player.getName() + " cancelled his/her quest.");
							}
						} catch (QuesterException e) {
							sender.sendMessage(e.message());
						}
					}
					return true;
				}
				
				// QUEST DONE
				if(args[0].equalsIgnoreCase("done")) {
					if(!permCheck(sender, QuestData.PERM_USE_DONE, true)) {
						return true;
					}
					if(player == null) {
						sender.sendMessage(ChatColor.RED + strings.MSG_ONLY_PLAYER);
						return true;
					}
					try {
						qm.complete(player, true);
					} catch (QuesterException e) {
						sender.sendMessage(e.message());
					}
					return true;
				}
				
				// QUEST PROGRESS
				if(args[0].equalsIgnoreCase("progress")) {
					if(!permCheck(sender, QuestData.PERM_USE_PROGRESS, true)) {
						return true;
					}
					if(player == null) {
						sender.sendMessage(ChatColor.RED + strings.MSG_ONLY_PLAYER);
					} else {
						try {
							qm.showProgress(player);
						} catch (QuesterException e) {
							sender.sendMessage(e.message());
						}
					}
					return true;
				}
				
				// QUEST SAVE
				if(args[0].equalsIgnoreCase("save")) {
					if(!permCheck(sender, QuestData.ADMIN_PERM, true)) {
						return true;
					}
					QuestData.saveProfiles();
					sender.sendMessage(ChatColor.GREEN + strings.MSG_PROFILES_SAVE);
					return true;
				}
				
				// QUEST START SAVE
				if(args[0].equalsIgnoreCase("startsave")) {
					if(!permCheck(sender, QuestData.ADMIN_PERM, true)) {
						return true;
					}
					if(QuestData.saveInterval == 0) {
						sender.sendMessage(ChatColor.RED + strings.MSG_AUTOSAVE_DISABLED);
						return true;
					}
					if(Quester.plugin.startSaving()) {
						sender.sendMessage(ChatColor.GREEN + strings.MSG_AUTOSAVE_STARTED.replaceAll("%interval", String.valueOf(QuestData.saveInterval)));
					} else {
						sender.sendMessage(ChatColor.RED + strings.MSG_AUTOSAVE_RUNNING);
					}
					return true;
				}
				// QUEST STOP SAVE
				if(args[0].equalsIgnoreCase("stopsave")) {
					if(!permCheck(sender, QuestData.ADMIN_PERM, true)) {
						return true;
					}
					if(QuestData.saveInterval == 0) {
						sender.sendMessage(ChatColor.RED + strings.MSG_AUTOSAVE_DISABLED);
						return true;
					}
					if(Quester.plugin.stopSaving()) {
						sender.sendMessage(ChatColor.GREEN + strings.MSG_AUTOSAVE_STOPPED);
					} else {
						sender.sendMessage(ChatColor.RED + strings.MSG_AUTOSAVE_NOT_RUNNING);
					}
					return true;
				}
				
				// QUEST RELOAD
				if(args[0].equalsIgnoreCase("reload")) {
					if(!permCheck(sender, QuestData.ADMIN_PERM, true)) {
						return true;
					}
					Quester.plugin.initializeConfig();
					Quester.plugin.loadLocal();
					sender.sendMessage(ChatColor.GREEN + strings.MSG_CONFIG_RELOADED);
					return true;
				}
				
				sender.sendMessage(ChatColor.RED + strings.ERROR_CMD_ARGUMENTS_UNKNOWN.replaceAll("%cmd", QuestData.displayedCmd));
			} else {
				sender.sendMessage(Quester.LABEL + ChatColor.GOLD + "version " + Quester.plugin.getDescription().getVersion());
				sender.sendMessage(Quester.LABEL + "http://dev.bukkit.org/server-mods/quester/");
				sender.sendMessage(Quester.LABEL + ChatColor.GRAY + "made by " + Quester.plugin.getDescription().getAuthors().get(0));
			}
			return true;
		}
		return false;
	}

	// UTILITY
	
}
