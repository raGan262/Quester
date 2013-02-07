package com.gmail.molnardad.quester.qevents;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.utils.ExpManager;
import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.classes.HeroClass.ExperienceType;

public final class ExperienceQevent extends Qevent {

	public static final String TYPE = "EXP";
	private final int amount;

	public ExperienceQevent(int occ, int del, int amt) {
		super(occ, del);
		this.amount = amt;
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
		return TYPE + ": " + amount + appendSuper();
	}

	@Override
	public void serialize(ConfigurationSection section) {
		super.serialize(section, TYPE);
		section.set("amount", amount);
	}

	public static ExperienceQevent deser(int occ, int del, ConfigurationSection section) {
		int amt;

		if(section.isInt("amount"))
			amt = section.getInt("amount");
		else
			return null;

		return new ExperienceQevent(occ, del, amt);
	}

	@Override
	void run(Player player) {
                if (Quester.heroes){
                    Hero hero = ((Heroes) Bukkit.getServer().getPluginManager().getPlugin("Heroes")).getCharacterManager().getHero(player);
                    if (hero.hasExperienceType(ExperienceType.QUESTING)){
                        hero.addExp(amount, hero.getHeroClass(), hero.getPlayer().getLocation());
                        hero.syncExperience();
                    }
                    return;
        	}
                ExpManager expMan = new ExpManager(player);
                expMan.changeExp(amount);
	}
}
