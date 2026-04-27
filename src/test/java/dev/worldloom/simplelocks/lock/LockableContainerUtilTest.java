package dev.worldloom.simplelocks.lock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import org.junit.jupiter.api.Test;

class LockableContainerUtilTest {
    @Test
    void renamedTripwireHookDetectsStorageKey() {
        assertTrue(LockableContainerUtil.isStorageKey(false, () -> true, () -> true, () -> "Storage Key", "Storage Key"));
    }

    @Test
    void unrenamedTripwireHookIsNotStorageKey() {
        assertFalse(LockableContainerUtil.isStorageKey(false, () -> false, () -> true, () -> "Tripwire Hook", "Storage Key"));
    }

    @Test
    void emptyStackIsNotStorageKey() {
        assertFalse(LockableContainerUtil.isStorageKey(true, () -> true, () -> true, () -> "Storage Key", "Storage Key"));
    }

    @Test
    void renamedNonHookIsNotStorageKey() {
        assertFalse(LockableContainerUtil.isStorageKey(false, () -> true, () -> false, () -> "Storage Key", "Storage Key"));
    }

    @Test
    void onlyExactStorageKeyNameMatches() {
        assertFalse(LockableContainerUtil.isStorageKey(false, () -> true, () -> true, () -> "storage key", "Storage Key"));
    }

    @Test
    void configuredKeyNameIsRespected() {
        assertTrue(LockableContainerUtil.isStorageKey(false, () -> true, () -> true, () -> "Lock Key", "Lock Key"));
        assertFalse(LockableContainerUtil.isStorageKey(false, () -> true, () -> true, () -> "Storage Key", "Lock Key"));
    }

    @Test
    void chestTrappedChestAndBarrelAreLockable() {
        assertTrue(LockableContainerUtil.isLockableContainerType(true, false, false, false, true, true, true, false));
        assertTrue(LockableContainerUtil.isLockableContainerType(false, true, false, false, true, true, true, false));
        assertTrue(LockableContainerUtil.isLockableContainerType(false, false, true, false, true, true, true, false));
    }

    @Test
    void otherBlocksAreNotLockable() {
        assertFalse(LockableContainerUtil.isLockableContainerType(false, false, false, false, true, true, true, false));
    }

    @Test
    void disabledBarrelsAreNotLockable() {
        assertFalse(LockableContainerUtil.isLockableContainerType(false, false, true, false, true, true, false, false));
    }

    @Test
    void shulkerBoxesRequireConfig() {
        assertFalse(LockableContainerUtil.isLockableContainerType(false, false, false, true, true, true, true, false));
        assertTrue(LockableContainerUtil.isLockableContainerType(false, false, false, true, true, true, true, true));
    }

    @Test
    void lockShulkerBoxesFalseCannotLockShulker() {
        assertFalse(LockableContainerUtil.isLockableContainerType(false, false, false, true, true, true, true, false));
    }

    @Test
    void lockShulkerBoxesTrueCanLockShulker() {
        assertTrue(LockableContainerUtil.isLockableContainerType(false, false, false, true, true, true, true, true));
    }

    @Test
    void lockDataStoresOwnerUuidAndName() {
        CompoundTag tag = new CompoundTag();
        UUID ownerUuid = UUID.fromString("11111111-2222-3333-4444-555555555555");

        LockableContainerUtil.setLocked(tag, ownerUuid, "Tester");

        assertTrue(LockableContainerUtil.isLocked(tag));
        assertEquals(ownerUuid, LockableContainerUtil.getOwnerUuid(tag));
        assertEquals("Tester", tag.getString(LockableContainerUtil.OWNER_NAME_KEY));
    }

    @Test
    void clearLockRemovesPersistentLockData() {
        CompoundTag tag = new CompoundTag();
        LockableContainerUtil.setLocked(tag, UUID.fromString("11111111-2222-3333-4444-555555555555"), "Tester");

        LockableContainerUtil.clearLock(tag);

        assertFalse(LockableContainerUtil.isLocked(tag));
        assertNull(LockableContainerUtil.getOwnerUuid(tag));
        assertFalse(tag.contains(LockableContainerUtil.OWNER_NAME_KEY));
    }

    @Test
    void savedTagKeepsLockDataAfterReloadCopy() {
        CompoundTag saved = new CompoundTag();
        UUID ownerUuid = UUID.fromString("11111111-2222-3333-4444-555555555555");
        LockableContainerUtil.setLocked(saved, ownerUuid, "Tester");

        CompoundTag reloaded = saved.copy();

        assertTrue(LockableContainerUtil.isLocked(reloaded));
        assertEquals(ownerUuid, LockableContainerUtil.getOwnerUuid(reloaded));
        assertEquals("Tester", reloaded.getString(LockableContainerUtil.OWNER_NAME_KEY));
    }

    @Test
    void unlockedContainerCanBeOpenedByAnyPlayer() {
        CompoundTag tag = new CompoundTag();

        assertTrue(LockableContainerUtil.canOpen(tag, UUID.fromString("11111111-2222-3333-4444-555555555555")));
    }

