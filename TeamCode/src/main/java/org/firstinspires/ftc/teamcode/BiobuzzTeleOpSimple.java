package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

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
    private final ElapsedTime gateDelay = new ElapsedTime();
    private boolean trackGateDelay = false;

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
                if(trackGateDelay == false){
                    gateDelay.reset();
                    trackGateDelay = true;
                }
                if(gateDelay.seconds() > 0.5){
                    flywheel.setState(Flywheel.State.SHOOTING);
                }
            } else if(gamepad2.aWasReleased()) {
                flywheel.setState(Flywheel.State.CLOSED);
                trackGateDelay = false;
            } else if(gamepad2.dpadUpWasPressed()) {
                flywheel.setState(Flywheel.State.CLOSED);
            } else if(gamepad2.dpadDownWasPressed()) {
                flywheel.setState(Flywheel.State.IDLE);
            }
        }


            if(flywheel.getState() == Flywheel.State.SHOOTING) {
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
