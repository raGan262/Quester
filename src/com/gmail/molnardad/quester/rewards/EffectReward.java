package com.gmail.molnardad.quester.rewards;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class EffectReward implements Reward {

	private static final long serialVersionUID = 13600L;
	private final String TYPE = "EFFECT";
	private final int effect;
	private final int duration;
	private final int amplifier;
	
	public EffectReward(int eff, int dur, int amp) {
		effect = eff;
		duration = dur;
		amplifier = amp;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public boolean checkReward(Player player) {
		return true;
	}

	@Override
	public boolean giveReward(Player player) {
		return player.addPotionEffect(new PotionEffect(PotionEffectType.getById(effect), duration*20, amplifier), true);
	}

	@Override
	public String checkErrorMessage() {
		return "EffectReward checkErrorMessage()";
	}

	@Override
	public String giveErrorMessage() {
		return "Failed to apply potion effect.";
	}
	
	@Override
	public String toString() {
		String power = " POWER: "+String.valueOf(amplifier);
		String dur = " "+String.valueOf(duration)+"s,";
		return TYPE+": "+PotionEffectType.getById(effect).getName()+dur+power;
	}

}
