package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class Turret {

    private final int TICKS_PER_REV = 760;
    private double encoder_offset = 0;
    private DcMotor motor;
    public enum State {
        TRACK_A,
        TRACK_B,
        LOCKED
    }
    private State state = State.LOCKED;

    public void update(double x, double y, double h) {
        double shooterX = x + 5 * Math.sin(Math.toRadians(h));
        double shooterY = y - 5 * Math.cos(Math.toRadians(h));
        double relativeX;
        double relativeY;
        switch(state) {
            case TRACK_A:
                relativeX = 60 - shooterX;
                relativeY = 60 - shooterY;
                aim(relativeX, relativeY, h);
                break;
            case TRACK_B:
                relativeX = 60 - shooterX;
                relativeY = 84 - shooterY;
                aim(relativeX, relativeY, h);
                break;
            case LOCKED:
                setAngle(0);
                break;
        }
    }

    public void init(HardwareMap hardwareMap) {
        motor = hardwareMap.get(DcMotor.class, "turret");
        motor.setTargetPosition(0);
        motor.setPower(0.3);
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void setState(State state) {
        this.state = state;
    }

    private void aim(double relativeX, double relativeY, double robotH) {
        double hAbsolute = AngleUnit.normalizeDegrees(Math.toDegrees(Math.atan2(relativeY, relativeX)) - 90);
        double hRelative = AngleUnit.normalizeDegrees(hAbsolute - robotH);
        setAngle(hRelative);
    }

    public void setAngle(double degrees) {
        double safeDegrees = Range.clip(degrees, -135, 90);
        motor.setTargetPosition((int) (safeDegrees / 360 * TICKS_PER_REV + encoder_offset));
    }
}
