package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Intake {

    private double swiperPos = 0;
    private double speed = 0;
    private ElapsedTime swiperTimer = new ElapsedTime();
    private DcMotor outerMotor;
    private DcMotor innerMotor;
    private Servo rightSwiper;
    private CRServo leftRoller;
    private enum State {
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
                rightSwiper.setPosition(swiperPos);
                break;
            case FEED:
                innerMotor.setPower(1);
                outerMotor.setPower(-0.3);
                leftRoller.setPower(0);
                rightSwiper.setPosition(0.5);
                break;
            case SPIT:
                innerMotor.setPower(0);
                outerMotor.setPower(-1);
                leftRoller.setPower(0);
                rightSwiper.setPosition(0.5);
                break;
            case IDLE:
                innerMotor.setPower(0);
                outerMotor.setPower(-0.3);
                leftRoller.setPower(0);
                rightSwiper.setPosition(0.5);
                break;
        }

        if(swiperTimer.seconds() > 0.5) {
            swiperTimer.reset();
            if(swiperPos == 0) {
                swiperPos = 0.6;
            } else {
                swiperPos = 0;
            }
        }
        telemetry.addData("swiper timer", swiperTimer.seconds());
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
        rightSwiper = hardwareMap.get(Servo.class, "right_roller");
        leftRoller = hardwareMap.get(CRServo.class, "left_roller");
        leftRoller.setDirection(DcMotor.Direction.REVERSE);
        this.telemetry = telemetry;
    }

    public void setPower(double speed) {
        speed = Range.clip(speed, 0, 1);
        this.speed = speed;
        if (speed == 0) {
            state = State.IDLE;
        } else {
            if(state != State.RUN) {
                swiperTimer.reset();
                swiperPos = 0;
                state = State.RUN;
            }

        }
    }

    public void feed() {
        state = State.FEED;
    }

    public void spit() {
        state = State.SPIT;
    }
}
