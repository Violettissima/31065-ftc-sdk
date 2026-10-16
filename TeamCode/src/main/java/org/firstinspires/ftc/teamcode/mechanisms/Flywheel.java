package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

public class Flywheel {

    private DcMotorEx motor;
    private Servo gate;
    private Servo hood;
    public enum State {
        TRACK_A,
        TRACK_B,
        IDLE
    }
    private State state = State.IDLE;

    public void update(double x, double y, double h) {
        double shooterX = x + 5 * Math.sin(Math.toRadians(h));
        double shooterY = y - 5 * Math.cos(Math.toRadians(h));
        double relativeX;
        double relativeY;
        double distance;
        switch(state) {
            case TRACK_A:
                relativeX = 60 - shooterX;
                relativeY = 48 - shooterY;
                distance = Math.sqrt(Math.pow(relativeX, 2) + Math.pow(relativeY, 2));
                run(distance);
                break;
            case TRACK_B:
                relativeX = 60 - x;
                relativeY = 96 - y;
                distance = Math.sqrt(Math.pow(relativeX, 2) + Math.pow(relativeY, 2));
                run(distance);
                break;
            case IDLE:
                motor.setVelocity(0);
        }
    }

    public void init(HardwareMap hardwareMap) {
        motor = hardwareMap.get(DcMotorEx.class, "flywheel");
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        gate = hardwareMap.get(Servo.class, "gate");
        hood = hardwareMap.get(Servo.class, "hood");
        setHoodPos(0);
        openGate();
    }


    public void run(double distance) {
        double ticks = (int) (0.000263695 * Math.pow(distance, 4) - 0.0515588 * Math.pow(distance, 3) + 3.3993 * Math.pow(distance, 2) - 82.49407 * distance + 2045.59012);
        motor.setVelocity(ticks);
        double hoodAngle = 0.295177 * distance - 7.56207;
        setHoodPos(hoodAngle);
    }

    public void setState(State state) {
        this.state = state;
    }

    private void setHoodPos(double degrees) {
        degrees = Range.clip(degrees, 0, 15);
        hood.setPosition(degrees * 0.027 + 0.054);
    }

    private void closeGate() {
        gate.setPosition(0.25);
    }

    private void openGate() {
        gate.setPosition(0.4);
    }
}
