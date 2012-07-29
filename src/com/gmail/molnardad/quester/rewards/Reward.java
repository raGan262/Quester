package com.gmail.molnardad.quester.rewards;

import java.io.Serializable;

import org.bukkit.entity.Player;

public interface Reward extends Serializable{

		public String getType();
		public boolean checkReward(Player player);
		public boolean giveReward(Player player);
		public String checkErrorMessage();
		public String giveErrorMessage();
		public String toString();
}