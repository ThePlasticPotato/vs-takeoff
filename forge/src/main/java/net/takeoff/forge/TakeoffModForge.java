package net.takeoff.forge;

import me.shedaniel.architectury.platform.forge.EventBuses;
import net.takeoff.TakeoffMod;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(TakeoffMod.MOD_ID)
public class TakeoffModForge {
    public TakeoffModForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(TakeoffMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        Takeoff.init();
    }
}
