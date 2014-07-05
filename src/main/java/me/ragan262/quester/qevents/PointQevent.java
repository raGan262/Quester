package me.ragan262.quester.qevents;

import me.ragan262.commandmanager.annotations.Command;
import me.ragan262.commandmanager.exceptions.CommandException;
import me.ragan262.quester.Quester;
import me.ragan262.quester.commandmanager.QuesterCommandContext;
import me.ragan262.quester.elements.QElement;
import me.ragan262.quester.elements.Qevent;
import me.ragan262.quester.profiles.PlayerProfile;
import me.ragan262.quester.storage.StorageKey;

import org.bukkit.entity.Player;

@QElement("POINT")
public final class PointQevent extends Qevent {
	
	private final int amount;
	
	public PointQevent(final int amt) {
		amount = amt;
	}
	
	@Override
	public String info() {
		return String.valueOf(amount);
	}
	
	@Override
	protected void run(final Player player, final Quester plugin) {
		final PlayerProfile prof = plugin.getProfileManager().getProfile(player);
		plugin.getProfileManager().addPoints(prof, amount);
	}
	
	@Command(min = 1, max = 1, usage = "<amount>")
	public static Qevent fromCommand(final QuesterCommandContext context) throws CommandException {
		final int amt = context.getInt(0);
		if(amt == 0) {
			throw new CommandException(context.getSenderLang().get("ERROR_CMD_AMOUNT_NONZERO"));
		}
		return new PointQevent(amt);
	}
	
	@Override
	protected void save(final StorageKey key) {
		key.setInt("amount", amount);
	}
	
	protected static Qevent load(final StorageKey key) {
		final int amt = key.getInt("amount", 0);
		if(amt == 0) {
			return null;
		}
		
		return new PointQevent(amt);
	}
}
