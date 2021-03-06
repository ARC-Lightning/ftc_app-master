package org.firstinspires.ftc.teamcode.tests

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Servo

/**
 * A rhythmic test of the BucketPour servos and the collector motors.
 */
@Autonomous(name = "Rhythmic", group = "Pragmaticos")
class RhythmTest : LinearOpMode() {

    /* ELECTRONICS */
    lateinit var leftPour: Servo
    lateinit var leftCollector: DcMotor
    lateinit var rightCollector: DcMotor

    companion object {
        /* POSITIONS and POWER */
        const val POUR_DOWN = 0.0
        const val POUR_UP = 0.3
        const val COLLECTOR_POWER = 0.5

        /* RHYTHM SPEED */
        const val BPM = 300.0
    }

    val drum = DrumAlternator(this)

    /* RHYTHM */
    val piece = arrayOf(
            drum::alternate to arrayOf(2.0, 2.0, 2.0, 2.0),
            this::activateCollectors to arrayOf(2.0, 2.0, 1.0, 1.0, 2.0),
            drum::alternate to arrayOf(2.0, 2.0, 2.0, 2.0),
            this::activateCollectors to arrayOf(2.0, 2.0, 1.0, 1.0, 2.0)
    )

    override fun runOpMode() {
        // Define electronics
        with(hardwareMap) {
            leftPour = servo.get("BucketPour")

            leftCollector = dcMotor.get("FlywheelLeft")
            rightCollector = dcMotor.get("FlywheelRight")
        }

        // Follow symmetry
        rightCollector.direction = DcMotorSimple.Direction.REVERSE

        telemetry.isAutoClear = false

        // Initialize instruments
        leftCollector.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        rightCollector.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        waitForStart()

        // Run through phrases
        for ((func, phrase) in piece) {
            runPhrase(func, phrase)
        }
    }

    fun activateCollectors() {
        leftCollector.power = COLLECTOR_POWER
        rightCollector.power = COLLECTOR_POWER
        sleep(100)
        leftCollector.power = 0.0
        rightCollector.power = 0.0
    }

    fun runPhrase(func: () -> Unit, phrase: Array<Double>) {
        for (beat in phrase) {
            func()
            sleep(Math.round(beat / (BPM / 60_000.0)))
        }
    }

    class DrumAlternator(val opMode: RhythmTest) {
        var isLeftDown: Boolean = false

        fun alternate() {
            opMode.leftPour.position = if (isLeftDown) POUR_UP else POUR_DOWN

            isLeftDown = !isLeftDown
        }
    }
}