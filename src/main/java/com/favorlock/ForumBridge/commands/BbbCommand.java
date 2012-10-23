package com.favorlock.ForumBridge.commands;

import com.favorlock.ForumBridge.OKB;

public class BbbCommand extends BaseCommand
{

    public BbbCommand()
    {
        this.command.add("bbb");
        this.commandOnly = false;
        this.helpDescription = "Shows OKB3 help";
        this.senderMustBePlayer = false;
    }
    
    public void perform()
    {
        for (BaseCommand OKBCommand : OKB.p.commands)
        {
            if (OKBCommand.hasPermission(sender))
            {
                sendMessage(OKBCommand.getUseageTemplate(true));
            }
        }
    }
}
