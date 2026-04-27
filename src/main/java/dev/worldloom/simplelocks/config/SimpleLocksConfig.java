package dev.worldloom.simplelocks.config;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

public final class SimpleLocksConfig {
    private static final Logger LOGGER = LogUtils.getLogger();
    static final String DEFAULT_KEY_ITEM = "minecraft:tripwire_hook";
    static final String DEFAULT_KEY_NAME = "Storage Key";
    private static String lastWarnedKeyItem = "";

    public static final ForgeConfigSpec SERVER_SPEC;

    public static final ForgeConfigSpec.ConfigValue<String> KEY_ITEM;
    public static final ForgeConfigSpec.ConfigValue<String> KEY_NAME;
    public static final ForgeConfigSpec.BooleanValue ALLOW_CREATIVE_BYPASS;
    public static final ForgeConfigSpec.BooleanValue ALLOW_OPS_BYPASS;
    public static final ForgeConfigSpec.BooleanValue LOCK_CHESTS;
    public static final ForgeConfigSpec.BooleanValue LOCK_TRAPPED_CHESTS;
    public static final ForgeConfigSpec.BooleanValue LOCK_BARRELS;
    public static final ForgeConfigSpec.BooleanValue LOCK_SHULKER_BOXES;
    public static final ForgeConfigSpec.BooleanValue CONSUME_KEY_ON_LOCK;
    public static final ForgeConfigSpec.BooleanValue CONSUME_KEY_ON_UNLOCK;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        KEY_ITEM = builder
                .comment("Item id used as the SimpleLocks key. Invalid ids fall back to minecraft:tripwire_hook.")
                .define("keyItem", DEFAULT_KEY_ITEM);
        KEY_NAME = builder
                .comment("Exact custom hover name required on the key item.")
                .define("keyName", DEFAULT_KEY_NAME);
        ALLOW_CREATIVE_BYPASS = builder
                .comment("Allows creative-mode players to open locked containers they do not own.")
                .define("allowCreativeBypass", true);
        ALLOW_OPS_BYPASS = builder
                .comment("Allows operators to open locked containers they do not own.")
                .define("allowOpsBypass", true);
        LOCK_CHESTS = builder.define("lockChests", true);
        LOCK_TRAPPED_CHESTS = builder.define("lockTrappedChests", true);
        LOCK_BARRELS = builder.define("lockBarrels", true);
        LOCK_SHULKER_BOXES = builder.define("lockShulkerBoxes", false);
        CONSUME_KEY_ON_LOCK = builder.define("consumeKeyOnLock", false);
        CONSUME_KEY_ON_UNLOCK = builder.define("consumeKeyOnUnlock", false);

        SERVER_SPEC = builder.build();
    }

    private SimpleLocksConfig() {
    }

    public static Item keyItem() {
        ResourceLocation keyItemId = keyItemIdOrFallback(KEY_ITEM.get());

        Item configuredItem = ForgeRegistries.ITEMS.getValue(keyItemId);
        if (configuredItem == null || configuredItem == Items.AIR) {
            warnInvalidKeyItemOnce(KEY_ITEM.get(), "Unknown");
            return Items.TRIPWIRE_HOOK;
        }

        return configuredItem;
    }

    static ResourceLocation keyItemIdOrFallback(String configuredKeyItem) {
        ResourceLocation keyItemId = ResourceLocation.tryParse(configuredKeyItem);
        if (keyItemId == null) {
            warnInvalidKeyItemOnce(configuredKeyItem, "Invalid");
            return ResourceLocation.tryParse(DEFAULT_KEY_ITEM);
        }

        return keyItemId;
    }

    private static void warnInvalidKeyItemOnce(String configuredKeyItem, String reason) {
        if (configuredKeyItem.equals(lastWarnedKeyItem)) {
            return;
        }

        lastWarnedKeyItem = configuredKeyItem;
        LOGGER.warn("{} SimpleLocks keyItem '{}'; falling back to {}.", reason, configuredKeyItem, DEFAULT_KEY_ITEM);
    }
}
