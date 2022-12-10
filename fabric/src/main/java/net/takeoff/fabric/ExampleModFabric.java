package net.takeoff.fabric;

import net.takeoff.TakeoffMod;
import net.fabricmc.api.ModInitializer;

public class ExampleModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        TakeoffMod.init();
    }
}
