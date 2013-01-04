package com.favorlock.ForumBridge;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class ForumBridgeEvents implements Listener
{

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        event.setJoinMessage(null);
        ForumBridge.p.getServer().getScheduler().scheduleSyncDelayedTask(ForumBridge.p, new ForumBridgeRunnable(ForumBridge.p, event), 0);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerTeleport(PlayerTeleportEvent event)
    {
	ForumBridge.p.getServer().getScheduler().scheduleSyncDelayedTask(ForumBridge.p, new ForumBridgeRunnable(ForumBridge.p, event), 0);
    }
}
