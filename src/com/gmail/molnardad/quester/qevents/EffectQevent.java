package com.gmail.molnardad.quester.qevents;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.utils.Util;

public class EffectQevent extends Qevent {

	public static final String TYPE = "EFFECT";
	private final PotionEffect effect;
	
	public EffectQevent(int occ, int del, PotionEffect eff) {
		super(occ, del);
		effect = eff;
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public int getOccasion() {
		return occasion;
	}

	@Override
	public String toString() {
		return TYPE + ": " + effect.getType().getName() + "; DUR: " + ((int)(effect.getDuration()/20)) + "s; AMP: " + effect.getAmplifier() + appendSuper();
	}

	@Override
	public void serialize(ConfigurationSection section) {
		super.serialize(section, TYPE);
		
		section.set("effect", Util.serializeEffect(effect));
	}
	
	public static EffectQevent deser(int occ, int del, ConfigurationSection section) {
		PotionEffect eff;
		
		if(section.isString("effect"))
			try {
				eff = Util.parseEffect(section.getString("effect"));
			} catch (QuesterException e) {
				Quester.log.severe("Error deserializing effect event: " + ChatColor.stripColor(e.message()));
				return null;
			}
		else
			return null;
		return new EffectQevent(occ, del, eff);
	}
	
	@Override
	public void run(Player player) {
		player.addPotionEffect(effect, true);
	}
}
