package org.firstinspires.ftc.teamcode;

import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@Autonomous
public class DummyAuto extends OpMode {

    @Override
    public void init() {
        Pose startPose = new Pose(9, 9, Math.toRadians(90));
        OpModeStorage.savedPose = startPose;
    }

    @Override
    public void loop() {

    }
}
