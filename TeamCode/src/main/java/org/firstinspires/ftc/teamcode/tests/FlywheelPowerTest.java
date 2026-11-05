package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.mechanisms.Intake;

@TeleOp
public class FlywheelPowerTest extends OpMode {
    //find good values for shooting speed
    double hoodPos = 0;
    double minSpeed = 100000000;
    Intake intake = new Intake();
    private final double P = 5;
    private final double F = 0.54;
    private final double C = 0.07;
    private final double GATE_CLOSE_POS = 0.25;
    private final double GATE_OPEN_POS = 0.4;
    private int speed = 700;
    private DcMotorEx motor;
    private Servo gate;
    private Servo hood;
    public enum State {
        SHOOTING,
        CLOSED,
        IDLE
    }
    private State state = State.CLOSED;

    @Override
    public void init() {
        motor = hardwareMap.get(DcMotorEx.class, "flywheel");
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        gate = hardwareMap.get(Servo.class, "gate");
        hood = hardwareMap.get(Servo.class, "hood");
        gate.setPosition(GATE_CLOSE_POS);
        intake.init(hardwareMap, telemetry);
    }

    @Override
    public void loop() {

        if(gamepad1.yWasPressed()) {
            minSpeed = 1000000;
        }
        minSpeed = Math.min(minSpeed, motor.getVelocity());

        if(state == State.SHOOTING) {
            intake.set(Intake.State.FEED);
        } else if(gamepad1.right_trigger > 0) {
            intake.set(Intake.State.RUN, gamepad1.right_trigger);
        } else {
            intake.set(Intake.State.IDLE);
        }

        if(gamepad1.dpadUpWasPressed()) {
            speed += 20;
        } else if(gamepad1.dpadDownWasPressed()) {
            speed -= 20;
        }

        if(gamepad1.a) {
            state = State.SHOOTING;
        } else if(gamepad1.aWasReleased()) {
            state = State.CLOSED;
        } else if(gamepad1.dpadUpWasPressed()) {
            state = State.CLOSED;
        } else if(gamepad1.dpadDownWasPressed()) {
            state = State.IDLE;
        }

        if(gamepad1.dpadRightWasPressed()) {
            hoodPos += 1;
        } else if(gamepad1.dpadLeftWasPressed()) {
            hoodPos -= 1;
        }

        if(state == State.SHOOTING) {
            gate.setPosition(GATE_OPEN_POS);
        } else {
            gate.setPosition(GATE_CLOSE_POS);
        }

        if(state != State.IDLE) {
            runFlywheel(speed);
        } else {
            motor.setPower(0);
        }

        setHoodPos(hoodPos);

        intake.update();

        telemetry.addData("speed", speed);
        telemetry.addData("velocity", motor.getVelocity());
        telemetry.addData("hood pos", hoodPos);
        telemetry.addData("state", state);
        telemetry.addData("min speed since reset", minSpeed);
    }

    public void runFlywheel(int target) {
        int currentSpeed = (int) motor.getVelocity();
        int error = target - currentSpeed;
        motor.setPower((P *  error + F * target) / 1000 + C);
    }

    private void setHoodPos(double degrees) {
        degrees = Range.clip(degrees, 0, 15);
        hood.setPosition(degrees * 0.027 + 0.162);
    }
}
