package me.ragan262.quester.dialogue;

import me.ragan262.quester.elements.Condition;
import me.ragan262.quester.elements.Qevent;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.Ql;
import me.ragan262.quester.utils.Util;
import org.apache.commons.lang.Validate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DialogueNodeFactory {

	private static final Map<String, DialogueNodeLoader> loaders = new HashMap<>();

	public static void registerNodeLoader(String type, DialogueNodeLoader loader) {
		Validate.notNull(type);
		Validate.notNull(loader);
		loaders.put(type.toUpperCase(), loader);
	}

	public static DialogueNode createNode(DialogueTree parent, StorageKey key) {
		String type = key.getString("type", "MISSING");
		DialogueNodeLoader loader = loaders.get(type.toUpperCase());
		if(loader == null) {
			Ql.severe("Failed to load dialogue node. Reason: Unknown node type " + type);
			throw new IllegalArgumentException("Unknown node type.");
		}
		final DialogueNode node = loader.loadNode(parent, key);

		node.setPromptText(Util.fmt(key.getString("text")));

		if(key.keyExists("delay")) {
			node.setDelay(key.getInt("delay"));
		}

		Condition con;
		if(key.getSubKey("conditions").hasSubKeys()) {
			final StorageKey subKey = key.getSubKey("conditions");
			final List<StorageKey> keys = subKey.getSubKeys();
			for(int i = 0; i < keys.size(); i++) {
				con = Condition.deserialize(subKey.getSubKey(String.valueOf(i)));
				if(con != null) {
					node.addCondition(con);
				}
				else {
					Ql.severe("Error occured when deserializing condition ID " + i
							+ " in dialogue node '" + key.getName() + "' in dialogue tree '"
							+ parent.getName() + "'.");
				}
			}
		}

		Qevent qvt;
		if(key.getSubKey("events").hasSubKeys()) {
			final StorageKey subKey = key.getSubKey("events");
			final List<StorageKey> keys = subKey.getSubKeys();
			for(int i = 0; i < keys.size(); i++) {
				qvt = Qevent.deserialize(subKey.getSubKey(String.valueOf(i)));
				if(qvt != null) {
					node.addEvent(qvt);
				}
				else {
					Ql.severe("Error occured when deserializing event ID " + i
							+ " in dialogue node '" + key.getName() + "' in dialogue tree '"
							+ parent.getName() + "'.");
				}
			}
		}

		return node;
	}
}
