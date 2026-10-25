package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Flywheel {

    private final double P = 10;
    private final double F = 0.7;
    private final double C = 0.1;
    private final double GATE_CLOSE_POS = 0.25;
    private final double GATE_OPEN_POS = 0.4;
    private int speedOffset = 0;
    private final ElapsedTime stateTimer = new ElapsedTime();
    private DcMotorEx motor;
    private Servo gate;
    private Servo hood;
    public enum Tracking {
        TRACK_A,
        TRACK_B,
        LOCKED
    }
    private Tracking tracking = Tracking.TRACK_A;
    public enum State {
        AUTO_SHOOTING,
        SHOOTING,
        CLOSED,
        IDLE
    }
    private State state = State.IDLE;
    private Telemetry telemetry;

    public void update(double x, double y, double h) {
        telemetry.addData("velocity", motor.getVelocity());

        switch(state) {
            case AUTO_SHOOTING:
                if(stateTimer.seconds() >= 4) {
                    state = State.IDLE;
                }
            case SHOOTING:
                if(gate.getPosition() != GATE_OPEN_POS) {
                    gate.setPosition(GATE_OPEN_POS);
                }
                break;
            case CLOSED:
                if(gate.getPosition() != GATE_CLOSE_POS) {
                    gate.setPosition(GATE_CLOSE_POS);
                }
                break;
        }

        if(state != State.IDLE) {
            double shooterX = x + 5 * Math.sin(Math.toRadians(h));
            double shooterY = y - 5 * Math.cos(Math.toRadians(h));
            double relativeX;
            double relativeY;
            double distance;
            switch(tracking) {
                case TRACK_A:
                    relativeX = 60 - shooterX;
                    relativeY = 48 - shooterY;
                    distance = Math.sqrt(Math.pow(relativeX, 2) + Math.pow(relativeY, 2));
                    track(distance);
                    break;
                case TRACK_B:
                    relativeX = 60 - x;
                    relativeY = 96 - y;
                    distance = Math.sqrt(Math.pow(relativeX, 2) + Math.pow(relativeY, 2));
                    track(distance);
                    break;
                case LOCKED:
                    runFlywheel(900 + speedOffset);
                    setHoodPos(0);
            }
        } else {
            motor.setPower(0);
            setHoodPos(0);
        }
    }

    public void init(HardwareMap hardwareMap, Telemetry telemetry) {
        motor = hardwareMap.get(DcMotorEx.class, "flywheel");
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        gate = hardwareMap.get(Servo.class, "gate");
        hood = hardwareMap.get(Servo.class, "hood");
        setHoodPos(0);
        gate.setPosition(GATE_CLOSE_POS);
        this.telemetry = telemetry;
    }

    public void track(double distance) {
        int ticks = (int) (0.000263695 * Math.pow(distance, 4) - 0.0515588 * Math.pow(distance, 3) + 3.3993 * Math.pow(distance, 2) - 82.49407 * distance + 2045.59012 + speedOffset);
        runFlywheel(ticks);
        double hoodAngle = 0.295177 * distance - 7.56207;
        setHoodPos(hoodAngle);
    }

    public void runFlywheel(int target) {
        int currentSpeed = (int) motor.getVelocity();
        int error = target - currentSpeed;
        motor.setPower((P *  error + F * target) / 1000 + C);
    }

    public void resetOffset() {
        speedOffset = 0;
    }

    public void addOffset(int offset) {
        speedOffset += offset;
    }

    public void setTracking(Tracking tracking) {
        this.tracking = tracking;
    }

    public void setState(State state) {
        stateTimer.reset();
        this.state = state;
    }

    public State getState() {
        return state;
    }

    private void setHoodPos(double degrees) {
        degrees = Range.clip(degrees, 0, 15);
        hood.setPosition(degrees * 0.027 + 0.162);
    }
}
