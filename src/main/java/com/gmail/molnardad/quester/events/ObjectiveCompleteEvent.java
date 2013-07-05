package com.gmail.molnardad.quester.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.gmail.molnardad.quester.ActionSource;
import com.gmail.molnardad.quester.quests.Quest;

public class ObjectiveCompleteEvent extends QuesterEvent implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
	private boolean cancelled = false;
	private final Player player;
	private final Quest quest;
	private final ActionSource actionSource;
	private final int objectiveID;
	
	public ObjectiveCompleteEvent(ActionSource actionSource, Player player, Quest quest, int objectiveID) {
		this.player = player;
		this.quest = quest;
		this.actionSource = actionSource;
		this.objectiveID = objectiveID;
	}
	
	public ActionSource getActionSource() {
		return actionSource;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Quest getQuest() {
		return quest;
	}
	
	public int getObjectiveID() {
		return objectiveID;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean value) {
		cancelled = false;
	}
	
}
