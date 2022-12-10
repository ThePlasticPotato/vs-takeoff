package net.takeoff.fabric.services;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;
import net.takeoff.fabric.DeferredRegisterImpl;
import net.takeoff.registry.DeferredRegister;
import net.takeoff.services.DeferredRegisterBackend;

public class DeferredRegisterBackendFabric implements DeferredRegisterBackend {

    @NotNull
    @Override
    public <T> DeferredRegister<T> makeDeferredRegister(@NotNull String id, @NotNull ResourceKey<Registry<T>> registry) {
        return new DeferredRegisterImpl<>(id, registry);
    }
}
