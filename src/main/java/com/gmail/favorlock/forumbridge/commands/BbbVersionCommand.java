package com.gmail.favorlock.forumbridge.commands;

import com.gmail.favorlock.forumbridge.ForumBridge;
import org.bukkit.ChatColor;

public class BbbVersionCommand extends BaseCommand {

    public BbbVersionCommand() {
        this.command.add("bbbversion");
        this.helpDescription = "Show the version of com.favorlock.ForumBridge";
        this.senderMustBePlayer = false;
    }

    public void perform() {
        sendMessage(colorizeText("--Bulletin Board Bridge by " +
                ForumBridge.getInstance().getDescription().getAuthors().get(0) +
                ". Original Author kalmanolah--", ChatColor.AQUA));
        sendMessage("This server is using " +
                colorizeText(ForumBridge.getInstance().getDescription().getName(), ChatColor.GREEN) + " version " +
                colorizeText(ForumBridge.getInstance().getDescription().getVersion(), ChatColor.GREEN) + ".");

    }
}
