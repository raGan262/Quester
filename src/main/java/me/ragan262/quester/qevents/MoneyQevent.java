package me.ragan262.quester.qevents;

import me.ragan262.commandmanager.annotations.Command;
import me.ragan262.commandmanager.exceptions.CommandException;
import me.ragan262.quester.Quester;
import me.ragan262.quester.commandmanager.QuesterCommandContext;
import me.ragan262.quester.elements.QElement;
import me.ragan262.quester.elements.Qevent;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.Ql;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;

@QElement("MONEY")
public final class MoneyQevent extends Qevent {
	
	private final double amount;
	
	public MoneyQevent(final double amt) {
		amount = amt;
	}
	
	@Override
	public String info() {
		return String.valueOf(amount);
	}
	
	@Override
	protected void run(final Player player, final Quester plugin) {
		if(!Quester.vault) {
			Ql.warning("Failed process money event on " + player.getName()
					+ ": Economy support disabled");
			return;
		}
		EconomyResponse resp;
		
		if(amount >= 0) {
			resp = Quester.econ.depositPlayer(player.getName(), amount);
		}
		else {
			resp = Quester.econ.withdrawPlayer(player.getName(), -amount);
		}
		if(!resp.transactionSuccess()) {
			Ql.warning("Failed process money event on " + player.getName() + ": "
					+ resp.errorMessage);
		}
	}
	
	@Command(min = 1, max = 1, usage = "<amount>")
	public static Qevent fromCommand(final QuesterCommandContext context) throws CommandException {
		final double amt = context.getDouble(0);
		if(amt == 0.0D) {
			throw new CommandException(context.getSenderLang().get("ERROR_CMD_AMOUNT_NONZERO"));
		}
		return new MoneyQevent(context.getDouble(0));
	}
	
	@Override
	protected void save(final StorageKey key) {
		key.setDouble("amount", amount);
	}
	
	protected static Qevent load(final StorageKey key) {
		final double amt = key.getDouble("amount", 0.0D);
		if(amt == 0) {
			return null;
		}
		
		return new MoneyQevent(amt);
	}
}
