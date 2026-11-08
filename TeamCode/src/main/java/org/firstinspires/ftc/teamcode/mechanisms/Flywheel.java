package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Flywheel {

    private final double P = 1.5;
    private final double F = 0.38;
    private final double C = 0.09;
    private final double SPEED_TOLERANCE = 20;
    private final double GATE_CLOSE_POS = 0.25;
    private final double GATE_OPEN_POS = 0.4;
    private int flywheelTarget = 0;
    private int speedOffset = 0;
    private final ElapsedTime stateTimer = new ElapsedTime();
    private DcMotorEx motor;
    private Servo gate;
    private Servo hood;
    private VoltageSensor batterySensor;
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
                if(stateTimer.seconds() >= 2) {
                    setState(State.IDLE);
                }
            case SHOOTING:
                if(gate.getPosition() != GATE_OPEN_POS) {
                    gate.setPosition(GATE_OPEN_POS);
                }
                break;
            case CLOSED:
            case IDLE:
                if(gate.getPosition() != GATE_CLOSE_POS) {
                    gate.setPosition(GATE_CLOSE_POS);
                }
                break;
        }

        if(state != State.IDLE) {
            double shooterX = x - 5 * Math.cos(Math.toRadians(h));
            double shooterY = y - 5 * Math.sin(Math.toRadians(h));
            telemetry.addData("shooter x", shooterX);
            telemetry.addData("shooter y", shooterY);
            double relativeX;
            double relativeY;
            double distance;
            switch(tracking) {
                case TRACK_A:
                    relativeX = 58 - shooterX;
                    relativeY = 86 - shooterY;
                    distance = Math.sqrt(Math.pow(relativeX, 2) + Math.pow(relativeY, 2));
                    track(distance);
                    break;
                case TRACK_B:
                    relativeX = 58 - shooterX;
                    relativeY = 54 - shooterY;
                    distance = Math.sqrt(Math.pow(relativeX, 2) + Math.pow(relativeY, 2));
                    track(distance);
                    break;
                case LOCKED:
                    runFlywheel(1350 + speedOffset);
                    setHoodPos(8);
            }
        } else {
            motor.setPower(0);
        }

        telemetry.addData("tracking", tracking);
    }

    public void init(HardwareMap hardwareMap, Telemetry telemetry) {
        motor = hardwareMap.get(DcMotorEx.class, "flywheel");
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        gate = hardwareMap.get(Servo.class, "gate");
        hood = hardwareMap.get(Servo.class, "hood");
        batterySensor = hardwareMap.get(VoltageSensor.class, "Control Hub");
        setHoodPos(0);
        gate.setPosition(GATE_CLOSE_POS);
        this.telemetry = telemetry;
    }

    public void track(double distance) {
        int ticks = 1350;
        runFlywheel(ticks);
        double hoodAngle = 8;
        setHoodPos(hoodAngle);
    }

    public void runFlywheel(int target) {
        int currentSpeed = (int) motor.getVelocity();
        int error = target - currentSpeed;
        double currentVoltage = batterySensor.getVoltage();
        double voltageOffset = Range.clip(((12 / currentVoltage) * 1.1), 1, 1.5);

        double power = (((P *  error + F * target) / 1000 + C) * voltageOffset);
        motor.setPower(power);
        flywheelTarget = target;

        telemetry.addData("voltageOffset", voltageOffset);
    }

    public boolean isAtSpeed() {
        int error = flywheelTarget - (int) motor.getVelocity();
        return error > -SPEED_TOLERANCE && error < SPEED_TOLERANCE;
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
