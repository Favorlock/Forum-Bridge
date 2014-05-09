package com.gmail.favorlock.forumbridge.listeners;

import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.gmail.favorlock.forumbridge.extras.Tools;
import com.gmail.favorlock.forumbridge.ForumBridgeSync;
import com.gmail.favorlock.forumbridge.ForumBridgeWebsiteDB;
import com.gmail.favorlock.forumbridge.ForumBridgeConfig;

public class SMF implements ForumBridgeSync {

    @Override
    public boolean accountExist(String username, String password) {
        boolean exist = false;
        try {
            PreparedStatement query = ForumBridgeWebsiteDB.dbm.prepare("SELECT passwd FROM " + ForumBridgeConfig.getTablePrefix() + "members WHERE member_name='" + username + "'");
            ResultSet result = query.executeQuery();
            if (result != null) {
                if (result.next()) {
                    if (result.getString("passwd").equals(Tools.SHA1(username.toLowerCase() + password))) {
                        exist = true;
                    }
                }
            }
            result.close();
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
            ForumBridgeWebsiteDB.dbm.prepare("UPDATE " + ForumBridgeConfig.getTablePrefix() + "members SET id_group=" + forumGroupId + " WHERE member_name='" + username + "'").executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ban(String username, int forumGroupId) {
        // TODO Use official com.gmail.favorlock.forumbridge.SMFisteners.SMF ban
        changeRank(username, forumGroupId);
    }

    @Override
    public void unban(String username, int forumGroupId) {
        // TODO Use official com.gmail.favorlock.forumbridge.SMFisteners.SMF ban
        changeRank(username, forumGroupId);
    }

    @Override
    public List<Integer> getGroup(String username) {
        //TODO Find how com.gmail.favorlock.forumbridge.SMFisteners.SMF multigroup works
        List<Integer> list = new ArrayList<Integer>();
        try {
            ResultSet result = ForumBridgeWebsiteDB.dbm.prepare("SELECT id_group FROM " + ForumBridgeConfig.getTablePrefix() + "members WHERE member_name='" + username + "'").executeQuery();
            if (result != null) {
                if (result.next()) {
                    list.add(result.getInt("id_group"));
                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return list;
    }

}
