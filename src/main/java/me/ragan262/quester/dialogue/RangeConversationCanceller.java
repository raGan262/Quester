package me.ragan262.quester.dialogue;

import org.bukkit.Location;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationCanceller;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class RangeConversationCanceller implements ConversationCanceller {

	private final int range;

	private Location loc = null;

	public RangeConversationCanceller(int range) {
		this.range = range;
	}

	public void setConversation(final Conversation conversation) {
		if(conversation.getForWhom() instanceof Player) {
			loc = ((Player) conversation.getForWhom()).getLocation();
			new BukkitRunnable() {
				public void run() {
					if(conversation.getState() == Conversation.ConversationState.UNSTARTED) {
						return;
					}
					if(loc == null || conversation.getState() == Conversation.ConversationState.ABANDONED) {
						cancel();
						return;
					}
					Player player = (Player) conversation.getForWhom();
					if(loc.distanceSquared(player.getLocation()) > 100) {
						conversation.abandon(new ConversationAbandonedEvent(conversation,
																			RangeConversationCanceller.this));
						cancel();
					}
				}
			}.runTaskTimer(conversation.getContext().getPlugin(), 20L, 20L);
		}
	}

	public boolean cancelBasedOnInput(ConversationContext conversationContext, String s) {
		return false;
	}

	public ConversationCanceller clone() {
		return new RangeConversationCanceller(range);
	}
}
