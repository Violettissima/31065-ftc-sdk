package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp
public class GateFlywheelTest extends OpMode {
    Servo swiper;
    double pos = 0;
    DcMotorEx flywheel;

    @Override
    public void init() {
        swiper = hardwareMap.get(Servo.class, "right_roller");
        flywheel = hardwareMap.get(DcMotorEx.class, "flywheel");
        flywheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        flywheel.setMotorType(new MotorConfigurationType());
    }

    @Override
    public void loop() {
        swiper.setPosition(pos);
        if(gamepad1.yWasPressed()) {
            pos += 0.1;
        } else if(gamepad1.aWasPressed()) {
            pos -= 0.1;
        }
        telemetry.addData("pos", pos);
        telemetry.addData("flywheel ticks/rev", flywheel.getMotorType().getTicksPerRev());
        telemetry.addData("flywheel ticks total", flywheel.getCurrentPosition());
        telemetry.addData("flywheel speed (°/s)", flywheel.getVelocity(AngleUnit.DEGREES));
    }
}
