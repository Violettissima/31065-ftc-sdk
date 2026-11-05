package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

@Disabled
public class FlywheelDefaultPIDFTuner extends OpMode {

    private DcMotorEx flywheel;
    private int vel = 800;
    double[] stepSizes = {1, 0.1, 0.01};
    int stepIndex = 0;
    double p = 18;
    double i = 0.2;
    double d = 0;
    double f = 22;

    @Override
    public void init() {
        flywheel = hardwareMap.get(DcMotorEx.class, "flywheel");
        flywheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        PIDFCoefficients coefs = flywheel.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);
        telemetry.addData("p", coefs.p);
        telemetry.addData("i", coefs.i);
        telemetry.addData("d", coefs.d);
        telemetry.addData("f", coefs.f);

        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(p, i, d, f);
        flywheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);
    }

    @Override
    public void loop() {

        if(gamepad1.rightTriggerWasPressed()) {
            if(vel == 800) {
                vel = 1000;
            } else {
                vel = 800;
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
            i += stepSizes[stepIndex];
        }

        if(gamepad1.dpadDownWasPressed()) {
            i -= stepSizes[stepIndex];
        }

        if(gamepad1.dpadRightWasPressed()) {
            d += stepSizes[stepIndex];
        }

        if(gamepad1.dpadLeftWasPressed()) {
            d -= stepSizes[stepIndex];
        }

        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(p, i, d, f);
        flywheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);

        flywheel.setVelocity(vel);

        telemetry.addData("target", vel);
        telemetry.addData("velocity", flywheel.getVelocity());
        telemetry.addData("error", flywheel.getVelocity() - vel);
        telemetry.addData("p", p);
        telemetry.addData("i", i);
        telemetry.addData("d", d);
        telemetry.addData("f", f);
        telemetry.addData("step size", stepSizes[stepIndex]);
    }
}
