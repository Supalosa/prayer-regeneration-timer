package com.supalosa;

import lombok.Getter;
import lombok.Setter;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;

import java.awt.*;

public class PrayerRegenerationInfobox extends InfoBox {

    @Getter
    @Setter
    private int timer = -1;

    public PrayerRegenerationInfobox(Plugin plugin) {
        super(null, plugin);
    }

    @Override
    public String getText() {
        // count 12 -> 1 instead of 11 -> 0
        return Integer.toString(timer + 1);
    }

    @Override
    public Color getTextColor() {
        return timer == 11 ? Color.GREEN : Color.WHITE;
    }

    @Override
    public String getTooltip() {
        return "Prayer Regeneration Potion";
    }
}
