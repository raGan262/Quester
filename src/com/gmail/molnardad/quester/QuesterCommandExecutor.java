package com.gmail.molnardad.quester;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import com.avaje.ebeaninternal.server.lib.util.InvalidDataException;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.objectives.*;
import com.gmail.molnardad.quester.rewards.*;
import com.gmail.molnardad.quester.conditions.*;
import static com.gmail.molnardad.quester.utils.Util.*;

public class QuesterCommandExecutor implements CommandExecutor {

	Player player = null;
	QuestManager qm = null;
	
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
					sender.sendMessage(line(ChatColor.BLUE, "Quester help", ChatColor.GOLD));
					sender.sendMessage(ChatColor.GOLD + "/quest help/? " + ChatColor.GRAY + "- this");
					sender.sendMessage(ChatColor.GOLD + "/quest info [name] " + ChatColor.GRAY + "- shows info about quest");
					sender.sendMessage(ChatColor.GOLD + "/quest show [name] " + ChatColor.GRAY + "- shows info about quest");
					sender.sendMessage(ChatColor.GOLD + "/quest start [name] " + ChatColor.GRAY + "- starts a quest");
					sender.sendMessage(ChatColor.GOLD + "/quest cancel " + ChatColor.GRAY + "- cancels current quest");
					sender.sendMessage(ChatColor.GOLD + "/quest done " + ChatColor.GRAY + "- completes current quest");
					sender.sendMessage(ChatColor.GOLD + "/quest progress " + ChatColor.GRAY + "- shows current quest progress");
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
					
