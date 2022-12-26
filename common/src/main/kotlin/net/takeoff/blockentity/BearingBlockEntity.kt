package net.takeoff.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.HitResult
import net.takeoff.TakeoffBlockEntities
import net.takeoff.TakeoffBlocks
import org.joml.AxisAngle4d
import org.joml.Quaterniond
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.apigame.constraints.VSAttachmentConstraint
import org.valkyrienskies.core.apigame.constraints.VSHingeOrientationConstraint
import org.valkyrienskies.core.impl.hooks.VSEvents
import org.valkyrienskies.mod.common.dimensionId
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.mod.common.util.toMinecraft
import org.valkyrienskies.mod.common.world.clipIncludeShips
import org.valkyrienskies.physics_api.ConstraintId
import kotlin.math.roundToInt

class BearingBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(TakeoffBlockEntities.BEARING.get(), pos, state) {
    var isBase = true
        private set
    private var otherPos: BlockPos? = null
    private var attachConstraintId: ConstraintId? = null
    private var hingeConstraintId: ConstraintId? = null

    override fun load(tag: CompoundTag) {
        super.load(tag)
        isBase = tag.getBoolean("isBase")

        if (tag.contains("otherPos")) {
            otherPos = BlockPos.of(tag.getLong("otherPos"))

            VSEvents.shipLoadEvent.on { (otherShip), handler ->
                if (otherShip.chunkClaim.contains(otherPos!!.x / 16, otherPos!!.z / 16)) {
                    handler.unregister()

                    if (!createConstraints()) {
                        VSEvents.shipLoadEvent.on { (ship), handler ->
                            if (ship.chunkClaim.contains(blockPos.x / 16, blockPos.z / 16)) {
                                handler.unregister()

                                if (!createConstraints(otherShip)) throw IllegalStateException("Could not create constraints for bearing block entity!")
                            }
                        }
                    }
                }
            }
        }
    }

    override fun saveAdditional(tag: CompoundTag) {
        super.saveAdditional(tag)
        tag.putBoolean("isBase", isBase)

        if (otherPos != null) {
            tag.putLong("otherPos", otherPos!!.asLong())
        }
    }

    fun makeTop() {
        if (!isBase) throw IllegalStateException("Calling make top on a top")
        if (level!!.isClientSide) return

        val level = level as ServerLevel

        val lookingTowards = blockState.getValue(BlockStateProperties.FACING).normal.toJOMLD()

        val ship = level.getShipObjectManagingPos(blockPos)

        val clipResult = level.clipIncludeShips(
            ClipContext(
                (Vector3d(basePoint()).let {
                    ship?.shipToWorld?.transformPosition(it) ?: it
                }).toMinecraft(),
                (blockPos.toJOMLD()
                    .add(0.5, 0.5,0.5)
                    .add(Vector3d(lookingTowards).mul(0.8))
                    .let {
                        ship?.shipToWorld?.transformPosition(it) ?: it
                    }).toMinecraft(),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                null), false)

        val (otherAttachmentPoint, otherShip) = if (clipResult.type == HitResult.Type.MISS) {
            val otherShip = level.shipObjectWorld.createNewShipAtBlock(
                blockPos.offset(blockState.getValue(BlockStateProperties.FACING).normal).toJOML(),
                false,
                ship?.transform?.shipToWorldScaling?.x() ?: 1.0,
                level.dimensionId
            )

            val shipCenterPos = BlockPos(
                (otherShip.transform.positionInShip.x() - 0.5).roundToInt(),
                (otherShip.transform.positionInShip.y() - 0.5).roundToInt(),
                (otherShip.transform.positionInShip.z() - 0.5).roundToInt()
            )

            val towards = blockState.getValue(BlockStateProperties.FACING).opposite
            val topPos = shipCenterPos.offset(towards.normal)

            level.setBlock(shipCenterPos, Blocks.GOLD_BLOCK.defaultBlockState(), 11)
            level.setBlock(topPos, TakeoffBlocks.BEARING_TOP.get().defaultBlockState()
                .setValue(BlockStateProperties.FACING, towards), 11)

            topPos to otherShip
        } else {
            level.getShipObjectManagingPos(clipResult.blockPos)?.let { otherShip ->
                val otherPos = clipResult.blockPos.offset(blockState.getValue(BlockStateProperties.FACING).opposite.normal)

                level.setBlock(
                    otherPos, TakeoffBlocks.BEARING_TOP.get().defaultBlockState().setValue(
                        BlockStateProperties.FACING, blockState.getValue(BlockStateProperties.FACING).opposite
                    ), 11
                )

                otherPos to otherShip
            } ?: (clipResult.blockPos to null)
        }

        this.otherPos = otherAttachmentPoint
        createConstraints(otherShip)
    }

