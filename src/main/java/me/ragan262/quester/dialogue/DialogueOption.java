package me.ragan262.quester.dialogue;

import org.bukkit.entity.Player;

public class DialogueOption {

	protected final DialogueNode node;

	public DialogueOption(DialogueNode node) {
		this.node = node;
	}

	public DialogueNode getNode() {
		return node;
	}

	public boolean isAvailable(Player player) {
		return node.areConditionsMet(player);
	}


}
