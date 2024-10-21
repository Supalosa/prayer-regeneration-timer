package com.supalosa;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("prayerregenerationhelper")
public interface PrayerRegenerationHelperConfig extends Config {
    @ConfigSection(name = "Notifications",
            description = "Configuration for notifications",
            position = 10)
    String notificationSection = "notificationSection";

    @ConfigSection(name = "Timers",
            description = "Configuration for timers",
            position = 20)
    String timerSection = "timerSection";

    @ConfigItem(
            keyName = "reminderEnabled",
            name = "Enable reminder",
            description = "Show an overlay reminding you to sip a Prayer Regeneration Potion",
            section = notificationSection
    )
    default boolean reminderEnabled() {
        return true;
    }

    @ConfigItem(
            keyName = "reminderText",
            name = "Reminder Text",
            description = "The text to show when a Prayer Regeneration Potion needs to be sipped",
            section = notificationSection
    )
    default String reminderText() {
        return "You need to sip a Prayer Regeneration Potion";
    }

    @Alpha
    @ConfigItem(
            keyName = "reminderColor",
            name = "Reminder Color",
            description = "The color to use for the infobox.",
            section = notificationSection
    )
    default Color reminderColor() {
        return new Color(255, 0, 0, 150);
    }

    @ConfigItem(
            keyName = "timerEnabled",
            name = "Enable regeneration timer",
            description = "Show an infobox with a timer ticking down to the next prayer point",
            section = timerSection
    )
    default boolean timerEnabled() {
        return false;
    }
}
