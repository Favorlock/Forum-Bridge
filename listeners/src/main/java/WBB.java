import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.favorlock.ForumBridge.ForumBridgeConfig;
import com.favorlock.ForumBridge.ForumBridgeSync;
import com.favorlock.ForumBridge.ForumBridgeWebsiteDB;
import com.favorlock.ForumBridge.extras.Tools;

public class WBB implements ForumBridgeSync {

	public WBB() {

	}

	@Override
	public boolean accountExist(String username, String password) {
		boolean exist = false;
		try {
			PreparedStatement query = ForumBridgeWebsiteDB.dbm.prepare("SELECT password, salt FROM " + ForumBridgeConfig.tablePrefix + "user WHERE username='" + username + "'");
			ResultSet rs = query.executeQuery();
			if (rs != null) {
				if (rs.next()) {
					if (rs.getString("password").equals(Tools.SHA1(rs.getString("salt") + Tools.SHA1(rs.getString("salt") + Tools.SHA1(password))))) {
						exist = true;
					}
				}
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
			ForumBridgeWebsiteDB.dbm.prepare("UPDATE " + ForumBridgeConfig.tablePrefix + "user SET userOnlineGroupID=" + forumGroupId + " WHERE username='" + username + "'").executeUpdate();
		} catch (SQLException e) {
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
		List<Integer> list = new ArrayList<Integer>();
		try {
			ResultSet result = ForumBridgeWebsiteDB.dbm.prepare("SELECT userOnlineGroupID FROM " + ForumBridgeConfig.tablePrefix + "user WHERE username='" + username + "'").executeQuery();
			if (result != null) {
				if (result.next()) {
					list.add(result.getInt("userOnlineGroupID"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

}