package com.gmail.favorlock.forumbridge.listeners;

import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.gmail.favorlock.forumbridge.ForumBridgeConfig;
import com.gmail.favorlock.forumbridge.ForumBridgeSync;
import com.gmail.favorlock.forumbridge.ForumBridgeWebsiteDB;
import com.gmail.favorlock.forumbridge.extras.Tools;

public class PunBB implements ForumBridgeSync {

    public PunBB() {

    }

    @Override
    public boolean accountExist(String username, String password) {
        boolean exist = false;
        try {
            PreparedStatement query = ForumBridgeWebsiteDB.dbm.prepare("SELECT password, salt FROM " + ForumBridgeConfig.getTablePrefix() + "users WHERE username='" + username + "'");
            ResultSet rs = query.executeQuery();
            if (rs != null) {
                if (rs.next()) {
                    if (rs.getString("password").equals(Tools.SHA1(rs.getString("salt") + Tools.SHA1(rs.getString("password")))))
                        ;
                    {
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
            ForumBridgeWebsiteDB.dbm.prepare("UPDATE " + ForumBridgeConfig.getTablePrefix() + "users SET group_id=" + forumGroupId + " WHERE username='" + username + "'").executeUpdate();
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
            ResultSet result = ForumBridgeWebsiteDB.dbm.prepare("SELECT group_id FROM " + ForumBridgeConfig.getTablePrefix() + "users WHERE username='" + username + "'").executeQuery();
            if (result != null) {
                if (result.next()) {
                    list.add(result.getInt("group_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

}