package com.gmail.molnardad.quester;

import net.citizensnpcs.api.exception.NPCLoadException;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;

public class QuesterTrait extends Trait {
	
	private QuestHolder holder = new QuestHolder();
	
	public QuesterTrait() {
		super("quester");
	}
	
	@Override
	public void load(DataKey key) throws NPCLoadException {
		String value = key.getString("quests", "");
		QuestHolder h = QuestHolder.deserialize(value);
		if(h != null) {
			holder = h;
		}
	}

	@Override
	public void save(DataKey key) {
		key.setString("quests", holder.serialize());
	}
	
	public QuestHolder getHolder() {
		return holder;
	}
}
