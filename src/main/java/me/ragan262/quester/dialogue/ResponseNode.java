package me.ragan262.quester.dialogue;

import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.Util;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import java.util.*;

public class ResponseNode extends DialogueNode {

	protected ResponseNode(DialogueTree tree) {
		super(tree);
	}

	@Override
	protected void addOption(DialogueOption option) {
		if(option instanceof ResponseOption) {
			super.addOption(option);
		}
	}

	@Override
	protected DialogueOption deserializeOption(DialogueNode node, StorageKey key) {
		ResponseOption o = new ResponseOption(node);
		o.addApplicable(key.getRaw("applicable"));
		o.response = Util.fmt(key.getString("response"));
		return o;
	}

	@Override
	public boolean blocksForInput(ConversationContext context) {
		return true;
	}

	@Override
	public String getPromptText(ConversationContext context) {
		Player player = (Player)context.getForWhom();
		final String glue = ", ";
		String result = super.getPromptText(context).replace("%p", player.getName())
				+ ChatColor.RESET + "\n[";
		String opts = "";
		for(DialogueOption o : options) {
			ResponseOption ro = ((ResponseOption)o);
			if(ro.isAvailable(player)) {
				opts += ChatColor.GREEN + ro.hint + ChatColor.RESET + ", ";
			}
		}
		if(opts.isEmpty()) {
			opts = ChatColor.RED + "No options available." + glue;
		}
		return result + opts.substring(0, opts.length() - glue.length()) + ChatColor.RESET + "]"
				+ "(\"" + ChatColor.GREEN + "-" + ChatColor.RESET + "\" to cancel)";
	}

	@Override
	public Prompt acceptInput(ConversationContext context, String input) {
		if(input == null) {
			return this;
		}
		DialogueNode result = null;
		String response = null;
		for(DialogueOption o : options) {
			ResponseOption ro = ((ResponseOption)o);
			if(ro.isApplicable(input) && ro.isAvailable((Player)context.getForWhom())) {
				result = ro.getNode();
				response = ro.response;
			}
		}
		if(result != null) {
			context.getForWhom().sendRawMessage(tree.getUserPrefix() + (response == null ? input : response));
		}
		return result == null ? this : result;
	}

	public static class ResponseOption extends DialogueOption {

		private String response = null;
		private String hint = null;
		private Set<String> applicable = new HashSet<>();

		public ResponseOption(DialogueNode node) {
			super(node);
		}

		private void addApplicable(Object object) {
			if(object instanceof Collection) {
				Collection c = (Collection)object;
				for(Object o: c) {
					if(o instanceof String) {
						if(hint == null) {
							hint = (String)o;
						}
						applicable.add(((String)o).toLowerCase());
					}
				}
			}
		}

		private boolean isApplicable(String input) {
			return applicable.contains(input.toLowerCase());
		}
	}

	public static class Loader implements DialogueNodeLoader {

		@Override
		public DialogueNode loadNode(DialogueTree tree, StorageKey key) {
			return new ResponseNode(tree);
		}
	}
}
