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
public class HelperAuto extends OpMode {

    private Follower follower;
    private final Flywheel flywheel = new Flywheel();
    private final Intake intake = new Intake();
    private final Turret turret = new Turret();
    private enum State {
        WAIT,
        SPIN_UP,
        SHOOT,
        PARK
    }
    private State state = State.WAIT;
    private State lastState = null;
    private boolean isFirstRun;
    private final ElapsedTime stateTimer = new ElapsedTime();
    private final ElapsedTime autoTimer = new ElapsedTime();

    @Override
    public void init() {
        follower = Constants.create(hardwareMap);
        follower.setPose(Paths.A);
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
        lastState = state;
        handleState();

        follower.update();
        intake.update();
        flywheel.update(0, 0, 0);
        turret.update(0, 0, 0);

        telemetry.addData("state", state);
        telemetry.addData("time", autoTimer.seconds());
    }

    @Override
    public void stop() {
        Pose currentPose = follower.pose();
        OpModeStorage.save(currentPose, turret.getCurrentPosition());
    }

    private void handleState() {
        switch(state) {
            case WAIT:
                if(autoTimer.seconds() >= 20) {
                    setState(State.SPIN_UP);
                }
                break;
            case SPIN_UP:
                if(isFirstRun) {
                    flywheel.setState(Flywheel.State.CLOSED);
                    intake.set(Intake.State.FEED);
                } else if(stateTimer.seconds() > 4 || flywheel.isAtSpeed()) {
                    setState(State.SHOOT);
                }
                break;
            case SHOOT:
                if(isFirstRun) {
                    flywheel.setState(Flywheel.State.SHOOTING);
                    intake.set(Intake.State.FEED);
                } else if(stateTimer.seconds() > 2) {
                    setState(State.PARK);
                }
                break;
            case PARK:
                if(isFirstRun) {
                    flywheel.setState(Flywheel.State.IDLE);
                    follower.follow(Paths.ap());
                    intake.set(Intake.State.IDLE);
                }
                break;
        }
    }

    private void setState(State state) {
        this.state = state;
        stateTimer.reset();
    }
}
