package com.gmail.molnardad.quester.objectives;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.QConfiguration;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.utils.Region;

/* DEPRECATED - use REGION objective instead */

@QElement("WORLD")
public final class WorldObjective extends Objective {

	private final String worldName;
	
	public WorldObjective(String worldName) {
		this.worldName = worldName;
	}
	
	@Override
	public int getTargetAmount() {
		return 1;
	}
	
	@Override
	protected String show(int progress) {
		return "Visit world '" + worldName + "'.";
	}
	
	@Override
	protected String info() {
		return worldName;
	}
	
	@QCommand(
			min = 1,
			max = 1,
			usage = "{<world>}")
	public static Objective fromCommand(QCommandContext context) throws QCommandException {
		World world = null;
		String label = QConfiguration.worldLabelThis;
		if(context.getString(0).equalsIgnoreCase(label)) {
			Player player = context.getPlayer();
			if(player != null) {
				world = player.getWorld();
			} else {
				throw new QCommandException(context.getSenderLang().ERROR_CMD_WORLD_THIS
						.replaceAll("%this", label));
			}
		} else {
			world = Bukkit.getServer().getWorld(context.getString(0));
		}
		if(world == null) {
			throw new QCommandException(context.getSenderLang().ERROR_CMD_WORLD_INVALID);
		}
		return new RegionObjective(new Region.World(world.getName()), context.hasFlag('i'));
	}

	@Override
	protected void save(StorageKey key) {
		key.setString("world", worldName);
	}
	
	protected static Objective load(StorageKey key) {
		String world = null;
		world = key.getString("world", null);
		if(world == null) {
			return null;
		}
		return new RegionObjective(new Region.World(world), key.getBoolean("inverted", false));
	}
	
	//Custom methods
	
	public boolean checkWorld(String wName) {
		return wName.equalsIgnoreCase(worldName);
	}
}
