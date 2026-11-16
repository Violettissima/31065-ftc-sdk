package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.mechanisms.Intake;

@TeleOp
public class FlywheelPowerTest extends OpMode {
    //find good values for shooting speed
    double hoodPos = 0;
    double minSpeed = 100000000;
    Intake intake = new Intake();
    private final double P = 1.5;
    private final double F = 0.38;
    private final double C = 0.09;
    private final double GATE_CLOSE_POS = 0.25;
    private final double GATE_OPEN_POS = 0.4;
    private int speed = 700;
    private final ElapsedTime gateDelay = new ElapsedTime();
    private boolean shooting = false;
    private DcMotorEx motor;
    private VoltageSensor batterySensor;
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
        batterySensor = hardwareMap.get(VoltageSensor.class, "Control Hub");
        intake.init(hardwareMap, telemetry);
    }

    @Override
    public void loop() {

        if(gamepad1.yWasPressed()) {
            minSpeed = 1000000;
        }
        minSpeed = Math.min(minSpeed, motor.getVelocity());

        if(shooting) {
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

        if(gamepad1.aWasPressed()) {
            gateDelay.reset();
            shooting = true;
        } else if(gamepad1.a) {
            if(gateDelay.seconds() >= 0.3) {
                state = State.SHOOTING;
            }
        } else if(gamepad1.aWasReleased()) {
            state = State.CLOSED;
            shooting = false;
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
        double currentVoltage = batterySensor.getVoltage();
        double voltageOffset = Range.clip(((12 / currentVoltage) * 1.1), 1, 1.5);

        double power = (((P *  error + F * target) / 1000 + C) * voltageOffset);
        motor.setPower(power);
    }

    private void setHoodPos(double degrees) {
        degrees = Range.clip(degrees, 0, 15);
        hood.setPosition(degrees * 0.027 + 0.162);
    }
}
