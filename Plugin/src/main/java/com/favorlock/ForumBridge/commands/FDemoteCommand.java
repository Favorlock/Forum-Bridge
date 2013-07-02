package com.favorlock.ForumBridge.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.favorlock.ForumBridge.ForumBridgeConfig;
import com.favorlock.ForumBridge.ForumBridgeFunctions;

public class FDemoteCommand extends BaseCommand {

	public FDemoteCommand() {
		this.command.add("fdemote");
		this.permFlag = "bbb.demote";
		this.helpDescription = "Demote a player both ingame and on the forum";
		this.requiredParameters.add("Username");
		this.senderMustBePlayer = false;
	}

	public void perform() {
		if (!ForumBridgeConfig.useSecondaryGroups) {
			Player player = Bukkit.getPlayer(this.parameters.get(0));
			if (player != null || ForumBridgeFunctions.hasAccount(this.parameters.get(0))) {
				String playerName = this.parameters.get(0);
				if (player != null) {
					playerName = player.getName();
				}
				List<Integer> groupList = ForumBridgeFunctions.getGroupList(playerName);
				int position = -1;

				for (int i = 0; i < ForumBridgeConfig.promotionList.length; i++) {
					if (groupList.get(0) == ForumBridgeConfig.promotionList[i]) {
						position = i;
					}
				}

				if (position != -1) {
					if (position != 0) {
						ForumBridgeFunctions.setPlayerRank(playerName, ForumBridgeConfig.promotionList[position - 1]);
						String rank = ForumBridgeConfig.promotionList[position - 1] + "";
						if (ForumBridgeConfig.rankIdentifier.containsKey(ForumBridgeConfig.promotionList[position - 1])) {
							rank = ForumBridgeConfig.rankIdentifier.get(ForumBridgeConfig.promotionList[position - 1]);
						}
						sendMessage(ChatColor.GREEN + "User demoted to rank " + rank + "!");
					} else {
						sendMessage(ChatColor.RED + "Unable to demote the player, he is already at the lowest rank!");
					}
				} else {
					sendMessage(ChatColor.RED + "Unable to demote the player, the forum rank is not in the promotion track!");
				}
			} else {
				sendMessage(ChatColor.RED + "Unable to demote the player, he didin't sync or isin't online!");
			}

		} else {
			sendMessage(ChatColor.RED + "Promotion is not currently compatible with multi-group enabled.");
		}
	}
}
