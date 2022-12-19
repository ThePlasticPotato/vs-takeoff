package net.takeoff

import net.minecraft.core.Registry
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.takeoff.gui.farter.FarterScreenMenu
import net.takeoff.registry.DeferredRegister

private typealias HFactory<T> = (syncId: Int, playerInv: Inventory) -> T

@Suppress("unused")
object TakeoffScreens {
    private val SCREENS = DeferredRegister.create(TakeoffMod.MOD_ID, Registry.MENU_REGISTRY)

    val FARTER = FarterScreenMenu.factory withName "farter"

    fun register() {
        SCREENS.applyAll()
    }

    private infix fun <T: AbstractContainerMenu> HFactory<T>.withName(name:String) = SCREENS.register(name) {MenuType(this)}
}