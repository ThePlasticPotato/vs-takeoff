package net.takeoff.gui.farter

import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.takeoff.TakeoffScreens
import net.takeoff.blockentity.FartBlockEntity

class FarterScreenMenu(syncId: Int, playerInv: Inventory, val blockEntity: FartBlockEntity?) : AbstractContainerMenu(TakeoffScreens.FARTER.get(), syncId) {

    constructor(syncId: Int, playerInv: Inventory) : this(syncId, playerInv, null)

    val thrust = blockEntity?.thrust ?: 0f

    override fun stillValid(player: Player): Boolean = true

    override fun clickMenuButton(player: Player, id: Int): Boolean {
        return super.clickMenuButton(player, id)
    }

    companion object {
        val factory: (syncId: Int, playerInv: Inventory) -> FarterScreenMenu = ::FarterScreenMenu
    }
}