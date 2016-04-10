package me.ragan262.quester.qevents;

import me.ragan262.commandmanager.annotations.Command;
import me.ragan262.commandmanager.exceptions.CommandException;
import me.ragan262.quester.Quester;
import me.ragan262.quester.commandmanager.QuesterCommandContext;
import me.ragan262.quester.elements.QElement;
import me.ragan262.quester.elements.Qevent;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.QLocation;
import me.ragan262.quester.utils.SerUtils;
import org.bukkit.entity.Player;

@QElement("BLOCK")
public final class SetBlockQevent extends Qevent {
	
	private final QLocation location;
	public final int material;
	public final byte data;
	
	public SetBlockQevent(final int mat, final int dat, final QLocation loc) {
		location = loc;
		material = mat;
		data = (byte)dat;
	}
	
	@Override
	public String info() {
		return material + ":" + data + "; " + "; LOC: " + SerUtils.displayLocation(location);
	}
	
	@Override
	protected void run(final Player player, final Quester plugin) {
		location.getLocation().getBlock().setTypeIdAndData(material, data, true);
	}
	
	@Command(min = 2, max = 2, usage = "{<block>} {<location>}")
	public static Qevent fromCommand(final QuesterCommandContext context) throws CommandException {
		final int[] itm = SerUtils.parseItem(context.getString(0));
		if(itm[0] > 255) {
			throw new CommandException(context.getSenderLang().get("ERROR_CMD_BLOCK_UNKNOWN"));
		}
		final int dat = itm[1] < 0 ? 0 : itm[1];
		final QLocation loc = SerUtils.getLoc(context.getPlayer(), context.getString(1));
		return new SetBlockQevent(itm[0], dat, loc);
	}
	
	@Override
	protected void save(final StorageKey key) {
		key.setString("block", SerUtils.serializeItem(material, data));
		key.setString("location", SerUtils.serializeLocString(location));
		
	}
	
	protected static Qevent load(final StorageKey key) {
		int mat = 0, dat = 0;
		QLocation loc = null;
		try {
			final int[] itm = SerUtils.parseItem(key.getString("block"));
			mat = itm[0];
			dat = itm[1];
			if(dat < 0) {
				dat = 0;
			}
			loc = SerUtils.deserializeLocString(key.getString("location"));
			if(loc == null) {
				return null;
			}
		}
		catch(final Exception e) {
			return null;
		}
		
		return new SetBlockQevent(mat, dat, loc);
	}
}
