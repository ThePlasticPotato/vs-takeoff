package net.takeoff.block

import com.mojang.math.Vector3f
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.BlockParticleOption
import net.minecraft.core.particles.DustParticleOptions
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.material.Material
import net.minecraft.world.phys.Vec3
import net.takeoff.ship.TakeoffShipControl
import net.takeoff.ship.TakeoffWings
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.util.toJOMLD
import java.util.*


class FartBlock : HorizontalDirectionalBlock(Properties.of(Material.BAMBOO)) {

    init {
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING)
    }

    override fun getRenderShape(blockState: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        super.onPlace(state, level, pos, oldState, isMoving)

        if (level.isClientSide) return
        level as ServerLevel

        val ship = level.getShipObjectManagingPos(pos) ?: level.getShipManagingPos(pos) ?: return
        TakeoffShipControl.getOrCreate(ship).addFarter(pos, state.getValue(FACING))
    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        super.onRemove(state, level, pos, newState, isMoving)

        if (level.isClientSide) return
        level as ServerLevel

        level.getShipManagingPos(pos)?.getAttachment<TakeoffShipControl>()?.removeFarter(pos, state.getValue(FACING))
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState {
        return defaultBlockState()
            .setValue(FACING, ctx.horizontalDirection.opposite)
    }

    override fun animateTick(state: BlockState, level: Level, pos: BlockPos, random: Random) {
        super.animateTick(state, level, pos, random)
        val dir = state.getValue(FACING)

        val x = pos.x.toDouble() + 0.5;
        val y = pos.y.toDouble() + 0.5;
        val z = pos.z.toDouble() + 0.5;
        val speedX = dir.stepX * 0.69
        val speedY = dir.stepY * 0.69
        val speedZ = dir.stepZ * 0.69

        for (i in 0..16) {
            val x2 = x + random.nextDouble() * 0.2 - 0.1
            val y2 = y + random.nextDouble() * 0.2 - 0.1
            val z2 = z + random.nextDouble() * 0.2 - 0.1
            level.addParticle(ParticleTypes.CLOUD, x2, y2, z2, speedX, speedY, speedZ)
        }

        level.addParticle(ParticleTypes.CRIT, x, y, z, speedX, speedY, speedZ)
    }
}