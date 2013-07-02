package com.favorlock.ForumBridge.commands;

import org.bukkit.ChatColor;

import com.favorlock.ForumBridge.ForumBridgeFunctions;

public class FUnbanCommand extends BaseCommand {

	public FUnbanCommand() {
		this.command.add("funban");
		this.requiredParameters.add("Player Name");
		this.permFlag = "bbb.unban";
		this.helpDescription = "Unban someone from the forum and ingame";
		this.senderMustBePlayer = false;
	}

	public void perform() {
		if (ForumBridgeFunctions.hasAccount(this.parameters.get(0))) {
			ForumBridgeFunctions.unbanUser(this.parameters.get(0));
			sendMessage("Player unbanned!");
		} else {
			sendMessage(ChatColor.RED + "Player not found.");
		}
	}
}
