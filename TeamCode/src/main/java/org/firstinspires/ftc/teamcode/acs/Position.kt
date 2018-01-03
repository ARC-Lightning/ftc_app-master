package org.firstinspires.ftc.teamcode.acs

import org.locationtech.jts.geom.Coordinate

/**
 * Defines a position in which the robot can be on the ACS GameMap. Includes both coordinates and
 * orientation (in radians).
 *
 * Created by: Michael Peng
 * For team: Lightning (4410)
 *
 * FIRST - Gracious Professionalism
 */
@Deprecated(message = "ACS has been abandoned for a simpler solution")
data class Position(val location: Coordinate, val orientation: Double)

// Oh, look how neat this is compared to its Java predecessor!
// Kotlin for the win!