package me.ragan262.quester.triggers;

import org.bukkit.entity.Player;

import me.ragan262.quester.commandbase.QCommand;
import me.ragan262.quester.commandbase.QCommandContext;
import me.ragan262.quester.commandbase.exceptions.QCommandException;
import me.ragan262.quester.elements.QElement;
import me.ragan262.quester.elements.Trigger;
import me.ragan262.quester.elements.TriggerContext;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.Region;

@QElement("REGION")
public class RegionTrigger extends Trigger {
	
	private final Region region;
	private final boolean inverted;
	
	public RegionTrigger(final Region region, final boolean inverted) {
		this.region = region;
		this.inverted = inverted;
	}
	
	@Override
	protected String info() {
		final String flags = inverted ? " (-i)" : "";
		return region.toString() + flags;
	}
	
	@QCommand(min = 1, max = 1, usage = "{<region>} (-i)")
	public static Trigger fromCommand(final QCommandContext context) throws QCommandException {
		final Region region = Region.fromString(context.getPlayer(), context.getString(0));
		if(region == null) {
			throw new QCommandException(context.getSenderLang().get("ERROR_CMD_REGION_INVALID"));
		}
		return new RegionTrigger(region, context.hasFlag('i'));
	}
	
	@Override
	protected boolean evaluate0(final Player player, final TriggerContext context) {
		if(context.getType().equals("LOCATION")) {
			return region.isWithin(player.getLocation()) != inverted;
		}
		return false;
	}
	
	@Override
	protected void save(final StorageKey key) {
		region.serialize(key.getSubKey("region"));
		if(inverted) {
			key.setBoolean("inverted", inverted);
		}
	}
	
	protected static Trigger load(final StorageKey key) {
		final Region region = Region.deserialize(key.getSubKey("region"));
		
		if(region == null) {
			return null;
		}
		return new RegionTrigger(region, key.getBoolean("inverted", false));
	}
	
}
