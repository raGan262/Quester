package me.ragan262.quester.objectives;

import me.ragan262.quester.QConfiguration;
import me.ragan262.quester.commandbase.QCommand;
import me.ragan262.quester.commandbase.QCommandContext;
import me.ragan262.quester.commandbase.exceptions.QCommandException;
import me.ragan262.quester.elements.Objective;
import me.ragan262.quester.elements.QElement;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.Region;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

/* DEPRECATED - use REGION objective instead */

@QElement("WORLD")
public final class WorldObjective extends Objective {
	
	private final String worldName;
	
	public WorldObjective(final String worldName) {
		this.worldName = worldName;
	}
	
	@Override
	public int getTargetAmount() {
		return 1;
	}
	
	@Override
	protected String show(final int progress) {
		return "Visit world '" + worldName + "'.";
	}
	
	@Override
	protected String info() {
		return worldName;
	}
	
	@QCommand(min = 1, max = 1, usage = "{<world>}")
	public static Objective fromCommand(final QCommandContext context) throws QCommandException {
		World world = null;
		final String label = QConfiguration.worldLabelThis;
		if(context.getString(0).equalsIgnoreCase(label)) {
			final Player player = context.getPlayer();
			if(player != null) {
				world = player.getWorld();
			}
			else {
				throw new QCommandException(context.getSenderLang().get("ERROR_CMD_WORLD_THIS")
						.replaceAll("%this", label));
			}
		}
		else {
			world = Bukkit.getServer().getWorld(context.getString(0));
		}
		if(world == null) {
			throw new QCommandException(context.getSenderLang().get("ERROR_CMD_WORLD_INVALID"));
		}
		return new RegionObjective(new Region.World(world.getName()), context.hasFlag('i'));
	}
	
	@Override
	protected void save(final StorageKey key) {
		key.setString("world", worldName);
	}
	
	protected static Objective load(final StorageKey key) {
		String world = null;
		world = key.getString("world", null);
		if(world == null) {
			return null;
		}
		return new RegionObjective(new Region.World(world), key.getBoolean("inverted", false));
	}
	
	// Custom methods
	
	public boolean checkWorld(final String wName) {
		return wName.equalsIgnoreCase(worldName);
	}
}
