package org.firstinspires.ftc.teamcode.autonomous

import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark
import org.firstinspires.ftc.teamcode.AllianceColor
import org.firstinspires.ftc.teamcode.io.DynamicConfig
import org.firstinspires.ftc.teamcode.io.Hardware
import org.locationtech.jts.algorithm.Angle
import org.locationtech.jts.math.Vector2D

/**
 * Provides basic abstraction of Autonomous movements.
 *
 * @author Michael Peng
 * For team: 4410
 *
 * FIRST - Gracious Professionalism
 */
class AutoNav : IAutoNav {

    // Tweakable Parameters
    companion object {
        /**
         * Width between the MIDDLE of the cryptobox columns in inches. 7.63 according to game manual.
         */
        val CRYPTOBOX_WIDTH = 7.63

        /**
         * Vector from the starting point to the optimal position to place a glyph in the middle column
         * of the desired cryptobox, where the starting point is closer to a corner of the playing field and is red.
         * This vector may need to be temporarily adjusted for leaving the balancing stone, for it may
         *   produce a slight inaccuracy in encoder-based positioning.
         */
        // This vector's orientation is relative to the front of our robot in its starting position.
        // Given that the jewel arm is on the left, consider the starting position as the origin, and
        //   positive y as up (in the diagram).
        // This vector will be flipped across the X axis when our alliance color is BLUE.
        val CRYPTOBOX_POSITION_CORNER = Vector2D(-2.0, 36.0)

        /**
         * Vector from the starting point to the optimal position to place a glyph in the middle column
         * of the desired cryptobox, where the starting point is farther to a corner of the playing field and is red.
         * This vector may need to be temporarily adjusted for leaving the balancing stone, for it may
         *   produce a slight inaccuracy in encoder-based positioning.
         */
        // This vector's orientation is relative to the front of our robot in its starting position.
        // Given that the jewel arm is on the left, consider the starting position as the origin, and
        //   positive y as up (in the diagram).
        // This vector will be flipped across the X axis when our alliance color is BLUE.
        val CRYPTOBOX_POSITION_CENTERED = Vector2D(12.0, -26.0)

        /**
         * Distance from starting point to the ideal location of dropping the jewel arm.
         * It is assumed that looking from the wall to which the cryptobox key is attached, the robot
         *   will ONLY move VERTICALLY for this distance in inches. In other words, the starting point
         *   is lined up with the white line between the jewels.
         */
        val JEWEL_ACCESS_OFFSET = 0.3
    }

    private val isStartingOnCorner: Boolean
        get() = DynamicConfig.isStartingLeft == (DynamicConfig.alliance == AllianceColor.RED)

    /**
     * Currently performs the following to the given vector and returns the final modified output:
     *
     *  - Mirror across the x axis if on BLUE alliance
     *
     *  @param vec The initial vector to modify
     *  @returns The final modified output
     */
    private fun finalizeVector(vec: Vector2D): Vector2D {
        // Pipeline operation requires clone
        var out = Vector2D(vec)

        if (DynamicConfig.alliance == AllianceColor.BLUE) {
            out = Vector2D(out.x, -out.y)
        }

        return out
    }

    // Jewel arm on left side (negative x)
    override fun beginJewelKnock() {
        Hardware.drivetrain.move(Vector2D(-JEWEL_ACCESS_OFFSET, 0.0))
    }

    override fun endJewelKnock() {
        Hardware.drivetrain.move(Vector2D(JEWEL_ACCESS_OFFSET, 0.0))
    }

    private fun instructionsToCryptoBox(): Pair<Vector2D, Double> {
        return if (isStartingOnCorner)
        // Same rotation for CORNER of both sides
            finalizeVector(CRYPTOBOX_POSITION_CORNER) to 90.0
        else
        // Red needs to turn 180deg for CENTERED, Blue is lined up already
            finalizeVector(CRYPTOBOX_POSITION_CENTERED) to
                    if (DynamicConfig.alliance == AllianceColor.BLUE) 0.0 else 180.0
    }

    private fun instructionsToColumn(vuMark: RelicRecoveryVuMark): Vector2D {
        if (vuMark == RelicRecoveryVuMark.UNKNOWN) {
            Hardware.telemetry.warning("Instructions to UNKNOWN cryptobox column?!")
            return Vector2D(0.0, 0.0)
        }

        // NOTE: The robot MUST face the cryptobox before moving in this vector.
        return Vector2D(when (vuMark) {
            RelicRecoveryVuMark.LEFT -> -CRYPTOBOX_WIDTH
            RelicRecoveryVuMark.RIGHT -> CRYPTOBOX_WIDTH
            else -> 0.0
        }, 0.0)
    }

    override fun goToCryptoBox(vuMark: RelicRecoveryVuMark) {
        val (movement, turnDeg) = instructionsToCryptoBox()
        with(Hardware.drivetrain) {
            move(movement)
            turn(Angle.toRadians(turnDeg))
            move(instructionsToColumn(vuMark))
        }
    }

    override fun returnFromCryptoBox(vuMark: RelicRecoveryVuMark) {
        val (movement, turnDeg) = instructionsToCryptoBox()
        with(Hardware.drivetrain) {
            move(instructionsToColumn(vuMark).negate())
            turn(Angle.toRadians(-turnDeg))
            move(movement.negate())
        }
    }
}