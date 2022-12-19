package net.takeoff.gui.farter

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.components.AbstractSliderButton
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextComponent
import net.minecraft.util.Mth
import net.takeoff.TakeoffMod
import net.takeoff.TakeoffMod.resource

class FarterSlider(x: Int, y: Int, text: Component, private val font: Font, value: Double) : AbstractSliderButton(x, y, 153, 23, text, value) {

    val resource = "textures/gui/fartslider.png".resource
    override fun updateMessage() {
        message = TextComponent.EMPTY
    }

    override fun applyValue() {
        return
    }

    override fun renderBg(poseStack: PoseStack?, minecraft: Minecraft?, mouseX: Int, mouseY: Int) {
        RenderSystem.setShaderTexture(0, resource)
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        val i = (if (this.isHoveredOrFocused) 2 else 1) * 20
        this.blit(poseStack, x + (value * (width - 8).toDouble()).toInt(), y, 0, 46 + i, 4, 20)
        this.blit(poseStack, x + (value * (width - 8).toDouble()).toInt() + 4, y, 196, 46 + i, 4, 20)
    }

    override fun onClick(mouseX: Double, mouseY: Double) {
        setValueFromMouse(mouseX)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        val bl = keyCode == 263
        if (bl || keyCode == 262) {
            val f = if (bl) -1.0f else 1.0f
            this.setValue(value + (f / (width - 8).toFloat()).toDouble())
        }
        return false
    }

    private fun setValueFromMouse(mouseX: Double) {
        this.setValue((mouseX - (x + 4).toDouble()) / (width - 8).toDouble())
    }

    private fun setValue(value: Double) {
        val d = this.value
        this.value = Mth.clamp(value, 0.0, 1.0)
        if (d != this.value) {
            applyValue()
        }
        updateMessage()
    }

    override fun onDrag(mouseX: Double, mouseY: Double, dragX: Double, dragY: Double) {
        setValueFromMouse(mouseX)
        super.onDrag(mouseX, mouseY, dragX, dragY)
    }

    companion object {
        private const val BUTTON_H_X = 0
        private const val BUTTON_H_Y = 166
        private const val BUTTON_P_X = 0
        private const val BUTTON_P_Y = 189
    }
}