package net.takeoff.services

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.takeoff.registry.DeferredRegister

interface DeferredRegisterBackend {
    fun <T> makeDeferredRegister(id: String, registry: ResourceKey<Registry<T>>): DeferredRegister<T>
}