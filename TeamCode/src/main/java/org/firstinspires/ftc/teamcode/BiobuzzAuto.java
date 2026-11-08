package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
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
        SPIN_UP_1,
        SHOOT_1,
        DRIVE_A_GARDEN,
        DRIVE_GARDEN,
        DRIVE_GARDEN_SHOOT_2,
        SPIN_UP_2,
        SHOOT_2,
        DRIVE_PARK
    }
    private State state = State.SPIN_UP_1;
    private State lastState = null;
    private boolean isFirstRun;
    private final ElapsedTime stateTimer = new ElapsedTime();
    private final ElapsedTime autoTimer = new ElapsedTime();

    @Override
    public void init() {
        follower = Constants.create(hardwareMap);
        follower.setPose(Paths.shootB);
        flywheel.init(hardwareMap, telemetry);
        intake.init(hardwareMap, telemetry);
        turret.init(hardwareMap, telemetry);
        flywheel.setTracking(Flywheel.Tracking.LOCKED);
        turret.setTracking(Turret.Tracking.LOCKED);
    }

    @Override
    public void start() {
        stateTimer.reset();
        autoTimer.reset();
    }

    @Override
    public void loop() {

        isFirstRun = state != lastState;

        handleState();

        follower.update();
        intake.update();
        flywheel.update(0, 0, 0);
        turret.update(0, 0, 0);

        telemetry.addData("state", state);
    }

    @Override
    public void stop() {
        Pose currentPose = follower.pose();
        OpModeStorage.save(currentPose, turret.getCurrentPosition());
    }

    private void handleState() {
        lastState = state;

        switch(state) {
            case SPIN_UP_1:
                if(isFirstRun) {
                    flywheel.setState(Flywheel.State.CLOSED);
                    intake.set(Intake.State.FEED);
                } else if(stateTimer.seconds() > 4 || flywheel.isAtSpeed()) {
                    setState(State.SHOOT_1);
                }
                break;
            case SHOOT_1:
                if(isFirstRun) {
                    flywheel.setState(Flywheel.State.AUTO_SHOOTING);
                    intake.set(Intake.State.FEED);
                } else if(flywheel.getState() == Flywheel.State.IDLE) {
                    setState(State.DRIVE_A_GARDEN);
                }
                break;
            case DRIVE_A_GARDEN:
                if(isFirstRun) {
                    intake.set(Intake.State.IDLE);
                    follower.follow(Paths.bg1());
                } else if(!follower.isBusy()) {
                    setState(State.DRIVE_GARDEN);
                }
                break;
            case DRIVE_GARDEN:
                if(isFirstRun) {
                    intake.set(Intake.State.RUN);
                    follower.follow(Paths.g1g2());
                } else if(stateTimer.seconds() >= 7) {
                    setState(State.DRIVE_GARDEN_SHOOT_2);
                }
                break;
            case DRIVE_GARDEN_SHOOT_2:
                if(isFirstRun) {
                    intake.set(Intake.State.IDLE);
                    follower.follow(Paths.g2a());
                } else if(!follower.isBusy()) {
                    setState(State.SPIN_UP_2);
                }
                break;
            case SPIN_UP_2:
                if(isFirstRun) {
                    flywheel.setState(Flywheel.State.CLOSED);
                    intake.set(Intake.State.IDLE);
                } else if(stateTimer.seconds() > 3 || flywheel.isAtSpeed()) {
                    setState(State.SHOOT_2);
                }
                break;
            case SHOOT_2:
                if(isFirstRun) {
                    flywheel.setState(Flywheel.State.AUTO_SHOOTING);
                    intake.set(Intake.State.FEED);
                } else if(flywheel.getState() == Flywheel.State.IDLE) {
                    setState(State.DRIVE_PARK);
                }
                break;
            case DRIVE_PARK:
                if(isFirstRun) {
                    intake.set(Intake.State.IDLE);
                    follower.follow(Paths.ap());
                }
                break;
        }
    }

    private void setState(State state) {
        this.state = state;
        stateTimer.reset();
    }
}
