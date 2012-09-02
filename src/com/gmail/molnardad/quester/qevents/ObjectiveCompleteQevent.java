package com.gmail.molnardad.quester.qevents;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.exceptions.ExceptionType;
import com.gmail.molnardad.quester.exceptions.QuesterException;

@SerializableAs("QuesterObjCompleteQevent")
public final class ObjectiveCompleteQevent extends Qevent {

	private final String TYPE = "OBJCOM";
	private final int objective;
	
	public ObjectiveCompleteQevent(int occ, int del, int obj) {
		super(occ, del);
		this.objective = obj;
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
		return TYPE + ": ON-" + parseOccasion(occasion) + "; OBJ: " + objective;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("objective", objective);
		map.put("occasion", occasion);
		map.put("delay", delay);
		
		return map;
	}
	
	public static ObjectiveCompleteQevent deserialize(Map<String, Object> map) {
		int occ, del, obj;
		
		obj = (Integer) map.get("objective");
		occ = (Integer) map.get("occasion");
		del = (Integer) map.get("delay");
		
		return new ObjectiveCompleteQevent(occ, del, obj);
	}

	@Override
	public void run(Player player) {
		try {
			List<Integer> prog = Quester.qMan.getProfile(player.getName()).getProgress();
			if(objective >= 0 && objective < prog.size()) {
				int req = Quester.qMan.getPlayerQuest(player.getName()).getObjective(objective).getTargetAmount();
				prog.set(objective, req);
			} else {
				throw new QuesterException(ExceptionType.OBJ_NOT_EXIST);
			}
		} catch (QuesterException e) {
			Quester.log.info("Event failed to quest quest. Reason: " + ChatColor.stripColor(e.message()));
		}
	}
}
