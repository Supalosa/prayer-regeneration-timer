package com.supalosa;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class PrayerRegenerationHelperTest {
    public static void main(String[] args) throws Exception {
        ExternalPluginManager.loadBuiltin(PrayerRegenerationHelper.class);
        RuneLite.main(args);
    }
}