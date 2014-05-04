package com.gmail.favorlock.forumbridge.commands;

import com.gmail.favorlock.forumbridge.ForumBridgeFunctions;
import com.gmail.favorlock.forumbridge.extras.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FBanCommand extends BaseCommand {

    public FBanCommand() {
        this.command.add("fban");
        this.permFlag = "bbb.ban";
        this.helpDescription = "Ban someone ingame and on the forum";
        this.requiredParameters.add("Player Name");
        this.optionalParameters.add("Reason");
        this.senderMustBePlayer = false;
    }

    public void perform() {
        Player player = Bukkit.getPlayer(this.parameters.get(0));
        if (player != null || ForumBridgeFunctions.hasAccount(this.parameters.get(0))) {
            String playerName = this.parameters.get(0);
            if (player != null) {
                playerName = player.getName();
            }

            String reason = "Banned";

            if (this.parameters.size() > 1) {
                reason = TextUtil.merge(this.parameters, 1);
            }

            ForumBridgeFunctions.banUser(playerName, reason);
            sendMessage("Player banned!");
        } else {
            sendMessage(ChatColor.RED + "No online player found or he didin't sync!");
        }
    }
}
