package net.takeoff.ship

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnore
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.*
import org.valkyrienskies.core.impl.api.ServerShipUser
import org.valkyrienskies.core.impl.api.ShipForcesInducer
import org.valkyrienskies.mod.common.util.toJOMLD

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)

class TakeoffWings(@JsonIgnore override var ship: ServerShip?) : ShipForcesInducer, ServerShipUser {
    private val wings = mutableListOf<Pair<BlockPos, Direction>>()

    override fun applyForces(physShip: PhysShip) {
        val ship = ship as ServerShip
        val forcesApplier = physShip
        wings.forEach {
            val (pos, dir) = it



            val tPos = ship.shipToWorld.transformPosition(pos.toJOMLD())
            val tDir = ship.shipToWorld.transformDirection(dir.normal.toJOMLD(), Vector3d())

                val currentVelocityOfPos = Vector3d(tPos)
                .sub(ship.transform.positionInWorld)
                .cross(ship.omega)
                .add(ship.velocity)

            //println(currentVelocityOfPos)

            //tDir.normalize(1000.0)

            forcesApplier.applyInvariantForceToPos(tDir, tPos)
        }
    }

    fun addWing(pos: BlockPos, dir: Direction) {
        wings.add(pos to dir)
    }

    fun removeWing(pos: BlockPos, dir: Direction) {
        wings.remove(pos to dir)
    }

    companion object {
        fun getOrCreate(ship: ServerShip): TakeoffWings =
            ship.getAttachment<TakeoffWings>()
                ?: TakeoffWings(ship).also { ship.saveAttachment(it) }
    }
}