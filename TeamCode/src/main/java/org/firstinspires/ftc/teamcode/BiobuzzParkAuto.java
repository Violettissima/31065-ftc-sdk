package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.mechanisms.Drivetrain;

@Autonomous
public class BiobuzzParkAuto extends OpMode {

    ElapsedTime timer = new ElapsedTime();
    Drivetrain drivetrain = new Drivetrain();

    @Override
    public void init() {
        drivetrain.init(hardwareMap);
    }

    @Override
    public void start() {
        timer.reset();
    }

    @Override
    public void loop() {
        if(timer.seconds() < 1) {
            drivetrain.driveRobotCentric(0.5, 0, 0);
        } else {
            drivetrain.drive(0, 0, 0);
        }
    }
}
