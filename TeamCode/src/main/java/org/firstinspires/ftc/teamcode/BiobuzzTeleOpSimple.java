package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.Drivetrain;
import org.firstinspires.ftc.teamcode.mechanisms.Flywheel;
import org.firstinspires.ftc.teamcode.mechanisms.Intake;
import org.firstinspires.ftc.teamcode.mechanisms.Turret;

@TeleOp
public class BiobuzzTeleOpSimple extends OpMode {
    private final Drivetrain drivetrain = new Drivetrain();
    private final Flywheel flywheel = new Flywheel();
    private final Intake intake = new Intake();
    private final Turret turret = new Turret();

    @Override
    public void init() {
        drivetrain.init(hardwareMap);
        flywheel.init(hardwareMap, telemetry);
        intake.init(hardwareMap, telemetry);
        turret.init(hardwareMap, telemetry);
        flywheel.setState(Flywheel.State.CLOSED);
        flywheel.setTracking(Flywheel.Tracking.LOCKED);
        turret.setTracking(Turret.Tracking.LOCKED);
        telemetry.addLine("Initialized");
    }

    @Override
    public void start() {
        drivetrain.start();
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
            if(gamepad2.right_trigger > 0) {
                intake.setPower(gamepad2.right_trigger);
            } else if(gamepad2.left_trigger > 0) {
                intake.spit();
            } else {
                intake.setPower(0);
            }

        }

        if(gamepad1.leftTriggerWasPressed()) {
            drivetrain.setSpeed(0.4);
        } else if(gamepad1.leftTriggerWasReleased()) {
            drivetrain.setSpeed(1);
        }

        if(gamepad1.a) {
            drivetrain.resetTracking();
        }

        drivetrain.drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);

        drivetrain.update();
        intake.update();
        flywheel.update(0, 0, 0);
        turret.update(0, 0, 0);

        telemetry.addData("x", drivetrain.getX());
        telemetry.addData("y", drivetrain.getY());
        telemetry.addData("h", drivetrain.getH());
    }
}
