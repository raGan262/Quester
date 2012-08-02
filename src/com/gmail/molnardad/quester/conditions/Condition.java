package com.gmail.molnardad.quester.conditions;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

public interface Condition extends ConfigurationSerializable {

	public String getType();
	public boolean isMet(Player player);
	public String show();
	public String toString();
}
