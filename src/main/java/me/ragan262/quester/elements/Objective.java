package me.ragan262.quester.elements;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import me.ragan262.quester.lang.LanguageManager;
import me.ragan262.quester.lang.QuesterLang;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.Ql;
import me.ragan262.quester.utils.SerUtils;

import org.apache.commons.lang.SerializationException;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public abstract class Objective extends Element {
	
	private String desc = "";
	private boolean isCustomMessage = false;
	private boolean hidden = false;
	private boolean displayProgress = true;
	private final Set<Integer> prerequisites = new HashSet<Integer>();
	
	public Set<Integer> getPrerequisites() {
		return prerequisites;
	}
	
	public void addPrerequisity(final int newPre) {
		prerequisites.add(newPre);
	}
	
	public void removePrerequisity(final int pre) {
		prerequisites.remove(pre);
	}
	
	private String coloredDesc(final LanguageManager langMan) {
		String result = "";
		if(!prerequisites.isEmpty()) {
			result += " PRE: " + SerUtils.serializePrerequisites(prerequisites, ",");
		}
		if(!desc.isEmpty()) {
			result += "\n  - ";
			if(isCustomMessage) {
				if(langMan != null) {
					if(langMan.customMessageExists(desc)) {
						result += ChatColor.GREEN;
					}
					else {
						result += ChatColor.RED;
					}
				}
				result += "LANG:";
			}
			result += ChatColor.translateAlternateColorCodes('&', desc) + ChatColor.RESET;
		}
		return result;
	}
	
	public void addDescription(final String msg) {
		final boolean doCheck = desc.isEmpty();
		desc += (" " + msg).trim();
		if(doCheck) {
			customMessageCheck();
		}
	}
	
	public void removeDescription() {
		desc = "";
		isCustomMessage = false;
	}
	
	private void customMessageCheck() {
		final String langMsg = LanguageManager.getCustomMessageKey(desc);
		if(langMsg != null) {
			desc = langMsg;
			isCustomMessage = true;
		}
		else {
			isCustomMessage = false;
		}
	}
	
	public void setHidden(final boolean value) {
		hidden = value;
	}
	
	public boolean isHidden() {
		return hidden;
	}
	
	public void setDisplayProgress(final boolean value) {
		displayProgress = value;
	}
	
	public boolean shouldDisplayProgress() {
		return displayProgress;
	}
	
	public final boolean isComplete(final int progress) {
		return progress >= getTargetAmount();
	}
	
	public abstract int getTargetAmount();
	
	protected String parseDescription(final String description) {
		return description;
	}
	
	protected abstract String show(int progress);
	
	protected abstract String info();
	
	public boolean tryToComplete(final Player player) {
		return false;
	}
	
	public String inShow(final QuesterLang lang) {
		return inShow(0, lang);
	}
	
	public String inShow(final int progress, final QuesterLang lang) {
		if(!desc.isEmpty()) {
			final String actualDesc;
			if(isCustomMessage) {
				actualDesc = lang.getCustom(desc);
			}
			else {
				actualDesc = desc;
			}
			final String partiallyParsed =
					actualDesc.replaceAll("%r", String.valueOf(getTargetAmount() - progress))
							.replaceAll("%t", String.valueOf(getTargetAmount()))
							.replaceAll("%a", String.valueOf(progress));
			return ChatColor.translateAlternateColorCodes('&', parseDescription(partiallyParsed));
		}
		return show(progress);
	}
	
	public String inInfo(final LanguageManager langMan) {
		final String flags = displayProgress ? "" : "(p)";
		return flags + getType() + ": " + info() + coloredDesc(langMan);
	}
	
	@Override
	public final String toString() {
		return "Objective (type=" + getType() + ")";
	}
	
	protected abstract void save(StorageKey key);
	
	public final void serialize(final StorageKey key) {
		final String type = getType();
		if(type.isEmpty()) {
			throw new SerializationException("Unknown type");
		}
		save(key);
		key.setString("type", type);
		if(!desc.isEmpty()) {
			if(isCustomMessage) {
				key.setString("description", "LANG:" + desc);
			}
			else {
				key.setString("description", desc);
			}
		}
		if(!prerequisites.isEmpty()) {
			key.setString("prerequisites", SerUtils.serializePrerequisites(prerequisites));
		}
		if(hidden) {
			key.setBoolean("hidden", hidden);
		}
		if(!displayProgress) {
			key.setBoolean("progress", hidden);
		}
	}
	
	public static final Objective deserialize(final StorageKey key) {
		if(!key.hasSubKeys()) {
			Ql.severe("Objective deserialization error: no subkeys");
			return null;
		}
		
		final String type = key.getString("type");
		if(type == null) {
			Ql.severe("Objective type missing.");
			return null;
		}
		
		Objective obj = null;
		
		final Class<? extends Objective> c = ElementManager.getInstance().getObjectiveClass(type);
		if(c != null) {
			try {
				final Method load = c.getDeclaredMethod("load", StorageKey.class);
				load.setAccessible(true);
				obj = (Objective) load.invoke(null, key);
				if(obj == null) {
					return null;
				}
				
				final String description = key.getString("description");
				if(description != null) {
					obj.addDescription(description);
				}
				obj.setHidden(key.getBoolean("hidden", false));
				obj.setDisplayProgress(key.getBoolean("progress", true));
				
				try {
					final Set<Integer> prereq =
							SerUtils.deserializePrerequisites(key.getString("prerequisites"));
					for(final int i : prereq) {
						obj.addPrerequisity(i);
					}
				}
				catch (final NullPointerException ignore) {}
				catch (final Exception ex) {
					Ql.debug("Failed to load prerequisites. (" + type + ")");
				}
			}
			catch (final Exception e) {
				Ql.severe("Error when deserializing " + c.getSimpleName()
						+ ". Method load() missing or invalid. " + e.getClass().getName());
				Ql.debug("Exception follows", e);
				return null;
			}
		}
		else {
			Ql.severe("Unknown objective type: '" + type + "'");
		}
		
		return obj;
	}
}
