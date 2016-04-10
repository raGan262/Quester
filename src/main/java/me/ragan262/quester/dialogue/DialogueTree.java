package me.ragan262.quester.dialogue;

import me.ragan262.quester.Quester;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.Util;
import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class DialogueTree {

	private final String name;
	private final String userPrefix;
	private final String messagePrefix;
	private final String cancelMessage;
	private final int cancelRange;
	private final int timeout;
	private final int delay;
	private DialogueNode initialNode = null;

	public DialogueTree(String name, String userPrefix, String messagePrefix, String cancelMessage,
						int cancelRange, int timeout, int delay) {
		this.name = name;
		this.userPrefix = userPrefix;
		this.messagePrefix = messagePrefix;
		this.cancelMessage = cancelMessage;
		this.cancelRange = cancelRange;
		this.timeout = timeout;
		this.delay = delay;
	}

	public String getName() {
		return name;
	}

	public boolean startDialogue(Player player) {
		if(player.isConversing()) {
			return false;
		}
		new TreeConversation(player).begin();
		return true;
	}

	public String getUserPrefix() {
		return userPrefix;
	}

	public String getMessagePrefix() {
		return messagePrefix;
	}

	protected void setInitialNode(DialogueNode node) {
		initialNode = node;
	}

	public DialogueNode getInintialNode() {
		return initialNode;
	}

	public int getDelay() {
		return delay;
	}

	private class TreeConversation extends Conversation {

		private BukkitTask scheduledReply = null;

		public TreeConversation(final Player forWhom) {
			super(Quester.getInstance(), forWhom, getInintialNode());
			modal = true;
			prefix = new NullConversationPrefix();
			cancellers.add(new ExactMatchConversationCanceller("-"));
			cancellers.add(new InactivityConversationCanceller(Quester.getInstance(), timeout));
			cancellers.add(new RangeConversationCanceller(cancelRange));

			for(ConversationCanceller cc : cancellers) {
				cc.setConversation(this);
			}

			abandonedListeners.add(new ConversationAbandonedListener() {
				public void conversationAbandoned(ConversationAbandonedEvent event) {
					if(event.getCanceller() != null) {
						event.getContext().getForWhom().sendRawMessage(messagePrefix + cancelMessage.replace("%p", forWhom.getName()));
					}
				}
			});
		}

		public Player getPlayer() {
			return (Player) getForWhom();
		}

		private DialogueNode currentNode() {
			return (DialogueNode) currentPrompt;
		}

		@Override
		public void acceptInput(String input) {
			if (currentPrompt == null) {
				return;
			}
			// cancel
			for(ConversationCanceller canceller : cancellers) {
				if (canceller.cancelBasedOnInput(context, input)) {
					abandon(new ConversationAbandonedEvent(this, canceller));
					if(scheduledReply != null) {
						scheduledReply.cancel();
						scheduledReply = null;
					}
					return;
				}
			}
			if(scheduledReply == null) {
				// Test for conversation abandonment based on input

				// Not abandoned, handle reply
				int delay = currentNode().getDelay();
				currentPrompt = currentPrompt.acceptInput(context, input);
				runNextPrompt(delay);
			}
		}

		@Override
		public synchronized void abandon(ConversationAbandonedEvent details) {
			super.abandon(details);
			if(scheduledReply != null) {
				scheduledReply.cancel();
			}
		}

		@Override
		public void outputNextPrompt() {
			if (currentPrompt == null) {
				getPlayer().sendRawMessage(ChatColor.RED + "Requested null prompt.");
				abandon(new ConversationAbandonedEvent(this));
				return;
			}
			String text = currentPrompt.getPromptText(context);
			if(text != null) {
				context.getForWhom().sendRawMessage(messagePrefix + text.replace("%p", getPlayer().getName()));
			}
			currentNode().runEvents(getPlayer());
			if (!currentPrompt.blocksForInput(context)) {
				int delay = currentNode().getDelay();
				currentPrompt = currentPrompt.acceptInput(context, null);
				runNextPrompt(delay);
			}
		}

		private void runNextPrompt(int delay) {
			if (currentPrompt == null) {
				abandon(new ConversationAbandonedEvent(this));
				return;
			}
			if(delay > 0) {
				scheduledReply = new BukkitRunnable() {
					public void run() {
						scheduledReply = null;
						outputNextPrompt();
					}
				}.runTaskLater(context.getPlugin(), delay);
			}
			else {
				outputNextPrompt();
			}
		}
	}

	public static DialogueTree deserialize(StorageKey key) {
		// base settings
		final String userPrefix = Util.fmt(key.getString("user-prefix"));
		final String messagePrefix = Util.fmt(key.getString("message-prefix"));
		final String cancelMessage = Util.fmt(key.getString("cancel-message"));
		final int cancelRange = key.getInt("range");
		final int timeout = key.getInt("timeout");
		final int delay = key.getInt("delay");
		final DialogueTree tree = new DialogueTree(key.getName(), userPrefix, messagePrefix,
												   cancelMessage, cancelRange, timeout, delay);
		// nodes
		List<DialogueNode> nodelist = new ArrayList<>();

		for(StorageKey nodeKey : key.getSubKey("nodes").getSubKeys()) {
			nodelist.add(DialogueNodeFactory.createNode(tree, nodeKey));
		}

		int cursor = 0;
		for(StorageKey nodeKey : key.getSubKey("nodes").getSubKeys()) {
			DialogueNode node = nodelist.get(cursor);
			for(StorageKey optionKey : nodeKey.getSubKey("options").getSubKeys()) {
				DialogueNode oNode = nodelist.get(Integer.parseInt(optionKey.getName()));
				node.addOption(node.deserializeOption(oNode, optionKey));
			}
			cursor++;
		}


		tree.setInitialNode(nodelist.get(0));
		return tree;
	}
}
