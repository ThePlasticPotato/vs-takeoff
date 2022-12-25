package net.takeoff
import net.minecraft.resources.ResourceLocation
import org.valkyrienskies.core.impl.config.VSConfigClass


object TakeoffMod {
    const val MOD_ID = "vs_takeoff"

    @JvmStatic
    fun init() {
        TakeoffBlocks.register()
        TakeoffBlockEntities.register()
        TakeoffItems.register()
        //TakeoffScreens.register()
        TakeoffEntities.register()
        TakeoffWeights.register()
        VSConfigClass.registerConfig("vs_takeoff", TakeoffConfig::class.java)
    }

    @JvmStatic
    fun initClient() {
        //TakeoffClientScreens.register()
        //
    }

    val String.resource: ResourceLocation get() = ResourceLocation(MOD_ID, this)
}