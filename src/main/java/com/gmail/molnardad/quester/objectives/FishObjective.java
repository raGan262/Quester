package com.gmail.molnardad.quester.objectives;

import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.storage.StorageKey;

@QElement("FISH")
public final class FishObjective extends Objective {

	private final int amount;
	
	public FishObjective(int amt) {
		amount = amt;
	}

	@Override
	public int getTargetAmount() {
		return amount;
	}
	
	@Override
	protected String show(int progress) {
		return "Catch fish - " + (amount - progress) + "x";
	}
	
	@Override
	protected String info() {
		return String.valueOf(amount);
	}
	
	@QCommand(
			min = 1,
			max = 1,
			usage = "<amount>")
	public static Objective fromCommand(QCommandContext context) throws QCommandException {
		int amt = context.getInt(0);
		if(amt < 1) {
			throw new QCommandException(context.getSenderLang().ERROR_CMD_AMOUNT_POSITIVE);
		}
		return new FishObjective(amt);
	}

	@Override
	protected void save(StorageKey key) {
		key.setInt("amount", amount);
	}
	
	protected static Objective load(StorageKey key) {
		int amt = 0;
		amt = key.getInt("amount", 0);
		if(amt < 1) {
			return null;
		}
		return new FishObjective(amt);
	}
}
