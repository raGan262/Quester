package me.ragan262.quester.qevents;

import me.ragan262.commandmanager.annotations.Command;
import me.ragan262.commandmanager.exceptions.CommandException;
import me.ragan262.quester.Quester;
import me.ragan262.quester.commandmanager.QuesterCommandContext;
import me.ragan262.quester.elements.QElement;
import me.ragan262.quester.elements.Qevent;
import me.ragan262.quester.exceptions.QuestException;
import me.ragan262.quester.exceptions.QuesterException;
import me.ragan262.quester.lang.LanguageManager;
import me.ragan262.quester.quests.Quest;
import me.ragan262.quester.quests.QuestManager;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.Ql;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@QElement("TOGGLE")
public final class ToggleQevent extends Qevent {
	
	private final String quest;
	
	public ToggleQevent(final String qst) {
		quest = qst;
	}
	
	@Override
	public String info() {
		return quest;
	}
	
	@Override
	protected void run(final Player player, final Quester plugin) {
		final QuestManager qm = plugin.getQuestManager();
		String warning = null;
		Quest q = qm.getQuest(quest);
		try {
			if(q == null) {
				try {
					q = qm.getQuest(Integer.valueOf(quest));
				}
				catch(final NumberFormatException ignore) {}
				if(q == null) {
					throw new QuestException(LanguageManager.defaultLang.get("ERROR_Q_NOT_EXIST"));
				}
				else {
					warning = "Deprecated usage of Quest ID in TOGGLE event detected.";
				}
			}
			if(warning != null) {
				Ql.warning(warning);
			}
			qm.toggleQuest(q, LanguageManager.defaultLang, plugin.getProfileManager());
		}
		catch(final QuesterException e) {
			Ql.warning("Event failed to toggle quest. Reason: "
					+ ChatColor.stripColor(e.getMessage()));
		}
	}
	
	@Command(min = 1, max = 1, usage = "<quest name>")
	public static Qevent fromCommand(final QuesterCommandContext context) throws CommandException {
		final Quester plugin = (Quester)Bukkit.getPluginManager().getPlugin("Quester");
		
		if(plugin.getQuestManager().getQuest(context.getString(0)) == null) {
			
			context.getSender().sendMessage(ChatColor.YELLOW
					+ context.getSenderLang().get("ERROR_Q_NOT_EXIST"));
		}
		return new ToggleQevent(context.getString(0));
	}
	
	@Override
	protected void save(final StorageKey key) {
		key.setString("quest", quest);
	}
	
	protected static Qevent load(final StorageKey key) {
		final String qst = key.getString("quest");
		if(qst == null) {
			return null;
		}
		
		return new ToggleQevent(qst);
	}
}
