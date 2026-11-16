package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.Locale;

@TeleOp
public class DistanceSensorTest extends OpMode {

    private final DistanceSensor[] distanceSensors = new DistanceSensor[4];

    @Override
    public void init() {
        distanceSensors[0] = hardwareMap.get(DistanceSensor.class, "color0");
        distanceSensors[1] = hardwareMap.get(DistanceSensor.class, "color1");
        distanceSensors[2] = hardwareMap.get(DistanceSensor.class, "color2");
        distanceSensors[3] = hardwareMap.get(DistanceSensor.class, "color3");
    }

    @Override
    public void loop() {
        for(int i = 0; i < 4; i++) {
            telemetry.addData(String.format(Locale.US, "distance from sensor %d", i), distanceSensors[i].getDistance(DistanceUnit.CM));
        }
    }
}
