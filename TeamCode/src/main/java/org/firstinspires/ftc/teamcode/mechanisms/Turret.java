package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class Turret {

    private final int TICKS_PER_REV = 774;
    private double encoder_offset = 0;
    private DcMotor motor;
    private Servo hood;
    public enum State {
        TRACK_A,
        TRACK_B,
        LOCKED
    }
    private State state = State.LOCKED;

    public void update(double x, double y, double h) {
        double xRelative;
        double yRelative;
        switch(state) {
            case TRACK_A:
                xRelative = 84 - x;
                yRelative = 90 - y;
                aim(xRelative, yRelative, h);
                break;
            case TRACK_B:
                xRelative = 84 - x;
                yRelative = 54 - y;
                aim(xRelative, yRelative, h);
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
        hood = hardwareMap.get(Servo.class, "hood");
    }

    public void setState(State state) {
        this.state = state;
    }

    private void aim(double xRelative, double yRelative, double robotH) {
        double hAbsolute = AngleUnit.DEGREES.toDegrees(Math.atan2(yRelative, xRelative));
        double hRelative = hAbsolute - robotH;
        setAngle(hRelative);
    }

    public void setAngle(double degrees) {
        double safeDegrees = Range.clip(degrees, -180, 90);
        motor.setTargetPosition((int) (safeDegrees / 360 * TICKS_PER_REV + encoder_offset));
    }
}
