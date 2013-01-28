package com.gmail.molnardad.quester.qevents;

import static com.gmail.molnardad.quester.utils.Util.parseEffect;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.elements.Qevent;
import com.gmail.molnardad.quester.utils.Util;

@QElement("EFFECT")
public class EffectQevent extends Qevent {

	private final PotionEffect effect;
	
	public EffectQevent(PotionEffect eff) {
		effect = eff;
	}

	@Override
	public String info() {
		return effect.getType().getName() + "; DUR: " + ((int)(effect.getDuration()/20)) + "s; AMP: " + effect.getAmplifier();
	}
	
	@Override
	protected void run(Player player) {
		player.addPotionEffect(effect, true);
	}

	@QCommand(
			min = 1,
			max = 1,
			usage = "{<potion effect>}")
	public static Qevent fromCommand(QCommandContext context) {
		PotionEffect eff = parseEffect(context.getString(0));
		return new EffectQevent(eff);
	}

	// TODO serialization
	
	public void serialize(ConfigurationSection section) {
		section.set("effect", Util.serializeEffect(effect));
	}
	
	public static EffectQevent deser(ConfigurationSection section) {
		PotionEffect eff;
		
		if(section.isString("effect"))
			try {
				eff = Util.parseEffect(section.getString("effect"));
			} catch (IllegalArgumentException e) {
				Quester.log.severe("Error deserializing effect event: " + ChatColor.stripColor(e.getMessage()));
				return null;
			}
		else
			return null;
		return new EffectQevent(eff);
	}
}
