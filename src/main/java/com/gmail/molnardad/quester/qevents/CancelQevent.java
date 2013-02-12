package com.gmail.molnardad.quester.qevents;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.elements.Qevent;
import com.gmail.molnardad.quester.exceptions.QuesterException;

@QElement("CANCEL")
public final class CancelQevent extends Qevent {

	public CancelQevent() {}

	@Override
	protected String info() {
		return "";
	}

	@Override
	protected void run(Player player, Quester plugin) {
		try {
			plugin.getQuestManager().cancelQuest(player, false, plugin.getLanguageManager().getPlayerLang(player.getName()));
		} catch (QuesterException e) {
			Quester.log.info("Event failed to cancel " + player.getName() + "'s quest. Reason: " + ChatColor.stripColor(e.getMessage()));
		}
	}
	
	@QCommand(
			max = 0)
	public static Qevent fromCommand(QCommandContext context) {
		return new CancelQevent();
	}
	
	// TODO serialization
	
	public void serialize(ConfigurationSection section) {
	}
	
	public static Qevent deser(ConfigurationSection section) {	
		return new CancelQevent();
	}
}
