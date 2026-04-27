package dev.worldloom.simplelocks.lock;

import dev.worldloom.simplelocks.SimpleLocks;
import dev.worldloom.simplelocks.config.SimpleLocksConfig;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SimpleLocks.MOD_ID)
public final class LockableContainerInteractionHandler {
    private LockableContainerInteractionHandler() {
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();

        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        Level level = event.getLevel();
        if (level.isClientSide()) {
            return;
        }

        BlockState clickedState = level.getBlockState(event.getPos());
        if (!LockableContainerUtil.isLockableContainer(clickedState)) {
            return;
        }

        BlockEntity blockEntity = level.getBlockEntity(event.getPos());
        if (blockEntity == null) {
            return;
        }

        if (LockableContainerUtil.isLocked(blockEntity)) {
            if (LockableContainerUtil.canOpen(blockEntity, player)) {
                if (player.isShiftKeyDown() && LockableContainerUtil.isStorageKey(event.getItemStack())) {
                    LockableContainerUtil.clearLock(blockEntity);
                    consumeKeyIfNeeded(event.getItemStack(), player, SimpleLocksConfig.CONSUME_KEY_ON_UNLOCK.get());
                    player.displayClientMessage(SimpleLocksMessages.unlocked(), true);
                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                }

                return;
            }

            if (LockableContainerUtil.canBypassLock(player)) {
                return;
            }

            player.displayClientMessage(SimpleLocksMessages.notOwner(), false);
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
            return;
        }

        ItemStack heldStack = event.getItemStack();
        if (!LockableContainerUtil.isStorageKey(heldStack)) {
            return;
        }

        LockableContainerUtil.setLocked(blockEntity, player);
        consumeKeyIfNeeded(heldStack, player, SimpleLocksConfig.CONSUME_KEY_ON_LOCK.get());
        player.displayClientMessage(SimpleLocksMessages.locked(), true);
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    private static void consumeKeyIfNeeded(ItemStack stack, Player player, boolean consumeEnabled) {
        if (LockableContainerUtil.shouldConsumeKey(player, consumeEnabled)) {
            stack.shrink(1);
        }
    }
}
