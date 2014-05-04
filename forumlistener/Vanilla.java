import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.favorlock.ForumBridge.ForumBridgeConfig;
import com.favorlock.ForumBridge.ForumBridgeSync;
import com.favorlock.ForumBridge.ForumBridgeWebsiteDB;
import com.favorlock.ForumBridge.extras.PHPass;

public class Vanilla implements ForumBridgeSync {

    static PHPass phpass = new PHPass(8, false);

    public Vanilla() {

    }

    @Override
    public boolean accountExist(String username, String password) {
        boolean exist = false;
        try {
            ResultSet rs = ForumBridgeWebsiteDB.dbm.prepare("SELECT Password FROM " + ForumBridgeConfig.tablePrefix +
                    "user WHERE Name = '" + username + "'").executeQuery();
            if (rs.next()) {
                do {
                    exist = phpass.CheckPassword(password, rs.getString("Password"));
                }
                while (rs.next());
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exist;
    }

    @Override
    public void changeRank(String username, int forumGroupId) {
        try {
            ResultSet rs = ForumBridgeWebsiteDB.dbm.prepare("SELECT UserID FROM " + ForumBridgeConfig.tablePrefix +
                    "user WHERE Name = '" + username + "'").executeQuery();
            if (rs != null && rs.next()) {
                ForumBridgeWebsiteDB.dbm.prepare("UPDATE " + ForumBridgeConfig.tablePrefix + "userrole SET RoleID=" +
                        forumGroupId + " WHERE UserID = " + rs.getString("UserID")).executeUpdate();
            }
            rs.close();
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
        List<Integer> group = new ArrayList<Integer>();
        try {
            ResultSet userID = ForumBridgeWebsiteDB.dbm.prepare("SELECT UserID FROM " + ForumBridgeConfig.tablePrefix +
                    "user WHERE Name = '" + username + "'").executeQuery();
            if (userID != null && userID.next()) {
                ResultSet rs = ForumBridgeWebsiteDB.dbm.prepare("SELECT RoleID FROM " + ForumBridgeConfig.tablePrefix +
                        "userrole WHERE UserID = " + userID.getString("UserID")).executeQuery();
                if (rs != null) {
                    while (rs.next()) {
                        group.add(rs.getInt("RoleID"));
                    }
                }
                rs.close();
            }
            userID.close();
        } catch (SQLException e) {
            e.printStackTrace();
            e.getMessage();
        }

        return group;
    }

}
