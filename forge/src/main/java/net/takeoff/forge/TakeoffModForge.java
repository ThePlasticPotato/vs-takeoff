package net.takeoff.forge;

import net.takeoff.TakeoffMod;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.valkyrienskies.core.impl.config.VSConfigClass;
import net.takeoff.TakeoffConfig;
import org.valkyrienskies.mod.compat.clothconfig.VSClothConfig;

@Mod(TakeoffMod.MOD_ID)
public class TakeoffModForge {
    boolean happendClientSetup = false;
    static IEventBus MOD_BUS;

    public TakeoffModForge() {
        // Submit our event bus to let architectury register our content on the right time
        MOD_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        MOD_BUS.addListener(this::clientSetup);

        ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class,
                () -> new ConfigGuiHandler.ConfigGuiFactory((Minecraft client, Screen parent) ->
                        VSClothConfig.createConfigScreenFor(parent,
                                VSConfigClass.Companion.getRegisteredConfig(TakeoffConfig.class)))
        );

//        MOD_BUS.addListener(this::onModelRegistry);
        MOD_BUS.addListener(this::clientSetup);
//        MOD_BUS.addListener(this::entityRenderers);

        TakeoffMod.init();
    }

    void clientSetup(final FMLClientSetupEvent event) {
        if (happendClientSetup) return;
        happendClientSetup = true;

        TakeoffMod.initClient();

//        WheelModels.INSTANCE.setModelGetter(woodType -> ForgeModelBakery.instance().getBakedTopLevelModels()
//                .getOrDefault(
//                        new ResourceLocation(TakeoffMod.MOD_ID, "block/" + woodType.getResourceName() + "_ship_helm_wheel"),
//                        Minecraft.getInstance().getModelManager().getMissingModel()
//                ));
    }

//    void entityRenderers(final EntityRenderersEvent.RegisterRenderers event) {
//        event.registerBlockEntityRenderer(
//                TakeoffBlockEntities.INSTANCE.getSHIP_HELM().get(),
//                ShipHelmBlockEntityRenderer::new
//        );
//    }

//    void onModelRegistry(final ModelRegistryEvent event) {
//        for (WoodType woodType : WoodType.values()) {
//            ForgeModelBakery.addSpecialModel(new ResourceLocation(TakeoffMod.MOD_ID, "block/" + woodType.getResourceName() + "_ship_helm_wheel"));
//        }
//    }
}
