package com.favorlock.ForumBridge;

import java.util.Iterator;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class ForumBridgeRunnable implements Runnable {
	public static ForumBridge plugin;
	Event event;

	public ForumBridgeRunnable(ForumBridge instance, Event event) {
		plugin = instance;
		this.event = event;
	}

	public void run() {
		//Somebody is connecting
		if (this.event instanceof PlayerJoinEvent) {
			PlayerJoinEvent joinevent = (PlayerJoinEvent) event;
			Player thePlayer = joinevent.getPlayer();
			if (!com.favorlock.ForumBridge.ForumBridgeDb.isBannedUser(thePlayer.getName())) {
				if (ForumBridgeConfig.isWhitelist) {
					if (ForumBridgeFunctions.hasAccount(thePlayer.getName())) {
						List<Integer> groupList = ForumBridgeFunctions.getGroupList(thePlayer.getName());
						Iterator<Integer> iterator = groupList.iterator();

						boolean isWhitelist = false;

						while (iterator.hasNext()) {
							if (ForumBridgeConfig.whitelist.contains(iterator.next())) {
								isWhitelist = true;
							}
						}
						if (!isWhitelist) {
							thePlayer.kickPlayer(ForumBridgeConfig.whitelistKickMsg);
							;
						}
					} else {
						//We give a grace period of 30 secs to sync
						com.favorlock.ForumBridge.p.getLogger().info("Let's wait 30 seconds");
						com.favorlock.ForumBridge.p.getServer().getScheduler().runTaskLaterAsynchronously(com.favorlock.ForumBridge.p, new ForumBridgeWhitelistWait(thePlayer), 600L);
					}
				}

				com.favorlock.ForumBridge.worldUpdate.put(thePlayer.getName(), thePlayer.getWorld().getName());
				//Everything done, we sync the player!
				ForumBridgeFunctions.syncPlayer(thePlayer.getName(), thePlayer.getWorld().getName());
			} else {
				thePlayer.kickPlayer(ForumBridgeConfig.bannedMsg + " : " + com.favorlock.ForumBridge.ForumBridgeDb.getBanReason(thePlayer.getName()));
			}
		} else if (this.event instanceof PlayerTeleportEvent) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Player thePlayer = ((PlayerTeleportEvent) this.event).getPlayer();
			boolean update = false;
			if (com.favorlock.ForumBridge.worldUpdate.containsKey(thePlayer.getName())) {
				if (!com.favorlock.ForumBridge.worldUpdate.get(thePlayer.getName()).equals(thePlayer.getWorld().getName())) {
					update = true;
				}
			} else {
				update = true;
			}

			if (com.favorlock.ForumBridge.p.getServer().getPlayer(thePlayer.getName()) != null) {
				if (update) {
					com.favorlock.ForumBridge.worldUpdate.put(thePlayer.getName(), thePlayer.getWorld().getName());
					ForumBridgeFunctions.syncPlayer(thePlayer.getName(), thePlayer.getWorld().getName());
				}
			}
		}
	}
}
