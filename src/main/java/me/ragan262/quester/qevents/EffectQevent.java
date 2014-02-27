package me.ragan262.quester.qevents;

import me.ragan262.quester.Quester;
import me.ragan262.quester.commandbase.QCommand;
import me.ragan262.quester.commandbase.QCommandContext;
import me.ragan262.quester.elements.QElement;
import me.ragan262.quester.elements.Qevent;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.Ql;
import me.ragan262.quester.utils.SerUtils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

@QElement("EFFECT")
public class EffectQevent extends Qevent {
	
	private final PotionEffect effect;
	
	public EffectQevent(final PotionEffect eff) {
		effect = eff;
	}
	
	@Override
	public String info() {
		return effect.getType().getName() + "; DUR: " + effect.getDuration() / 20 + "s; AMP: "
				+ effect.getAmplifier();
	}
	
	@Override
	protected void run(final Player player, final Quester plugin) {
		player.addPotionEffect(effect, true);
	}
	
	@QCommand(min = 1, max = 1, usage = "{<potion effect>}")
	public static Qevent fromCommand(final QCommandContext context) {
		final PotionEffect eff = SerUtils.parseEffect(context.getString(0));
		return new EffectQevent(eff);
	}
	
	@Override
	protected void save(final StorageKey key) {
		key.setString("effect", SerUtils.serializeEffect(effect));
	}
	
	protected static Qevent load(final StorageKey key) {
		PotionEffect eff;
		
		if(key.getString("effect") != null) {
			try {
				eff = SerUtils.parseEffect(key.getString("effect", ""));
			}
			catch (final IllegalArgumentException e) {
				Ql.severe("Error deserializing effect event: "
						+ ChatColor.stripColor(e.getMessage()));
				return null;
			}
		}
		else {
			return null;
		}
		return new EffectQevent(eff);
	}
}
