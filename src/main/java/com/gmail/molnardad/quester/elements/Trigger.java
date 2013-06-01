package com.gmail.molnardad.quester.elements;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.SerializationException;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.conditions.ItemCondition;
import com.gmail.molnardad.quester.storage.StorageKey;


public abstract class Trigger extends Element {
	
	private List<Condition> conditions = new ArrayList<Condition>();
	private List<Qevent> qevents = new ArrayList<Qevent>();

	protected abstract String info();
	public abstract boolean evaluate(Event event);
	
	public String inInfo() {
		return getType() + ": " + info();
	}
	
	public void addCondition(Condition con) {
		if(con != null) {
			conditions.add(con);
		}
	}
	
	public void addEvent(Qevent evt) {
		if(evt != null) {
			qevents.add(evt);
		}
	}
	
	public boolean execute(Player player, Quester plugin) {
		for(Condition c : conditions) {
			if(!c.isMet(player, plugin)) {
				// TODO run conditions' events
				return false;
			}
		}
		for(Qevent e : qevents) {
			e.execute(player, plugin);
		}
		return true;
	}
	
	public final String toString() {
		return "Trigger (type=" + getType() + ")";
	}
	
	protected abstract void save(StorageKey key);
	
//	public final void serialize(StorageKey key) throws SerializationException {
//		String type = getType();
//		if(type.isEmpty()) {
//			throw new SerializationException("Unknown type");
//		}
//		save(key);
//		key.setString("type", type);
//		if(!conditions.isEmpty()) {
//			StorageKey subKey = key.getSubKey("conditions");
//			for(int i=0; i<conditions.size(); i++) {
//				conditions.get(i).serialize(subKey.getSubKey(String.valueOf(i)));
//			}
//		}
//		if(!qevents.isEmpty()) {
//			StorageKey subKey = key.getSubKey("events");
//			for(int i=0; i<qevents.size(); i++) {
//				qevents.get(i).serialize(subKey.getSubKey(String.valueOf(i)));
//			}
//		}
//	}
//	
//	public static final Trigger deserialize(StorageKey key) {
//		
//		Qevent qvt = null;
//		if(key.getSubKey("events").hasSubKeys()) {
//			StorageKey subKey = key.getSubKey("events");
//			List<StorageKey> keys = subKey.getSubKeys();
//			for(int i=0; i<keys.size(); i++) {
//				qvt = Qevent.deserialize(subKey.getSubKey(String.valueOf(i)));
//				if(qvt != null) {
//					qevents.add(qvt);
//				} else
//					Quester.log.severe("Error occured when deserializing event ID:" + i + " in quest '" + quest.getName() + "'.");
//			}
//		}
//
//		Condition con = null;
//		if(key.getSubKey("conditions").hasSubKeys()) {
//			StorageKey subKey = key.getSubKey("conditions");
//			List<StorageKey> keys = subKey.getSubKeys();
//			for(int i=0; i<keys.size(); i++) {
//				con = Condition.deserialize(subKey.getSubKey(String.valueOf(i)));
//				if(con != null) {
//					conditions.add(con);
//				} else
//					Quester.log.severe("Error occured when deserializing condition ID " + i + " in quest '" + quest.getName() + "'.");
//			}
//		}
//		return null;
//	}
}
