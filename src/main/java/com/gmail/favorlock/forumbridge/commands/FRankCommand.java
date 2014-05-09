package com.gmail.favorlock.forumbridge.commands;

import com.gmail.favorlock.forumbridge.ForumBridgeConfig;
import com.gmail.favorlock.forumbridge.ForumBridgeFunctions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.Map.Entry;

public class FRankCommand extends BaseCommand {

    public FRankCommand() {
        this.command.add("frank");
        this.requiredParameters.add("Username");
        this.requiredParameters.add("Rank ID / Name");
        this.helpDescription = "Set the rank of a player both ingame and on the forum";
        this.permFlag = "bbb.rank";
        this.senderMustBePlayer = false;
    }

    public void perform() {
        if (!ForumBridgeConfig.isUseSecondaryGroups()) {
            Player player = Bukkit.getPlayer(this.parameters.get(0));
            if (player != null || ForumBridgeFunctions.hasAccount(this.parameters.get(0))) {
                String rank = "";
                String playerName = this.parameters.get(0);
                if (player != null) {
                    playerName = player.getName();
                }
                int rankID = -1;
                try {
                    rankID = Integer.parseInt(this.parameters.get(1));
                } catch (NumberFormatException e) {
                }

                if (rankID == -1) {
                    Iterator<Entry<Integer, String>> iterator = ForumBridgeConfig.getRankIdentifier().entrySet().iterator();
                    boolean found = false;
                    while (iterator.hasNext() && found == false) {
                        Entry<Integer, String> entry = iterator.next();
                        if (entry.getValue().equals(this.parameters.get(1))) {
                            rankID = entry.getKey();
                            rank = entry.getValue();
                            found = true;
                        }
                    }

                } else {
                    if (ForumBridgeConfig.getRankIdentifier().containsKey(rankID)) {
                        rank = ForumBridgeConfig.getRankIdentifier().get(rankID);
                    } else {
                        sendMessage(ChatColor.RED + "rank not found!");
                        return;
                    }
                }

                if (rankID != -1) {
                    ForumBridgeFunctions.setPlayerRank(playerName, rankID);

                    sendMessage(ChatColor.GREEN + "User rank changed to " + rank);
                } else {
                    sendMessage(ChatColor.RED + "Rank not found!");
                }
            } else {
                sendMessage(ChatColor.RED + "Unable to change the rank of the player, he didin't sync or isin't online!");
            }
        } else {
            sendMessage(ChatColor.RED + "Promotion is not currently compatible with multi-group enabled.");
        }
    }
}
