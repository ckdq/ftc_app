package org.firstinspires.ftc.team417;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="Autonomous Tests", group = "Swerve")
// @Disabled

public class AutonomousTests extends MasterAutonomous
{

    public void runOpMode() throws InterruptedException
    {
        // Initialize hardware and other important things
        initializeRobot();

        VuforiaNav.initVuforia();
        telemetry.addData("Path", "Select Team and Pos...");

        // Wait until we're told to go
        while (!isStarted())
        {
            // allow driver to choose a team
            if (gamepad1.b)
            {
                isRedTeam = true;
            }

            if (gamepad1.x)
            {
                isRedTeam = false;
            }

            // select position one or two, one is closer to the origin
            if (gamepad1.y)
            {
                isStartingPosOne = true;
            }
            if (gamepad1.a)
            {
                isStartingPosOne = false;
            }

            if (isRedTeam)
            {
                if (isStartingPosOne)
                {
                    telemetry.addData("Team: ", "Red 1");
                }
                else
                {
                    telemetry.addData("Team: ", "Red 2");
                }
            }
            else
            {
                if (isStartingPosOne)
                {
                    telemetry.addData("Team: ", "Blue 1");
                }
                else
                {
                    telemetry.addData("Team: ", "Blue 2");
                }
            }
            telemetry.update();
            idle();
        }
        telemetry.update();

        if (isRedTeam) // if team RED
        {
            if (isStartingPosOne)
            {
                // OPTION RED ONE (TOOLS)
                startDelay = 2000;
                pivotAngle = 55; // pivot this amount before acquiring target
                targetAngle = 0; // Vuforia angle
                startDist = 2286;
                targetIndex = 1;
                targetPos[0] = 2743.2f;
                targetPos[1] = mmFTCFieldWidth;
                //telemetry.addData("Team: ", "Red 1"); // display what team we're on after choosing with the buttons
            }
            else
            {
                // OPTION RED TWO (GEARS)
                startDelay = 0;
                pivotAngle = 55; // pivot this amount before acquiring target
                targetAngle = 0; // Vuforia angle
                startDist = 1397;
                targetIndex = 3;
                targetPos[0] = 1524;
                targetPos[1] = mmFTCFieldWidth;
                //telemetry.addData("Team: ", "Red 2"); // display what team we're on after choosing with the buttons
            }
            telemetry.update();
        }
        else // if team BLUE
        {
            if (isStartingPosOne)
            {
                // OPTION BLUE ONE (LEGOS)
                startDelay = 2000;
                pivotAngle = -55; // recalc pivot?? also for red one??
                targetAngle = -90;
                startDist = 2286;
                targetIndex = 2;
                targetPos[0] = mmFTCFieldWidth;
                targetPos[1] = 2743.2f;
                //telemetry.addData("Team: ", "Blue 1");
            }
            else
            {
                // OPTION BLUE TWO (WHEELS)
                startDelay = 0;
                pivotAngle = -55;
                targetAngle = -90;
                startDist = 1397;
                targetIndex = 0;
                targetPos[0] = mmFTCFieldWidth;
                targetPos[1] = 1524;
                //telemetry.addData("Team: ", "Blue 2");
            }
        }

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        autoRuntime.reset(); // set the 30 second timer

        VuforiaNav.startTracking();
        //     pause(startDelay);
        VuforiaNav.getLocation(targetIndex);

        //pause(delay);

// START OF AUTONOMOUS

        TOL_ANGLE = 3.0;
        VUFORIA_TOL_ANGLE = 3.0;
        TOL = 60;
        Kmove = 1.0/1200.0;
        Kpivot = 1/120.0;
        MINSPEED = 0.35;

        telemetry.addData("Path", "start forwards");
        telemetry.update();
        // go towards target
        moveAverage(0, startDist, 0, 0.7, 3);
        pause(100);

        telemetry.addData("Path", "pivot 70");
        telemetry.update();
        // pivot to face target
        pivot(pivotAngle, 0.9); // make sure IMU is on
        pause(200);

        TOL = 60;
        VUFORIA_TOL = 50;
        TOL_ANGLE = 3.0; // tol angle for scan is 3, not accurate
        VUFORIA_TOL_ANGLE = 3.0; // tol angle for scan is 3, not accurate
        Kmove = 1.0/1000.0;
        Kpivot = 1.0/150.0;
        MINSPEED = 0.3;

        telemetry.addData("Path", "scanning for first target");
        telemetry.update();
        pivotDetectTarget(30, 5);

        // setting for align pivot Vuforia
        TOL_ANGLE = 3.0;
        VUFORIA_TOL_ANGLE = 3.0;
        MINSPEED = 0.3;

        telemetry.addData("Path", "align pivot vuf");
        telemetry.update();
        alignPivotVuforia(0.6, 0, 600, 4);
        pause(50);

        // setting for pivot Vuforia
        TOL_ANGLE = 2.0;
        VUFORIA_TOL_ANGLE = 2.0;
        Kpivot = 1.0/200.0;
        MINSPEED = 0.3;
        telemetry.addData("Path", "pivotVuforia");
        telemetry.update();
        pivotVuforia(targetAngle, 0.5);

        do
        {
            VuforiaNav.getLocation(targetIndex); // update target location and angle
        }
        while (VuforiaNav.lastLocation == null);

        // error came up here

// detect beacon color of left side: 0 is blue, 1 is red
        int beaconColor = VuforiaNav.GetBeaconColor();
        telemetry.log().add(String.format("LeftSide: %f, RightSide: %f", VuforiaNav.leftColorHSV[0], VuforiaNav.rightColorHSV[0]));
        telemetry.log().add(String.format("Returned Color: %d", beaconColor));
        if (isRedTeam)
        {
            telemetry.log().add(String.format("team red"));
        }
        else
        {
            telemetry.log().add(String.format("team blue"));
        }
        telemetry.update();


// shift left or right before pushing button

        TOL_ANGLE = 3.0;
        VUFORIA_TOL_ANGLE = 3.0;
        TOL = 40;
        Kmove = 1.0/1200.0;
        Kpivot = 1/50.0;
        MINSPEED = 0.35;

        if (beaconColor == 0)   // if left side beacon is blue
        {
            if (isRedTeam)     // red team
            {
                telemetry.addData("Path", "shift right");
                telemetry.update();
                //move(100, 0, 0.3, 3); // shift right
                moveAverage(120, 0, 0, 0.7, 3);
                PushButton();
            }
            else    // blue team
            {
                telemetry.addData("Path", "shift left");
                telemetry.update();
                //move(-18, 0, 0.3, 4); // shift left used to be 38
                moveAverage(-45, 0, 0, 0.7, 3);
                PushButton();
            }
        }
        else if (beaconColor == 1)  // if left side beacon is red
        {
            if (isRedTeam)     // red team
            {
                telemetry.addData("Path", "shift left");
                telemetry.update();
                //move(-18, 0, 0.3, 4); // shift left
                moveAverage(-45, 0, 0, 0.7, 3);
                PushButton();
            }
            else    // blue team
            {
                telemetry.addData("Path", "shift right");
                telemetry.update();
                //move(100, 0, 0.3, 3); // shift right
                moveAverage(120, 0, 0, 0.7, 3);
                PushButton();
            }
        }
        else // when the color is unknown
        {
            telemetry.addData("Path", "unknown color, moving on");
            telemetry.update();
        }

        pause(100);

        TOL_ANGLE = 3.0;
        VUFORIA_TOL_ANGLE = 3.0;
        TOL = 60;
        Kmove = 1.0/1200.0;
        Kpivot = 1/140.0;
        MINSPEED = 0.35;
        move(0, -250, 0.5, 3); // back up from button (or just back up)

        // determine next beacon target
        if (isRedTeam) // if team RED
        {
            // OPTION RED ONE (TOOLS)
            targetIndex = 1;
            targetPos[0] = 2743.2f;
            targetPos[1] = mmFTCFieldWidth;
            //telemetry.addData("Team: ", "Red 1"); // display what team we're on after choosing with the buttons
        }
        else // if team BLUE
        {
            // OPTION BLUE ONE (LEGOS)
            targetIndex = 2;
            targetPos[0] = mmFTCFieldWidth;
            targetPos[1] = 2743.2f;
            //telemetry.addData("Team: ", "Blue 1");
        }



        // for big move left or right
        TOL = 40;
        TOL_ANGLE = 3.0;
        Kmove = 1.0/2000.0;
        Kpivot = 1.0/100.0;
// shift to new target!!
        telemetry.addData("Path", "shift to new target");
        telemetry.update();


        if (beaconColor == 0) // if left side blue
        {
            if (isRedTeam) // move shorter
            {
                //moveAverage(1125, 0, 0, 0.7, 4);
                pivot(-90, 0.7);
                moveAverage(0, 1220, 0, 0.8, 3);
                pivot(90, 0.7);
            }
            else // move shorter
            {
                //moveAverage(-1125, 0, 0, 0.7, 4);
                pivot(90, 0.7);
                moveAverage(0, 1220, 0, 0.8, 3);
                pivot(-90, 0.7);
            }
        }
        else if (beaconColor == 1) // if left side red
        {
            if (isRedTeam) // move longer
            {
                //moveAverage(1220, 0, 0, 0.7, 4);
                pivot(-90, 0.7);
                moveAverage(0, 1300, 0, 0.8, 3);
                pivot(90, 0.7);
            }
            else // move longer
            {
                //moveAverage(-1220, 0, 0, 0.7, 4);
                pivot(90, 0.7);
                moveAverage(0, 1300, 0, 0.8, 3);
                pivot(-90, 0.7);
            }
        }
        pause(200);

        TOL = 60;
        VUFORIA_TOL = 50;
        TOL_ANGLE = 3.0; // tol angle for scan is 3, not accurate
        VUFORIA_TOL_ANGLE = 3.0; // tol angle for scan is 3, not accurate
        Kmove = 1.0/1000.0;
        Kpivot = 1.0/150.0;
        MINSPEED = 0.3;

        telemetry.addData("Path", "scanning for target");
        telemetry.update();
        pivotDetectTarget(30, 5);

        telemetry.addData("Path", "align pivot vuf");
        telemetry.update();
        alignPivotVuforia(0.6, 0, 600, 4);
        pause(50);

        // setting for pivot Vuforia
        TOL_ANGLE = 2.0;
        VUFORIA_TOL_ANGLE = 2.0;
        MINSPEED = 0.3;
        Kpivot = 1.0/200.0;
        telemetry.addData("Path", "pivotVuforia");
        telemetry.update();
        pivotVuforia(targetAngle, 0.5);

        // detect beacon color of left side: 0 is blue, 1 is red
        beaconColor = VuforiaNav.GetBeaconColor();

        // shift left or right before pushing button
        TOL_ANGLE = 3.0;
        VUFORIA_TOL_ANGLE = 3.0;
        TOL = 40;
        Kmove = 1.0/1200.0;
        Kpivot = 1/50.0;
        MINSPEED = 0.35;

        if (beaconColor == 0)   // if left side beacon is blue
        {
            if (isRedTeam)     // red team
            {
                telemetry.addData("Path", "shift right");
                telemetry.update();
                //move(100, 0, 0.3, 3); // shift right
                moveAverage(120, 0, 0, 0.7, 3);
                PushButton();
            }
            else    // blue team
            {
                telemetry.addData("Path", "shift left");
                telemetry.update();
                //move(-18, 0, 0.3, 4); // shift left
                moveAverage(-45, 0, 0, 0.7, 3);
                PushButton();
            }
        }
        else if (beaconColor == 1)  // if left side beacon is red
        {
            if (isRedTeam)     // red team
            {
                telemetry.addData("Path", "shift left");
                telemetry.update();
                //move(-18, 0, 0.3, 4); // shift left
                moveAverage(-45, 0, 0, 0.7, 3);
                PushButton();
            }
            else    // blue team
            {
                telemetry.addData("Path", "shift right");
                telemetry.update();
                //move(100, 0, 0.3, 3); // shift right
                moveAverage(120, 0, 0, 0.7, 3);
                PushButton();
            }
        }
        else // when the color is unknown
        {
            telemetry.addData("Path", "unknown color, standing by");
            telemetry.update();
        }

        pause(100);

        TOL_ANGLE = 3.0;
        VUFORIA_TOL_ANGLE = 3.0;
        TOL = 60;
        Kmove = 1.0/1200.0;
        Kpivot = 1/140.0;
        MINSPEED = 0.35;

        move(0, -150, 0.5, 3); // back up

        if (autoRuntime.milliseconds() < 28000)
        {
            TOL_ANGLE = 3.0;
            VUFORIA_TOL_ANGLE = 3.0;
            Kpivot = 1/100.0;
            MINSPEED = 0.35;

            motorLauncher.setPower(0.85);

            if (isRedTeam) // RED
            {
                if (beaconColor == 0)
                {
                    // if red team, right side
                    move(0, -100, 0.5, 3); // back up less
                    pivot(-45, 0.8);
                    move(0, -300, 0.6, 2); // come closer more
                }
                else
                {
                    // if red team, left side
                    move(0, -200, 0.5, 3); // back up more
                    pivot(-45, 0.8);
                    move(0, -200, 0.6, 2); // come closer less
                }
            }
            else // BLUE
            {
                if (beaconColor == 0)
                {
                    // if blue team, left side
                    move(0, -100, 0.5, 3); // back up less
                    pivot(55, 0.8);
                    move(0, -300, 0.6, 2); // come closer more
                }
                else
                {
                    // if blue team, right side
                    move(0, -200, 0.5, 3); // back up more
                    pivot(55, 0.8);
                    move(0, -200, 0.6, 2); // come closer less
                }
            }

            motorCollector.setPower(1.0);
            servoParticle.setPosition(0.8);
            pause(300);
            servoParticle.setPosition(0.0);
            pause(1500);
            servoParticle.setPosition(0.8);
            pause(300);
            servoParticle.setPosition(0.0);
            pause(300);
            motorLauncher.setPower(0.0);
            motorCollector.setPower(0.0);
        }

        telemetry.addData("Path", "Complete");
        telemetry.update();
    }
}
