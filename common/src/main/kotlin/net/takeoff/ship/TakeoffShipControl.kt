package net.takeoff.ship

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnore
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.Block
import org.joml.AxisAngle4d
import org.joml.Math.clamp
import org.joml.Quaterniond
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.saveAttachment
import org.valkyrienskies.core.impl.api.ServerShipUser
import org.valkyrienskies.core.impl.api.ShipForcesInducer
import org.valkyrienskies.core.impl.api.Ticked
import org.valkyrienskies.core.impl.api.shipValue
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import org.valkyrienskies.core.impl.pipelines.SegmentUtils
import net.takeoff.TakeoffConfig
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.mod.common.util.toJOMLD
import kotlin.math.*

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)

class TakeoffShipControl : ShipForcesInducer, ServerShipUser, Ticked {
    @JsonIgnore
    override var ship: ServerShip? = null

    private var extraForce = 0.0
    private var physConsumption = 0f
    private var totalloc = Vector3d(0.0,0.0,0.0)
    private var avgloc = Vector3d(0.0, 0.0, 0.0)
    private val farters = mutableListOf<Pair<BlockPos, Direction>>()
    var consumed = 0f
        private set

    private var controlData: ControlData? = null

    private data class ControlData(
        var forwardImpulse: Float = 0.0f,
        var leftImpulse: Float = 0.0f,
        var upImpulse: Float = 0.0f,
        var liftImpulse: Float = 0.0f,
    )

    override fun applyForces(physShip: PhysShip) {

        val forcesApplier = physShip

        physShip as PhysShipImpl

        val mass = physShip.inertia.shipMass
        val moiTensor = physShip.inertia.momentOfInertiaTensor
        val segment = physShip.segments.segments[0]?.segmentDisplacement!!
        val omega = SegmentUtils.getOmega(physShip.poseVel, segment, Vector3d())
        val vel = SegmentUtils.getVelocity(physShip.poseVel, segment, Vector3d())

//        val buoyantFactorPerFloater = min(
//            TakeoffConfig.SERVER.floaterBuoyantFactorPerKg / 15 / mass,
//            TakeoffConfig.SERVER.maxFloaterBuoyantFactor
//        )

        physShip.buoyantFactor = 1.0 //+ floaters * buoyantFactorPerFloater
        // Revisiting eureka control code.
        // [x] Move torque stabilization code
        // [x] Move linear stabilization code
        // [x] Revisit player controlled torque
        // [x] Revisit player controlled linear force
        // [x] Anchor freezing
        // [x] Rewrite Alignment code
        // [x] Revisit Elevation code
        // [x] Balloon limiter
        // [ ] Add Cruise code
        // [ ] Rotation based of shipsize
        // [x] Engine consumption
        // [ ] Fix elevation sensititvity

        // region Balloons
        var idealUpwardVel = Vector3d(0.0, 0.0, 0.0)
        //        val elevationSnappiness = 10.0
        val idealUpwardForce = Vector3d(
            0.0,
            idealUpwardVel.y() - vel.y() - (GRAVITY / 10.0),
            0.0
        ).mul(mass * 10.0)

        val balloonForceProvided = forcePerBalloon

//        val actualUpwardForce = Vector3d(0.0, min(balloonForceProvided, max(idealUpwardForce.y(), 0.0)), 0.0)

//        for (i in 1..balloons) {
//            var loc = (balloonpos.get(i-1).toJOMLD())
//            totalloc.x += loc.x
//            totalloc.y += loc.y
//            totalloc.z += loc.z
//        }
//        totalloc.div(balloons.toDouble())
//        totalloc.x = totalloc.x/balloons
//        totalloc.y = totalloc.y/balloons
//        totalloc.div = totalloc.z/balloons
        val shipCoords = Vector3d(ship?.transform?.positionInShip)
        val shipCoordsinworld = Vector3d(ship?.transform?.positionInWorld)
        var falloffamount = 0.0
        var falloff = 1.0
        if (shipCoordsinworld.y > 256) {
            falloffamount = shipCoordsinworld.y - 256
            falloff = falloffamount*(1.5)

        } else {
            falloff = 1.0
        }
        val actualUpwardForce = Vector3d(0.0, (5000.0/falloff), 0.0)
        balloonpos.forEach() {
            if (actualUpwardForce.isFinite) {
                forcesApplier.applyInvariantForceToPos(actualUpwardForce, Vector3d(it).sub(shipCoords))
//                println("APPLIED AT " + it.sub(shipCoords).toString())
            }
        }
        // end region



        // region Aligning

//        val invRotation = physShip.poseVel.rot.invert(Quaterniond())
//        val invRotationAxisAngle = AxisAngle4d(invRotation)
//        // Floor makes a number 0 to 3, which corresponds to direction
//
//        stabilize(
//            physShip,
//            omega,
//            vel,
//            segment,
//            forcesApplier,
//            controllingPlayer == null && !aligning,
//            controllingPlayer == null
//        )
//
//        var idealUpwardVel = Vector3d(0.0, 0.0, 0.0)
//
//
//        controlData?.let { control ->
//            // region Player controlled rotation
//            var rotationVector = Vector3d(
//                0.0,
//                if (control.leftImpulse != 0.0f)
//                    (control.leftImpulse.toDouble() * 3.0)
//                else
//                    -omega.y() * 3.0,
//                0.0
//            )
//
//            rotationVector.sub(0.0, omega.y(), 0.0)
//
//            SegmentUtils.transformDirectionWithScale(
//                physShip.poseVel,
//                segment,
//                moiTensor.transform(
//                    SegmentUtils.invTransformDirectionWithScale(
//                        physShip.poseVel,
//                        segment,
//                        rotationVector,
//                        rotationVector
//                    )
//                ),
//                rotationVector
//            )
//
//            forcesApplier.applyInvariantTorque(rotationVector)
            // endregion

            // region Player controlled banking

//            physShip.poseVel.transformDirection(rotationVector)
//
//            rotationVector.y = 0.0
//
//            rotationVector.mul(control.leftImpulse.toDouble() * 3.0 * -1.5)
//
//            SegmentUtils.transformDirectionWithScale(
//                physShip.poseVel,
//                segment,
//                moiTensor.transform(
//                    SegmentUtils.invTransformDirectionWithScale(
//                        physShip.poseVel,
//                        segment,
//                        rotationVector,
//                        rotationVector
//                    )
//                ),
//                rotationVector
//            )
//
//            forcesApplier.applyInvariantTorque(rotationVector)
            // endregion



//            val playerUpDirection = physShip.poseVel.transformDirection(Vector3d(0.0, 1.0, 0.0))
//            val velOrthogonalToPlayerUp =
//                vel.sub(playerUpDirection.mul(playerUpDirection.dot(vel), Vector3d()), Vector3d())
//
//            // This is the speed that the ship is always allowed to go out, without engines
//            val baseForwardVel = Vector3d(forwardVector).mul(3.0)
//            val baseForwardForce = Vector3d(baseForwardVel).sub(velOrthogonalToPlayerUp).mul(mass * 10)
//
//            // This is the maximum speed we want to go in any scenario (when not sprinting)
//            val idealForwardVel = Vector3d(forwardVector).mul(20.0)
//            val idealForwardForce = Vector3d(idealForwardVel).sub(velOrthogonalToPlayerUp).mul(mass * 10)
//
//            val extraForceNeeded = Vector3d(idealForwardForce).sub(baseForwardForce)
//            val actualExtraForce = Vector3d(baseForwardForce)
//
//            if (extraForce != 0.0) {
//                actualExtraForce.fma(min(extraForce / extraForceNeeded.length(), 1.0), extraForceNeeded)
//            }

//            forcesApplier.applyInvariantForce(actualExtraForce)
            // endregion

            // Player controlled elevation
//            if (control.upImpulse != 0.0f) {
//                idealUpwardVel = Vector3d(0.0, 1.0, 0.0)
//                    .mul(control.upImpulse.toDouble())
//                    .mul(7.0)
//            }
//        }

        // region Elevation
        // Higher numbers make the ship accelerate to max speed faster

//        forcesApplier.applyInvariantForce(actualUpwardForce)
        // endregion

        // Drag
        // forcesApplier.applyInvariantForce(Vector3d(vel.y()).mul(-mass))

        val ship = ship ?: return

        farters.forEach {
            val (pos, dir) = it

            val tPos = ship.shipToWorld.transformPosition(pos.toJOMLD()).sub(ship.transform.positionInWorld)
            val tDir = ship.shipToWorld.transformDirection(dir.normal.toJOMLD(), Vector3d())
            tDir.normalize(-10000.0)

            physShip.applyInvariantForceToPos(tDir, tPos)
        }
    }
    var power = 0.0