    fun createConstraints(otherShip: ServerShip? = null): Boolean {
        if (isBase && otherPos != null && level != null
            && attachConstraintId == null && hingeConstraintId == null
            && !level!!.isClientSide) {
            val level = level as ServerLevel
            val shipId = level.getShipObjectManagingPos(blockPos)?.id ?: level.shipObjectWorld.dimensionToGroundBodyIdImmutable[level.dimensionId]!!
            val otherShipId = otherShip?.id ?: level.getShipObjectManagingPos(otherPos!!)?.id ?: level.shipObjectWorld.dimensionToGroundBodyIdImmutable[level.dimensionId]!!

            // If both ships aren't loaded don't make the constraint (itl crash vs2)
            if (level.getShipManagingPos(blockPos) != null && level.getShipManagingPos(blockPos)!!.id != shipId) return false
            if (level.getShipManagingPos(otherPos!!) != null && level.getShipManagingPos(otherPos!!)!!.id != otherShipId) return false

            // Orientation
            val lookingTowards = blockState.getValue(BlockStateProperties.FACING).normal.toJOMLD()
            val x = Vector3d(1.0, 0.0, 0.0)
            val xCross = Vector3d(lookingTowards).cross(x)
            val hingeOrientation = if (xCross.lengthSquared() < 1e-6)
                Quaterniond()
            else
                Quaterniond(AxisAngle4d(lookingTowards.angle(x), xCross.normalize()))



            val hingeOrientationCompliance = constraintComplience
            val hingeMaxTorque = maxForce
            val hingeConstraint = VSHingeOrientationConstraint(
                shipId, otherShipId, hingeOrientationCompliance, hingeOrientation, hingeOrientation, hingeMaxTorque
            )

            val attachmentCompliance = constraintComplience
            val attachmentMaxForce = maxForce
            val attachmentFixedDistance = 0.0
            val attachmentConstraint = VSAttachmentConstraint(
                shipId, otherShipId, attachmentCompliance, basePoint(), otherPoint()!!,
                attachmentMaxForce, attachmentFixedDistance
            )

            hingeConstraintId = level.shipObjectWorld.createNewConstraint(hingeConstraint)
            attachConstraintId = level.shipObjectWorld.createNewConstraint(attachmentConstraint)
            return true
        }

        return false
    }

    fun destroyConstraints() {
        if (level?.shipObjectWorld != null && !level!!.isClientSide) {
            val level = level as ServerLevel
            hingeConstraintId?.let { level.shipObjectWorld.removeConstraint(it) }
            attachConstraintId?.let { level.shipObjectWorld.removeConstraint(it) }
        }
    }

    private fun basePoint(): Vector3d = blockPos.toJOMLD()
        .add(0.5, 0.5,0.5)
        .add(blockState.getValue(BlockStateProperties.FACING).normal.toJOMLD().mul(0.5 + baseOffset))

    private fun otherPoint(): Vector3d? = otherPos?.toJOMLD()
        ?.add(0.5, 0.5, 0.5)
        ?.add(blockState.getValue(BlockStateProperties.FACING).normal.toJOMLD().mul(0.5))

    override fun setRemoved() {
        super.setRemoved()
        destroyConstraints()
    }

    override fun clearRemoved() {
        super.clearRemoved()
        createConstraints()
    }

    companion object {
        private val baseOffset = 0.2
        private val constraintComplience = 1e-8
        private val maxForce = 1e10
    }
}