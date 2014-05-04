package com.favorlock.ForumBridge;

import org.bukkit.entity.Player;

public class ForumBridgeWhitelistWait implements Runnable {
    Player player;

    public ForumBridgeWhitelistWait(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        ForumBridge.p.getLogger().info("We waited 30s!");
        // TODO Auto-generated method stub
        if (!ForumBridgeFunctions.hasAccount(player.getName())) {
            player.kickPlayer("You went over the 30 secs limit to sync. Sync faster!");
        }
    }

}
