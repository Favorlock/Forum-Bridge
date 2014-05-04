package com.gmail.favorlock.forumbridge.listeners;

import com.gmail.favorlock.forumbridge.ForumBridgeConfig;
import com.gmail.favorlock.forumbridge.ForumBridgeSync;
import com.gmail.favorlock.forumbridge.ForumBridgeWebsiteDB;
import com.gmail.favorlock.forumbridge.extras.BCrypt;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WBB4 implements ForumBridgeSync {

    public boolean accountExist(String username, String password) {
        boolean exist = false;
        try {
            PreparedStatement query = ForumBridgeWebsiteDB.dbm.prepare("SELECT password FROM " + ForumBridgeConfig.tablePrefix + "user WHERE username='" + username + "'");
            ResultSet rs = query.executeQuery();
            if (rs != null) {
                if (rs.next()) {
                    do {
                        exist = BCrypt.checkpw(password, rs.getString("Password"));
                    }
                    while (rs.next());
                }
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exist;
    }

    public void changeRank(String username, int forumGroupId) {
        try {
            ForumBridgeWebsiteDB.dbm.prepare("UPDATE " + ForumBridgeConfig.tablePrefix + "user SET userOnlineGroupID=" + forumGroupId + " WHERE username='" + username + "'").executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void ban(String username, int forumGroupId) {
        changeRank(username, forumGroupId);
    }

    public void unban(String username, int forumGroupId) {
        changeRank(username, forumGroupId);
    }

    public List<Integer> getGroup(String username) {
        List list = new ArrayList();
        try {
            ResultSet result = ForumBridgeWebsiteDB.dbm.prepare("SELECT userOnlineGroupID FROM " + ForumBridgeConfig.tablePrefix + "user WHERE username='" + username + "'").executeQuery();
            if ((result != null) &&
                    (result.next()))
                list.add(Integer.valueOf(result.getInt("userOnlineGroupID")));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

}
