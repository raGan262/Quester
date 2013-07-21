package com.gmail.molnardad.quester.holder;

import net.citizensnpcs.api.exception.NPCLoadException;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;

public class QuesterTrait extends Trait {
	
	private int holder = -1;
	
	public QuesterTrait() {
		super("quester");
	}
	
	@Override
	public void load(DataKey key) throws NPCLoadException {
		holder = key.getInt("quester.holder", -1);
	}

	@Override
	public void save(DataKey key) {
		key.setInt("quester.holder", holder);
	}
	
	public int getHolderID() {
		return holder;
	}
	
	public void setHolderID(int newID) {
		holder = newID;
	}
}
