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
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

@QElement("SOUND")
public final class SoundQevent extends Qevent {
	
	private final QLocation location;
	private final Sound sound;
	private final float volume;
	private final float pitch;
	
	public SoundQevent(final QLocation loc, final Sound snd, final float vol, final float pit) {
		location = loc;
		sound = snd;
		volume = vol;
		pitch = pit;
	}
	
	@Override
	public String info() {
		String locStr = "PLAYER";
		if(location != null) {
			locStr = SerUtils.displayLocation(location);
		}
		return sound.name() + "; LOC: " + locStr + "; VOL: " + volume + "; PIT: " + pitch;
	}
	
	@Override
	protected void run(final Player player, final Quester plugin) {
		Location temp;
		if(location == null) {
			temp = player.getLocation();
		}
		else {
			temp = location.getLocation();
		}
		temp.getWorld().playSound(temp, sound, volume, pitch);
	}
	
	@Command(min = 1, max = 4, usage = "{<sound>} {[location]} [volume] [pitch]")
	public static Qevent fromCommand(final QuesterCommandContext context) throws CommandException {
		final Sound snd = SerUtils.parseSound(context.getString(0));
		if(snd == null) {
			throw new CommandException(context.getSenderLang().get("ERROR_CMD_SOUND_UNKNOWN"));
		}
		float vol = 1F;
		float pit = 1F;
		QLocation loc = null;
		if(context.length() > 1) {
			loc = SerUtils.getLoc(context.getPlayer(), context.getString(1));
		}
		if(context.length() > 2) {
			vol = (float)context.getDouble(2);
			if(context.length() > 3) {
				pit = (float)context.getDouble(3);
			}
			if(vol < 0) {
				throw new CommandException(context.getSenderLang().get("ERROR_CMD_VOL_PIT"));
			}
		}
		return new SoundQevent(loc, snd, vol, pit);
	}
	
	@Override
	protected void save(final StorageKey key) {
		key.setString("sound", sound.name());
		if(location != null) {
			key.setString("location", SerUtils.serializeLocString(location));
		}
		if(volume != 1F) {
			key.setDouble("volume", volume);
		}
		if(pitch != 1F) {
			key.setDouble("pitch", pitch);
		}
	}
	
	protected static Qevent load(final StorageKey key) {
		final Sound snd = SerUtils.parseSound(key.getString("sound", ""));
		if(snd == null) {
			return null;
		}
		final QLocation loc = SerUtils.deserializeLocString(key.getString("location", ""));
		float vol = (float)key.getDouble("volume", 1F);
		float pit = (float)key.getDouble("pitch", 1F);
		if(vol < 0F) {
			vol = 1F;
		}
		if(pit < 0F) {
			pit = 1F;
		}
		
		return new SoundQevent(loc, snd, vol, pit);
	}
}
