package com.gmail.molnardad.quester.objectives;

import java.io.Serializable;

import org.bukkit.entity.Player;

public interface Objective extends Serializable{

		public String getType();
		public int getTargetAmount();
		public boolean isComplete(Player player, int progress);
		public boolean finish(Player player);
		public String progress(int progress);
		public String toString();
}
