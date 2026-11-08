package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

public class Paths {

    private static final PoseFactory poseFactory = PoseFactory.degrees();

    private static final Pose shootA = poseFactory.of(58, 131, -90);
    public static final Pose shootB = poseFactory.of(58, 9, 90);
    private static final Pose garden1 = poseFactory.of(30, 9, 180);
    private static final Pose garden2 = poseFactory.of(9, 9, 180);
    private static final Pose park = poseFactory.of(10, 96, 90);
    private static final Pose backup = poseFactory.of(20, 20, 0);
    private static final Pose shootAParkControl = poseFactory.of(59, 109, 0);
    private static final Pose shootBGarden1Control = poseFactory.of(45, 24, 0);
    private static final Pose shootBParkControl = poseFactory.of(25, 25, 0);
    private static final Pose garden2ShootAControl1 = poseFactory.of(30, 40, 0);
    private static final Pose garden2ShootAControl2 = poseFactory.of(5, 110, 0);
    private static final Pose garden2ShootAControl3 = poseFactory.of(62, 112, 0);
    private static final Pose garden2ShootBControl = poseFactory.of(45, 24, 0);

    public static Path bg1() {
        return com.pedropathing.api.Paths.curve(shootB, shootBGarden1Control, garden1).linear(shootB, garden1);
    }

    public static Path g1g2() {
        return com.pedropathing.api.Paths.line(garden1, garden2).tangent().with(Constants.foresightConfig.maxPathSpeed.at(0.2));
    }

    public static Path g2a() {
        return com.pedropathing.api.Paths.curve(garden2, garden2ShootAControl1, garden2ShootAControl2, garden2ShootAControl3, shootA).linear(garden2, shootA);
    }

    public static Path g2b() {
        return com.pedropathing.api.Paths.curve(garden2, garden2ShootBControl, shootB).linear(garden2, shootB);
    }

    public static Path ap() {
        return com.pedropathing.api.Paths.curve(shootA, shootAParkControl, park).linear(shootA, park);
    }

    public static Path bp() {
        return com.pedropathing.api.Paths.curve(shootB, shootBParkControl, park).constant(park);
    }

    public static Path backup() {
        return com.pedropathing.api.Paths.line(backup, park).tangent();
    }
}