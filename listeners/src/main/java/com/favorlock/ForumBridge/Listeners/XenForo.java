package com.favorlock.ForumBridge.Listeners;

import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.favorlock.ForumBridge.extras.Tools;
import com.favorlock.ForumBridge.ForumBridgeSync;
import com.favorlock.ForumBridge.ForumBridgeWebsiteDB;
import com.favorlock.ForumBridge.ForumBridgeConfig;

public class XenForo implements ForumBridgeSync {
	public XenForo() {
	}

	@Override
	public boolean accountExist(String username, String password) {
		boolean exist = false;
		try {
			ResultSet rs = ForumBridgeWebsiteDB.dbm.prepare("SELECT data FROM " + (String) ForumBridgeConfig.tablePrefix + "user_authenticate," + (String) ForumBridgeConfig.tablePrefix
					+ "user WHERE " + ForumBridgeConfig.tablePrefix + "user.username = '" + username + "' AND " + ForumBridgeConfig.tablePrefix + "user.user_id = " + ForumBridgeConfig.tablePrefix + "user_authenticate.user_id").executeQuery();
			if (rs.next()) {
				do {
					if (Tools.SHA256(Tools.SHA256(password) + Tools.regmatch("\"salt\";.:..:\"(.*)\";.:.:\"hashFunc\"", rs.getString("data"))).equals(Tools.regmatch("\"hash\";.:..:\"(.*)\";.:.:\"salt\"", rs.getString("data")))) {
						exist = true;
					}
				}
				while (rs.next());
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return exist;
	}

	@Override
	public void changeRank(String username, int forumGroupId) {
		try {
			ForumBridgeWebsiteDB.dbm.prepare("UPDATE " + ForumBridgeConfig.tablePrefix + "user," + ForumBridgeConfig.tablePrefix + "user_authenticate SET user_group_id='" + forumGroupId + "' WHERE " + ForumBridgeConfig.tablePrefix + "user.username='" + username + "' AND " + ForumBridgeConfig.tablePrefix + "user.user_id=xf_user_authenticate.user_id").executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void ban(String username, int forumGroupId) {
		changeRank(username, forumGroupId);
	}

	@Override
	public void unban(String username, int forumGroupId) {
		changeRank(username, forumGroupId);
	}

	@Override
	public List<Integer> getGroup(String username) {
		//first one
		List<Integer> group = new ArrayList<Integer>();
		String query1 = "SELECT user_group_id,data FROM " + ForumBridgeConfig.tablePrefix + "user," + ForumBridgeConfig.tablePrefix + "user_authenticate WHERE " + ForumBridgeConfig.tablePrefix + "user.username = '" + username + "'  AND " + ForumBridgeConfig.tablePrefix + "user.user_id = " + ForumBridgeConfig.tablePrefix + "user_authenticate.user_id";
		try {
			ResultSet rs = ForumBridgeWebsiteDB.dbm.prepare(query1).executeQuery();
			if (rs.next()) {
				group.add(rs.getInt("user_group_id"));
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (ForumBridgeConfig.useSecondaryGroups) {
			//second one
			String query2 = "SELECT secondary_group_ids,data FROM " + ForumBridgeConfig.tablePrefix + "user," + ForumBridgeConfig.tablePrefix + "user_authenticate WHERE " + ForumBridgeConfig.tablePrefix + "user.username = '" + username + "'  AND " + ForumBridgeConfig.tablePrefix + "user.user_id = " + ForumBridgeConfig.tablePrefix + "user_authenticate.user_id";
			try {
				ResultSet rs2 = ForumBridgeWebsiteDB.dbm.prepare(query2).executeQuery();
				String secondarygroups = rs2.getString("secondary_group_ids");
				String[] splitgroups = secondarygroups.split(",");
				for (int i = 0; i < splitgroups.length; i++) {
					group.add(Integer.parseInt(splitgroups[i]));
				}
				rs2.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		return group;
	}

}