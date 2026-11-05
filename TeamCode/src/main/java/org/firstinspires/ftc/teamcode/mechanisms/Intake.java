package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Intake {

    private double speed = 0;
    private boolean flowerIntaking = false;
    private DcMotor outerMotor;
    private DcMotor innerMotor;
    private CRServo leftRoller;
    private Servo flower;
    public enum State {
        RUN,
        FEED,
        SPIT,
        IDLE
    }
    private State state = State.IDLE;
    private Telemetry telemetry;

    public void update() {
        switch(state) {
            case RUN:
                innerMotor.setPower(speed);
                outerMotor.setPower(speed);
                leftRoller.setPower(1);
                break;
            case FEED:
                innerMotor.setPower(1);
                outerMotor.setPower(-0.3);
                leftRoller.setPower(0);
                break;
            case SPIT:
                innerMotor.setPower(0);
                outerMotor.setPower(-1);
                leftRoller.setPower(0);
                break;
            case IDLE:
                innerMotor.setPower(0);
                outerMotor.setPower(-0.3);
                leftRoller.setPower(0);
                break;
        }

        if(flowerIntaking) {
            flower.setPosition(1);
        } else {
            flower.setPosition(0);
        }
    }

    public void init(HardwareMap hardwareMap, Telemetry telemetry) {
        outerMotor = hardwareMap.get(DcMotor.class, "intake_outer");
        innerMotor = hardwareMap.get(DcMotor.class, "intake_inner");
        outerMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        innerMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        outerMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        innerMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        outerMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        innerMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftRoller = hardwareMap.get(CRServo.class, "left_roller");
        flower = hardwareMap.get(Servo.class, "flower");
        leftRoller.setDirection(DcMotor.Direction.REVERSE);
        this.telemetry = telemetry;
    }

    public void set(State state, double speed) {
        speed = Range.clip(speed, 0, 1);
        this.speed = speed;
        this.state = state;
    }

    public void set(State state) {
        this.speed = 1;
        this.state = state;
    }

    public void setFlowerIntaking(boolean flowerIntaking) {
        this.flowerIntaking = flowerIntaking;
    }
}
