package com.gmail.molnardad.quester.rewards;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;


@SerializableAs("QuesterTeleportReward")
public final class TeleportReward implements Reward {

	public TeleportReward() {
	}
	
	@Override
	public String getType() {
		return "";
	}

	@Override
	public boolean checkReward(Player player) {
		return false;
	}

	@Override
	public boolean giveReward(Player player) {
		return true;
	}

	@Override
	public String checkErrorMessage() {
		return "";
	}

	@Override
	public String giveErrorMessage() {
		return "";
	}
	
	@Override
	public String toString() {
		return "REMOVE THIS REWARD";
	}

	@Override
	public Map<String, Object> serialize() {
		return new HashMap<String, Object>();
	}

	public static TeleportReward deserialize(Map<String, Object> map) {
		return null;
	}
}