    var balloons = 0 // Amount of balloons
        set(v) {
            field = v; deleteIfEmpty()
        }

    var balloonpos = mutableListOf<Vector3dc>()

    override fun tick() {
        extraForce = power
        power = 0.0
        consumed = physConsumption * /* should be phyics ticks based*/ 0.1f
        physConsumption = 0.0f
//        balloonTick()
    }

//    private fun balloonTick() {
//        if (balloons>0) {
//            totalloc = Vector3d(0.0,0.0,0.0)
//            for (i in 1..balloons) {
//                var loc = (balloonpos.get(i-1))
//                totalloc.x += loc.x
//                totalloc.y += loc.y
//                totalloc.z += loc.z
//            }
//            avgloc = totalloc.div(balloons.toDouble())
//        }
//    }

    private fun deleteIfEmpty() {
        if (balloons == 0) {
            ship?.saveAttachment<TakeoffShipControl>(null)
        }
    }

    fun addFarter(pos: BlockPos, dir: Direction) {
        farters.add(pos to dir)
    }

    fun removeFarter(pos: BlockPos, dir: Direction) {
        farters.remove(pos to dir)
    }

    companion object {
        fun getOrCreate(ship: ServerShip): TakeoffShipControl {
            return ship.getAttachment<TakeoffShipControl>()
                ?: TakeoffShipControl().also { ship.saveAttachment(it) }
        }

        private val forcePerBalloon get() = 5000 * -GRAVITY

        private const val GRAVITY = -10.0
    }
}