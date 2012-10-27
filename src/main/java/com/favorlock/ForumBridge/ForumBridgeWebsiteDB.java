package com.favorlock.ForumBridge;

import java.sql.SQLException;

import com.favorlock.ForumBridge.extras.lib.PatPeter.SQLibrary.MySQL;

public class ForumBridgeWebsiteDB
{
	public static MySQL dbm;
	public ForumBridgeWebsiteDB(ForumBridge thePlugin) throws SQLException
	{
		dbm = new MySQL(thePlugin.getLogger(), "ForumBridge", ForumBridgeConfig.databaseHost, ForumBridgeConfig.databasePort, ForumBridgeConfig.databaseDB, ForumBridgeConfig.databaseUser, ForumBridgeConfig.databasePassword);
		dbm.open();
		if (!dbm.checkConnection())
		{
			throw new SQLException("Impossible to connect to MySQL database.");
		}
		thePlugin.getServer().getScheduler().scheduleSyncRepeatingTask(thePlugin, new Runnable()
        {
            public void run()
            {
                try
                {
                    dbm.query("SELECT 1 FROM DUAL");
                }
                catch (Exception e)
                {
                }
            }
        }, 300, (30 * 20));
	}
}
