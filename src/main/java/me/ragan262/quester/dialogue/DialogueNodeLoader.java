package me.ragan262.quester.dialogue;

import me.ragan262.quester.storage.StorageKey;

public interface DialogueNodeLoader {

	DialogueNode loadNode(DialogueTree tree, StorageKey key);
}
