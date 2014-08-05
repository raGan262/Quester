package me.ragan262.quester.holder;

import me.ragan262.quester.QConfiguration;
import me.ragan262.quester.Quester;
import me.ragan262.quester.lang.QuesterLang;
import me.ragan262.quester.utils.Util;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignHolderActionHandler extends QuestHolderActionHandler<QuesterSign> implements Listener {
	
	public SignHolderActionHandler(final Quester plugin) {
		super(plugin);
	}
	
	private boolean isSign(final Block block) {
		return block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN;
	}
	
	@Override
	public String getHeaderText(final Player player, final QuestHolder qh, final QuesterSign data) {
		return langMan.getLang(profMan.getProfile(player).getLanguage()).get("SIGN_HEADER");
	}
	
	@Override
	public String getUsePermission() {
		return QConfiguration.PERM_USE_SIGN;
	}
	
	@Override
	public void assignHolder(final QuestHolder qh, final QuesterSign data) {
		data.setHolderID(qh.getId());
	}
	
	@Override
	public void unassignHolder(final QuestHolder qh, final QuesterSign data) {
		data.setHolderID(-1);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onSignInteract(final PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		if(event.getAction() != Action.LEFT_CLICK_BLOCK
				&& event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		
		final Block block = event.getClickedBlock();
		final QuesterSign qSign = holMan.getSign(block.getLocation());
		if(qSign == null) {
			return;
		}
		
		final QuesterLang lang = langMan.getLang(profMan.getProfile(player).getLanguage());
		if(!isSign(block)) {
			holMan.removeSign(block.getLocation());
			player.sendMessage(Quester.LABEL + lang.get("SIGN_UNREGISTERED"));
			return;
		}
		
		final Sign sign = (Sign)block.getState();
		if(!sign.getLine(0).equals(ChatColor.BLUE + "[Quester]")) {
			block.breakNaturally();
			holMan.removeSign(block.getLocation());
			player.sendMessage(Quester.LABEL + lang.get("SIGN_UNREGISTERED"));
			return;
		}
		
		final QuestHolder qHolder = holMan.getHolder(qSign.getHolderID());
		event.setCancelled(true);
		if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if(player.isSneaking()) {
				event.setCancelled(false);
				return;
			}
			onLeftClick(player, qHolder, qSign);
		}
		else {
			onRightClick(player, qHolder, qSign);
			
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSignBreak(final BlockBreakEvent event) {
		final Block block = event.getBlock();
		if(!isSign(block)) {
			return;
		}
		
		final Sign sign = (Sign)block.getState();
		if(holMan.getSign(sign.getLocation()) != null) {
			final Player p = event.getPlayer();
			final QuesterLang lang = langMan.getLang(profMan.getProfile(p).getLanguage());
			if(!event.getPlayer().isSneaking()
					|| !Util.permCheck(p, QConfiguration.PERM_MODIFY, false, null)) {
				event.setCancelled(true);
				return;
			}
			holMan.removeSign(sign.getLocation());
			p.sendMessage(Quester.LABEL + lang.get("SIGN_UNREGISTERED"));;
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSignChange(final SignChangeEvent event) {
		final Block block = event.getBlock();
		if(event.getLine(0).equals("[Quester]")) {
			final QuesterLang lang = langMan.getLang(profMan.getProfile(event.getPlayer()).getLanguage());
			if(!Util.permCheck(event.getPlayer(), QConfiguration.PERM_MODIFY, true, lang)) {
				block.breakNaturally();
			}
			event.setLine(0, ChatColor.BLUE + "[Quester]");
			final QuesterSign sign = new QuesterSign(block.getLocation());
			holMan.addSign(sign);
			event.getPlayer().sendMessage(Quester.LABEL + lang.get("SIGN_REGISTERED"));;
		}
	}
}
