package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Flywheel {

    private DcMotor motor;
    private Servo gate;

    public void init(HardwareMap hardwareMap) {
        motor = hardwareMap.get(DcMotor.class, "flywheel");
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        gate = hardwareMap.get(Servo.class, "gate");
    }
}
