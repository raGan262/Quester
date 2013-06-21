package com.gmail.molnardad.quester.conditions;

import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.Condition;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.utils.Util;

@QElement("PERM")
public final class PermissionCondition extends Condition {
	
	private final String perm;
	
	public PermissionCondition(String perm) {
		this.perm = perm;
	}

	@Override
	public boolean isMet(Player player, Quester plugin) {
		return Util.permCheck(player, perm, false, null);
	}
	
	@Override
	protected String parseDescription(String description) {
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
	
	@QCommand(
			min = 1,
			max = 1,
			usage = "<permission>")
	public static Condition fromCommand(QCommandContext context) throws QCommandException {
		String perm = context.getString(0);
		return new PermissionCondition(perm);
	}

	@Override
	protected void save(StorageKey key) {
		key.setString("permission", perm);
	}

	protected static Condition load(StorageKey key) {
		String perm;
		
		if(key.getString("permission") != null)
			perm = key.getString("permission");
		else
			return null;
		
		return new PermissionCondition(perm);
	}
}
