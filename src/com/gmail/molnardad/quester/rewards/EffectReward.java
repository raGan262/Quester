package com.gmail.molnardad.quester.rewards;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@SerializableAs("QuesterEffectReward")
public final class EffectReward implements Reward {

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

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("effect", effect);
		map.put("duration", duration);
		map.put("amplifier", amplifier);
		
		return map;
	}

	public static EffectReward deserialize(Map<String, Object> map) {
		int eff, dur, amp;
		
		try {
			eff = (Integer) map.get("effect");
			if(PotionEffectType.getById(eff) == null)
				return null;
			dur = (Integer) map.get("duration");
			if(dur < 0)
				return null;
			amp = (Integer) map.get("amplifier");
			if(amp < 0)
				return null;
			return new EffectReward(eff, dur, amp);
		} catch (Exception e) {
			return null;
		}
	}
}