					qm.showProfile(sender);
					return true;
				}
				
				// QUEST SHOW
				if(args[0].equalsIgnoreCase("show")) {
					if(args.length > 1){
						String questName = sconcat(args, 1);
						try {
							qm.showQuest(sender, questName);
						} catch (QuesterException e) {
							sender.sendMessage(e.message());
						}
					} else {
						sender.sendMessage(ChatColor.RED + "Usage: /quest show [quest_name].");
					}
					return true;
				}
				
				// QUEST INFO
				if(args[0].equalsIgnoreCase("info")) {
					if(args.length > 1){
						String questName = sconcat(args, 1);
						try {
							if(permCheck(sender, QuestData.MODIFY_PERM, false)) {
								qm.showQuestInfo(sender, questName);
							} else {
								qm.showQuest(sender, questName);
							}
						} catch (QuesterException e) {
							sender.sendMessage(e.message());
						}
					} else {
						try {
							if(permCheck(sender, QuestData.MODIFY_PERM, false)) {
								qm.showQuestInfo(sender);
							} else {
								sender.sendMessage(ChatColor.RED + "Usage: /quest info [quest_name*]. (optional if selected)");
							}
						} catch (QuesterException e) {
							sender.sendMessage(e.message());
						}
					}
					return true;
				}
				
				// QUEST LIST
				if(args[0].equalsIgnoreCase("list")) {
					if(permCheck(sender, QuestData.MODIFY_PERM, false)) {
						qm.showFullQuestList(sender);
					} else {
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
							String questName = sconcat(args, 1);
							qm.createQuest(sender.getName(), questName);
							sender.sendMessage(ChatColor.GREEN + "Quest created and selected.");
							if(QuestData.verbose) {
								Quester.log.info(sender.getName() + " created quest '" + questName + "'.");
							}
						} catch (QuesterException e) {
							sender.sendMessage(e.message());
						}
					} else {
						sender.sendMessage(ChatColor.RED + "Usage: /quest create [quest_name].");
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
							String questName = sconcat(args, 1);
							qm.removeQuest(sender.getName(), questName);
							sender.sendMessage(ChatColor.GREEN + "Quest removed.");
							if(QuestData.verbose) {
								Quester.log.info(sender.getName() + " removed quest '" + questName + "'.");
							}
						} catch (QuesterException e) {
							sender.sendMessage(e.message());
						}
					} else {
						sender.sendMessage(ChatColor.RED + "Usage: /quest remove [quest_name].");
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
							String questName = sconcat(args, 1);
							qm.selectQuest(sender.getName(), questName);
							sender.sendMessage(ChatColor.GREEN + "Quest selected.");
						} catch (QuesterException e) {
							sender.sendMessage(e.message());
						}
					} else {
						sender.sendMessage(ChatColor.RED + "Usage: /quest select [quest_name].");
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
							String questName = sconcat(args, 1);
							qm.changeQuestName(sender.getName(), questName);
							sender.sendMessage(ChatColor.GREEN + "Quest name changed to '" + questName + "'.");
						} catch (QuesterException e) {
							sender.sendMessage(e.message());
						}
					} else {
						sender.sendMessage(ChatColor.RED + "Usage: /quest name [new_name].");
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
						String questDesc = sconcat(args, 2).replaceAll("\\\\n", "\n");
						
						// SET DESCRIPTION
						if(args[1].equalsIgnoreCase("set") || args[1].equalsIgnoreCase("s")){
							try {
								qm.setQuestDescription(sender.getName(), questDesc);
								sender.sendMessage(ChatColor.GREEN + "Quest description set.");
							} catch (QuesterException e) {
								sender.sendMessage(e.message());
							}
							return true;
						}
						
						// ADD DESCRIPTION
						if(args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("a")){
							try {
								qm.addQuestDescription(sender.getName(), questDesc);
								sender.sendMessage(ChatColor.GREEN + "Quest description added.");
							} catch (QuesterException e) {
								sender.sendMessage(e.message());
							}
							return true;
						}
					}
					sender.sendMessage(ChatColor.RED + "Usage: /quest desc [set|add] [quest_description].");
					return true;
				}
				
				// QUEST ORDERED
				if(args[0].equalsIgnoreCase("ordered") || args[0].equalsIgnoreCase("ord")) {
					if(!permCheck(sender, QuestData.MODIFY_PERM, true)) {
						return true;
					}
					try {
						qm.setOrdered(sender.getName(), true);
						sender.sendMessage(ChatColor.GREEN + "Quest set as ordered.");
					} catch (QuesterException e) {
						sender.sendMessage(e.message());
					}
					return true;
				}
				
				// QUEST UNORDERED
				if(args[0].equalsIgnoreCase("unordered") || args[0].equalsIgnoreCase("unord")) {
					if(!permCheck(sender, QuestData.MODIFY_PERM, true)) {
						return true;
					}
					try {
						qm.setOrdered(sender.getName(), false);
						sender.sendMessage(ChatColor.GREEN + "Quest set as unordered.");
					} catch (QuesterException e) {
						sender.sendMessage(e.message());
					}
					return true;
				}
				
				// QUEST TOGGLE
				if(args[0].equalsIgnoreCase("toggle")) {
					if(!permCheck(sender, QuestData.MODIFY_PERM, true)) {
						return true;
					}
					String questName = sconcat(args, 1);
					boolean active;
					try {
						if(questName.isEmpty()){
							qm.toggleQuest(sender);
							active = qm.isQuestActive(sender);
						} else {
							qm.toggleQuest(questName);
							active = qm.isQuestActive(questName);
						}
					} catch (QuesterException e) {
						sender.sendMessage(e.message());
						return true;
					}
					String status;
					if(active){
						status = "activated.";
					} else {
						status = "deactivated.";
					}
					sender.sendMessage(ChatColor.GREEN + "Quest " + status);
					return true;
				}
				
				// QUEST REWARD
				if(args[0].equalsIgnoreCase("reward") || args[0].equalsIgnoreCase("rew")) {
					if(!permCheck(sender, QuestData.MODIFY_PERM, true)) {
						return true;
					}
					if(args.length > 2){
						
						// ADD REWARD
						if(args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("a")){
							
							// EFFECT REWARD
							if(args[2].equalsIgnoreCase("effect")) {
								if(args.length > 5) {
									try {
										int dur = Integer.parseInt(args[4]);
										int pow = Integer.parseInt(args[5]);
										if(dur < 0 || pow < 0) {
											sender.sendMessage(ChatColor.RED + "Duration and power must be >= 0.");
											return true;
										}
										PotionEffectType eff = PotionEffectType.getByName(args[3].toUpperCase());
										if(eff == null) {
											eff = PotionEffectType.getById(Integer.parseInt(args[3]));
										}
										if(eff == null) {
											sender.sendMessage(ChatColor.RED + "Unknown effect.");
											return true;
										}
										qm.addQuestReward(sender.getName(), new EffectReward(eff.getId(), dur, pow));
										sender.sendMessage(ChatColor.GREEN + "Effect reward added.");
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + "All arguments must be >= 0, first can be name.");
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + "Usage: /quest reward add effect [effect_id/name] [duration_secs] [power].");
								return true;
							}
							
							// ITEM REWARD
							if(args[2].equalsIgnoreCase("item")) {
								if(args.length > 3) {
									Material mat;
									int amt = 1;
									int dat = 0;
									try {
										int[] itm = parseItem(args[3]);
										mat = Material.getMaterial(itm[0]);
										dat = itm[1] == -1 ? 0 : itm[1];
										if(args.length > 4) {
											amt = Integer.parseInt(args[4]);
										}
										if(amt < 1 || dat < 0) {
											sender.sendMessage(ChatColor.RED + "Amount must be > 0. Data must be >= 0");
											return true;
										}
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + "Amount and data must be numbers.");
										return true;
									} catch (InvalidDataException e) {
										sender.sendMessage(ChatColor.RED + "Unknown item.");
										return true;
									}
									Map<Integer, Integer> enchs = new HashMap<Integer, Integer>();
									if(args.length > 5) {
										try {
											enchs = parseEnchants(args, 5);
											ItemStack test = new ItemStack(mat, amt, (short)dat);
											for(Integer i : enchs.keySet()) {
												test.addEnchantment(Enchantment.getById(i), enchs.get(i));
											}
										} catch (NumberFormatException e) {
											sender.sendMessage(ChatColor.RED + "Enchantment level must be > 0.");
											return true;
										} catch (InvalidDataException e) {
											sender.sendMessage(ChatColor.RED + "Invalid enchantment.");
											return true;
										} catch (IllegalArgumentException e){
											sender.sendMessage(ChatColor.RED + "One or more enchantments cannot be applied to specified item.");
											return true;
										}
									}
									try {
										qm.addQuestReward(sender.getName(), new ItemReward(mat, amt, dat, enchs));
										sender.sendMessage(ChatColor.GREEN + "Item reward added.");
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + "Usage: /quest reward add item [item_id/name][:data*] [amount*] {ench1*}... .\n"
										+ "{ench} - [enchantment_id/name]:[level] ; * - optional");
								return true;
							}
							
							// MONEY REWARD
							if(args[2].equalsIgnoreCase("money")) {
								if(args.length > 3) {
									try {
										double amt = Double.parseDouble(args[3]);
										qm.addQuestReward(sender.getName(), new MoneyReward(amt));
										sender.sendMessage(ChatColor.GREEN + "Money reward added.");
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + "Amount must be number. (negative to take)");
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + "Usage: /quest reward add money [amount].");
								return true;
							}
							
							// EXPERIENCE REWARD
							if(args[2].equalsIgnoreCase("exp")) {
								if(args.length > 3) {
									try {
										int amt = Integer.parseInt(args[3]);
										qm.addQuestReward(sender.getName(), new ExpReward(amt));
										sender.sendMessage(ChatColor.GREEN + "Experience reward added.");
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + "Amount must be number. (negative to take)");
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + "Usage: /quest reward add exp [amount].");
								return true;
							}
							
							// COMMAND REWARD
							if(args[2].equalsIgnoreCase("cmd")) {
								if(args.length > 3) {
									try {
										String command = sconcat(args, 3);
										qm.addQuestReward(sender.getName(), new CommandReward(command));
										sender.sendMessage(ChatColor.GREEN + "Command reward added.");
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + "Usage: /quest reward add cmd [command].");
								return true;
							}
							
							// TELEPORT REWARD
							if(args[2].equalsIgnoreCase("tele")) {
								if(args.length > 3) {
									if(args[3].equalsIgnoreCase("here")) {
										if(player != null) {
											try {
												qm.addQuestReward(sender.getName(), new TeleportReward(player.getLocation()));
												sender.sendMessage(ChatColor.GREEN + "Teleport reward added.");
											} catch (QuesterException e) {
												sender.sendMessage(e.message());
											}
										} else {
											sender.sendMessage(ChatColor.RED + "This command requires player context."
													+ "Use /quest reward add tele [X] [Y] [Z] [world].");
										}
										return true;
									}
									if(args.length > 6) {
										try {
											double x = Double.parseDouble(args[3]);
											double y = Double.parseDouble(args[4]);
											double z = Double.parseDouble(args[5]);
											if(y < 0) {
												sender.sendMessage(ChatColor.RED + "Y must be >= 0.");
												return true;
											}
											World world = null;
											if(player != null && args[6].equalsIgnoreCase("this")) {
												world = player.getWorld();
											} else {
												world = Quester.plugin.getServer().getWorld(args[6]);
											}
											if(world == null) {
												sender.sendMessage(ChatColor.RED + "World '" + args[6] + "' not found.");
												return true;
											}
											qm.addQuestReward(sender.getName(), new TeleportReward(new Location(world, x, y, z)));
											sender.sendMessage(ChatColor.GREEN + "Teleport reward added.");
										} catch (NumberFormatException e) {
											sender.sendMessage(ChatColor.RED + "Coordinates must be numbers.");
										} catch (QuesterException e) {
											sender.sendMessage(e.message());
										}
										return true;
									}
								}
								sender.sendMessage(ChatColor.RED + "Usage: /quest reward add tele {location}.\n"
										+ "{location} - [X] [Y] [Z] [world or 'this'] /OR/ 'here'");
								return true;
							}
							
							// POINT REWARD
							if(args[2].equalsIgnoreCase("point")) {
								if(args.length > 3) {
									try {
										int amt = Integer.parseInt(args[3]);
										qm.addQuestReward(sender.getName(), new PointReward(amt));
										sender.sendMessage(ChatColor.GREEN + "Point reward added.");
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + "Amount must be number. (negative to take)");
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + "Usage: /quest reward add point [amount].");
								return true;
							}
							
							sender.sendMessage(ChatColor.RED + "Available reward types: " + ChatColor.WHITE + "item, money, exp, effect, tele, cmd, point");
							return true;
						}
						
						// REMOVE REWARD
						if(args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("r")){
							try {
								int id = Integer.parseInt(args[2]);
								qm.removeQuestReward(sender.getName(), id);
								sender.sendMessage(ChatColor.GREEN + "Reward " + args[2] + " removed.");
							} catch (NumberFormatException e) {
								sender.sendMessage(ChatColor.RED + "Usage: /quest reward remove [id_number].");
							} catch (QuesterException e) {
								sender.sendMessage(e.message());
							}
							return true;
						}
						
						sender.sendMessage(ChatColor.RED + "Usage: /quest reward [add|remove] [reward_type] [args].");
						return true;
					}
					
					if(args.length > 1) {
						if(args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("a")){
							sender.sendMessage(ChatColor.RED + "Available reward types: " + ChatColor.WHITE + "item, money, exp, effect, tele, cmd, point");
							return true;
						}
						if(args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("r")) {
							sender.sendMessage(ChatColor.RED + "Usage: /quest reward remove [id_number].");
							return true;
						}
					}
					
					sender.sendMessage(ChatColor.RED + "Usage: /quest reward [add|remove] [reward_type] [args].");
					return true;
				}
				
				// QUEST OBJECTIVE
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
										sender.sendMessage(ChatColor.GREEN + "Break objective added.");
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + "Amount must be > 0. Data must be >= 0.");
									} catch (InvalidDataException e) {
										sender.sendMessage(ChatColor.RED + "Unknown block.");
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + "Usage: /quest objective add break [block_id/name][:data*] [amount] [hand*].\n"
										+ "* - optional");
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
										sender.sendMessage(ChatColor.GREEN + "Place objective added.");
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + "Amount must be > 0. Data must be >= 0.");
									} catch (InvalidDataException e) {
										sender.sendMessage(ChatColor.RED + "Unknown block.");
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + "Usage: /quest objective add place [block_id/name][:data*] [amount].\n"
										+ "* - optional");
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
										sender.sendMessage(ChatColor.RED + "Amount must be > 0. Data must be >= 0.");
										return true;
									} catch (InvalidDataException e) {
										sender.sendMessage(ChatColor.RED + "Unknown item.");
										return true;
									}
									Map<Integer, Integer> enchs = new HashMap<Integer, Integer>();
									if(args.length > 5) {
										try {
											enchs = parseEnchants(args, 5);
											ItemStack test = new ItemStack(mat, amt, (short)dat);
											for(Integer i : enchs.keySet()) {
												test.addEnchantment(Enchantment.getById(i), enchs.get(i));
											}
										} catch (NumberFormatException e) {
											sender.sendMessage(ChatColor.RED + "Enchantment level must be > 0.");
											return true;
										} catch (InvalidDataException e) {
											sender.sendMessage(ChatColor.RED + "Invalid enchantment.");
											return true;
										} catch (IllegalArgumentException e){
											sender.sendMessage(ChatColor.RED + "One or more enchantments cannot be applied to specified item.");
											return true;
										}
									}
									try {
										qm.addQuestObjective(sender.getName(), new ItemObjective(mat, amt, dat, enchs));
										sender.sendMessage(ChatColor.GREEN + "Item objective added.");
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + "Usage: /quest objective add item [item_id/name][:data*] [amount*] {ench1*}... .\n"
										+ "{ench} - [enchantment_id/name]:[level] ; * - optional");
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
										sender.sendMessage(ChatColor.GREEN + "Collect objective added.");
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + "Amount must be > 0. Data must be >= 0.");
									} catch (InvalidDataException e) {
										sender.sendMessage(ChatColor.RED + "Unknown item.");
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + "Usage: /quest objective add collect [block_id/name][:data*] [amount].\n"
										+ "* - optional");
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
										sender.sendMessage(ChatColor.RED + "Amount must be > 0.");
										return true;
									} catch (InvalidDataException e) {
										sender.sendMessage(ChatColor.RED + "Unknown item.");
										return true;
									}
									Map<Integer, Integer> enchs = new HashMap<Integer, Integer>();
									if(args.length > 5) {
										try {
											enchs = parseEnchants(args, 5);
											ItemStack test = new ItemStack(mat);
											for(Integer i : enchs.keySet()) {
												test.addEnchantment(Enchantment.getById(i), enchs.get(i));
											}
										} catch (NumberFormatException e) {
											sender.sendMessage(ChatColor.RED + "Enchantment level must be > 0.");
											return true;
										} catch (InvalidDataException e) {
											sender.sendMessage(ChatColor.RED + "Invalid enchantment.");
											return true;
										} catch (IllegalArgumentException e){
											sender.sendMessage(ChatColor.RED + "One or more enchantments cannot be applied to specified item.");
											return true;
										}
									}
									try {
										qm.addQuestObjective(sender.getName(), new EnchantObjective(mat, amt, enchs));
										sender.sendMessage(ChatColor.GREEN + "Enchant objective added.");
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + "Usage: /quest objective add ench [item_id/name] [amount*] {ench1*}... .\n"
										+ "{ench} - [enchantment_id/name]:[level] ; * - optional");
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
										sender.sendMessage(ChatColor.GREEN + "Experience objective added.");
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + "Amount must be positive number.");
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + "Usage: /quest objective add exp [amount].");
								return true;
							}
							
							// LOCATION OBJECTIVE
							if(args[2].equalsIgnoreCase("loc")) {
								if(args.length > 3) {
									Location loc = null;
									int rng = 3;
									int rangeArg = 0;
									if(args[3].equalsIgnoreCase("here")) {
										if(player != null) {
											loc = player.getLocation();
										} else {
											sender.sendMessage(ChatColor.RED + "Location 'here' requires player context.");
											return true;
										}
										if(args.length > 4) {
											rangeArg = 4;
										}
									} else if(args.length > 6) {
										try {
											loc = getLoc(sender, args, 3);
										} catch (NumberFormatException e) {
											sender.sendMessage(ChatColor.RED + "X, Y and Z must be >= 0.");
											return true;
										}
										if(loc == null) {
											if(args[6].equalsIgnoreCase("this")) {
												sender.sendMessage(ChatColor.RED + "World 'this' requires player context.");
											} else {
												sender.sendMessage(ChatColor.RED + "World not found.");
											}
											return true;
										}
										if(args.length > 7) {
											rangeArg = 7;
										}
									}
									if(rangeArg > 0){
										try {
											rng = Integer.parseInt(args[rangeArg]);
											if(rng < 1) {
												throw new NumberFormatException();
											}
										} catch (NumberFormatException e) {
											sender.sendMessage(ChatColor.RED + "Range must be > 0.");
											return true;
										}
									}
									try {
										qm.addQuestObjective(sender.getName(), new LocObjective(loc, rng));
										sender.sendMessage(ChatColor.GREEN + "Location objective added.");
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + "Usage: /quest objective add loc {location} [range*].\n"
										+ "{location} - [X] [Y] [Z] [world or 'this']; * - optional");
								return true;
							}
							
							// DEATH OBJECTIVE
							if(args[2].equalsIgnoreCase("death")) {
								if(args.length > 3) {
									int amt = 1;
									Location loc = null;
									int rng = 5;
									// regular location
									if(args.length > 7) {
										try {
											loc = getLoc(sender, args, 4);
											amt = Integer.parseInt(args[3]);
											rng = Integer.parseInt(args[5]);
											if(rng <= 0) {
												throw new NumberFormatException();
											}
										} catch (NumberFormatException e) {
											if(args.length > 8) {
												sender.sendMessage(ChatColor.RED + "Amount, X, Y, Z and range must be > 0.");
											} else {
												sender.sendMessage(ChatColor.RED + "Amount, X, Y and Z must be > 0.");
											}
											return true;
										}
										if(loc == null) {
											if(args[7].equalsIgnoreCase("this")) {
												sender.sendMessage(ChatColor.RED + "World 'this' requires player context.");
											} else {
												sender.sendMessage(ChatColor.RED + "World not found.");
											}
											return true;
											
										}
										
									// 'here' or no location
									} else {
										try {
											amt = Integer.parseInt(args[3]);
											if(args.length > 5)
												rng = Integer.parseInt(args[5]);
											if(amt <= 0 || rng <= 0) {
												throw new NumberFormatException();
											}
										} catch(NumberFormatException e) {
											if(args.length > 5)
												sender.sendMessage(ChatColor.RED + "Amount and range must be > 0.");
											else 
												sender.sendMessage(ChatColor.RED + "Amount must be > 0.");
											return true;
										}
										// has 'here'
										if(args.length > 4) {
											if(args[4].equalsIgnoreCase("here")) {
												if(player != null) {
													loc = player.getLocation();
												} else {
													sender.sendMessage(ChatColor.RED + "Location 'here' requires player context.");
													return true;
												}
											} else {
												sender.sendMessage(ChatColor.RED + "Invalid location.");
												return true;
											}
										}
									}
									try {
										qm.addQuestObjective(sender.getName(), new DeathObjective(amt, loc, rng));
										sender.sendMessage(ChatColor.GREEN + "Death objective added.");
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + "Usage: /quest objective add death [amount] {location*} [range*].\n"
										+ "{location} - [X] [Y] [Z] [world or 'this']; * - optional");
								return true;
							}
							
							// WORLD OBJECTIVE
							if(args[2].equalsIgnoreCase("world")) {
								if(args.length > 3) {
									World world = null;
									if(args[3].equalsIgnoreCase("this")) {
										if(player != null) {
											world = player.getWorld();
										} else {
											sender.sendMessage(ChatColor.RED + "World 'this' requires player context.");
											return true;
										}
									} else {
										world = sender.getServer().getWorld(args[3]);
									}
									if(world == null) {
										sender.sendMessage(ChatColor.RED + "Invalid world.");
										return true;
									}
									try {
										qm.addQuestObjective(sender.getName(), new WorldObjective(world.getName()));
										sender.sendMessage(ChatColor.GREEN + "World objective added.");
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + "Usage: /quest objective add world [world or 'this']");
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
											ent = EntityType.fromName(args[4].toUpperCase());
											if(ent == null) {
												ent = EntityType.fromId(Integer.parseInt(args[4]));
												if(ent == null || ent.getTypeId() < 50) {
													sender.sendMessage(ChatColor.RED + "Unknown entity.");
													return true;
												}
											}
										}
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + "Amount must be > 0. Id must be number or valid entity name.");
										return true;
									}
									try {
										qm.addQuestObjective(sender.getName(), new MobKillObjective(amt, ent));
										sender.sendMessage(ChatColor.GREEN + "Mob kill objective added.");
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + "Usage: /quest objective add mobkill [amount] [entity_id/name*]"
										+ "* - optional");
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
										sender.sendMessage(ChatColor.RED + "Amount must be > 0.");
										return true;
									}
									if(args.length > 4) {
										name = args[4];
									}
									try {
										qm.addQuestObjective(sender.getName(), new PlayerKillObjective(amt, name));
										sender.sendMessage(ChatColor.GREEN + "Player kill objective added.");
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + "Usage: /quest objective add kill [amount] [player_name*]"
										+ "* - optional");
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
										qm.addQuestObjective(sender.getName(), new CraftObjective(mat, amt, dat));
										sender.sendMessage(ChatColor.GREEN + "Craft objective added.");
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + "Amount must be > 0. Data must be >= 0.");
									} catch (InvalidDataException e) {
										sender.sendMessage(ChatColor.RED + "Unknown item.");
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + "Usage: /quest objective add craft [item_id/name][:data*] [amount].\n"
										+ "* - optional");
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
										qm.addQuestObjective(sender.getName(), new SmeltObjective(mat, amt, dat));
										sender.sendMessage(ChatColor.GREEN + "Smelt objective added.");
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + "Amount must be > 0. Data must be >= 0.");
									} catch (InvalidDataException e) {
										sender.sendMessage(ChatColor.RED + "Unknown item.");
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + "Usage: /quest objective add smelt [item_id/name][:data*] [amount].\n"
										+ "* - optional");
								return true;
							}
							
							// SHEAR OBJECTIVE
							if(args[2].equalsIgnoreCase("shear")) {
								if(args.length > 3) {
									try {
										byte dat = -1;
										int amt = Integer.parseInt(args[3]);
										if(args.length > 4)
											dat = parseColor(args[4]);
										if(amt < 1)
											throw new NumberFormatException();
										qm.addQuestObjective(sender.getName(), new ShearObjective(amt, dat));
										sender.sendMessage(ChatColor.GREEN + "Shear objective added.");
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + "Amount must be positive number.");
									} catch (InvalidDataException e) {
										sender.sendMessage(ChatColor.RED + "Unknown color.");
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + "Usage: /quest objective add shear [amount] [color_id/name*].\n" +
											"* - optional");
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
										sender.sendMessage(ChatColor.GREEN + "Fish objective added.");
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + "Amount must be positive number.");
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + "Usage: /quest objective add fish [amount].");
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
										sender.sendMessage(ChatColor.GREEN + "Milk objective added.");
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + "Amount must be positive number.");
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + "Usage: /quest objective add milk [amount].");
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
										sender.sendMessage(ChatColor.RED + "Amount must be > 0. Id must be number or valid entity name.");
										return true;
									} catch (InvalidDataException e){
										sender.sendMessage(ChatColor.RED + "Unknown entity.");
										return true;
									}
									try {
										qm.addQuestObjective(sender.getName(), new TameObjective(amt, ent));
										sender.sendMessage(ChatColor.GREEN + "Tame objective added.");
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + "Usage: /quest objective add tame [amount] [entity_id/name*]"
										+ "* - optional");
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
										sender.sendMessage(ChatColor.GREEN + "Money objective added.");
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + "Amount must be positive number.");
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + "Usage: /quest objective add money [amount].");
								return true;
							}
							
							sender.sendMessage(ChatColor.RED + "Available objective types: " + ChatColor.WHITE + "break, place, item, exp, loc, death, world, " +
									"mobkill, kill, craft, ench, smelt, shear, fish, milk, collect, tame, money");
							return true;
						}
						
						// REMOVE OBJECTIVE
						if(args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("r")){
							try {
								int id = Integer.parseInt(args[2]);
								qm.removeQuestObjective(sender.getName(), id);
								sender.sendMessage(ChatColor.GREEN + "Objective " + args[2] + " removed.");
							} catch (NumberFormatException e) {
								sender.sendMessage(ChatColor.RED + "Usage: /quest objective remove [id_number].");
							} catch (QuesterException e) {
								sender.sendMessage(e.message());
							}
							return true;
						}
						
						sender.sendMessage(ChatColor.RED + "Usage: /quest objective [add|remove] [type] [args].");
						return true;
					}
					
					if(args.length > 1) {
						if(args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("a")){
							sender.sendMessage(ChatColor.RED + "Available objective types: " + ChatColor.WHITE + "break, place, item, exp, loc, death, world, " +
									"mobkill, kill, craft, ench, smelt, shear, fish, milk, collect, tame, money");
							return true;
						}
						if(args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("r")) {
							sender.sendMessage(ChatColor.RED + "Usage: /quest objective remove [id_number].");
							return true;
						}
					}
					
					sender.sendMessage(ChatColor.RED + "Usage: /quest objective [add|remove] [objective_type] [args].");
					return true;
				}
				
				// QUEST CONDITION
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
									String questName = sconcat(args, 3);
									try {
										qm.addQuestCondition(sender.getName(), new QuestCondition(questName));
										sender.sendMessage(ChatColor.GREEN + "Quest condition added.");
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + "Usage: /quest condition add quest [quest_name]");
								return true;
							}
							
							// QUESTNOT CONDITION
							if(args[2].equalsIgnoreCase("questnot")) {
								if(args.length > 3) {
									String questName = sconcat(args, 3);
									try {
										qm.addQuestCondition(sender.getName(), new QuestNotCondition(questName));
										sender.sendMessage(ChatColor.GREEN + "QuestNot condition added.");
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + "Usage: /quest condition add questnot [quest_name]");
								return true;
							}
							
							// PERMISSION CONDITION
							if(args[2].equalsIgnoreCase("perm")) {
								if(args.length > 3) {
									String perm = args[3];
									try {
										qm.addQuestCondition(sender.getName(), new PermissionCondition(perm));
										sender.sendMessage(ChatColor.GREEN + "Permission condition added.");
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + "Usage: /quest condition add perm [quest_name]");
								return true;
							}
							
							// MONEY CONDITION
							if(args[2].equalsIgnoreCase("money")) {
								if(args.length > 3) {
									try {
										int amt = Integer.parseInt(args[3]);
										qm.addQuestCondition(sender.getName(), new MoneyCondition(amt));
										sender.sendMessage(ChatColor.GREEN + "Money condition added.");
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + "Amount must be number.");
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + "Usage: /quest condition add money [amount]");
								return true;
							}
							
							// ITEM CONDITION
							if(args[2].equalsIgnoreCase("item")) {
								if(args.length > 3) {
									try {
										int amt = Integer.parseInt(args[4]);
										int[] itm = parseItem(args[3]);
										Material mat = Material.getMaterial(itm[0]);
										int dat = itm[1];
										qm.addQuestCondition(sender.getName(), new ItemCondition(mat, amt, dat));
										sender.sendMessage(ChatColor.GREEN + "Item condition added.");
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + "Amount must be number. Data must be number.");
									} catch (InvalidDataException e) {
										sender.sendMessage(ChatColor.RED + "Unknown item.");
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + "Usage: /quest condition add item [item_id/name][:data*] [amount]");
								return true;
							}
							
							// POINT CONDITION
							if(args[2].equalsIgnoreCase("point")) {
								if(args.length > 3) {
									try {
										int amt = Integer.parseInt(args[3]);
										qm.addQuestCondition(sender.getName(), new PointCondition(amt));
										sender.sendMessage(ChatColor.GREEN + "Point condition added.");
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + "Amount must be number.");
									} catch (QuesterException e) {
										sender.sendMessage(e.message());
									}
									return true;
								}
								sender.sendMessage(ChatColor.RED + "Usage: /quest condition add point [amount]");
								return true;
							}
							
							sender.sendMessage(ChatColor.RED + "Available condition types: " + ChatColor.WHITE + "quest, questnot, perm, money, item, point");
							return true;
						}
						
						// REMOVE CONDITION
						if(args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("r")){
							try {
								int id = Integer.parseInt(args[2]);
								qm.removeQuestObjective(sender.getName(), id);
								sender.sendMessage(ChatColor.GREEN + "Condition " + args[2] + " removed.");
							} catch (NumberFormatException e) {
								sender.sendMessage(ChatColor.RED + "Usage: /quest condition remove [id_number].");
							} catch (QuesterException e) {
								sender.sendMessage(e.message());
							}
							return true;
						}
						
						sender.sendMessage(ChatColor.RED + "Usage: /quest condition [add|remove] [condition_type] [args].");
						return true;
					}
					
					if(args.length > 1) {
						if(args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("a")){
							sender.sendMessage(ChatColor.RED + "Available condition types: " + ChatColor.WHITE + "quest, questnot, perm, money, item, point");
							return true;
						}
						if(args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("r")) {
							sender.sendMessage(ChatColor.RED + "Usage: /quest condition remove [id_number].");
							return true;
						}
					}
					
					sender.sendMessage(ChatColor.RED + "Usage: /quest condition [add|remove] [condition_type] [args].");
					return true;
				}
				
				// QUEST START
				if(args[0].equalsIgnoreCase("start")) {
					if(!permCheck(sender, QuestData.USE_PERM, true)) {
						return true;
					}
					if(player == null) {
						sender.sendMessage(ChatColor.RED + "This command can only be run by player.");
						return true;
					}
					if(QuestData.disUseCmds) {
						sender.sendMessage(Quester.LABEL + "Quest start/done commands are disabled.");
						return true;
					}
					try {
						if(args.length > 1){
							String questName = sconcat(args, 1);
							qm.startQuest(player, questName);
						} else {
							qm.startRandomQuest(player);
						}
					} catch (QuesterException e) {
						sender.sendMessage(e.message());
					}
					return true;
				}
				
				// QUEST CANCEL
				if(args[0].equalsIgnoreCase("cancel")) {
					if(!permCheck(sender, QuestData.USE_PERM, true)) {
						return true;
					}
					if(player == null) {
						sender.sendMessage(ChatColor.RED + "This command can only be run by player.");
					} else {
						try {
							qm.cancelQuest(player);
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
					if(!permCheck(sender, QuestData.USE_PERM, true)) {
						return true;
					}
					if(QuestData.disUseCmds) {
						sender.sendMessage(Quester.LABEL + "Quest start/done commands are disabled.");
						return true;
					}
					if(player == null) {
						sender.sendMessage(ChatColor.RED + "This command can only be run by player.");
						return true;
					}
					if(QuestData.disUseCmds) {
						sender.sendMessage(ChatColor.RED + "Quest start/done commands are disabled.");
						return true;
					}
					try {
						qm.complete(player);
					} catch (QuesterException e) {
						sender.sendMessage(e.message());
					}
					return true;
				}
				
				// QUEST PROGRESS
				if(args[0].equalsIgnoreCase("progress")) {
					if(!permCheck(sender, QuestData.USE_PERM, true)) {
						return true;
					}
					if(player == null) {
						sender.sendMessage(ChatColor.RED + "This command can only be run by player.");
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
					sender.sendMessage(ChatColor.GREEN + "Profiles saved.");
					return true;
				}
				
				// QUEST START SAVE
				if(args[0].equalsIgnoreCase("startsave")) {
					if(!permCheck(sender, QuestData.ADMIN_PERM, true)) {
						return true;
					}
					if(QuestData.saveInterval == 0) {
						sender.sendMessage(ChatColor.RED + "AutoSaving is disabled in config.");
						return true;
					}
					if(Quester.plugin.startSaving()) {
						sender.sendMessage(ChatColor.GREEN + "Saving started. Interval: " + QuestData.saveInterval + "m");
					} else {
						sender.sendMessage(ChatColor.RED + "Saving already running.");
					}
					return true;
				}
				
				// QUEST STOP SAVE
				if(args[0].equalsIgnoreCase("stopsave")) {
					if(!permCheck(sender, QuestData.ADMIN_PERM, true)) {
						return true;
					}
					if(QuestData.saveInterval == 0) {
						sender.sendMessage(ChatColor.RED + "AutoSaving is disabled in config.");
						return true;
					}
					if(Quester.plugin.stopSaving()) {
						sender.sendMessage(ChatColor.GREEN + "Saving Stopped.");
					} else {
						sender.sendMessage(ChatColor.RED + "Saving not running.");
					}
					return true;
				}
				
				// QUEST RELOAD
				if(args[0].equalsIgnoreCase("reload")) {
					if(!permCheck(sender, QuestData.ADMIN_PERM, true)) {
						return true;
					}
					Quester.plugin.initializeConfig();
					sender.sendMessage(ChatColor.GREEN + "Quest config reloaded.");
					return true;
				}
				
				sender.sendMessage(ChatColor.RED + "Unknown arguments. Type /quest help.");
			} else {
				sender.sendMessage(Quester.LABEL + "Quest what ? /quest ?");
			}
			return true;
		}
		return false;
	}

	// UTILITY
	
}
