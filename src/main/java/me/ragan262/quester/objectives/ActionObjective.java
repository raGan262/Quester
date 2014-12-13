package me.ragan262.quester.objectives;

import me.ragan262.commandmanager.annotations.Command;
import me.ragan262.commandmanager.exceptions.CommandException;
import me.ragan262.quester.commandmanager.QuesterCommandContext;
import me.ragan262.quester.elements.Objective;
import me.ragan262.quester.elements.QElement;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.SerUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

@QElement("ACTION")
public final class ActionObjective extends Objective {
	
	private final Material block;
	private final int blockData;
	private final Material inHand;
	private final int inHandData;
	private final byte click;
	private final Location location;
	private final int range;
	
	public ActionObjective(final Material blck, final int blckdat, final Material hnd, final int hnddat, final int clck, final Location loc, final int rng) {
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
	protected String show(final int progress) {
		final String clickStr = click == 1 ? "Left-click" : click == 2 ? "Right-click" : click == 3
				? "Walk on" : "Click";
		String blockStr = block == null ? "" : " " + block.name().toLowerCase().replace('_', ' ');
		if(blockStr.isEmpty() && click == 3) {
			blockStr = " pressure plate";
		}
		final String datStr = blockData < 0 ? "" : "(data" + blockData + ")";
		final String handStr = inHand == null ? "" : inHand == Material.AIR ? " with empty hand "
				: " with " + inHand.name().toLowerCase().replace('_', ' ') + " in hand";
		final String handDatStr = inHandData < 0 ? "" : "(data" + inHandData + ")";
		final String locStr = location == null ? "" : " " + range + " blocks close to "
				+ SerUtils.displayLocation(location);
		return clickStr + blockStr + datStr + handStr + handDatStr + locStr + ".";
	}
	
	@Override
	protected String info() {
		final String datStr = blockData < 0 ? "" : ":" + blockData;
		final String blockStr = block == null ? "ANY" : block.name() + "[" + block.getId() + datStr
				+ "]";
		final String handDatStr = inHandData < 0 ? "" : ":" + inHandData;
		final String handStr = inHand == null ? "ANY" : inHand.name() + "[" + inHand.getId()
				+ handDatStr + "]";
		final String clickStr = click == 1 ? "LEFT" : click == 2 ? "RIGHT" : click == 3 ? "PUSH"
				: "ALL";
		return clickStr + "; BLOCK: " + blockStr + "; HAND: " + handStr + "; LOC: "
				+ SerUtils.displayLocation(location) + "; RNG: " + range;
	}
	
	@Command(min = 1, max = 5, usage = "{<click>} {[block]} {[item]} {[location]} [range]")
	public static Objective fromCommand(final QuesterCommandContext context) throws CommandException {
		Material mat = null, hmat = null;
		int dat = -1, hdat = -1, rng = 0, click = 0;
		Location loc = null;
		int[] itm;
		click = SerUtils.parseAction(context.getString(0));
		if(context.length() > 1) {
			if(!context.getString(1).equalsIgnoreCase("ANY")) {
				itm = SerUtils.parseItem(context.getString(1));
				if(itm[0] > 255) {
					throw new CommandException(context.getSenderLang().get("ERROR_CMD_BLOCK_UNKNOWN"));
				}
				mat = Material.getMaterial(itm[0]);
				dat = itm[1];
			}
			if(context.length() > 2) {
				if(!context.getString(2).equalsIgnoreCase("ANY")) {
					itm = SerUtils.parseItem(context.getString(2));
					hmat = Material.getMaterial(itm[0]);
					hdat = itm[1];
				}
				if(context.length() > 3) {
					loc = SerUtils.getLoc(context.getPlayer(), context.getString(3));
					if(context.length() > 4) {
						rng = Integer.parseInt(context.getString(4));
					}
				}
			}
		}
		return new ActionObjective(mat, dat, hmat, hdat, click, loc, rng);
	}
	
	@Override
	protected void save(final StorageKey key) {
		if(block != null) {
			key.setString("block", SerUtils.serializeItem(block, blockData));
		}
		if(inHand != null) {
			key.setString("hand", SerUtils.serializeItem(inHand, inHandData));
		}
		if(click > 0 && click < 4) {
			key.setInt("click", click);
		}
		if(location != null) {
			key.setString("location", SerUtils.serializeLocString(location));
			if(range > 0) {
				key.setInt("range", range);
			}
		}
	}
	
	protected static Objective load(final StorageKey key) {
		Material mat = null, hnd = null;
		Location loc = null;
		int dat = -1, hdat = -1, rng = 0, clck = 0;
		int[] itm;
		try {
			itm = SerUtils.parseItem(key.getString("block", ""));
			mat = Material.getMaterial(itm[0]);
			dat = itm[1];
		}
		catch(final IllegalArgumentException ignore) {}
		try {
			itm = SerUtils.parseItem(key.getString("hand", ""));
			hnd = Material.getMaterial(itm[0]);
			hdat = itm[1];
		}
		catch(final IllegalArgumentException ignore) {}
		clck = key.getInt("click", 0);
		loc = SerUtils.deserializeLocString(key.getString("location", ""));
		rng = key.getInt("range", 0);
		
		return new ActionObjective(mat, dat, hnd, hdat, clck, loc, rng);
	}
	
	// Custom methods
	
	public boolean checkBlock(final Block blck) {
		if(block == null) {
			return true;
		}
		if(blck == null) {
			return false;
		}
		if(block == blck.getType()) {
			if(blockData == blck.getData() || blockData < 0) {
				return true;
			}
		}
		return false;
	}
	
	public boolean checkHand(final ItemStack hand) {
		if(inHand == null) {
			return true;
		}
		if(hand == null) {
			return inHand == Material.AIR;
		}
		if(inHand == hand.getType()) {
			if(inHandData == hand.getDurability() || inHandData < 0) {
				return true;
			}
		}
		return false;
	}
	
	public boolean checkLocation(final Location loc) {
		return location == null
				|| location.getWorld().getName().equals(loc.getWorld().getName()) && location.distance(loc) <= range;
	}
	
	public boolean checkClick(final Action act) {
		return click == 0 || click == 1 && (act == Action.LEFT_CLICK_AIR
				|| act == Action.LEFT_CLICK_BLOCK) || click == 2 && (act == Action.RIGHT_CLICK_AIR
				|| act == Action.RIGHT_CLICK_BLOCK) || click == 3 && act == Action.PHYSICAL;
	}
}
