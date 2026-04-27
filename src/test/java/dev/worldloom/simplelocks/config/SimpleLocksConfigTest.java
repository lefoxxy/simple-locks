package dev.worldloom.simplelocks.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

class SimpleLocksConfigTest {
    @Test
    void serverConfigSpecExistsForGeneration() {
        assertNotNull(SimpleLocksConfig.SERVER_SPEC);
    }

    @Test
    void defaultsMatchRequestedServerConfig() {
        assertEquals("minecraft:tripwire_hook", SimpleLocksConfig.KEY_ITEM.getDefault());
        assertEquals("Storage Key", SimpleLocksConfig.KEY_NAME.getDefault());
        assertTrue(SimpleLocksConfig.ALLOW_CREATIVE_BYPASS.getDefault());
        assertTrue(SimpleLocksConfig.ALLOW_OPS_BYPASS.getDefault());
        assertTrue(SimpleLocksConfig.LOCK_CHESTS.getDefault());
        assertTrue(SimpleLocksConfig.LOCK_TRAPPED_CHESTS.getDefault());
        assertTrue(SimpleLocksConfig.LOCK_BARRELS.getDefault());
        assertFalse(SimpleLocksConfig.LOCK_SHULKER_BOXES.getDefault());
        assertFalse(SimpleLocksConfig.CONSUME_KEY_ON_LOCK.getDefault());
        assertFalse(SimpleLocksConfig.CONSUME_KEY_ON_UNLOCK.getDefault());
    }

    @Test
    void configuredKeyItemCanUseIronIngot() {
        assertEquals(ResourceLocation.tryParse("minecraft:iron_ingot"), SimpleLocksConfig.keyItemIdOrFallback("minecraft:iron_ingot"));
    }

    @Test
    void invalidKeyItemFallsBackToTripwireHook() {
        assertEquals(ResourceLocation.tryParse("minecraft:tripwire_hook"), SimpleLocksConfig.keyItemIdOrFallback("not a valid id"));
    }
}
