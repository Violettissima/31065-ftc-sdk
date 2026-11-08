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
public class BiobuzzTeleOp extends OpMode {

    private Follower follower;
    private final Flywheel flywheel = new Flywheel();
    private final Intake intake = new Intake();
    private final Turret turret = new Turret();
    private final ElapsedTime gateDelay = new ElapsedTime();
    private boolean shooting = false;
    private boolean flywheelInterrupted = true;
    private double speed = 1;
    private boolean shooterLocked = false;

    @Override
    public void init() {
        Pose startPose = new Pose(0, 60, Math.toRadians(90));
        follower = Constants.create(hardwareMap);
        follower.setPose(startPose);
        flywheel.init(hardwareMap, telemetry);
        intake.init(hardwareMap, telemetry);
        turret.init(hardwareMap, telemetry);
        flywheel.setState(Flywheel.State.CLOSED);
        flywheel.setTracking(Flywheel.Tracking.TRACK_A);
        turret.setTracking(Turret.Tracking.TRACK_A);
        telemetry.addLine("Initialized");
    }

    @Override
    public void loop() {

        double x = follower.pose().x();
        double y = follower.pose().y();
        double h = Math.toDegrees(follower.pose().heading());

        if(!shooterLocked) {
            if (y < 48) {
                flywheel.setTracking(Flywheel.Tracking.TRACK_B);
                turret.setTracking(Turret.Tracking.TRACK_B);
            } else if (y > 96) {
                flywheel.setTracking(Flywheel.Tracking.TRACK_A);
                turret.setTracking(Turret.Tracking.TRACK_A);
            }
        }

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
                if(flywheel.getState() == Flywheel.State.CLOSED) {
                    flywheel.setState(Flywheel.State.IDLE);
                    flywheelInterrupted = true;
                }
            } else if(gamepad2.left_trigger > 0) {
                intake.set(Intake.State.SPIT);
                if(flywheel.getState() == Flywheel.State.CLOSED) {
                    flywheel.setState(Flywheel.State.IDLE);
                    flywheelInterrupted = true;
                }
            } else {
                intake.set(Intake.State.IDLE);
                if(flywheelInterrupted) {
                    flywheel.setState(Flywheel.State.CLOSED);
                    flywheelInterrupted = false;
                }
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

        if(gamepad2.yWasPressed()) {
            shooterLocked = !shooterLocked;
            if(shooterLocked) {
                flywheel.setTracking(Flywheel.Tracking.LOCKED);
                turret.setTracking(Turret.Tracking.LOCKED);
            }
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
        follower.manual(powers);

        follower.update();
        intake.update();
        flywheel.update(x, y, h);
        turret.update(x, y, h);

        telemetry.addData("x", x);
        telemetry.addData("y", y);
        telemetry.addData("h", h);
    }
}
