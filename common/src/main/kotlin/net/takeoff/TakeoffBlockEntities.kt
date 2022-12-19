package net.takeoff

import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.util.datafix.fixes.References
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.takeoff.blockentity.FartBlockEntity
import net.takeoff.registry.DeferredRegister
import net.takeoff.registry.RegistrySupplier

@Suppress("unused")
object TakeoffBlockEntities {
    private val BLOCKENTITIES = DeferredRegister.create(TakeoffMod.MOD_ID, Registry.BLOCK_ENTITY_TYPE_REGISTRY)

//    val SHIP_HELM = setOf(
//        TakeoffBlocks.OAK_SHIP_HELM,
//        TakeoffBlocks.SPRUCE_SHIP_HELM,
//        TakeoffBlocks.BIRCH_SHIP_HELM,
//        TakeoffBlocks.JUNGLE_SHIP_HELM,
//        TakeoffBlocks.ACACIA_SHIP_HELM,
//        TakeoffBlocks.DARK_OAK_SHIP_HELM,
//        TakeoffBlocks.CRIMSON_SHIP_HELM,
//        TakeoffBlocks.WARPED_SHIP_HELM
//    ) withBE ::ShipHelmBlockEntity byName "ship_helm"
//
//    val ENGINE = TakeoffBlocks.ENGINE withBE ::EngineBlockEntity byName "engine"

    val FARTER = TakeoffBlocks.FARTER withBE ::FartBlockEntity byName "farter"

    fun register() {
        BLOCKENTITIES.applyAll()
    }

    private infix fun <T : BlockEntity> Set<RegistrySupplier<out Block>>.withBE(blockEntity: (BlockPos, BlockState) -> T) =
        Pair(this, blockEntity)

    private infix fun <T : BlockEntity> RegistrySupplier<out Block>.withBE(blockEntity: (BlockPos, BlockState) -> T) =
        Pair(setOf(this), blockEntity)

    private infix fun <T : BlockEntity> Block.withBE(blockEntity: (BlockPos, BlockState) -> T) = Pair(this, blockEntity)
    private infix fun <T : BlockEntity> Pair<Set<RegistrySupplier<out Block>>, (BlockPos, BlockState) -> T>.byName(name: String): RegistrySupplier<BlockEntityType<T>> =
        BLOCKENTITIES.register(name) {
            val type = Util.fetchChoiceType(References.BLOCK_ENTITY, name)

            BlockEntityType.Builder.of(
                this.second,
                *this.first.map { it.get() }.toTypedArray()
            ).build(type)
        }
}
