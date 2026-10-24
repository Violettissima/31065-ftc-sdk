package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.controllers.Controller;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Matrix;
import com.pedropathing.math.Pose;
import com.pedropathing.math.Vector2D;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.PinpointConfig;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Constants {

    public static MecanumConfig drivetrainConfig = new MecanumConfig(c -> {
        c.frontLeftName.set("fl");
        c.frontRightName.set("fr");
        c.backLeftName.set("bl");
        c.backRightName.set("br");
        c.frontLeftDirection.set(DcMotorSimple.Direction.REVERSE);
        c.frontRightDirection.set(DcMotorSimple.Direction.FORWARD);
        c.backLeftDirection.set(DcMotorSimple.Direction.REVERSE);
        c.backRightDirection.set(DcMotorSimple.Direction.FORWARD);
        c.manualBrakeMode.set(true);
    });

    public static PinpointConfig localizerConfig = new PinpointConfig(c -> {
        c.name.set("pinpoint");
        c.podType.set(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        c.xPodOffset.set(0.0);
        c.yPodOffset.set(-8.0625);
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.offsetUnits.set(DistanceUnit.INCH);
    });

    /*public static ForesightConfig foresightConfig = new ForesightConfig(
            c -> {
                Controller primaryTranslationalForward = Controller.proportional(0.2939302072923117);
                Controller secondaryTranslationalForward = Controller.proportional(0.10859935763472185);
                Controller primaryTranslationalLateral = Controller.proportional(0.49207685480469);
                Controller secondaryTranslationalLateral = Controller.proportional(0.1818092493146125);

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.015429599528667381));
                c.brake.set(Controller.proportionalFeedforward(0.013115159599367274));

                c.headingFeedback.set(Controller.proportional(4.640437358457719));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.06731596208662204, 0.003908997177238078));

                c.linearBrakeCoefficients.set(Matrix.diag(0.10382487320850277, 0.06513083897499879));
                c.quadraticBrakeCoefficients.set(Matrix.diag(7.533253121304388E-4, 0.0014854026144342405));

                c.maxAchievableForwardVelocity.set(67.21132843487295);
                c.maxAchievableStrafeVelocity.set(51.57536776625788);
                c.naturalForwardDeceleration.set(40.66125354094247);
                c.naturalStrafeDeceleration.set(71.685246160532);
            }
    );*/

    public static ForesightConfig foresightConfig = new ForesightConfig(
            c -> {
                Controller primaryTranslationalForward = Controller.proportional(0.25877487497222845);
                Controller secondaryTranslationalForward = Controller.proportional(0.0956104017102313);
                Controller primaryTranslationalLateral = Controller.proportional(0.38246524311714325);
                Controller secondaryTranslationalLateral = Controller.proportional(0.14131068767227017);

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.01460388919277334));
                c.brake.set(Controller.proportionalFeedforward(0.012413305813857339));

                c.headingFeedback.set(Controller.proportional(4.234316584026245));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.06011341314424436, 0.005803135902080492));

                c.linearBrakeCoefficients.set(Matrix.diag(0.10133707165997902, 0.08768099524506794));
                c.quadraticBrakeCoefficients.set(Matrix.diag(0.0010242600635961693, 0.0011224249122114476));

                c.maxAchievableForwardVelocity.set(70.7474225348798);
                c.maxAchievableStrafeVelocity.set(54.19192693599665);
                c.naturalForwardDeceleration.set(40.95105601285128);
                c.naturalStrafeDeceleration.set(75.72905389044155);

                c.maxPathSpeed.set(0.5);
            }
    );

    public static Follower create(HardwareMap h) {
        return new Follower(
                new PinpointLocalizer(h, localizerConfig),
                new Mecanum(h, drivetrainConfig),
                new Foresight(foresightConfig)
        );
    }
}