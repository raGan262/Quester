package com.gmail.molnardad.quester.rewards;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

@SerializableAs("QuesterCommandReward")
public final class CommandReward implements Reward {

	public CommandReward() {
	}
	
	@Override
	public String getType() {
		return "";
	}

	@Override
	public boolean giveReward(Player player) {
		return true;
	}

	@Override
	public boolean checkReward(Player player) {
		return true;
	}
	
	@Override
	public String toString() {
		return "REMOVE THIS REWARD";
	}
	
	public String checkErrorMessage(){
		return "";
	}
	
	public String giveErrorMessage() {
		return "";
	}

	@Override
	public Map<String, Object> serialize() {
		return new HashMap<String, Object>();
	}

	public static CommandReward deserialize(Map<String, Object> map) {
		return null;
	}

}
