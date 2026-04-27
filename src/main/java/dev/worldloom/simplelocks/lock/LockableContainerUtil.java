package dev.worldloom.simplelocks.lock;

import dev.worldloom.simplelocks.config.SimpleLocksConfig;
import java.util.function.Supplier;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.TrappedChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class LockableContainerUtil {
    public static final String LOCKED_KEY = "simplelocks_locked";
    public static final String OWNER_UUID_KEY = "simplelocks_owner_uuid";
    public static final String OWNER_NAME_KEY = "simplelocks_owner_name";

    private LockableContainerUtil() {
    }

    public static boolean isLockableContainer(BlockState state) {
        return isLockableContainerBlock(state.getBlock());
    }

    static boolean isLockableContainerBlock(Block block) {
        // Item-form lock persistence is out of scope for this release.
        return isLockableContainerType(
                block instanceof ChestBlock,
                block instanceof TrappedChestBlock,
                block instanceof BarrelBlock,
                block instanceof ShulkerBoxBlock,
                SimpleLocksConfig.LOCK_CHESTS.get(),
                SimpleLocksConfig.LOCK_TRAPPED_CHESTS.get(),
                SimpleLocksConfig.LOCK_BARRELS.get(),
                SimpleLocksConfig.LOCK_SHULKER_BOXES.get()
        );
    }

    static boolean isLockableContainerType(
            boolean chest,
            boolean trappedChest,
            boolean barrel,
            boolean shulkerBox,
            boolean lockChests,
            boolean lockTrappedChests,
            boolean lockBarrels,
            boolean lockShulkerBoxes
    ) {
        return (chest && lockChests)
                || (trappedChest && lockTrappedChests)
                || (barrel && lockBarrels)
                || (shulkerBox && lockShulkerBoxes);
    }

    public static boolean isStorageKey(ItemStack stack) {
        return isStorageKey(
                stack.isEmpty(),
                stack::hasCustomHoverName,
                () -> stack.is(SimpleLocksConfig.keyItem()),
                () -> stack.getHoverName().getString(),
                SimpleLocksConfig.KEY_NAME.get()
        );
    }

    static boolean isStorageKey(
            boolean empty,
            Supplier<Boolean> hasCustomHoverName,
            Supplier<Boolean> matchesKeyItem,
            Supplier<String> hoverName,
            String configuredKeyName
    ) {
        if (empty || !matchesKeyItem.get() || !hasCustomHoverName.get()) {
            return false;
        }

        return configuredKeyName.equals(hoverName.get());
    }

    public static boolean isLocked(BlockEntity be) {
        return isLocked(be.getPersistentData());
    }

    @Nullable
    public static UUID getOwnerUuid(BlockEntity be) {
        return getOwnerUuid(be.getPersistentData());
    }

    public static boolean canOpen(BlockEntity be, Player player) {
        return canOpen(be.getPersistentData(), player.getUUID());
    }

    public static void setLocked(BlockEntity be, Player player) {
        if (isClientSide(be)) {
            return;
        }

        CompoundTag tag = be.getPersistentData();
        tag.putBoolean(LOCKED_KEY, true);
        tag.putUUID(OWNER_UUID_KEY, player.getUUID());
        tag.putString(OWNER_NAME_KEY, player.getScoreboardName());
        markChangedAndUpdate(be);
    }

    public static void clearLock(BlockEntity be) {
        if (isClientSide(be)) {
            return;
        }

        CompoundTag tag = be.getPersistentData();
        tag.remove(LOCKED_KEY);
        tag.remove(OWNER_UUID_KEY);
        tag.remove(OWNER_NAME_KEY);
        markChangedAndUpdate(be);
    }

    static boolean isLocked(CompoundTag tag) {
        return tag.getBoolean(LOCKED_KEY);
    }

    @Nullable
    static UUID getOwnerUuid(CompoundTag tag) {
        return tag.hasUUID(OWNER_UUID_KEY) ? tag.getUUID(OWNER_UUID_KEY) : null;
    }

    static boolean canOpen(CompoundTag tag, UUID playerUuid) {
        if (!isLocked(tag)) {
            return true;
        }

        UUID ownerUuid = getOwnerUuid(tag);
        return playerUuid.equals(ownerUuid);
    }

    public static boolean canBypassLock(Player player) {
        return canBypassLock(
                player.getAbilities().instabuild,
                player.hasPermissions(2),
                SimpleLocksConfig.ALLOW_CREATIVE_BYPASS.get(),
                SimpleLocksConfig.ALLOW_OPS_BYPASS.get()
        );
    }

    static boolean canBypassLock(boolean creative, boolean operator, boolean allowCreativeBypass, boolean allowOpsBypass) {
        return (creative && allowCreativeBypass) || (operator && allowOpsBypass);
    }

    public static boolean shouldConsumeKey(Player player, boolean consumeEnabled) {
        return shouldConsumeKey(consumeEnabled, player.getAbilities().instabuild);
    }

    static boolean shouldConsumeKey(boolean consumeEnabled, boolean creative) {
        return consumeEnabled && !creative;
    }

    static void setLocked(CompoundTag tag, UUID ownerUuid, String ownerName) {
        tag.putBoolean(LOCKED_KEY, true);
        tag.putUUID(OWNER_UUID_KEY, ownerUuid);
        tag.putString(OWNER_NAME_KEY, ownerName);
    }

    static void clearLock(CompoundTag tag) {
        tag.remove(LOCKED_KEY);
        tag.remove(OWNER_UUID_KEY);
        tag.remove(OWNER_NAME_KEY);
    }

    private static void markChangedAndUpdate(BlockEntity be) {
        be.setChanged();

        Level level = be.getLevel();
        if (level == null) {
            return;
        }

        BlockPos pos = be.getBlockPos();
        BlockState state = be.getBlockState();
        level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
    }

    private static boolean isClientSide(BlockEntity be) {
        Level level = be.getLevel();
        return level != null && level.isClientSide();
    }
}
