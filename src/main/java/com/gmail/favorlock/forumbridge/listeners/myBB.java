package com.gmail.favorlock.forumbridge.listeners;

import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.gmail.favorlock.forumbridge.extras.Tools;
import com.gmail.favorlock.forumbridge.ForumBridgeSync;
import com.gmail.favorlock.forumbridge.ForumBridgeWebsiteDB;
import com.gmail.favorlock.forumbridge.ForumBridgeConfig;

public class myBB implements ForumBridgeSync {

    public myBB() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean accountExist(String username, String password) {
        String encpass = "";
        boolean exist = false;
        try {
            ResultSet rs =
                    ForumBridgeWebsiteDB.dbm.prepare(
                            "SELECT password,salt FROM " + ForumBridgeConfig.getTablePrefix()
                                    + "users WHERE username = '" + username + "'"
                    ).executeQuery();
            if (rs.next()) {
                do {
                    encpass = Tools.md5(Tools.md5(rs.getString("salt")) + (Tools.md5(password)));
                    if (encpass.equals(rs.getString("password"))) {
                        exist = true;
                    }
                } while (rs.next());
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
            ForumBridgeWebsiteDB.dbm.prepare("UPDATE " + ForumBridgeConfig.getTablePrefix() + "users SET usergroup=" + forumGroupId + " WHERE username = '" + username + "'").executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ban(String username, int forumGroupId) {
        // TODO use com.gmail.favorlock.forumbridge.listenersge.myBB ban system
        changeRank(username, forumGroupId);
    }

    @Override
    public void unban(String username, int forumGroupId) {
        // TODO use com.gmail.favorlock.forumbridge.listenersge.myBB ban system
        changeRank(username, forumGroupId);
    }

    @Override
    public List<Integer> getGroup(String username) {
        List<Integer> group = new ArrayList<Integer>();
        try {
            ResultSet rs = ForumBridgeWebsiteDB.dbm.prepare("SELECT usergroup FROM " + ForumBridgeConfig.getTablePrefix() + "users WHERE username = '" + username + "'").executeQuery();
            if (rs.next()) {
                do {
                    group.add(rs.getInt("usergroup"));
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
