package com.gmail.molnardad.quester.objectives;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.qevents.Qevent;

public abstract class Objective implements ConfigurationSerializable{

		List<Qevent> qevents = new ArrayList<Qevent>();
		
		public abstract String getType();
		
		public void addQevent(Qevent newQevent) {
			qevents.add(newQevent);
		}
		
		public void removeQevent(int id) {
			if(id >= 0 && id < qevents.size())
				qevents.remove(id);
		}
		
		public List<Qevent> getQevents() {
			return qevents;
		}
		
		public void runQevents(Player player) {
			for(Qevent qv : qevents) {
				qv.execute(player);
			}
		}
		
		public String stringQevents() {
			String result = "";
			for(int i=0; i<qevents.size(); i++) {
				result += "\n " + ChatColor.RESET + " <" + i + "> " + qevents.get(i).toString();
			}
			return result;
		}
		
		public int getTargetAmount() {
			return 1;
		}
		
		public boolean isComplete(Player player, int progress) {
			return progress > 0;
		}
		
		public boolean tryToComplete(Player player) {
			return false;
		}
		
		public boolean finish(Player player) {
			return true;
		}
		
		@SuppressWarnings("unchecked")
		protected void loadQevents(Map<String, Object> map) {
			List<Qevent> qvts = new ArrayList<Qevent>();
			try{
				if(map.get("events") != null)
					qvts = (List<Qevent>) map.get("events");
			} catch (Exception e) {}
			qevents = qvts;
		}
		
		@Override
		public Map<String, Object> serialize() {
			Map<String, Object> map = new HashMap<String, Object>();
				
			map.put("events", qevents);
			
			return map;
		}
		
		public abstract String progress(int progress);
		public abstract String toString();
}
