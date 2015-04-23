package me.ragan262.quester.dialogue;

import me.ragan262.quester.Quester;
import me.ragan262.quester.storage.ConfigStorage;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.Ql;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DialogueManager {

	private final ConfigStorage dialogueStorage;

	private final Map<String, DialogueTree> dialogueTrees = new HashMap<>();

	public DialogueManager(Quester quester) {
		File dialogueFile = new File(quester.getDataFolder(), "dialogues.yml");
		dialogueStorage = new ConfigStorage(dialogueFile, quester.getLogger(), null);
	}

	public DialogueTree getDialogue(String name) {
		if(name == null) {
			return null;
		}
		return dialogueTrees.get(name.toLowerCase());
	}

	public void loadDialogues() {
		dialogueStorage.load();
		dialogueTrees.clear();
		for(StorageKey key : dialogueStorage.getKey("").getSubKeys()) {
			try {
				DialogueTree t = DialogueTree.deserialize(key);
				dialogueTrees.put(t.getName().toLowerCase(), t);
			}
			catch(Exception e) {
				Ql.severe("Error occured when deserializing dialogue tree " + key.getName() + "'.");
			}
		}
	}
}
