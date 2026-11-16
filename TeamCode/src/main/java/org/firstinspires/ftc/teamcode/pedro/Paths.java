package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

public class Paths {

    private static final PoseFactory poseFactory = PoseFactory.degrees();

    public static final Pose A = poseFactory.of(60, 132, -90);
    public static final Pose B = poseFactory.of(58, 9, 90);
    public static final Pose G1 = poseFactory.of(30, 9, 180);
    public static final Pose G2 = poseFactory.of(9, 9, 180);
    public static final Pose P = poseFactory.of(12, 96, 90);
    public static final Pose S = poseFactory.of(9, 80, 90);
    public static final Pose X1 = poseFactory.of(20, 48, 180);
    public static final Pose X2 = poseFactory.of(15, 48, 180);
    public static final Pose Y1 = poseFactory.of(47, 121, 90);
    public static final Pose Y2 = poseFactory.of(47, 126, 90);

    private static final Pose APC1 = poseFactory.of(59, 109, 0);
    private static final Pose AY1C1 = poseFactory.of(63, 119, 0);
    private static final Pose BG1C1 = poseFactory.of(45, 24, 0);
    private static final Pose BPC1 = poseFactory.of(25, 25, 0);
    private static final Pose G2AC1 = poseFactory.of(30, 40, 0);
    private static final Pose G2AC2 = poseFactory.of(5, 110, 0);
    private static final Pose G2AC3 = poseFactory.of(62, 112, 0);
    private static final Pose G2BC1 = poseFactory.of(45, 24, 0);
    private static final Pose X2AC1 = poseFactory.of(35, 48, 0);
    private static final Pose X2AC2 = poseFactory.of(0, 130, 0);
    private static final Pose X2AC3 = poseFactory.of(60, 113, 0);
    private static final Pose Y2AC1 = poseFactory.of(52, 112, 0);

    public static Path ap() {
        return com.pedropathing.api.Paths.curve(A, APC1, P).linear(A, P);
    }
    public static Path ay1() {
        return com.pedropathing.api.Paths.curve(A, AY1C1, Y1).linear(A, Y1);
    }
    public static Path bg1() {
        return com.pedropathing.api.Paths.curve(B, BG1C1, G1).linear(B, G1);
    }
    public static Path bp() {
        return com.pedropathing.api.Paths.curve(B, BPC1, P).constant(P);
    }
    public static Path bx1() {
        return com.pedropathing.api.Paths.line(B, X1).linear(B, X1);
    }
    public static Path g1g2() {
        return com.pedropathing.api.Paths.line(G1, G2).tangent().with(Constants.foresightConfig.maxPathSpeed.at(0.2));
    }
    public static Path g2a() {
        return com.pedropathing.api.Paths.curve(G2, G2AC1, G2AC2, G2AC3, A).linear(G2, A);
    }
    public static Path g2b() {
        return com.pedropathing.api.Paths.curve(G2, G2BC1, B).linear(G2, B);
    }
    public static Path sp() {
        return com.pedropathing.api.Paths.line(S, P).constant(P);
    }
    public static Path x1x2() {
        return com.pedropathing.api.Paths.line(X1, X2).constant(X2);
    }
    public static Path x2a() {
        return com.pedropathing.api.Paths.curve(X2, X2AC1, X2AC2, X2AC3, A).linear(X2, A);
    }
    public static Path y1y2() {
        return com.pedropathing.api.Paths.line(Y1, Y2).constant(Y2);
    }
    public static Path y2a() {
        return com.pedropathing.api.Paths.curve(Y2, Y2AC1, A).linear(Y2, A);
    }

}