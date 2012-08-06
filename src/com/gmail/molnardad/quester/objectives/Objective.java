package com.gmail.molnardad.quester.objectives;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

public interface Objective extends ConfigurationSerializable{

		public String getType();
		public int getTargetAmount();
		public boolean isComplete(Player player, int progress);
		public boolean tryToComplete(Player player);
		public boolean finish(Player player);
		public String progress(int progress);
		public String toString();
}
