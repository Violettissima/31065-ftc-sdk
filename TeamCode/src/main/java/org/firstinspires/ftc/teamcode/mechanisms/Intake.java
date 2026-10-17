package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

public class Intake {

    private DcMotor outerMotor;
    private DcMotor innerMotor;
    private CRServo rightRoller;
    private CRServo leftRoller;

    public void init(HardwareMap hardwareMap) {
        outerMotor = hardwareMap.get(DcMotor.class, "intake_outer");
        innerMotor = hardwareMap.get(DcMotor.class, "intake_inner");
        outerMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        innerMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        outerMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        innerMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        outerMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        innerMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightRoller = hardwareMap.get(CRServo.class, "right_roller");
        leftRoller = hardwareMap.get(CRServo.class, "left_roller");
        leftRoller.setDirection(DcMotor.Direction.REVERSE);
    }

    public void setPower(double speed) {
        speed = Range.clip(speed, 0, 1);
        if (speed > 0) {
            innerMotor.setPower(speed);
            outerMotor.setPower(speed);
            leftRoller.setPower(1);
            rightRoller.setPower(1);
        } else {
            innerMotor.setPower(0);
            outerMotor.setPower(-0.3);
            leftRoller.setPower(0);
            rightRoller.setPower(0);
        }
    }

    public void feed() {
        innerMotor.setPower(1);
        outerMotor.setPower(-0.3);
        leftRoller.setPower(0);
        rightRoller.setPower(0);
    }
}
