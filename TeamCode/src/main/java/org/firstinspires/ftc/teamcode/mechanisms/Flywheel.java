package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

public class Flywheel {

    private final double GATE_CLOSE_POS = 0.25;
    private final double GATE_OPEN_POS = 0.4;
    private int speedOffset = 0;
    // The speed we most recently asked the flywheel to spin at (encoder ticks per second).
    // Saved every time run() picks a new speed, so isAtSpeed() knows what "up to speed" means.
    private double targetVelocity = 0;
    // How close is "close enough" when checking the flywheel speed.
    // 150 ticks/sec of wiggle room avoids false alarms from the normal wobble of a spinning wheel.
    private static final double SPEED_TOLERANCE = 150;
    private DcMotorEx motor;
    private Servo gate;
    private Servo hood;
    public enum Tracking {
        TRACK_A,
        TRACK_B
    }
    private Tracking tracking = Tracking.TRACK_A;
    public enum State {
        SHOOTING,
        CLOSED,
        IDLE
    }
    private State state = State.CLOSED;

    public void update(double x, double y, double h) {

        switch(state) {
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
                    run(distance);
                    break;
                case TRACK_B:
                    relativeX = 60 - x;
                    relativeY = 96 - y;
                    distance = Math.sqrt(Math.pow(relativeX, 2) + Math.pow(relativeY, 2));
                    run(distance);
                    break;
            }
        } else {
            targetVelocity = 0;
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
        gate.setPosition(GATE_CLOSE_POS);
    }


    public void run(double distance) {
        double ticks = (int) (0.000263695 * Math.pow(distance, 4) - 0.0515588 * Math.pow(distance, 3) + 3.3993 * Math.pow(distance, 2) - 82.49407 * distance + 2045.59012 + speedOffset);
        targetVelocity = ticks;
        motor.setVelocity(ticks);
        double hoodAngle = 0.295177 * distance - 7.56207;
        setHoodPos(hoodAngle);
    }

    /**
     * True when the flywheel is actually spinning at the speed we asked for.
     * The auto waits for this before feeding each ball, so every shot leaves
     * at full power instead of while the wheel is still catching up.
     */
    public boolean isAtSpeed() {
        return Math.abs(motor.getVelocity() - targetVelocity) < SPEED_TOLERANCE;
    }

    public void addOffset(int offset) {
        speedOffset += offset;
    }

    public void setTracking(Tracking tracking) {
        this.tracking = tracking;
    }

    public void setState(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    private void setHoodPos(double degrees) {
        degrees = Range.clip(degrees, 0, 15);
        hood.setPosition(degrees * 0.027 + 0.054);
    }
}
