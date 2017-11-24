package org.firstinspires.ftc.teamcode.io

import com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.teamcode.AllianceColor
import org.firstinspires.ftc.teamcode.teleop.GamepadRule

/**
 * Allows the robot to be dynamically configured during the time between init and play in OpModes.
 * Designed for team color or starting position selection without reuploading the APK.
 *
 * @author Michael Peng
 * For team: 4410 (Lightning)
 *
 * FIRST - Gracious Professionalism
 */
object DynamicConfig {
    /*

        To add config properties, just follow the pattern of existing properties.
        Config properties are non-Nullable (thus having a default value) to prevent NPEs.

        NOTE: Most edits right before the match happen here.

     */
    // -- START CONFIG PROPERTIES --

    var alliance: AllianceColor = AllianceColor.RED
    var isStartingLeft: Boolean = true

    // -- END CONFIG PROPERTIES --

    // Mappings from gamepad controls to properties are expressed in lambda form.
    // They are registered here to be added as GamepadRules to the main GamepadListener.
    object Mapping {

        /**
         * A registry of mappings from gamepad controls to Dyn. Conf. properties.
         * All values should be in lambda form.
         */
        val mappings: List<GamepadRule> = listOf(
                // Button X toggles team color
                { pad: Gamepad -> pad.x } to { value ->
                    if (value) {
                        alliance = AllianceColor.oppositeOf(alliance)
                        updateTelemetry("AllianceColor", alliance)
                    }
                },

                // Button A toggles starting position
                { pad: Gamepad -> pad.a } to { value ->
                    if (value) {
                        isStartingLeft = !isStartingLeft
                        updateTelemetry("Start position is left", isStartingLeft)
                    }
                }
        )
    }

    private fun updateTelemetry(name: String, newValue: Any) {
        Hardware.instance.telemetry.data("DynConf: $name now", newValue)
    }
}
