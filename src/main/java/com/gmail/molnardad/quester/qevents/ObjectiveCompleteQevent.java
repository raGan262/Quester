package com.gmail.molnardad.quester.qevents;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.elements.Qevent;
import com.gmail.molnardad.quester.exceptions.ObjectiveException;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.storage.StorageKey;

@QElement("OBJCOM")
public final class ObjectiveCompleteQevent extends Qevent {

	private final int objective;
	
	public ObjectiveCompleteQevent(int obj) {
		this.objective = obj;
	}
	
	@Override
	public String info() {
		return String.valueOf(objective);
	}

	@Override
	protected void run(Player player, Quester plugin) {
		try {
			List<Integer> prog = plugin.getProfileManager().getProfile(player.getName()).getProgress();
			if(objective >= 0 && objective < prog.size()) {
				int req = plugin.getQuestManager().getPlayerQuest(player.getName()).getObjective(objective).getTargetAmount();
				prog.set(objective, req);
			} else {
				throw new ObjectiveException("Objective index out of bounds."); // objective does not exist
			}
		} catch (QuesterException e) {
			Quester.log.info("Event failed to complete objective. Reason: " + ChatColor.stripColor(e.getMessage()));
		}
	}

	@QCommand(
			min = 1,
			max = 1,
			usage = "<objective ID>")
	public static Qevent fromCommand(QCommandContext context) {
		return new ObjectiveCompleteQevent(context.getInt(0));
	}

	@Override
	protected void save(StorageKey key) {
		key.setInt("objective", objective);
	}
	
	protected static Qevent load(StorageKey key) {
		int obj;
		
		obj = key.getInt("objective", -1);
		if(obj < 0) {
			return null;
		}
		
		return new ObjectiveCompleteQevent(obj);
	}
}
