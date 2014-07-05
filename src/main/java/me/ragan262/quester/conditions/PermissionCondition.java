package me.ragan262.quester.conditions;

import me.ragan262.commandmanager.annotations.Command;
import me.ragan262.commandmanager.exceptions.CommandException;
import me.ragan262.quester.commandmanager.QuesterCommandContext;
import me.ragan262.quester.elements.Condition;
import me.ragan262.quester.elements.QElement;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.Util;

import org.bukkit.entity.Player;

@QElement("PERM")
public final class PermissionCondition extends Condition {
	
	private final String perm;
	
	public PermissionCondition(final String perm) {
		this.perm = perm;
	}
	
	@Override
	public boolean isMet(final Player player) {
		return Util.permCheck(player, perm, false, null);
	}
	
	@Override
	protected String parseDescription(final Player player, final String description) {
		return description.replaceAll("%perm", perm);
	}
	
	@Override
	public String show() {
		return "Must have permission '" + perm + "'";
	}
	
	@Override
	public String info() {
		return perm;
	}
	
	@Command(min = 1, max = 1, usage = "<permission>")
	public static Condition fromCommand(final QuesterCommandContext context) throws CommandException {
		final String perm = context.getString(0);
		return new PermissionCondition(perm);
	}
	
	@Override
	protected void save(final StorageKey key) {
		key.setString("permission", perm);
	}
	
	protected static Condition load(final StorageKey key) {
		String perm;
		
		if(key.getString("permission") != null) {
			perm = key.getString("permission");
		}
		else {
			return null;
		}
		
		return new PermissionCondition(perm);
	}
}
