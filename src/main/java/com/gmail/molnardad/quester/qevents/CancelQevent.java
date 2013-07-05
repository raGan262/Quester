package com.gmail.molnardad.quester.qevents;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.ActionSource;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.elements.Qevent;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.storage.StorageKey;

@QElement("CANCEL")
public final class CancelQevent extends Qevent {

	// TODO option to choose which quest to cancel
	public CancelQevent() {}

	@Override
	protected String info() {
		return "";
	}

	@Override
	protected void run(Player player, Quester plugin) {
		try {
			plugin.getProfileManager().cancelQuest(player, ActionSource.eventSource(this), plugin.getLanguageManager().getPlayerLang(player.getName()));
		} catch (QuesterException e) {
			Quester.log.info("Event failed to cancel " + player.getName() + "'s quest. Reason: " + ChatColor.stripColor(e.getMessage()));
		}
	}
	
	@QCommand(
			max = 0)
	public static Qevent fromCommand(QCommandContext context) {
		return new CancelQevent();
	}

	@Override
	protected void save(StorageKey key) {
	}
	
	protected static Qevent load(StorageKey key) {	
		return new CancelQevent();
	}
}
