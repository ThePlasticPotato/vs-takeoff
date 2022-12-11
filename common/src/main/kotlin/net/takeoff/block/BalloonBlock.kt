package net.takeoff.block


import com.mojang.math.Vector3d
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Position
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import org.valkyrienskies.core.api.ships.getAttachment
import net.takeoff.TakeoffConfig
import net.takeoff.ship.TakeoffShipControl
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.util.toJOMLD

class BalloonBlock(properties: Properties) : Block(properties) {
    override fun fallOn(level: Level, state: BlockState, blockPos: BlockPos, entity: Entity, f: Float) {
        entity.causeFallDamage(f, 0.2f, DamageSource.FALL)
    }
    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        super.onPlace(state, level, pos, oldState, isMoving)

        if (level.isClientSide) return
        level as ServerLevel
        val vecpos = pos.toJOMLD()
        vecpos.x = vecpos.x+0.5
        vecpos.y = vecpos.y+0.5
        vecpos.z = vecpos.z+0.5
        val ship = level.getShipObjectManagingPos(pos) ?: level.getShipManagingPos(pos) ?: return
        TakeoffShipControl.getOrCreate(ship).balloons += 1
        TakeoffShipControl.getOrCreate(ship).balloonpos.add(vecpos)
    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        super.onRemove(state, level, pos, newState, isMoving)

        if (level.isClientSide) return
        level as ServerLevel
        val vecpos = pos.toJOMLD()
        vecpos.x = vecpos.x+0.5
        vecpos.y = vecpos.y+0.5
        vecpos.z = vecpos.z+0.5
        level.getShipManagingPos(pos)?.getAttachment<TakeoffShipControl>()?.let {
            it.balloons -= 1
            it.balloonpos.remove(vecpos)
        }
    }
    override fun onProjectileHit(level: Level, state: BlockState, hit: BlockHitResult, projectile: Projectile) {
        if (level.isClientSide) return

        level.destroyBlock(hit.blockPos, false)
        Direction.values().forEach {
            val neighbor = hit.blockPos.relative(it)
            if (level.getBlockState(neighbor).block == this &&
                level.random.nextFloat() < TakeoffConfig.SERVER.popSideBalloonChance
            ) {
                level.destroyBlock(neighbor, false)
            }
        }
    }
}
