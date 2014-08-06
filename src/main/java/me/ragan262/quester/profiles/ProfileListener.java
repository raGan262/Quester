package me.ragan262.quester.profiles;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ProfileListener implements Listener {
	
	private final ProfileManager profMan;
	
	public ProfileListener(final ProfileManager profMan) {
		this.profMan = profMan;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(final PlayerJoinEvent event) {
		profMan.getProfile(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onLeave(final PlayerQuitEvent event) {
		profMan.saveProfile(profMan.getProfile(event.getPlayer()));
	}
}
