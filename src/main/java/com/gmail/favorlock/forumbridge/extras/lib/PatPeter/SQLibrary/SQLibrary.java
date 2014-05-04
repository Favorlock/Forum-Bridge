/*
 * Date Created: 2012-03-11 14:20
 * @author <a href="http://forums.bukkit.org/members/jertocvil.18552/">jertocvil</a>
 */
package com.gmail.favorlock.forumbridge.extras.lib.PatPeter.SQLibrary;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLibrary extends JavaPlugin {

    public static final Logger logger = Logger.getLogger("Minecraft");

    @Override
    public void onDisable() {
        SQLibrary.logger.log(Level.INFO, "SQLibrary stopped.");
    }

    @Override
    public void onEnable() {
        SQLibrary.logger.log(Level.INFO, "SQLibrary loaded.");
    }
} 