package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

public class Drivetrain {

    private double speed = 1;
    private DcMotor frDrive;
    private DcMotor flDrive;
    private DcMotor brDrive;
    private DcMotor blDrive;
    private GoBildaPinpointDriver pinpoint;

    public void update() {
        pinpoint.update();
    }

    public void init(HardwareMap hardwareMap) {
        frDrive = hardwareMap.get(DcMotor.class, "fr");
        flDrive = hardwareMap.get(DcMotor.class, "fl");
        brDrive = hardwareMap.get(DcMotor.class, "br");
        blDrive = hardwareMap.get(DcMotor.class, "bl");
        frDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        flDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        brDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        blDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        flDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        brDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        blDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        flDrive.setDirection(DcMotor.Direction.REVERSE);
        blDrive.setDirection(DcMotor.Direction.REVERSE);
        frDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        flDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        brDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        blDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
        configurePinpoint();
    }

    public void start() {
        pinpoint.setPosition(new Pose2D(DistanceUnit.INCH, 60, 9, AngleUnit.DEGREES, 90));
    }

    public void drive(double forward, double right, double rotate){
        forward *= speed;
        right *= speed;
        rotate *= speed;
        double robotAngle = Math.toRadians(getH());
        double theta = Math.atan2(forward, right);
        double r = Math.hypot(forward, right);
        theta = AngleUnit.normalizeRadians(theta - robotAngle);
        double newForward = r * Math.sin(theta);
        double newRight = r * Math.cos(theta);
        driveRobotCentric(newForward, newRight, rotate);
    }

    public void driveRobotCentric(double forward, double right, double rotate){
        double frontRightPower = forward - right - rotate;
        double frontLeftPower = forward + right + rotate;
        double backRightPower = forward + right - rotate;
        double backLeftPower = forward - right + rotate;
        double maxSpeed = 1.0;

        maxSpeed = Math.max(maxSpeed, Math.abs(frontRightPower));
        maxSpeed = Math.max(maxSpeed, Math.abs(frontLeftPower));
        maxSpeed = Math.max(maxSpeed, Math.abs(backRightPower));
        maxSpeed = Math.max(maxSpeed, Math.abs(backLeftPower));

        frontRightPower = frontRightPower / maxSpeed;
        frontLeftPower = frontLeftPower / maxSpeed;
        backRightPower = backRightPower / maxSpeed;
        backLeftPower = backLeftPower / maxSpeed;

        frDrive.setPower(frontRightPower);
        flDrive.setPower(frontLeftPower);
        brDrive.setPower(backRightPower);
        blDrive.setPower(backLeftPower);
    }

    public void configurePinpoint(){
        pinpoint.setOffsets(0, 0, DistanceUnit.MM);
        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        pinpoint.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.REVERSED);
        pinpoint.resetPosAndIMU();
    }

    public void resetTracking() {
        pinpoint.setPosition(new Pose2D(DistanceUnit.INCH, 9, 9, AngleUnit.DEGREES, 90));
    }

    public void setSpeed(double speed) {
        this.speed = Range.clip(speed, 0, 1);
    }

    public double getX() {
        return pinpoint.getPosition().getX(DistanceUnit.INCH);
    }

    public double getY() {
        return pinpoint.getPosition().getY(DistanceUnit.INCH);
    }

    public double getH() {
        return pinpoint.getPosition().getHeading(AngleUnit.DEGREES);
    }

}
