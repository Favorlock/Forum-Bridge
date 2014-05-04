package com.gmail.favorlock.forumbridge;

import com.gmail.favorlock.forumbridge.listeners.*;
import lombok.Getter;

public enum ForumListeners {

    IPB("IPB", com.gmail.favorlock.forumbridge.listeners.IPB.class),
    MYBB("myBB", myBB.class),
    PHPBB3("PHPBB3", com.gmail.favorlock.forumbridge.listeners.PHPBB3.class),
    PUNBB("PUNBB", PunBB.class),
    SMF("SMF", com.gmail.favorlock.forumbridge.listeners.SMF.class),
    VANILLA("Vanilla", Vanilla.class),
    WBB("WBB", WBB.class),
    WBB4("WBB4", WBB4.class),
    XENFORO("XenForo", XenForo.class);

    @Getter private String name;
    @Getter private Class<?> listener;

    ForumListeners(String name, Class<?> listener) {
        this.name = name;
        this.listener = listener;
    }

}
