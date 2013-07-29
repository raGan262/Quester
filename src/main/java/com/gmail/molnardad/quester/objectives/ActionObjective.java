package com.gmail.molnardad.quester.objectives;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.utils.Util;

public final class ActionObjective extends Objective {
	
	public static final String TYPE = "ACTION";
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
		click = (byte) clck;
		location = loc;
		range = rng;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	public boolean checkBlock(final Block blck) {
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
	
	public boolean checkHand(final ItemStack hand) {
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
	
	public boolean checkLocation(final Location loc) {
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
	
	public boolean checkClick(final Action act) {
		if(click == 0) {
			return true;
		}
		if(click == 1 && (act == Action.LEFT_CLICK_AIR || act == Action.LEFT_CLICK_BLOCK) || click == 2 && (act == Action.RIGHT_CLICK_AIR || act == Action.RIGHT_CLICK_BLOCK) || click == 3 && act == Action.PHYSICAL) {
			return true;
		}
		return false;
	}
	
	@Override
	public String progress(final int progress) {
		if(!desc.isEmpty()) {
			return ChatColor.translateAlternateColorCodes('&', desc)
					.replaceAll("%r", String.valueOf(1 - progress))
					.replaceAll("%t", String.valueOf(1));
		}
		final String clickStr = click == 1 ? "Left-click" : click == 2 ? "Right-click" : click == 3 ? "Walk on" : "Click";
		String blockStr = block == null ? "" : " " + block.name().toLowerCase().replace('_', ' ');
		if(blockStr.isEmpty() && click == 3) {
			blockStr = " pressure plate";
		}
		final String datStr = blockData < 0 ? "" : "(data" + blockData + ")";
		final String handStr = inHand == null ? "" : inHand.getId() == 0 ? " with empty hand " : " with " + inHand
				.name().toLowerCase().replace('_', ' ') + " in hand";
		final String handDatStr = inHandData < 0 ? "" : "(data" + inHandData + ")";
		final String locStr = location == null ? "" : " " + range + " blocks close to " + Util
				.displayLocation(location);
		return clickStr + blockStr + datStr + handStr + handDatStr + locStr + ".";
	}
	
	@Override
	public String toString() {
		final String datStr = blockData < 0 ? "" : ":" + blockData;
		final String blockStr = block == null ? "ANY" : block.name() + "[" + block.getId() + datStr + "]";
		final String handDatStr = inHandData < 0 ? "" : ":" + inHandData;
		final String handStr = inHand == null ? "ANY" : inHand.name() + "[" + inHand.getId() + handDatStr + "]";
		final String clickStr = click == 1 ? "LEFT" : click == 2 ? "RIGHT" : click == 3 ? "PUSH" : "ALL";
		return TYPE + ": " + clickStr + "; BLOCK: " + blockStr + "; HAND: " + handStr + "; LOC: " + Util
				.displayLocation(location) + "; RNG: " + range + coloredDesc();
	}
	
	@Override
	public void serialize(final ConfigurationSection section) {
		super.serialize(section, TYPE);
		
		if(block != null) {
			section.set("block", Util.serializeItem(block, blockData));
		}
		if(inHand != null) {
			section.set("hand", Util.serializeItem(inHand, inHandData));
		}
		if(click > 0 && click < 4) {
			section.set("click", click);
		}
		if(location != null) {
			section.set("location", Util.serializeLocString(location));
			if(range > 0) {
				section.set("range", range);
			}
		}
	}
	
	public static Objective deser(final ConfigurationSection section) {
		Material mat = null, hnd = null;
		Location loc = null;
		int dat = -1, hdat = -1, rng = 0, clck = 0;
		int[] itm;
		try {
			itm = Util.parseItem(section.getString("block", ""));
			mat = Material.getMaterial(itm[0]);
			dat = itm[1];
		}
		catch (final QuesterException ignore) {}
		try {
			itm = Util.parseItem(section.getString("hand", ""));
			hnd = Material.getMaterial(itm[0]);
			hdat = itm[1];
		}
		catch (final QuesterException ignore) {}
		clck = section.getInt("click", 0);
		loc = Util.deserializeLocString(section.getString("location", ""));
		rng = section.getInt("range", 0);
		
		return new ActionObjective(mat, dat, hnd, hdat, clck, loc, rng);
	}
}
