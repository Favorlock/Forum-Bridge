package com.favorlock.ForumBridge;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ForumBridgeFunctions {
	public static boolean accountExist(String username, String password) {
		return com.favorlock.ForumBridge.sync.accountExist(username, password);
	}

	/**
	 * Get all forum groups ID from a user.
	 *
	 * @param playerName The player Name
	 * @return A List of forum group ID or null if the user never synced.
	 */
	public static List<Integer> getGroupList(String playerName) {
		List<Integer> returnList = null;
		if (com.favorlock.ForumBridge.ForumBridgeDb.existUser(playerName)) {
			returnList = com.favorlock.ForumBridge.sync.getGroup(com.favorlock.ForumBridge.ForumBridgeDb.getUser(playerName));
		}
		return returnList;
	}

	public static synchronized boolean hasAccount(String playerName) {
		return com.favorlock.ForumBridge.ForumBridgeDb.existUser(playerName);
	}

	public static synchronized void banUser(String playerName, String reason) {
		if (com.favorlock.ForumBridge.ForumBridgeDb.existUser(playerName)) {
			com.favorlock.ForumBridge.ForumBridgeDb.banUser(playerName, reason);
			com.favorlock.ForumBridge.sync.changeRank(com.favorlock.ForumBridge.ForumBridgeDb.getUser(playerName), ForumBridgeConfig.bannedGroupID);

			Player p = Bukkit.getPlayer(playerName);

			if (p != null) {
				p.kickPlayer(ForumBridgeConfig.bannedMsg + " : " + reason);
			}
		}
	}

	public static synchronized void unbanUser(String playerName) {
		if (com.favorlock.ForumBridge.ForumBridgeDb.existUser(playerName) && com.favorlock.ForumBridge.ForumBridgeDb.isBannedUser(playerName)) {
			com.favorlock.ForumBridge.ForumBridgeDb.unbanUser(playerName);
			com.favorlock.ForumBridge.sync.changeRank(com.favorlock.ForumBridge.ForumBridgeDb.getUser(playerName), ForumBridgeConfig.unbannedGroupID);
		}
	}

	public static synchronized void setPlayerRank(String playerName, int rankID) {
		if (hasAccount(playerName)) {
			com.favorlock.ForumBridge.sync.changeRank(com.favorlock.ForumBridge.ForumBridgeDb.getUser(playerName), rankID);
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
			List<Integer> groupList = com.favorlock.ForumBridge.sync.getGroup(com.favorlock.ForumBridge.ForumBridgeDb.getUser(playerName));
			Iterator<Integer> groupIterator = groupList.iterator();
			//We reset groups
			String[] permGroupList = com.favorlock.ForumBridge.perms.getPlayerGroups(com.favorlock.ForumBridge.p.getServer().getPlayer(playerName));
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
					com.favorlock.ForumBridge.perms.playerRemoveGroup(com.favorlock.ForumBridge.p.getServer().getPlayer(playerName).getWorld(), com.favorlock.ForumBridge.p.getServer().getPlayer(playerName).getName(), groupName);
				} else {
					com.favorlock.ForumBridge.perms.playerRemoveGroup(worldName, com.favorlock.ForumBridge.p.getServer().getPlayer(playerName).getName(), groupName);
				}

			} else {
				if (worldName.equals("default")) {
					com.favorlock.ForumBridge.perms.playerAddGroup(com.favorlock.ForumBridge.p.getServer().getPlayer(playerName), groupName);
				} else {
					com.favorlock.ForumBridge.perms.playerAddGroup(worldName, playerName, groupName);
				}
			}
		}
	}
}
