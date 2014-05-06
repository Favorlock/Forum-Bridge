package com.gmail.favorlock.forumbridge.commands;

import com.gmail.favorlock.forumbridge.ForumBridge;
import com.gmail.favorlock.forumbridge.ForumBridgeFunctions;
import org.bukkit.entity.Player;

public class FsyncAllCommand extends BaseCommand {

    public FsyncAllCommand() {
        this.command.add("fsyncall");
        this.helpDescription = "Force sync all accounts";
        this.permFlag = "bbb.forceall";
        this.senderMustBePlayer = false;
    }

    public void perform() {
        Player[] players = ForumBridge.getInstance().getServer().getOnlinePlayers();
        for (Player p : players) {
            if (ForumBridgeFunctions.hasAccount(p.getName())) {
                ForumBridgeFunctions.syncPlayer(p.getName(), p.getWorld().getName());
            }
        }
        sendMessage("All players with saved accounts has been synced!");
    }

}
