package net.takeoff.ship

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnore
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.Vector3i
import org.valkyrienskies.core.api.ships.*
import org.valkyrienskies.core.impl.api.ServerShipUser
import org.valkyrienskies.core.impl.api.ShipForcesInducer
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toJOMLD
import kotlin.math.absoluteValue
import kotlin.math.atan2

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)

class TakeoffWings(@JsonIgnore override var ship: ServerShip?) : ShipForcesInducer, ServerShipUser {
    private val wings = mutableListOf<Pair<Vector3i, Direction>>()

    override fun applyForces(physShip: PhysShip) {
        val ship = ship as ServerShip
        wings.forEach {
            val (pos, dir) = it

            val angularDrag = 0.5;
            val Drag = 0.1;

            val tPos = Vector3d(pos).add( 0.5, 0.5, 0.5).sub(ship.transform.positionInShip)
            val tDir = ship.shipToWorld.transformDirection(dir.normal.toJOMLD())

            val currentPosVelocityOfPos = Vector3d(tPos)
                .sub(ship.transform.positionInWorld)
                .cross(ship.omega)
                .add(ship.velocity)

            /* Angular Drag */
            var dragTorque: Vector3dc = Vector3d(0.0, 1.0, 1.0).mul(ship.inertiaData.momentOfInertiaTensor.transform(ship.omega.mul(-angularDrag, Vector3d())))

            println(dragTorque)
        }
    }


    fun addWing(pos: BlockPos, dir: Direction) {
        wings.add(pos.toJOML() to dir)
    }

    fun removeWing(pos: BlockPos, dir: Direction) {
        wings.remove(pos.toJOML() to dir)
    }

    companion object {
        fun getOrCreate(ship: ServerShip): TakeoffWings =
            ship.getAttachment<TakeoffWings>()
                ?: TakeoffWings(ship).also { ship.saveAttachment(it) }
    }
}