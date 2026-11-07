package org.firstinspires.ftc.teamcode;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.mechanisms.Flywheel;
import org.firstinspires.ftc.teamcode.mechanisms.Intake;
import org.firstinspires.ftc.teamcode.mechanisms.Turret;
import org.firstinspires.ftc.teamcode.pedro.Constants;

@TeleOp
public class BiobuzzTeleOpSimple extends OpMode {

    private Follower follower;
    private final Flywheel flywheel = new Flywheel();
    private final Intake intake = new Intake();
    private final Turret turret = new Turret();
    private final ElapsedTime gateDelay = new ElapsedTime();
    private boolean shooting = false;
    private double speed = 1;

    @Override
    public void init() {
        Pose startPose = new Pose(0, 0, Math.toRadians(90));
        follower = Constants.create(hardwareMap);
        follower.setPose(startPose);
        flywheel.init(hardwareMap, telemetry);
        intake.init(hardwareMap, telemetry);
        turret.init(hardwareMap, telemetry);
        flywheel.setState(Flywheel.State.CLOSED);
        flywheel.setTracking(Flywheel.Tracking.LOCKED);
        turret.setTracking(Turret.Tracking.LOCKED);
        telemetry.addLine("Initialized");
    }

    @Override
    public void loop() {

        if(gamepad2.back) {
            if(gamepad2.yWasPressed()) {
                flywheel.addOffset(50);
            } else if(gamepad2.aWasReleased()) {
                flywheel.addOffset(-50);
            } else if(gamepad2.xWasPressed()) {
                turret.addOffset(10);
            } else if(gamepad2.bWasPressed()) {
                turret.addOffset(-10);
            }
        } else {
            if(gamepad2.aWasPressed()) {
                gateDelay.reset();
                shooting = true;
            } else if(gamepad2.a) {
                if(gateDelay.seconds() >= 0.3) {
                    flywheel.setState(Flywheel.State.SHOOTING);
                }
            } else if(gamepad2.aWasReleased()) {
                flywheel.setState(Flywheel.State.CLOSED);
                shooting = false;
            } else if(gamepad2.dpadUpWasPressed()) {
                flywheel.setState(Flywheel.State.CLOSED);
            } else if(gamepad2.dpadDownWasPressed()) {
                flywheel.setState(Flywheel.State.IDLE);
            }
        }

        if(shooting) {
            intake.set(Intake.State.FEED);
        } else {
            if(gamepad2.right_trigger > 0) {
                intake.set(Intake.State.RUN, gamepad2.right_trigger);
            } else if(gamepad2.left_trigger > 0) {
                intake.set(Intake.State.SPIT);
            } else {
                intake.set(Intake.State.IDLE);
            }
        }

        if(gamepad2.bWasPressed()) {
            intake.setFlowerIntaking(true);
        } else if (gamepad2.bWasReleased()) {
            intake.setFlowerIntaking(false);
        }

        if(gamepad1.leftTriggerWasPressed()) {
            speed = 0.4;
        } else if(gamepad1.leftTriggerWasReleased()) {
            speed = 1;
        }

        if(gamepad1.a) {
            Pose resetPose = new Pose(9, 9, Math.toRadians(90));
            follower.setPose(resetPose);
        }

        DrivePowers powers = ManualDrive.fieldCentric(
                -gamepad1.left_stick_y * speed,
                -gamepad1.left_stick_x * speed,
                -gamepad1.right_stick_x * speed,
                follower.pose().heading()
        );
        ManualDrive.driveOrHold(follower, powers);

        follower.update();
        intake.update();
        flywheel.update(0, 0, 0);
        turret.update(0, 0, 0);
    }
}
