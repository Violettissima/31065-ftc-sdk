package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Intake {

    private double speed = 0;
    private int possession = 0;
    private boolean flowerIntaking = false;
    private final double[] detectionPoints = {1.6, 1.3, 2.0, 1.7};
    public enum State {
        RUN,
        FEED,
        PARTIAL_EJECT,
        FULL_EJECT,
        IDLE
    }
    private State state = State.IDLE;
    public enum Mode {
        AUTO,
        MANUAL
    }
    private Mode mode = Mode.MANUAL;
    private DcMotor outerMotor;
    private DcMotor innerMotor;
    private CRServo leftRoller;
    private Servo flower;
    private final DistanceSensor[] distanceSensors = new DistanceSensor[4];
    private Telemetry telemetry;

    public void update() {
        possession = 0;
        for(int i = 0; i < 4; i++) {
            if (distanceSensors[i].getDistance(DistanceUnit.INCH) < detectionPoints[i]) {
                possession++;
            }
        }

        if(mode == Mode.AUTO) {
            if(possession >= 4) {
                set(State.PARTIAL_EJECT);
            } else {
                set(State.RUN);
            }
        }

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
            case PARTIAL_EJECT:
                innerMotor.setPower(0.5);
                outerMotor.setPower(-1);
                leftRoller.setPower(0);
                break;
            case FULL_EJECT:
                innerMotor.setPower(-1);
                outerMotor.setPower(-1);
                leftRoller.setPower(0);
                break;
            case IDLE:
                innerMotor.setPower(0);
                outerMotor.setPower(0);
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
        distanceSensors[0] = hardwareMap.get(DistanceSensor.class, "color0");
        distanceSensors[1] = hardwareMap.get(DistanceSensor.class, "color1");
        distanceSensors[2] = hardwareMap.get(DistanceSensor.class, "color2");
        distanceSensors[3] = hardwareMap.get(DistanceSensor.class, "color3");
        this.telemetry = telemetry;
    }

    public void set(State state, double speed) {
        speed = Range.clip(speed, 0, 1);
        this.speed = speed;
        this.state = state;
        this.mode = Mode.MANUAL;
    }

    public void set(State state) {
        this.speed = 1;
        set(state, 1);
    }

    public void setAuto() {
        this.mode = Mode.AUTO;
    }

    public void setFlowerIntaking(boolean flowerIntaking) {
        this.flowerIntaking = flowerIntaking;
    }

    public int getPossession() {
        return possession;
    }

    public State getState() {
        return state;
    }
}
