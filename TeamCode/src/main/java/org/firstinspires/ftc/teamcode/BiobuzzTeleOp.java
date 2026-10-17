package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.Drivetrain;
import org.firstinspires.ftc.teamcode.mechanisms.Flywheel;
import org.firstinspires.ftc.teamcode.mechanisms.Intake;
import org.firstinspires.ftc.teamcode.mechanisms.Turret;

@TeleOp
public class BiobuzzTeleOp extends OpMode {

    private final Drivetrain drivetrain = new Drivetrain();
    private final Flywheel flywheel = new Flywheel();
    private final Intake intake = new Intake();
    private final Turret turret = new Turret();

    @Override
    public void init() {
        drivetrain.init(hardwareMap);
        flywheel.init(hardwareMap);
        intake.init(hardwareMap);
        turret.init(hardwareMap);
    }

    @Override
    public void loop() {

        double x = drivetrain.getX();
        double y = drivetrain.getY();
        double h = drivetrain.getH();

        if(y < 48) {
            flywheel.setTracking(Flywheel.Tracking.TRACK_A);
            turret.setTracking(Turret.Tracking.TRACK_A);
        } else if(y > 96) {
            flywheel.setTracking(Flywheel.Tracking.TRACK_B);
            turret.setTracking(Turret.Tracking.TRACK_B);
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
            if(gamepad2.a) {
                flywheel.setState(Flywheel.State.SHOOTING);
            } else if(gamepad2.aWasReleased()) {
                flywheel.setState(Flywheel.State.CLOSED);
            } else if(gamepad2.dpadUpWasPressed()) {
                flywheel.setState(Flywheel.State.CLOSED);
            } else if(gamepad2.dpadDownWasPressed()) {
                flywheel.setState(Flywheel.State.IDLE);
            }
        }

        if(flywheel.getState() == Flywheel.State.SHOOTING) {
            intake.feed();
        } else {
            intake.setPower(gamepad2.right_trigger);
        }

        if(gamepad1.leftTriggerWasPressed()) {
            drivetrain.setSpeed(0.4);
        } else if(gamepad1.leftTriggerWasReleased()) {
            drivetrain.setSpeed(1);
        }

        drivetrain.drive(-gamepad1.left_stick_x, -gamepad1.left_stick_y, gamepad1.right_stick_x);
        flywheel.update(x, y, h);
        turret.update(x, y, h);

        telemetry.addData("x", x);
        telemetry.addData("y", y);
        telemetry.addData("h", h);
    }
}