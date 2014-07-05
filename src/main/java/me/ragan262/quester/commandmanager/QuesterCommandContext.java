package me.ragan262.quester.commandmanager;

import me.ragan262.commandmanager.CommandManager;
import me.ragan262.commandmanager.context.CommandContext;
import me.ragan262.quester.lang.QuesterLang;

import org.bukkit.command.CommandSender;

public class QuesterCommandContext extends CommandContext {
	
	private final QuesterLang lang;
	
	private QuesterCommandContext(final CommandContext context, final QuesterLang senderLang) {
		super(context);
		lang = senderLang;
	}
	
	protected QuesterCommandContext(final QuesterCommandContext context) {
		this(context, context.getSenderLang());
	}
	
	protected QuesterCommandContext(final String[] args, final String[] parentArgs, final CommandSender sender, final CommandManager cMan, final QuesterLang lang) {
		super(args, parentArgs, sender, cMan);
		this.lang = lang;
	}
	
	@Override
	public QuesterCommandContext getSubContext(final int level) {
		final CommandContext context = super.getSubContext(level);
		if(context == null) {
			return null;
		}
		return new QuesterCommandContext(context, lang);
	}
	
	public QuesterLang getSenderLang() {
		return lang;
	}
	
	static {
		
	}
}
