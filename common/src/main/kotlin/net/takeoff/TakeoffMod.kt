package net.takeoff
import org.valkyrienskies.core.impl.config.VSConfigClass


object TakeoffMod {
    const val MOD_ID = "vs_takeoff"
    private var isTick1 = false

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
    }
}