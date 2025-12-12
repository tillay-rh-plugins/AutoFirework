package tilley.rocket;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.Items;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.accessors.entity.IMixinFireworkRocketEntity;
import org.rusherhack.client.api.events.client.EventUpdate;
import org.rusherhack.client.api.feature.module.ModuleCategory;
import org.rusherhack.client.api.feature.module.ToggleableModule;
import org.rusherhack.client.api.utils.InventoryUtils;
import org.rusherhack.client.api.utils.WorldUtils;
import org.rusherhack.core.event.subscribe.Subscribe;
import org.rusherhack.core.setting.BooleanSetting;
import org.rusherhack.core.setting.NumberSetting;

public class AutoFireworkModule extends ToggleableModule {
    private final BooleanSetting underSpeed = new BooleanSetting("UnderSpeed", "Deploy rockets when player speed drops below a value", true);
    private final NumberSetting<Float> minSpeed = new NumberSetting<>("MinSpeed", "Value to deploy rocket at", 20f, 1f, 34f).incremental(1f);
    private final BooleanSetting underHeight = new BooleanSetting("UnderHeight", "Deploy rockets when player height (y coord) drops below a value", false);
    private final NumberSetting<Float> minHeight = new NumberSetting<>("MinSpeed", "Y coordinate to deploy rocket when player drops below", 120f, 1f, 340f).incremental(1f);

    private boolean waitingForFirework = false;

    public AutoFireworkModule() {
        super("AutoFirework", "Automatically redeploy fireworks when player drops below a certain speed or height", ModuleCategory.MOVEMENT);
        this.registerSettings(underSpeed, underHeight);
        underSpeed.addSubSettings(minSpeed);
        underHeight.addSubSettings(minHeight);
    }

    @Subscribe
    private void onUpdate(EventUpdate event) {
        if (mc.player == null || mc.gameMode == null) return;
        double speed = mc.player.getDeltaMovement().length() * RusherHackAPI.getServerState().getTPS();

        if (mc.player.isFallFlying() &&
           ((speed < minSpeed.getValue()) && underSpeed.getValue() ||
             mc.player.getY() < minHeight.getValue() && underHeight.getValue())) {

            if (waitingForFirework) {
                if (hasActiveFirework()) waitingForFirework = false;
                return;
            }

            if (!hasActiveFirework()) {
                int oldSlot = InventoryUtils.getSelectedHotbarSlot();
                int slot = InventoryUtils.findItemHotbar(Items.FIREWORK_ROCKET);
                if (slot == -1) return;
                InventoryUtils.setHotbarSlot(slot);
                mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
                InventoryUtils.setHotbarSlot(oldSlot);
                waitingForFirework = true;
            }
        } else waitingForFirework = false;
    }

    private boolean hasActiveFirework() {
        for (Entity entity : WorldUtils.getEntities()) {
            if (entity instanceof FireworkRocketEntity firework) {
                final IMixinFireworkRocketEntity fireworkAccessor = (IMixinFireworkRocketEntity) firework;
                if(fireworkAccessor.getAttachedEntity() != null && fireworkAccessor.getAttachedEntity().equals(mc.player)) {
                    return true;
                }
            }
        }
        return false;
    }

}
