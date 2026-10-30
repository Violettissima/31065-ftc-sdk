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
        PARKED,
        BACKUP
    }
    private State state = State.SPIN_UP;
    private ElapsedTime stateTimer = new ElapsedTime();
    private ElapsedTime autoTimer = new ElapsedTime();

    @Override
    public void init() {
        follower = Constants.create(hardwareMap);
        follower.setPose(Paths.shootB);
        flywheel.init(hardwareMap, telemetry);
        intake.init(hardwareMap, telemetry);
        turret.init(hardwareMap, telemetry);
        flywheel.setState(Flywheel.State.CLOSED);
        flywheel.setTracking(Flywheel.Tracking.LOCKED);
        intake.set(Intake.State.IDLE);
        turret.setTracking(Turret.Tracking.LOCKED);
    }

    @Override
    public void start() {
        stateTimer.reset();
        autoTimer.reset();
    }

    @Override
    public void loop() {
        telemetry.addData("state", state);
        switch(state) {
            case SPIN_UP:
                if(stateTimer.seconds() > 3) {
                    state = State.SHOOT;
                    flywheel.setState(Flywheel.State.AUTO_SHOOTING);
                    intake.set(Intake.State.FEED);
                }
                break;
            case SHOOT:
                if(flywheel.getState() == Flywheel.State.IDLE) {
                    state = State.DRIVE_SHOOT_GARDEN;
                    intake.set(Intake.State.IDLE);
                    follower.follow(Paths.bg1());
                }
                break;
            case DRIVE_SHOOT_GARDEN:
                if(!follower.isBusy()) {
                    state = State.DRIVE_INTAKE_GARDEN;
                    intake.set(Intake.State.RUN);
                    follower.follow(Paths.g1g2());
                }
                break;
            case DRIVE_INTAKE_GARDEN:
                if(!follower.isBusy()) {
                    state = State.DRIVE_GARDEN_PARK;
                    intake.set(Intake.State.IDLE);
                    follower.follow(Paths.g2p());
                }
                break;
            case DRIVE_GARDEN_PARK:
                if(!follower.isBusy()) {
                    state = State.PARKED;
                }
                break;
            case BACKUP:
                follower.follow(Paths.backup());
                intake.set(Intake.State.IDLE);
                flywheel.setState(Flywheel.State.IDLE);
        }

        if(autoTimer.seconds() >= 20) {
            state = State.BACKUP;
        }

        follower.update();
        intake.update();
        flywheel.update(0, 0, 0);
        turret.update(0, 0, 0);
    }
}
