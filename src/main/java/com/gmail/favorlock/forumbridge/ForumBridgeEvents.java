package com.gmail.favorlock.forumbridge;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class ForumBridgeEvents implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        ForumBridge.getInstance().getServer().getScheduler().runTaskAsynchronously(ForumBridge.getInstance(), new ForumBridgeRunnable(ForumBridge.getInstance(), event));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        ForumBridge.getInstance().getServer().getScheduler().runTaskAsynchronously(ForumBridge.getInstance(), new ForumBridgeRunnable(ForumBridge.getInstance(), event));
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String[] args = event.getMessage().split(" ");
        if (args[0].equalsIgnoreCase("sync")) {
            event.setCancelled(true);
        }
    }
}