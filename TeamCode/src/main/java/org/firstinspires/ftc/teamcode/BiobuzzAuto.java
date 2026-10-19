package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.mechanisms.Drivetrain;
import org.firstinspires.ftc.teamcode.mechanisms.Flywheel;
import org.firstinspires.ftc.teamcode.mechanisms.Intake;
import org.firstinspires.ftc.teamcode.mechanisms.Turret;

/**
 * BiobuzzAuto - the robot's driverless routine for the 30-second autonomous period.
 *
 * <p>What it does, in order:
 *   1. LEAVE (3 pts) - drive forward off the starting tile.
 *   2. SPIN UP - aim the turret at the hive and wait for the flywheel to reach full speed.
 *   3. SHOOT (20 pts) - feed all 4 pollen into the hive's up-cell to tip it.
 *   4. SETTLE - pause so the last balls land and the tip counts.
 *   5. PARK (5 pts) - currently OFF, see TRY_PARK below.
 *
 * <p>The whole thing is written as a simple step-by-step list on purpose:
 * each step finishes before the next one starts, so it is easy to follow
 * and easy to test one step at a time.
 */
@Autonomous(name = "Biobuzz Auto", group = "Auto")
public class BiobuzzAuto extends LinearOpMode {

    // ------------------------------------------------------------------
    // TUNING KNOBS - change these numbers to tune the auto.
    // You should never need to change the code below this block.
    // ------------------------------------------------------------------

    /** How fast to drive while leaving the starting tile (0 to 1). */
    private static final double LEAVE_POWER = 0.5;
    /** How long to drive forward to leave the tile (milliseconds). */
    private static final long LEAVE_MS = 2000;
    /** Longest we wait for the flywheel to spin up before giving up (milliseconds). */
    private static final long SPINUP_TIMEOUT_MS = 5000;
    /** Pause between shots so the flywheel recovers to full speed (milliseconds). */
    private static final long FEED_GAP_MS = 1200;
    /** Pause after the last shot so balls land and the tip registers (milliseconds). */
    private static final long SETTLE_MS = 3000;
    /** Number of pollen we start with. The rules give us exactly 4. */
    private static final int SHOTS = 4;

    /**
     * Set to true to drive to the park zone at the end (worth 5 pts).
     * Kept OFF for now: we have not measured the drive from the shooting
     * spot to the park zone yet. See the TODO in step 5 below.
     */
    private static final boolean TRY_PARK = false;

    // One of each mechanism, just like the teleop uses.
    private final Drivetrain drivetrain = new Drivetrain();
    private final Turret turret = new Turret();
    private final Flywheel flywheel = new Flywheel();
    private final Intake intake = new Intake();

    @Override
    public void runOpMode() {
        // Same setup as the teleop: connect each mechanism to the hardware.
        drivetrain.init(hardwareMap);
        turret.init(hardwareMap);
        flywheel.init(hardwareMap);
        intake.init(hardwareMap);

        // Aim everything at the same hive the whole time.
        // TRACK_A = the cell that starts the match facing UP. We tip that one.
        turret.setTracking(Turret.Tracking.TRACK_A);
        flywheel.setTracking(Flywheel.Tracking.TRACK_A);
        // SHOOTING opens the gate and spins the wheel up to the right speed
        // for wherever the robot is standing.
        flywheel.setState(Flywheel.State.SHOOTING);

        telemetry.addData("Status", "Ready - press PLAY");
        telemetry.update();

        // Wait here until the driver presses PLAY (or STOP).
        waitForStart();

        // ---- Step 1: LEAVE (3 pts) ----
        // Drive forward, away from the wall behind our starting tile.
        // The rules award LEAVE as long as we end the auto off that tile.
        driveForTime(LEAVE_POWER, 0, 0, LEAVE_MS);

        // ---- Step 2: SPIN UP ----
        // Wait for the flywheel to reach full speed before shooting.
        // The timeout means we never get stuck here: if the wheel has
        // a problem, we move on and try the shots anyway.
        waitForFlywheel(SPINUP_TIMEOUT_MS);

        // ---- Step 3: SHOOT (20 pts for the tip) ----
        // Feed our 4 pollen one at a time. Before each shot we make sure
        // the flywheel is back up to speed, so every ball leaves at full power.
        for (int shot = 1; shot <= SHOTS && opModeIsActive(); shot++) {
            waitForFlywheel(2000);
            telemetry.addData("Shooting", "ball " + shot + " of " + SHOTS);
            telemetry.update();
            intake.feed();       // push one ball up into the flywheel
            sleep(FEED_GAP_MS);  // let it clear before feeding the next one
        }
        intake.setPower(0); // all balls gone - stop the intake

        // ---- Step 4: SETTLE ----
        // Give the last balls time to land and the hive time to tip over.
        sleep(SETTLE_MS);

        // ---- Step 5: PARK (5 pts) - disabled for now ----
        if (TRY_PARK && opModeIsActive()) {
            // TODO: measure the drive from our shooting spot to the park zone,
            // then replace this with the real drive. Example:
            // driveForTime(0.5, 0, 0, 1500);
            telemetry.addData("Parking", "not measured yet - skipping");
            telemetry.update();
        }

        // ---- Shut everything down ----
        // Setting IDLE tells the flywheel to stop; update() applies it.
        flywheel.setState(Flywheel.State.IDLE);
        flywheel.update(drivetrain.getX(), drivetrain.getY(), drivetrain.getH());
        intake.setPower(0);
        drivetrain.driveRobotCentric(0, 0, 0);
    }

    /**
     * Drive the robot (robot-centric) for a fixed amount of time, then stop.
     * Robot-centric means "forward" is the direction the robot is facing,
     * which is away from the wall at our starting tile.
     */
    private void driveForTime(double forward, double right, double rotate, long ms) {
        long start = System.currentTimeMillis();
        while (opModeIsActive() && System.currentTimeMillis() - start < ms) {
            drivetrain.driveRobotCentric(forward, right, rotate);
            sleep(20);
        }
        drivetrain.driveRobotCentric(0, 0, 0); // always stop driving at the end
    }

    /**
     * Keep the flywheel and turret updating until the flywheel is up to speed,
     * or we run out of time. The timeout always wins, so this can never
     * freeze the auto if the flywheel has a problem.
     */
    private void waitForFlywheel(long timeoutMs) {
        long start = System.currentTimeMillis();
        while (opModeIsActive()
                && !flywheel.isAtSpeed()
                && System.currentTimeMillis() - start < timeoutMs) {
            // The flywheel and turret need fresh position numbers to aim
            // and pick the right speed, so we feed them updates while we wait.
            double x = drivetrain.getX();
            double y = drivetrain.getY();
            double h = drivetrain.getH();
            flywheel.update(x, y, h);
            turret.update(x, y, h);
            telemetry.addData("Flywheel", "spinning up...");
            telemetry.update();
            sleep(50);
        }
    }
}
