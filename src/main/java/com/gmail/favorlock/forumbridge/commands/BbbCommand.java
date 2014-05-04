package com.gmail.favorlock.forumbridge.commands;

import com.gmail.favorlock.forumbridge.ForumBridge;

public class BbbCommand extends BaseCommand {

    public BbbCommand() {
        this.command.add("bbb");
        this.commandOnly = false;
        this.helpDescription = "Shows com.favorlock.ForumBridge help";
        this.senderMustBePlayer = false;
    }

    public void perform() {
        for (BaseCommand ForumBridgeCommand : ForumBridge.p.commands) {
            if (ForumBridgeCommand.hasPermission(sender)) {
                sendMessage(ForumBridgeCommand.getUseageTemplate(true));
            }
        }
    }
}
