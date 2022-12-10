package net.takeoff.fabric;

import net.takeoff.TakeoffMod;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import org.valkyrienskies.core.impl.config.VSConfigClass;
import net.takeoff.TakeoffBlockEntities;
import net.takeoff.TakeoffConfig;
//import net.takeoff.block.WoodType;
//import net.takeoff.blockentity.renderer.ShipHelmBlockEntityRenderer;
//import net.takeoff.blockentity.renderer.WheelModels;
import org.valkyrienskies.mod.compat.clothconfig.VSClothConfig;
import org.valkyrienskies.mod.fabric.common.ValkyrienSkiesModFabric;

public class TakeoffModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // force VS2 to load before takeoff
        new ValkyrienSkiesModFabric().onInitialize();

        TakeoffMod.init();
    }

    @Environment(EnvType.CLIENT)
    public static class Client implements ClientModInitializer {

        @Override
        public void onInitializeClient() {
            TakeoffMod.initClient();
//            BlockEntityRendererRegistry.INSTANCE.register(
//                    TakeoffBlockEntities.INSTANCE.getSHIP_HELM().get(),
//                    ShipHelmBlockEntityRenderer::new
//            );

//            ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> {
//                for (WoodType woodType : WoodType.values()) {
//                    out.accept(new ResourceLocation(TakeoffMod.MOD_ID, "block/" + woodType.getResourceName() + "_ship_helm_wheel"));
//                }
//            });
//
//            WheelModels.INSTANCE.setModelGetter(woodType ->
//                    Minecraft.getInstance().getModelManager().getModel(
//                            new ModelResourceLocation(
//                                    new ResourceLocation(TakeoffMod.MOD_ID, "ship_helm_wheel"),
//                                    "wood=" + woodType.getResourceName()
//                            )));
        }
    }

    public static class ModMenu implements ModMenuApi {
        @Override
        public ConfigScreenFactory<?> getModConfigScreenFactory() {
            return (parent) -> VSClothConfig.createConfigScreenFor(
                    parent,
                    VSConfigClass.Companion.getRegisteredConfig(TakeoffConfig.class)
            );
        }
    }
}
