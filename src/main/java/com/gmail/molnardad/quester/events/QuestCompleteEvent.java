package com.gmail.molnardad.quester.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import com.gmail.molnardad.quester.ActionSource;
import com.gmail.molnardad.quester.quests.Quest;

public class QuestCompleteEvent extends QuesterEvent {
	
	private static final HandlerList handlers = new HandlerList();
	
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
	private final Player player;
	private final Quest quest;
	private final ActionSource actionSource;
	
	public QuestCompleteEvent(ActionSource actionSource, Player player, Quest quest) {
		this.player = player;
		this.quest = quest;
		this.actionSource = actionSource;
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
}
