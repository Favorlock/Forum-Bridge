import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import java.util.List;

import com.favorlock.ForumBridge.extras.PHPBB3Password;
import com.favorlock.ForumBridge.ForumBridgeSync;
import com.favorlock.ForumBridge.ForumBridgeWebsiteDB;
import com.favorlock.ForumBridge.ForumBridgeConfig;

public class PHPBB3 implements ForumBridgeSync {
	public boolean accountExist(String username, String password) {
		boolean exist = false;
		PHPBB3Password phpbb = new PHPBB3Password();
		try {
			PreparedStatement query = ForumBridgeWebsiteDB.dbm.prepare("SELECT user_password FROM " + ForumBridgeConfig.tablePrefix + "users WHERE username='" + username + "'");
			ResultSet result = query.executeQuery();
			if (result != null) {
				if (result.next()) {
					if (phpbb.phpbb_check_hash(password, result.getString("user_password"))) {
						exist = true;
					}

				}
			}
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return exist;
	}

	@Override
	public void changeRank(String username, int forumGroupId) {
		try {
			ForumBridgeWebsiteDB.dbm.prepare("UPDATE " + ForumBridgeConfig.tablePrefix + "users SET group_id=" + forumGroupId + " WHERE username='" + username + "'").executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void ban(String username, int forumGroupId) {
		//TODO: Use official PHPBB ban system
		changeRank(username, forumGroupId);
	}

	@Override
	public void unban(String username, int forumGroupId) {
		//TODO: Use official PHPBB ban system
		changeRank(username, forumGroupId);
	}

	@Override
	public List<Integer> getGroup(String username) {
		PreparedStatement query;
		List<Integer> list = new ArrayList<Integer>();
		ResultSet result;

		try {
			if (ForumBridgeConfig.useSecondaryGroups) {
				query = ForumBridgeWebsiteDB.dbm.prepare("SELECT group_id FROM " + ForumBridgeConfig.tablePrefix + "user_group WHERE " + ForumBridgeConfig.tablePrefix + "users.username=." + username + "' && " + ForumBridgeConfig.tablePrefix + "users.user_id = " + ForumBridgeConfig.tablePrefix + "user_group.user_id && " + ForumBridgeConfig.tablePrefix + "user_group.user_pending = 0");
			} else {
				query = ForumBridgeWebsiteDB.dbm.prepare("SELECT group_id FROM " + ForumBridgeConfig.tablePrefix + "users WHERE username='" + username + "'");
			}
			result = query.executeQuery();
			if (result != null) {
				if (result.next()) {
					do {
						list.add(result.getInt("group_id"));
					}
					while (result.next());
				}
			}
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
}
