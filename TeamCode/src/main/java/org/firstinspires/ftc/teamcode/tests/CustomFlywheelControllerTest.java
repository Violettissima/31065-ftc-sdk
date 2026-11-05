package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp
public class CustomFlywheelControllerTest extends OpMode {
    private double p = 0;
    private double f = 0;
    private double c = 0;
    private int vel = 700;
    double[] stepSizes = {1, 0.1, 0.01};
    int stepIndex = 0;
    private DcMotorEx motor;

    @Override
    public void init() {
        motor = hardwareMap.get(DcMotorEx.class, "flywheel");
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        telemetry.addLine("Initialized");
    }

    @Override
    public void loop() {

        telemetry.addData("vel", motor.getVelocity());
        if(gamepad1.rightTriggerWasPressed()) {
            if(vel == 700) {
                vel = 1000;
            } else {
                vel = 700;
            }
        }

        if(gamepad1.leftTriggerWasPressed()) {
            stepIndex = (stepIndex + 1) % stepSizes.length;
        }

        if(gamepad1.yWasPressed()) {
            f += stepSizes[stepIndex];
        }

        if(gamepad1.aWasPressed()) {
            f -= stepSizes[stepIndex];
        }

        if(gamepad1.bWasPressed()) {
            p += stepSizes[stepIndex];
        }

        if(gamepad1.xWasPressed()) {
            p -= stepSizes[stepIndex];
        }

        if(gamepad1.dpadUpWasPressed()) {
            c += stepSizes[stepIndex];
        }

        if(gamepad1.dpadDownWasPressed()) {
            c -= stepSizes[stepIndex];
        }

        runFlywheel(vel);

        telemetry.addData("target", vel);
        telemetry.addData("velocity", motor.getVelocity());
        telemetry.addData("error", motor.getVelocity() - vel);
        telemetry.addData("p", p);
        telemetry.addData("f", f);
        telemetry.addData("c", c);
        telemetry.addData("step size", stepSizes[stepIndex]);
    }

    public void runFlywheel(int target) {
        double currentSpeed = motor.getVelocity();
        double error = target - currentSpeed;
        telemetry.addData("error", error);
        motor.setPower((p *  error + f * target) / 1000 + c);
    }
}
