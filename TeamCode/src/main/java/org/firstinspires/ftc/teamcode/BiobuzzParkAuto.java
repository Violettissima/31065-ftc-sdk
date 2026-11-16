package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.pedro.Paths;

@Disabled
public class BiobuzzParkAuto extends OpMode {

    private Follower follower;

    @Override
    public void init() {
        follower = Constants.create(hardwareMap);
        follower.setPose(Paths.B);
    }

    @Override
    public void start() {
        follower.follow(Paths.sp());
    }

    @Override
    public void loop() {
        follower.update();
    }
}
