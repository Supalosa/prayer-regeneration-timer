package com.supalosa;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemID;
import net.runelite.api.Varbits;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

import javax.inject.Inject;
import java.util.Arrays;

@Slf4j
@PluginDescriptor(
        name = "Prayer Regeneration Helper"
)
public class PrayerRegenerationHelper extends Plugin {
    private static final int NO_REGEN = -1;

    private static final int PRAYER_REGENERATION_INTERVAL_TICKS = 12;

    private static final int[] PRAYER_REGENERATION_POTION_IDS = new int[]{
            ItemID.PRAYER_REGENERATION_POTION1,
            ItemID.PRAYER_REGENERATION_POTION2,
            ItemID.PRAYER_REGENERATION_POTION3,
            ItemID.PRAYER_REGENERATION_POTION4
    };

    @Inject
    private Client client;

    @Inject
    private PrayerRegenerationHelperConfig config;

    @Inject
    private InfoBoxManager infoBoxManager;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private ItemManager itemManager;

    private PrayerRegenerationInfobox infoBox;

    @Inject
    private PrayerRegenerationOverlay overlay;

    private int nextRegenTicks = NO_REGEN;

    private boolean hasRegenPotion = false;

    @Override
    protected void startUp() throws Exception {
        this.infoBox = new PrayerRegenerationInfobox(this);
        this.infoBoxManager.addInfoBox(this.infoBox);

        this.nextRegenTicks = NO_REGEN;

        this.infoBox.setImage(itemManager.getImage(ItemID.PRAYER_REGENERATION_POTION4));
    }

    @Override
    protected void shutDown() throws Exception {
        this.infoBoxManager.removeInfoBox(this.infoBox);
        this.overlayManager.remove(this.overlay);
    }

    @Subscribe
    protected void onGameTick(GameTick tick) {
        --this.nextRegenTicks;
        updateInfoBox();
    }

    @Subscribe
    protected void onVarbitChanged(VarbitChanged change) {
        if (change.getVarbitId() == Varbits.BUFF_PRAYER_REGENERATION) {
            int value = change.getValue();
            if (value > 0) {
                this.nextRegenTicks = PRAYER_REGENERATION_INTERVAL_TICKS;
            } else {
                this.nextRegenTicks = NO_REGEN;
                this.onPrayerRegenPotionExpired();
            }
        }
    }

    @Subscribe
    protected void onItemContainerChanged(ItemContainerChanged itemContainerChanged) {
        if (itemContainerChanged.getContainerId() != InventoryID.INVENTORY.getId()) {
            return;
        }
        this.hasRegenPotion = Arrays.stream(PRAYER_REGENERATION_POTION_IDS).anyMatch(itemContainerChanged.getItemContainer()::contains);
        this.updateOverlay();
    }

    @Subscribe
    protected void onConfigChanged(ConfigChanged event) {
        this.updateOverlay();
        this.updateInfoBox();
    }

    private boolean isRegenActive() {
        return this.nextRegenTicks >= 0;
    }

    private void onPrayerRegenPotionExpired() {
        this.updateOverlay();
    }

    private void updateOverlay() {
        if (this.isRegenActive() || !this.hasRegenPotion || !config.reminderEnabled()) {
            this.overlayManager.remove(this.overlay);
            return;
        }
        if (!this.overlayManager.anyMatch(o -> o instanceof PrayerRegenerationOverlay)) {
            this.overlayManager.add(this.overlay);
        }
    }

    private void updateInfoBox() {
        if (!this.isRegenActive() || !config.timerEnabled()) {
            this.infoBoxManager.removeInfoBox(this.infoBox);
            return;
        }
        this.infoBox.setTimer(this.nextRegenTicks);
        if (!this.infoBoxManager.getInfoBoxes().contains(this.infoBox)) {
            this.infoBoxManager.addInfoBox(this.infoBox);
        }
    }

    @Provides
    PrayerRegenerationHelperConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(PrayerRegenerationHelperConfig.class);
    }
}
