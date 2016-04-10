package me.ragan262.quester.qevents;

import me.ragan262.commandmanager.annotations.Command;
import me.ragan262.quester.Quester;
import me.ragan262.quester.commandmanager.QuesterCommandContext;
import me.ragan262.quester.elements.QElement;
import me.ragan262.quester.elements.Qevent;
import me.ragan262.quester.lang.LanguageManager;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.Ql;
import me.ragan262.quester.utils.Util;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@QElement("MSGS")
public final class MessagesQevent extends Qevent {
	
	private final SortedSet<DelayedMessage> messages;
	
	public MessagesQevent(final List<DelayedMessage> msgs) {
		messages = new TreeSet<>(msgs);
	}
	
	@Override
	public String info() {
		StringBuilder sb = new StringBuilder();
		for(DelayedMessage m : messages) {
			sb.append("\n    ").append(ChatColor.RESET).append(m.delay).append(':').append(m.message);
		}
		return sb.toString();
	}
	
	@Override
	protected void run(final Player player, final Quester plugin) {
		final Iterator<DelayedMessage> it = messages.iterator();
		class Messenger extends BukkitRunnable {
			private DelayedMessage current;

			private Messenger(DelayedMessage current) {
				this.current = current;
			}

			public void run() {
				player.sendMessage(current.message.replace("%p", player.getName()));
				if(it.hasNext()) {
					schedulenext(it.next());
				}
			}

			private void schedulenext(DelayedMessage next) {
				if(next == null) {
					return;
				}
				new Messenger(next).runTaskLater(plugin, 20L * (next.delay - current.delay));
			}
		}
		if(it.hasNext()) {
			DelayedMessage m = it.next();
			new Messenger(m).runTaskLater(plugin, 20L * m.delay);
		}
	}
	
	@Command(min = 1, max = 0, usage = "<can't be done>")
	public static Qevent fromCommand(final QuesterCommandContext context) {
		return null;
	}
	
	@Override
	protected void save(final StorageKey key) {
		key.removeKey("messages");
		StorageKey sub = key.getSubKey("messages");
		for(DelayedMessage m : messages) {
			sub.setString(String.valueOf(m.delay), m.raw);
		}
	}
	
	protected static Qevent load(final StorageKey key) {
		StorageKey sub = key.getSubKey("messages");
		if(!sub.hasSubKeys()) {
			return null;
		}
		List<DelayedMessage> msgs = new ArrayList<>();
		for(StorageKey k : sub.getSubKeys()) {
			msgs.add(new DelayedMessage(Integer.parseInt(k.getName()), k.getString("")));
		}
		return new MessagesQevent(msgs);
	}

	private static class DelayedMessage implements Comparable<DelayedMessage> {
		private final int delay;
		private final String raw;
		private final String message;

		public DelayedMessage(int delay, String message) {
			Validate.isTrue(delay >= 0, "Message delay can only be 0 or more.");
			this.delay = delay;
			this.raw = message;
			this.message = Util.fmt(message);
		}

		@Override
		public boolean equals(Object o) {
			if(this == o) {
				return true;
			}
			if(o == null || getClass() != o.getClass()) {
				return false;
			}

			DelayedMessage that = (DelayedMessage)o;

			if(delay != that.delay) {
				return false;
			}
			return raw.equals(that.raw);
		}

		@Override
		public int hashCode() {
			return delay;
		}

		@Override
		public int compareTo(DelayedMessage o) {
			return Integer.compare(delay, o.delay);
		}
	}
}
