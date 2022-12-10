package net.takeoff.registry

interface RegistrySupplier<T> {

    val name: String
    fun get(): T

}