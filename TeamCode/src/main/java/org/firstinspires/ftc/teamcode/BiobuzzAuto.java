package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.mechanisms.Flywheel;
import org.firstinspires.ftc.teamcode.mechanisms.Intake;
import org.firstinspires.ftc.teamcode.mechanisms.Turret;
import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.pedro.Paths;

@Autonomous
public class BiobuzzAuto extends OpMode {

    private Follower follower;
    private final Flywheel flywheel = new Flywheel();
    private final Intake intake = new Intake();
    private final Turret turret = new Turret();
    private enum State {
        SPIN_UP,
        SHOOT,
        DRIVE_SHOOT_GARDEN,
        DRIVE_INTAKE_GARDEN,
        DRIVE_GARDEN_PARK,
        PARKED
    }
    private State state = State.SPIN_UP;
    private ElapsedTime stateTimer = new ElapsedTime();

    @Override
    public void init() {
        follower = Constants.create(hardwareMap);
        follower.setPose(Paths.garden2);
        flywheel.init(hardwareMap, telemetry);
        intake.init(hardwareMap, telemetry);
        turret.init(hardwareMap, telemetry);
        flywheel.setState(Flywheel.State.CLOSED);
        flywheel.setTracking(Flywheel.Tracking.LOCKED);
        intake.setPower(0);
        turret.setTracking(Turret.Tracking.LOCKED);
    }

    @Override
    public void start() {
        stateTimer.reset();
    }

    @Override
    public void loop() {
        switch(state) {
            case SPIN_UP:
                if(stateTimer.seconds() > 7) {
                    state = State.SHOOT;
                    flywheel.setState(Flywheel.State.AUTO_SHOOTING);
                    intake.feed();
                }
                break;
            case SHOOT:
                if(flywheel.getState() == Flywheel.State.IDLE) {
                    state = State.DRIVE_SHOOT_GARDEN;
                    intake.setPower(0);
                    follower.follow(Paths.bg1());
                }
                break;
            case DRIVE_SHOOT_GARDEN:
                if(!follower.isBusy()) {
                    state = State.DRIVE_INTAKE_GARDEN;
                    intake.setPower(1);
                    follower.follow(Paths.g1g2());
                }
                break;
            case DRIVE_INTAKE_GARDEN:
                if(!follower.isBusy()) {
                    state = State.DRIVE_GARDEN_PARK;
                    intake.setPower(0);
                    follower.follow(Paths.g2p());
                }
                break;
            case DRIVE_GARDEN_PARK:
                if(!follower.isBusy()) {
                    state = State.PARKED;
                }
                break;

        }

        follower.update();
        intake.update();
        flywheel.update(0, 0, 0);
        turret.update(0, 0, 0);
    }
}
