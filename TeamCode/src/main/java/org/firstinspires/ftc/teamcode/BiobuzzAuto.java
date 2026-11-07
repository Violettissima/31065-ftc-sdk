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
        DRIVE_GARDEN_SHOOT2,
        SPIN_UP_2,
        SHOOT2,
        PARK,
        BACKUP
    }
    private State state = State.SPIN_UP;
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

    private void handleState() {
        telemetry.addData("flywheel at speed", flywheel.isAtSpeed());

        lastState = state;

        switch(state) {
            case SPIN_UP:
                if(isFirstRun) {
                    flywheel.setState(Flywheel.State.CLOSED);
                    intake.set(Intake.State.IDLE);
                } else if(stateTimer.seconds() > 3 || flywheel.isAtSpeed()) {
                    setState(State.SHOOT);
                }
                break;
            case SHOOT:
                if(isFirstRun) {
                    flywheel.setState(Flywheel.State.AUTO_SHOOTING);
                    intake.set(Intake.State.FEED);
                } else if(flywheel.getState() == Flywheel.State.IDLE) {
                    setState(State.DRIVE_SHOOT_GARDEN);
                }
                break;
            case DRIVE_SHOOT_GARDEN:
                if(isFirstRun) {
                    intake.set(Intake.State.IDLE);
                    follower.follow(Paths.bg1());
                } else if(!follower.isBusy()) {
                    setState(State.DRIVE_INTAKE_GARDEN);
                }
                break;
            case DRIVE_INTAKE_GARDEN:
                if(isFirstRun) {
                    intake.set(Intake.State.RUN);
                    follower.follow(Paths.g1g2());
                } else if(stateTimer.seconds() >= 3) {
                    setState(State.DRIVE_GARDEN_SHOOT2);
                }
                break;
            case DRIVE_GARDEN_SHOOT2:
                if(isFirstRun) {
                    intake.set(Intake.State.IDLE);
                    follower.follow(Paths.g2b());
                } else if(!follower.isBusy()) {
                    setState(State.SPIN_UP_2);
                }
                break;
            case SPIN_UP_2:
                if(isFirstRun) {
                    flywheel.setState(Flywheel.State.CLOSED);
                    intake.set(Intake.State.IDLE);
                } else if(stateTimer.seconds() > 3 || flywheel.isAtSpeed()) {
                    setState(State.SHOOT2);
                }
                break;
            case SHOOT2:
                if(isFirstRun) {
                    flywheel.setState(Flywheel.State.AUTO_SHOOTING);
                    intake.set(Intake.State.FEED);
                } else if(flywheel.getState() == Flywheel.State.IDLE) {
                    setState(State.PARK);
                }
                break;
            case PARK:
                if(isFirstRun) {
                    intake.set(Intake.State.IDLE);
                    follower.follow(Paths.bp());
                }
                break;
            case BACKUP:
                if(isFirstRun) {
                    follower.follow(Paths.backup());
                    intake.set(Intake.State.IDLE);
                    flywheel.setState(Flywheel.State.IDLE);
                }
        }

        if(autoTimer.seconds() >= 20 && state != State.PARK) {
            setState(State.BACKUP);
        }
    }

    private void setState(State state) {
        this.state = state;
        stateTimer.reset();
    }
}
