package com.gmail.favorlock.forumbridge;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Iterator;
import java.util.List;

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
            if (!ForumBridge.ForumBridgeDb.isBannedUser(thePlayer.getName())) {
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
                        ForumBridge.getInstance().getLogger().info("Let's wait 30 seconds");
                        ForumBridge.getInstance().getServer().getScheduler().runTaskLaterAsynchronously(ForumBridge.getInstance(), new ForumBridgeWhitelistWait(thePlayer), 600L);
                    }
                }

                ForumBridge.worldUpdate.put(thePlayer.getName(), thePlayer.getWorld().getName());
                //Everything done, we sync the player!
                ForumBridgeFunctions.syncPlayer(thePlayer.getName(), thePlayer.getWorld().getName());
            } else {
                thePlayer.kickPlayer(ForumBridgeConfig.bannedMsg + " : " + ForumBridge.ForumBridgeDb.getBanReason(thePlayer.getName()));
            }
        } else if (this.event instanceof PlayerTeleportEvent) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Player thePlayer = ((PlayerTeleportEvent) this.event).getPlayer();
            boolean update = false;
            if (ForumBridge.worldUpdate.containsKey(thePlayer.getName())) {
                if (!ForumBridge.worldUpdate.get(thePlayer.getName()).equals(thePlayer.getWorld().getName())) {
                    update = true;
                }
            } else {
                update = true;
            }

            if (ForumBridge.getInstance().getServer().getPlayer(thePlayer.getName()) != null) {
                if (update) {
                    ForumBridge.worldUpdate.put(thePlayer.getName(), thePlayer.getWorld().getName());
                    ForumBridgeFunctions.syncPlayer(thePlayer.getName(), thePlayer.getWorld().getName());
                }
            }
        }
    }
}
