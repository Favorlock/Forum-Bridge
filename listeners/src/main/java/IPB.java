import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.favorlock.ForumBridge.extras.Tools;
import com.favorlock.ForumBridge.ForumBridgeSync;
import com.favorlock.ForumBridge.ForumBridgeWebsiteDB;
import com.favorlock.ForumBridge.ForumBridgeConfig;

public class IPB implements ForumBridgeSync {

	public IPB() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean accountExist(String username, String password) {
		boolean exist = false;
		String encpass = "nope";
		try {
			ResultSet rs = ForumBridgeWebsiteDB.dbm.prepare("SELECT members_pass_hash,members_pass_salt FROM " + ForumBridgeConfig.tablePrefix + "members WHERE members_l_username = '" + username + "'").executeQuery();
			if (rs.next()) {
				do {
					encpass = Tools.md5(Tools.md5(rs.getString("members_pass_salt")) + Tools.md5(password));
					if (encpass.equals(rs.getString("members_pass_hash"))) {
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
			ForumBridgeWebsiteDB.dbm.prepare("UPDATE " + ForumBridgeConfig.tablePrefix + "members SET member_group_id=" + forumGroupId + " WHERE members_l_username = '" + username + "'").executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void ban(String username, int forumGroupId) {
		// TODO use com.com.favorlock.com.favorlock.ForumBridge.Listeners.IPB ban system
		changeRank(username, forumGroupId);
	}

	@Override
	public void unban(String username, int forumGroupId) {
		// TODO use com.com.favorlock.com.favorlock.ForumBridge.Listeners.IPB ban system
		changeRank(username, forumGroupId);
	}

	@Override
	public List<Integer> getGroup(String username) {
		List<Integer> group = new ArrayList<Integer>();
		try {
			ResultSet rs = ForumBridgeWebsiteDB.dbm.prepare("SELECT member_group_id, mgroup_others FROM " + ForumBridgeConfig.tablePrefix + "members WHERE members_l_username = '" + username + "'").executeQuery();
			if (rs.next()) {
				do {
					group.add(rs.getInt("member_group_id"));
					String[] list = rs.getString("mgroup_others").split(",");
					for (int i = 0; i < list.length; i++) {
						if (!list[i].equals("")) {
							group.add(Integer.parseInt(list[i]));
						}
					}
				}
				while (rs.next());
			}
			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return group;
	}

}
