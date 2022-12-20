package net.takeoff.util

import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.impl.util.multiplyTerms
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.math.atan

object FieldVariables {

    val permea: Double = 1/(4*Math.PI)


    fun solvethisshitforme(n: Vector3dc, xr: Double, yr: Double, zr: Double, zprime: Double): Double {
        var norm = Vector3d(n)
        var x : Double
        var y : Double
        var z : Double
        var rpq: Double
        var tpq: Double
        var spq: Double
        var upq: Double
        val firstpart: Vector3d = norm.mul(permea)
        var secondpartBx: Double = 0.0
        var secondpartBy: Double = 0.0
        var secondpartBz: Double = 0.0

        for (p in 1..2) {
            for (q in 1..2) {
                if (q == 1) {
                    x = (-0.5) - xr
                } else if (q == 2) {
                    x = 0.5 - xr
                }
                if (p == 1) {
                    y = (-0.5) - yr
                } else if (p == 2) {
                    y = 0.5 - yr
                }
                z = zprime - zr
                rpq = sqrt((x*x) + (y*y) + (z*z))
                tpq = rpq + y
                secondpartBx += (-1.0).pow(p+q) * ln(tpq)
            }
        }

        val Bx = firstpart.mul(secondpartBx)

        for (p in 1..2) {
            for (q in 1..2) {
                if (q == 1) {
                    x = (-0.5) - xr
                } else if (q == 2) {
                    x = 0.5 - xr
                }
                if (p == 1) {
                    y = (-0.5) - yr
                } else if (p == 2) {
                    y = 0.5 - yr
                }
                z = zprime - zr
                rpq = sqrt((x*x) + (y*y) + (z*z))
                spq = x + rpq
                secondpartBy += (-1.0).pow(p+q) * ln(spq)
            }
        }

        val By = firstpart.mul(secondpartBy)

        for (p in 1..2) {
            for (q in 1..2) {
                if (q == 1) {
                    x = (-0.5) - xr
                } else if (q == 2) {
                    x = 0.5 - xr
                }
                if (p == 1) {
                    y = (-0.5) - yr
                } else if (p == 2) {
                    y = 0.5 - yr
                }
                z = zprime - zr
                rpq = sqrt((x*x) + (y*y) + (z*z))
                upq = (-x*y).div((z*rpq))
                secondpartBz += (-1.0).pow(p+q) * atan(upq)
            }
        }

        val Bz = firstpart.mul(secondpartBz)

        return Bx.add
    }
}