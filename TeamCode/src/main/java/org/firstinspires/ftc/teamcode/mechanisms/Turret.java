package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Turret {

    private DcMotor motor;
    private Servo hood;

    public void init(HardwareMap hardwareMap) {
        motor = hardwareMap.get(DcMotor.class, "turret");
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        hood = hardwareMap.get(Servo.class, "hood");
    }
}
