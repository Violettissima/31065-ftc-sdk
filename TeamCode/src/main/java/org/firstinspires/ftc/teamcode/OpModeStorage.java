package org.firstinspires.ftc.teamcode;

import com.pedropathing.math.Pose;

public class OpModeStorage {
    public static Pose savedPose = new Pose(9, 9 , Math.toRadians(90));
    public static int turretPos = 0;

    public static void save(Pose pose, int pos) {
        savedPose = pose;
        turretPos = pos;
    }

    public static Pose getSavedPose() {
        return savedPose;
    }

    public static int getSavedTurretPos() {
        return turretPos;
    }
}
