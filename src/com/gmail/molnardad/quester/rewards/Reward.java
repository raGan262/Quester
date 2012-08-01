package com.gmail.molnardad.quester.rewards;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

public interface Reward extends ConfigurationSerializable{

		public String getType();
		public boolean checkReward(Player player);
		public boolean giveReward(Player player);
		public String checkErrorMessage();
		public String giveErrorMessage();
		public String toString();
}