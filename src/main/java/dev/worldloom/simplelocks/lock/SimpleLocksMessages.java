package dev.worldloom.simplelocks.lock;

import net.minecraft.network.chat.Component;

public final class SimpleLocksMessages {
    public static final String LOCKED = "message.simplelocks.locked";
    public static final String UNLOCKED = "message.simplelocks.unlocked";
    public static final String NOT_OWNER = "message.simplelocks.not_owner";
    public static final String ALREADY_LOCKED = "message.simplelocks.already_locked";
    public static final String INVALID_LOCK = "message.simplelocks.invalid_lock";
    public static final String INVALID_KEY = "message.simplelocks.invalid_key";

    private SimpleLocksMessages() {
    }

    public static Component locked() {
        return Component.translatable(LOCKED);
    }

    public static Component unlocked() {
        return Component.translatable(UNLOCKED);
    }

    public static Component notOwner() {
        return Component.translatable(NOT_OWNER);
    }
}
