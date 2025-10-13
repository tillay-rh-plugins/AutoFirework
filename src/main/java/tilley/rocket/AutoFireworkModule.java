package tilley.rocket;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.events.client.EventUpdate;
import org.rusherhack.client.api.feature.module.ModuleCategory;
import org.rusherhack.client.api.feature.module.ToggleableModule;
import org.rusherhack.client.api.utils.InventoryUtils;
import org.rusherhack.core.event.subscribe.Subscribe;
import org.rusherhack.core.setting.NumberSetting;


public class AutoFireworkModule extends ToggleableModule {
    private final NumberSetting<Float> minSpeed = new NumberSetting<>("MinSpeed", "speed at which rocket is fired", 20f, 0.1f, 34f).incremental(1f);
    public AutoFireworkModule() {
        super("AutoFirework", "Automatically redeploy fireworks", ModuleCategory.MOVEMENT);
        this.registerSettings(minSpeed);
    }

    @Subscribe
    private void onUpdate(EventUpdate event) {
        if (mc.player == null || mc.gameMode == null) return;
        double speed = mc.player.getDeltaMovement().length() * RusherHackAPI.getServerState().getTPS();
        if (mc.player.isFallFlying() && speed < minSpeed.getValue()) {
            int oldSlot = InventoryUtils.getSelectedHotbarSlot();
            int slot = InventoryUtils.findItemHotbar(Items.FIREWORK_ROCKET);
            if (slot == -1) return;
            InventoryUtils.setHotbarSlot(slot);
            mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
            mc.player.swing(InteractionHand.MAIN_HAND);
            InventoryUtils.setHotbarSlot(oldSlot);
        }
    }
}
