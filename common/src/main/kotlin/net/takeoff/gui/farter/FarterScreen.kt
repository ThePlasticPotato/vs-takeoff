package net.takeoff.gui.farter

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

class FarterScreen(handler: FarterScreenMenu, playerInventory: Inventory, text: Component) :
    AbstractContainerScreen<FarterScreenMenu>(handler, playerInventory, text) {

        lateinit var thrustSlider: FarterSlider

        init {
            titleLabelX = 120
        }

        override fun init() {
            super.init()
            val x = (width - imageWidth) / 2
            val y = (height - imageHeight) / 2

            thrustSlider = addRenderableWidget(FarterSlider(x + BUTTON_1_X))
        }
}