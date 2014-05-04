package me.ragan262.quester.qevents;

import me.ragan262.quester.ActionSource;
import me.ragan262.quester.Quester;
import me.ragan262.quester.commandbase.QCommand;
import me.ragan262.quester.commandbase.QCommandContext;
import me.ragan262.quester.elements.QElement;
import me.ragan262.quester.elements.Qevent;
import me.ragan262.quester.exceptions.QuesterException;
import me.ragan262.quester.profiles.ProfileManager;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.Ql;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@QElement("CANCEL")
public final class CancelQevent extends Qevent {
	
	// TODO option to choose which quest to cancel
	public CancelQevent() {}
	
	@Override
	protected String info() {
		return "";
	}
	
	@Override
	protected void run(final Player player, final Quester plugin) {
		try {
			final ProfileManager profMan = plugin.getProfileManager();
			profMan.cancelQuest(player, ActionSource.eventSource(this), plugin.getLanguageManager()
					.getLang(profMan.getProfile(player).getLanguage()));
		}
		catch (final QuesterException e) {
			Ql.info("Event failed to cancel " + player.getName() + "'s quest. Reason: "
					+ ChatColor.stripColor(e.getMessage()));
		}
	}
	
	@QCommand(max = 0)
	public static Qevent fromCommand(final QCommandContext context) {
		return new CancelQevent();
	}
	
	@Override
	protected void save(final StorageKey key) {}
	
	protected static Qevent load(final StorageKey key) {
		return new CancelQevent();
	}
}
