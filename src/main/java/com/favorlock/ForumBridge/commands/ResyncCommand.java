package com.favorlock.ForumBridge.commands;

import org.bukkit.ChatColor;

import com.favorlock.ForumBridge.ForumBridgeFunctions;


public class ResyncCommand extends BaseCommand
{
    public ResyncCommand()
    {
        this.command.add("resync");
        this.helpDescription = "Resync with the forum";
    }

    public void perform()
    {
        if (ForumBridgeFunctions.syncPlayer(player.getName(), player.getWorld().getName()))
        {
            sendMessage("You are now synced!");
        }
        else
        {
            sendMessage(ChatColor.RED + "You need to setup your sync first! Type /sync for more information.");
        }
    }
}
