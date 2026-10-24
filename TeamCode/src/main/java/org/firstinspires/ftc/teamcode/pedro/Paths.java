package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

public class Paths {

    private static final PoseFactory poseFactory = PoseFactory.degrees();

    public static final Pose shootB = poseFactory.of(60, 10, 90);
    public static final Pose garden1 = poseFactory.of(30, 10, 180);
    public static final Pose garden2 = poseFactory.of(10, 10, 180);
    public static final Pose park = poseFactory.of(10, 96, 90);
    public static final Pose gardenParkControl1 = poseFactory.of(50, 50, 0);
    public static final Pose gardenParkControl2 = poseFactory.of(10, 70, 0);

    public static Path bg1() {
        return com.pedropathing.api.Paths.line(shootB, garden1).linear(shootB, garden1);
    }

    public static Path g1g2() {
        return com.pedropathing.api.Paths.line(garden1, garden2).tangent();
    }

    public static Path g2p() {
        return com.pedropathing.api.Paths.curve(garden2, gardenParkControl1, gardenParkControl2, park).linear(garden2, park);
    }
}