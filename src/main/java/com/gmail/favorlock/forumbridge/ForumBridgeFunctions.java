package com.gmail.favorlock.forumbridge;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ForumBridgeFunctions {
    public static boolean accountExist(String username, String password) {
        return ForumBridge.sync.accountExist(username, password);
    }

    /**
     * Get all forum groups ID from a user.
     *
     * @param playerName The player Name
     * @return A List of forum group ID or null if the user never synced.
     */
    public static List<Integer> getGroupList(String playerName) {
        List<Integer> returnList = null;
        if (ForumBridge.ForumBridgeDb.existUser(playerName)) {
            returnList = ForumBridge.sync.getGroup(ForumBridge.ForumBridgeDb.getUser(playerName));
        }
        return returnList;
    }

    public static synchronized boolean hasAccount(String playerName) {
        return ForumBridge.ForumBridgeDb.existUser(playerName);
    }

    public static synchronized void banUser(String playerName, String reason) {
        if (ForumBridge.ForumBridgeDb.existUser(playerName)) {
            ForumBridge.ForumBridgeDb.banUser(playerName, reason);
            ForumBridge.sync.changeRank(ForumBridge.ForumBridgeDb.getUser(playerName), ForumBridgeConfig.bannedGroupID);

            Player p = Bukkit.getPlayer(playerName);

            if (p != null) {
                p.kickPlayer(ForumBridgeConfig.bannedMsg + " : " + reason);
            }
        }
    }

    public static synchronized void unbanUser(String playerName) {
        if (ForumBridge.ForumBridgeDb.existUser(playerName) && ForumBridge.ForumBridgeDb.isBannedUser(playerName)) {
            ForumBridge.ForumBridgeDb.unbanUser(playerName);
            ForumBridge.sync.changeRank(ForumBridge.ForumBridgeDb.getUser(playerName), ForumBridgeConfig.unbannedGroupID);
        }
    }

    public static synchronized void setPlayerRank(String playerName, int rankID) {
        if (hasAccount(playerName)) {
            ForumBridge.sync.changeRank(ForumBridge.ForumBridgeDb.getUser(playerName), rankID);
        }
    }

    /**
     * Sync a player
     *
     * @param playerName The player name
     * @param worldName  The world to sync to
     * @return True if the user synced, false if the user doesn't have a account.
     */
    public static synchronized boolean syncPlayer(String playerName, String worldName) {
        if (!ForumBridgeConfig.groupList.containsKey(worldName)) {
            worldName = "default";
        }
        if (hasAccount(playerName)) {
            List<Integer> groupList = ForumBridge.sync.getGroup(ForumBridge.ForumBridgeDb.getUser(playerName));
            Iterator<Integer> groupIterator = groupList.iterator();
            //We reset groups
            String[] permGroupList = ForumBridge.perms.getPlayerGroups(ForumBridge.p.getServer().getPlayer(playerName));
            for (int i = 0; i < permGroupList.length; i++) {
                modifyGroup(true, playerName, permGroupList[i], worldName);
            }
            HashMap<Integer, String> configurationGroup = ForumBridgeConfig.groupList.get(worldName);

            while (groupIterator.hasNext()) {
                String groupName = configurationGroup.get(groupIterator.next());

                if (groupName != null) {
                    modifyGroup(false, playerName, groupName, worldName);
                }
            }
            return true;
        }
        return false;
    }

    public static synchronized void modifyGroup(boolean remove, String playerName, String groupName, String worldName) {
        if (groupName != null) {
            if (remove) {
                if (worldName.equals("default")) {
                    ForumBridge.perms.playerRemoveGroup(ForumBridge.p.getServer().getPlayer(playerName).getWorld(), ForumBridge.p.getServer().getPlayer(playerName).getName(), groupName);
                } else {
                    ForumBridge.perms.playerRemoveGroup(worldName, ForumBridge.p.getServer().getPlayer(playerName).getName(), groupName);
                }

            } else {
                if (worldName.equals("default")) {
                    ForumBridge.perms.playerAddGroup(ForumBridge.p.getServer().getPlayer(playerName), groupName);
                } else {
                    ForumBridge.perms.playerAddGroup(worldName, playerName, groupName);
                }
            }
        }
    }
}