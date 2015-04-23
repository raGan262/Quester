package me.ragan262.quester.dialogue;

import me.ragan262.quester.storage.StorageKey;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

public class BranchNode extends DialogueNode {

	protected BranchNode(DialogueTree tree) {
		super(tree);
	}

	@Override
	public boolean blocksForInput(ConversationContext context) {
		return false;
	}

	@Override
	public Prompt acceptInput(ConversationContext context, String s) {
		for(DialogueOption o : options) {
			if(o.isAvailable((Player)context.getForWhom())) {
				return o.getNode();
			}
		}
		return END_OF_CONVERSATION;
	}

	public static class Loader implements DialogueNodeLoader {

		@Override
		public DialogueNode loadNode(DialogueTree tree, StorageKey key) {
			return new BranchNode(tree);
		}
	}
}
