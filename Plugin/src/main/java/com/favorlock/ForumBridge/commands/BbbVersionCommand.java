package com.favorlock.ForumBridge.commands;

import com.favorlock.ForumBridge.ForumBridge;
import org.bukkit.ChatColor;

public class BbbVersionCommand extends BaseCommand {

	public BbbVersionCommand() {
		this.command.add("bbbversion");
		this.helpDescription = "Show the version of com.favorlock.ForumBridge";
		this.senderMustBePlayer = false;
	}

	public void perform() {
		sendMessage(colorizeText("--Bulletin Board Bridge by " + ForumBridge.authors.get(0) + ". Original Author kalmanolah--", ChatColor.AQUA));
		sendMessage("This server is using " + colorizeText(ForumBridge.name, ChatColor.GREEN) + " version " + colorizeText(ForumBridge.version, ChatColor.GREEN) + ".");

	}
}
