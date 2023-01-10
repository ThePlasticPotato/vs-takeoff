package net.takeoff.ship

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnore
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import org.joml.AxisAngle4d
import org.joml.Quaterniond
import org.joml.Quaterniondc
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.Vector3i
import org.valkyrienskies.core.api.ships.*
import org.valkyrienskies.core.impl.api.ServerShipUser
import org.valkyrienskies.core.impl.api.ShipForcesInducer
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toJOMLD
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

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
            val (pos, wingDirection) = it

            val wingNormalLocal: Vector3dc = wingDirection.normal.toJOMLD()

            val localPos: Vector3dc = Vector3d(pos).add( 0.5, 0.5, 0.5)
            // Pos relative to center of mass, in global coordinates
            val tDir: Vector3dc = ship.shipToWorld.transformPosition(localPos, Vector3d()).sub(ship.transform.positionInWorld)

            // Velocity at the block position, in global coordinates
            val velAtWingGlobal: Vector3dc = (Vector3d(ship.omega).cross(tDir)).add(ship.velocity)

            val wingNormalGlobal: Vector3dc = ship.shipToWorld.transformDirection(wingNormalLocal, Vector3d())
            val liftVel: Vector3dc = velAtWingGlobal.sub(Vector3d(wingNormalGlobal).mul(wingNormalGlobal.dot(velAtWingGlobal)), Vector3d())
            val liftVelDirection: Vector3dc = Vector3d(liftVel).normalize()

            if (liftVelDirection.lengthSquared() > 1e-12) {
                // Angle of attack, in radians
                val angleOfAttack = liftVelDirection.angle(velAtWingGlobal)


                val dragDirection = velAtWingGlobal.mul(-1.0, Vector3d()).normalize()
                // val liftVel = velAtWingGlobal.dot(liftVelDirection)

                val liftPower = 150.0
                val liftCoefficient = sin(2.0 * angleOfAttack)
                val liftForceMagnitude = liftPower * liftCoefficient * liftVel.lengthSquared()
                val liftForceVector: Vector3dc = wingNormalGlobal.mul(liftForceMagnitude, Vector3d())


                // TODO: Need to compute [dragCoefficient] more effectively
                val dragCoefficient = liftCoefficient * liftCoefficient
                val dragForceMagnitude = 150.0 * dragCoefficient * liftVel.lengthSquared()
                val dragForceVector: Vector3dc = dragDirection.mul(dragForceMagnitude, Vector3d())

                val totalForce: Vector3dc = liftForceVector.add(dragForceVector, Vector3d())

                val localForce = ship.worldToShip.transformDirection(totalForce, Vector3d())
                val localPos2 = ship.worldToShip.transformDirection(tDir, Vector3d())

                val tPos = Vector3d(pos).add( 0.5, 0.5, 0.5).sub(ship!!.transform.positionInShip)
                physShip.applyRotDependentForceToPos(localForce, tPos)
            } else {
                // TODO: Do nothing?
            }
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