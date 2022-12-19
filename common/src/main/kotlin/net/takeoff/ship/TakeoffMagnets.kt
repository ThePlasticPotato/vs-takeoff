package net.takeoff.ship

import com.fasterxml.jackson.annotation.JsonIgnore
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized
import net.minecraft.core.BlockPos
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.Vector3i
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.saveAttachment
import org.valkyrienskies.core.impl.api.ServerShipUser
import org.valkyrienskies.core.impl.api.ShipForcesInducer
import org.valkyrienskies.core.impl.api.Ticked
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import org.valkyrienskies.core.impl.pipelines.SegmentUtils
import org.valkyrienskies.mod.common.util.toJOML
import java.util.concurrent.CopyOnWriteArrayList

@OptIn(InternalCoroutinesApi::class)
class TakeoffMagnets(@JsonIgnore override var ship: ServerShip?) : ShipForcesInducer, ServerShipUser, Ticked {
    private val magnets = CopyOnWriteArrayList<Magnet>()

    override fun applyForces(physShip: PhysShip) {
        if (ship == null) return

        physShip as PhysShipImpl
        val shipPos = physShip.poseVel.pos

        magnets.forEach {
            val (pos, otherMagnets) = it
            it.physShip = physShip

            val tPos = Vector3d(pos).add(0.5, 0.5, 0.5).sub(ship!!.transform.positionInShip)
            SegmentUtils.transformPos(physShip.poseVel, physShip.segments.segments.values.first().segmentDisplacement, tPos, tPos)

            otherMagnets.forEach { other ->
                if (other.physShip == null) return@forEach

                val oPhysShip = other.physShip as PhysShipImpl

                val tPosOther = Vector3d(other.pos).add(0.5, 0.5, 0.5).sub(other!!.ship!!.transform.positionInShip)
                SegmentUtils.transformPos(oPhysShip.poseVel, oPhysShip.segments.segments.values.first().segmentDisplacement, tPosOther, tPosOther)

                var dist = tPosOther.distanceSquared(tPos)
                if (dist > MAGNET_DISTANCE) return
                if (dist < 0.1) dist = 0.1

                val power = MAGNET_POWER / dist

                //if (it.north == other.north) power = -power

                val force = Vector3d(tPos).sub(tPosOther).normalize(-power)

                physShip.applyInvariantForceToPos(force, Vector3d(tPos).sub(shipPos))
            }
        }
    }

    fun addMagnet(pos: BlockPos, north: Boolean) {
        val magnet = Magnet(pos.toJOML(), mutableListOf(), north, ship = ship!!)
        magnets.add(magnet)
        allMagnets.add(magnet)
    }

    fun removeMagnet(bpos: BlockPos) {
        val pos = bpos.toJOML()
        magnets.removeIf { it.pos == pos }
        allMagnets.removeIf { it.pos == pos }
    }

    companion object {
        val MAGNET_DISTANCE = 10 * 10
        val MAGNET_POWER = 600.0 * 600.0

        fun getOrCreate(ship: ServerShip): TakeoffMagnets =
            ship.getAttachment<TakeoffMagnets>()
                ?: TakeoffMagnets(ship).also { ship.saveAttachment(it) }

        private val allMagnets = mutableListOf<Magnet>()
    }

    data class Magnet(
        val pos: Vector3i,
        @JsonIgnore var otherMagnets: List<Magnet> = mutableListOf(),
        val north: Boolean,
        @JsonIgnore var lastWorldPos: Vector3d = Vector3d(),
        @JsonIgnore var ship: ServerShip? = null,
        @JsonIgnore var physShip: PhysShip? = null
    )

    override fun tick() {
        magnets.forEach {
            it.lastWorldPos = ship!!.shipToWorld.transformPosition(Vector3d(it.pos).add(0.5, 0.5, 0.5))
            val otherMagnets = mutableListOf<Magnet>()
            for (magnet in allMagnets) {
                if (magnet.ship != null && magnet.ship!!.id != this.ship!!.id && (magnet.lastWorldPos.distanceSquared(it.lastWorldPos) < MAGNET_DISTANCE)) {
                    otherMagnets.add(magnet)
                }
            }
            it.otherMagnets = otherMagnets
        }
    }
}