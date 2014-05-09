package com.gmail.favorlock.forumbridge.commands;

import com.gmail.favorlock.forumbridge.ForumBridge;
import com.gmail.favorlock.forumbridge.ForumBridgeFunctions;
import org.bukkit.ChatColor;

public class SyncCommand extends BaseCommand {
    public SyncCommand() {
        this.command.add("sync");
        this.permFlag = "bbb.sync";
        this.helpDescription = "Sync your account with the forum";
        this.requiredParameters.add("Website Username");
        this.requiredParameters.add("Website Password");
    }

    public void perform() {
        if (ForumBridgeFunctions.accountExist(this.parameters.get(0), this.parameters.get(1))) {
            ForumBridge.getForumBridgeDb().addUser(player.getName(), this.parameters.get(0));
            ForumBridgeFunctions.syncPlayer(player.getName(), player.getWorld().getName());
            sendMessage("You are now synced with your website account! Each time you connect your group will be automaticly imported.");
        } else {
            sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + "Incorrect username or password.");
            sendMessage(ChatColor.DARK_AQUA + "[Website Username] " + ChatColor.WHITE + "is your username on the website/forum");
            sendMessage(ChatColor.DARK_AQUA + "[Website Password] " + ChatColor.WHITE + "is your website/forum password.");
        }
    }

}
