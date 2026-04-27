package dev.worldloom.simplelocks;

import dev.worldloom.simplelocks.config.SimpleLocksConfig;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(SimpleLocks.MOD_ID)
public final class SimpleLocks {
    public static final String MOD_ID = "simplelocks";

    public SimpleLocks() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SimpleLocksConfig.SERVER_SPEC);
    }
}
