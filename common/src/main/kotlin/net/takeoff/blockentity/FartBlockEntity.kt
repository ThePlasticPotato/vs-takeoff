package net.takeoff.blockentity

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.block.entity.BlockEntity
import net.takeoff.TakeoffBlockEntities
import net.takeoff.gui.farter.FarterScreenMenu
import org.valkyrienskies.core.impl.api.ServerShipProvider

class FartBlockEntity : BlockEntity(TakeoffBlockEntities.FARTER.get(), pos, state), MenuProvider {

    var thrust: Float = 0f

    override fun createMenu(i: Int, inventory: Inventory, player: Player): AbstractContainerMenu? {
        return FarterScreenMenu(i, inventory, this)
    }

    override fun getDisplayName(): Component {
        return TranslatableComponent("gui.vs_takeoff.farter")
    }
}