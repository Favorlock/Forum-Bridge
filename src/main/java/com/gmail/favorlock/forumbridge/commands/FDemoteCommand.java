package com.gmail.favorlock.forumbridge.commands;

import com.gmail.favorlock.forumbridge.ForumBridgeConfig;
import com.gmail.favorlock.forumbridge.ForumBridgeFunctions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class FDemoteCommand extends BaseCommand {

    public FDemoteCommand() {
        this.command.add("fdemote");
        this.permFlag = "bbb.demote";
        this.helpDescription = "Demote a player both ingame and on the forum";
        this.requiredParameters.add("Username");
        this.senderMustBePlayer = false;
    }

    public void perform() {
        if (!ForumBridgeConfig.isUseSecondaryGroups()) {
            Player player = Bukkit.getPlayer(this.parameters.get(0));
            if (player != null || ForumBridgeFunctions.hasAccount(this.parameters.get(0))) {
                String playerName = this.parameters.get(0);
                if (player != null) {
                    playerName = player.getName();
                }
                List<Integer> groupList = ForumBridgeFunctions.getGroupList(playerName);
                int position = -1;

                for (int i = 0; i < ForumBridgeConfig.getPromotionList().length; i++) {
                    if (groupList.get(0) == ForumBridgeConfig.getPromotionList()[i]) {
                        position = i;
                    }
                }

                if (position != -1) {
                    if (position != 0) {
                        ForumBridgeFunctions.setPlayerRank(playerName, ForumBridgeConfig.getPromotionList()[position - 1]);
                        String rank = ForumBridgeConfig.getPromotionList()[position - 1] + "";
                        if (ForumBridgeConfig.getRankIdentifier().containsKey(ForumBridgeConfig.getPromotionList()[position - 1])) {
                            rank = ForumBridgeConfig.getRankIdentifier().get(ForumBridgeConfig.getPromotionList()[position - 1]);
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
