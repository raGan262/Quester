package com.gmail.molnardad.quester.objectives;

import static com.gmail.molnardad.quester.utils.Util.getLoc;
import static com.gmail.molnardad.quester.utils.Util.parseAction;
import static com.gmail.molnardad.quester.utils.Util.parseItem;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.utils.Util;

@QElement("ACTION")
public final class ActionObjective extends Objective {

	private final Material block;
	private final int blockData;
	private final Material inHand;
	private final int inHandData;
	private final byte click;
	private final Location location;
	private final int range;
	
	public ActionObjective(Material blck, int blckdat, Material hnd, int hnddat, int clck, Location loc, int rng) {
		block = blck;
		if(block == null) {
			blockData = -1;
		}
		else {
			blockData = blckdat;
		}
		inHand = hnd;
		if(inHand == null) {
			inHandData = -1;
		}
		else {
			inHandData = hnddat;
		}
		click = (byte)clck;
		location = loc;
		range = rng;
	}
	
	@Override
	public int getTargetAmount() {
		return 1;
	}
	
	@Override
	protected String show(int progress) {
		String clickStr = click == 1 ? "Left-click" : click == 2 ? "Right-click" : click == 3 ? "Walk on" : "Click";
		String blockStr = (block == null) ? "" : " " + block.name().toLowerCase().replace('_', ' ');
		if(blockStr.isEmpty() && click == 3) {
			blockStr = " pressure plate";
		}
		String datStr = blockData < 0 ? "" : "(data" + blockData + ")";
		String handStr = (inHand == null) ? "" : (inHand.getId() == 0) ? " with empty hand " : " with " + inHand.name().toLowerCase().replace('_', ' ') + " in hand";
		String handDatStr = inHandData < 0 ? "" : "(data" + inHandData + ")";
		String locStr = location == null ? "" : " " + range + " blocks close to " + Util.displayLocation(location);
		return clickStr + blockStr + datStr + handStr + handDatStr + locStr + ".";
	}
	
	@Override
	protected String info() {
		String datStr = blockData < 0 ? "" : ":" + blockData;
		String blockStr = block == null ? "ANY" : block.name() + "[" + block.getId() + datStr + "]";
		String handDatStr = inHandData < 0 ? "" : ":" + inHandData;
		String handStr = inHand == null ? "ANY" : inHand.name() + "[" + inHand.getId() + handDatStr + "]";
		String clickStr = click == 1 ? "LEFT" : click == 2 ? "RIGHT" : click == 3 ? "PUSH" : "ALL";
		return clickStr + "; BLOCK: " + blockStr + "; HAND: " + handStr + "; LOC: " + Util.displayLocation(location) + "; RNG: " + range;
	}
	
	@QCommand(
			min = 1,
			max = 5,
			usage = "{<click>} {[block]} {[item]} {[location]} [range]")
	public static Objective fromCommand(QCommandContext context) throws QCommandException {
		Material mat = null, hmat = null;
		int dat = -1, hdat = -1, rng = 0, click = 0;
		Location loc = null;
		int[] itm;
		click = parseAction(context.getString(0));
		if(context.length() > 1) {
			if(!context.getString(1).equalsIgnoreCase("ANY")) {
				itm = parseItem(context.getString(1));
				if(itm[0] > 255) {
					throw new QCommandException(context.getSenderLang().ERROR_CMD_BLOCK_UNKNOWN);
				}
				mat = Material.getMaterial(itm[0]);
				dat = itm[1];
			}
			if(context.length() > 2) {
				if(!context.getString(2).equalsIgnoreCase("ANY")) {
					itm = parseItem(context.getString(2));
					hmat = Material.getMaterial(itm[0]);
					hdat = itm[1];
				}
				if(context.length() > 3) {
					loc = getLoc(context.getPlayer(), context.getString(3));
					if(context.length() > 4) {
						rng = Integer.parseInt(context.getString(4));	
					}
				}
			}
		}
		return new ActionObjective(mat, dat, hmat, hdat, click, loc, rng);
	}

	@Override
	protected void save(StorageKey key) {
		if(block != null) {
			key.setString("block", Util.serializeItem(block, blockData));
		}
		if(inHand != null) {
			key.setString("hand", Util.serializeItem(inHand, inHandData));
		}
		if(click > 0 && click < 4) {
			key.setInt("click", click);
		}
		if(location != null) {
			key.setString("location", Util.serializeLocString(location));
			if(range > 0) {
				key.setInt("range", range);
			}
		}
	}
	
	protected static Objective load(StorageKey key) {
		Material mat = null, hnd = null;
		Location loc = null;
		int dat = -1, hdat = -1, rng = 0, clck = 0;
		int[] itm;
		try {
			itm = Util.parseItem(key.getString("block", ""));
			mat = Material.getMaterial(itm[0]);
			dat = itm[1];
		} catch (IllegalArgumentException ignore) {}
		try {
			itm = Util.parseItem(key.getString("hand", ""));
			hnd = Material.getMaterial(itm[0]);
			hdat = itm[1];
		} catch (IllegalArgumentException ignore) {}
		clck = key.getInt("click", 0);
		loc = Util.deserializeLocString(key.getString("location", ""));
		rng = key.getInt("range", 0);
		
		return new ActionObjective(mat, dat, hnd, hdat, clck, loc, rng);
	}
	
	// Custom methods
	
	public boolean checkBlock(Block blck) {
		if(block == null) {
			return true;
		}
		if(blck == null) {
			return false;
		}
		if(block.getId() == blck.getTypeId()) {
			if(blockData == blck.getData() || blockData < 0) {
				return true;
			}
		}
		return false;
	}
	
	public boolean checkHand(ItemStack hand) {
		if(inHand == null) {
			return true;
		}
		if(hand == null) {
			return inHand.getId() == 0;
		}
		if(inHand.getId() == hand.getTypeId()) {
			if(inHandData == hand.getDurability() || inHandData < 0) {
				return true;
			}
		}
		return false;
	}
	
	public boolean checkLocation(Location loc) {
		if(location == null) {
			return true;
		}
		if(location.getWorld().getName().equals(loc.getWorld().getName())) {
			return location.distance(loc) <= range;
		}
		else {
			return false;
		}
	}
	
	public boolean checkClick(Action act) {
		if(click == 0) {
			return true;
		}
		if((click == 1 && (act == Action.LEFT_CLICK_AIR || act == Action.LEFT_CLICK_BLOCK)) || 
				(click == 2 && (act == Action.RIGHT_CLICK_AIR || act == Action.RIGHT_CLICK_BLOCK)) || 
				(click == 3 && (act == Action.PHYSICAL))) {
			return true;
		}
		return false;
	}
}
