package me.ragan262.quester.commandmanager;

import me.ragan262.commandmanager.CommandManager;
import me.ragan262.commandmanager.context.CommandContext;
import me.ragan262.commandmanager.context.ContextFactory;
import me.ragan262.quester.lang.LanguageManager;
import me.ragan262.quester.lang.QuesterLang;
import me.ragan262.quester.profiles.ProfileManager;

import org.bukkit.command.CommandSender;

public class QuesterContextFactory implements ContextFactory {
	
	private final LanguageManager langMan;
	private final ProfileManager profMan;
	
	public QuesterContextFactory(final LanguageManager langMan, final ProfileManager profMan) {
		this.langMan = langMan;
		this.profMan = profMan;
	}
	
	@Override
	public CommandContext getContext(final String[] args, final String[] parentArgs, final CommandSender sender, final CommandManager comMan) {
		final QuesterLang lang = langMan.getLang(profMan.getSenderProfile(sender).getLanguage());
		return new QuesterCommandContext(args, parentArgs, sender, comMan, lang);
	}
	
	@Override
	public Class<? extends CommandContext> getContextClass() {
		return QuesterCommandContext.class;
	}
	
}
