import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.favorlock.ForumBridge.extras.Tools;
import com.favorlock.ForumBridge.ForumBridgeSync;
import com.favorlock.ForumBridge.ForumBridgeWebsiteDB;
import com.favorlock.ForumBridge.ForumBridgeConfig;

public class vBulletin implements ForumBridgeSync {

    public vBulletin() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean accountExist(String username, String password) {
        String encpass = "";
        boolean exist = false;
        try {
            ResultSet rs =
                    ForumBridgeWebsiteDB.dbm.prepare(
                            "SELECT password,salt FROM " + ForumBridgeConfig.tablePrefix
                                    + "user WHERE username = '" + username + "'").executeQuery();
            if (rs.next()) {
                do {
                    encpass = Tools.md5(Tools.md5(password) + rs.getString("salt"));
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
            ForumBridgeWebsiteDB.dbm.prepare("UPDATE " + ForumBridgeConfig.tablePrefix + "user SET usergroupid=" + forumGroupId + " WHERE username = '" + username + "'").executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ban(String username, int forumGroupId) {
        // TODO use vBulletin ban system
        changeRank(username,forumGroupId);
    }

    @Override
    public void unban(String username, int forumGroupId) {
        // TODO use vBulletin ban system
        changeRank(username,forumGroupId);
    }

    @Override
    public List<Integer> getGroup(String username) {
        List<Integer> group = new ArrayList<Integer>();
        try
        {
            ResultSet rs = ForumBridgeWebsiteDB.dbm.prepare("SELECT usergroupid FROM " + ForumBridgeConfig.tablePrefix + "user WHERE username = '" + username + "'").executeQuery();
            if (rs.next())
            {
                do
                {
                    group.add(rs.getInt("usergroupid"));
                }
                while(rs.next());
            }
            rs.close();
            if (ForumBridgeConfig.useSecondaryGroups)
            {
            	rs = ForumBridgeWebsiteDB.dbm.prepare("SELECT membergroupids FROM " + ForumBridgeConfig.tablePrefix + "user WHERE username = '" + username + "'").executeQuery();
                if (rs.next())
                {
                    do
                    {
                        group.add(rs.getInt("membergroupids"));
                    }
                    while(rs.next());
                }
                rs.close();
            }
            
            
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        
        return group;
    }

}
