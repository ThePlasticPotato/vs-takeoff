package net.takeoff.forge.services;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;
import net.takeoff.forge.DeferredRegisterImpl;
import net.takeoff.registry.DeferredRegister;
import net.takeoff.services.DeferredRegisterBackend;

public class DeferredRegisterBackendForge implements DeferredRegisterBackend {

    @NotNull
    @Override
    public <T> DeferredRegister<T> makeDeferredRegister(@NotNull String id, @NotNull ResourceKey<Registry<T>> registry) {
        return new DeferredRegisterImpl(id, registry);
    }
}
