package net.takeoff

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.takeoff.registry.CreativeTabs
import net.takeoff.registry.DeferredRegister

@Suppress("unused")
object TakeoffItems {
    private val ITEMS = DeferredRegister.create(TakeoffMod.MOD_ID, Registry.ITEM_REGISTRY)
    val TAB: CreativeModeTab = CreativeTabs.create(
        ResourceLocation(
            TakeoffMod.MOD_ID,
            "takeoff_tab"
        )
    ) { ItemStack(TakeoffBlocks.BALLOON.get()) }

    fun register() {
        TakeoffBlocks.registerItems(ITEMS)
        ITEMS.applyAll()
    }

    private infix fun Item.byName(name: String) = ITEMS.register(name) { this }
}
