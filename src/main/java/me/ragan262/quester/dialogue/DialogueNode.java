package me.ragan262.quester.dialogue;

import me.ragan262.quester.elements.Condition;
import me.ragan262.quester.elements.Qevent;
import me.ragan262.quester.storage.StorageKey;
import org.apache.commons.lang.Validate;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class DialogueNode implements Prompt {

	protected final DialogueTree tree;
	protected String promptText = null;

	protected Integer delay = null;

	protected List<Condition> conditions = new ArrayList<>();
	protected List<Qevent> events = new ArrayList<>();
	protected List<DialogueOption> options = new ArrayList<>();

	protected DialogueNode(DialogueTree tree) {
		Validate.notNull(tree);
		this.tree = tree;
	}

	public void addCondition(Condition c) {
		if(c != null && !conditions.contains(c)) {
			conditions.add(c);
		}
	}

	public void addEvent(Qevent e) {
		if(e != null) {
			events.add(e);
		}
	}

	protected void addOption(DialogueOption option) {
		if(option.getNode() != null) {
			options.add(option);
		}
	}

	protected DialogueOption deserializeOption(DialogueNode node, StorageKey key) {
		return new DialogueOption(node);
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public int getDelay() {
		return delay == null ? tree.getDelay() : delay;
	}

	public boolean areConditionsMet(Player player) {
		for(Condition c : conditions) {
			if(!c.isMet(player)) {
				return false;
			}
		}
		return true;
	}

	public void runEvents(Player player) {
		for(Qevent e : events) {
			e.execute(player);
		}
	}

	public void setPromptText(String promptText) {
		this.promptText = promptText;
	}

	@Override
	public String getPromptText(ConversationContext context) {
		return promptText;
	}
}