    @Test
    void ownerCanOpenLockedContainer() {
        CompoundTag tag = new CompoundTag();
        UUID ownerUuid = UUID.fromString("11111111-2222-3333-4444-555555555555");
        LockableContainerUtil.setLocked(tag, ownerUuid, "PlayerA");

        assertTrue(LockableContainerUtil.canOpen(tag, ownerUuid));
    }

    @Test
    void nonOwnerCannotOpenLockedContainer() {
        CompoundTag tag = new CompoundTag();
        UUID ownerUuid = UUID.fromString("11111111-2222-3333-4444-555555555555");
        UUID otherUuid = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
        LockableContainerUtil.setLocked(tag, ownerUuid, "PlayerA");

        assertFalse(LockableContainerUtil.canOpen(tag, otherUuid));
    }

    @Test
    void missingOwnerUuidDeniesLockedContainerForNonBypassPlayer() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean(LockableContainerUtil.LOCKED_KEY, true);

        assertNull(LockableContainerUtil.getOwnerUuid(tag));
        assertFalse(LockableContainerUtil.canOpen(tag, UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee")));
    }

    @Test
    void malformedOwnerUuidDeniesWithoutCrashing() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean(LockableContainerUtil.LOCKED_KEY, true);
        tag.put(LockableContainerUtil.OWNER_UUID_KEY, StringTag.valueOf("not-a-uuid"));

        assertNull(LockableContainerUtil.getOwnerUuid(tag));
        assertFalse(LockableContainerUtil.canOpen(tag, UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee")));
    }

    @Test
    void reloadedLockStillAllowsOwnerAndRejectsNonOwner() {
        CompoundTag saved = new CompoundTag();
        UUID ownerUuid = UUID.fromString("11111111-2222-3333-4444-555555555555");
        UUID otherUuid = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
        LockableContainerUtil.setLocked(saved, ownerUuid, "PlayerA");

        CompoundTag reloaded = saved.copy();

        assertTrue(LockableContainerUtil.canOpen(reloaded, ownerUuid));
        assertFalse(LockableContainerUtil.canOpen(reloaded, otherUuid));
    }

    @Test
    void ownerUnlockFlowAllowsOtherPlayersAfterClear() {
        CompoundTag tag = new CompoundTag();
        UUID playerA = UUID.fromString("11111111-2222-3333-4444-555555555555");
        UUID playerB = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");

        LockableContainerUtil.setLocked(tag, playerA, "PlayerA");

        assertTrue(LockableContainerUtil.canOpen(tag, playerA));
        assertFalse(LockableContainerUtil.canOpen(tag, playerB));

        LockableContainerUtil.clearLock(tag);

        assertFalse(LockableContainerUtil.isLocked(tag));
        assertTrue(LockableContainerUtil.canOpen(tag, playerB));
    }

    @Test
    void shulkerOwnerOpenNonOwnerBlockAndOwnerUnlockUseSameLockDataRules() {
        assertTrue(LockableContainerUtil.isLockableContainerType(false, false, false, true, true, true, true, true));

        CompoundTag shulkerTag = new CompoundTag();
        UUID owner = UUID.fromString("11111111-2222-3333-4444-555555555555");
        UUID nonOwner = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");

        LockableContainerUtil.setLocked(shulkerTag, owner, "PlayerA");

        assertTrue(LockableContainerUtil.canOpen(shulkerTag, owner));
        assertFalse(LockableContainerUtil.canOpen(shulkerTag, nonOwner));

        LockableContainerUtil.clearLock(shulkerTag);

        assertFalse(LockableContainerUtil.isLocked(shulkerTag));
        assertTrue(LockableContainerUtil.canOpen(shulkerTag, nonOwner));
    }

    @Test
    void reloadedOwnerUnlockFlowAllowsOtherPlayersAfterClear() {
        CompoundTag saved = new CompoundTag();
        UUID playerA = UUID.fromString("11111111-2222-3333-4444-555555555555");
        UUID playerB = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
        LockableContainerUtil.setLocked(saved, playerA, "PlayerA");

        CompoundTag reloaded = saved.copy();

        assertTrue(LockableContainerUtil.canOpen(reloaded, playerA));
        assertFalse(LockableContainerUtil.canOpen(reloaded, playerB));

        LockableContainerUtil.clearLock(reloaded);

        assertTrue(LockableContainerUtil.canOpen(reloaded, playerB));
    }

    @Test
    void creativeAndOpBypassRespectConfigToggles() {
        assertTrue(LockableContainerUtil.canBypassLock(true, false, true, false));
        assertFalse(LockableContainerUtil.canBypassLock(true, false, false, false));
        assertTrue(LockableContainerUtil.canBypassLock(false, true, false, true));
        assertFalse(LockableContainerUtil.canBypassLock(false, true, false, false));
    }

    @Test
    void consumeKeyOnlyWhenEnabledAndNotCreative() {
        assertTrue(LockableContainerUtil.shouldConsumeKey(true, false));
        assertFalse(LockableContainerUtil.shouldConsumeKey(true, true));
        assertFalse(LockableContainerUtil.shouldConsumeKey(false, false));
    }
}
