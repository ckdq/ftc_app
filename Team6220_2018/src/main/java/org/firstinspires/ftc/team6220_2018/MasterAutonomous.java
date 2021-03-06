package org.firstinspires.ftc.team6220_2018;

import android.graphics.Color;

import com.qualcomm.robotcore.hardware.DcMotor;

/*
    Contains important methods for use in our autonomous programs
*/
abstract public class MasterAutonomous extends MasterOpMode
{
    // Necessary for runSetup()
    DriverInput driverInput;

    // Initialize booleans and variables used in runSetup()
    public boolean isBlueSide = true;

    // Stores orientation of robot
    double currentAngle = 0.0;

    /*
    // Use for more advanced auto
    ArrayList<Alliance> routine = new ArrayList<>();
    RoutineOption routineOption = RoutineOption.;
    int delay = 0;
    */

    // todo Implement runSetup()
    // Used for object initializations only necessary in autonomous
    void initializeAuto() throws InterruptedException
    {
        initializeRobot();

        vuforiaHelper = new VuforiaHelper();
        vuforiaHelper.setupVuforia();
    }

    // Note: not currently in use
    /*
     Allows the 1st driver to decide which autonomous routine should be run during the match through
     gamepad input
    */
    void runSetup()
    {
        // Accounts for delay between initializing the program and starting TeleOp
        lTime = timer.seconds();

        // Ensure log can't overflow
        telemetry.log().setCapacity(2);
        telemetry.log().add("Alliance Blue/Red = X/B");
        telemetry.log().add("Balancing stone Left/Right = Left/Right bumper");

        boolean settingUp = true;

        while (settingUp)
        {
            // Finds the time elapsed each loop
            double eTime = timer.seconds() - lTime;
            lTime = timer.seconds();


            // Select alliance
            if (driver1.isButtonJustPressed(Button.X))
                isBlueSide = true;
            else if (driver1.isButtonJustPressed(Button.B))
                isBlueSide = false;

            // If the driver presses start, we exit setup
            else if (driver1.isButtonJustPressed(Button.START))
                settingUp = false;

            // Display the current setup
            telemetry.addData("Is robot on blue alliance: ", isBlueSide);


            updateCallback(eTime);
            telemetry.update();
            idle();
        }

        telemetry.log().clear();
        telemetry.log().add("Setup finished.");
    }


    // Tell the robot to turn to a specified angle
    public void turnTo(double targetAngle)
    {
        double turningPower;
        currentAngle = getAngularOrientationWithOffset();
        double angleDiff = normalizeRotationTarget(targetAngle, currentAngle);

        // Robot only stops turning when it is within angle tolerance
        while(Math.abs(angleDiff) >= Constants.ANGLE_TOLERANCE_DEG && opModeIsActive())
        {
            currentAngle = getAngularOrientationWithOffset();

            // Give robot raw value for turning power
            angleDiff = normalizeRotationTarget(targetAngle, currentAngle);

            // Send raw turning power through PID filter to adjust range and minimize oscillation
            rotationFilter.roll(angleDiff);
            turningPower = rotationFilter.getFilteredValue();

            // Make sure turningPower doesn't go above maximum power
            if (Math.abs(turningPower) > 1.0)
            {
                turningPower = Math.signum(turningPower);
            }

            // Makes sure turningPower doesn't go below minimum power
            if(Math.abs(turningPower) < Constants.MINIMUM_TURNING_POWER)
            {
                turningPower = Math.signum(turningPower) * Constants.MINIMUM_TURNING_POWER;
            }

            // Turns robot
            driveMecanum(0.0, 0.0, turningPower);

            telemetry.addData("angleDiff: ", angleDiff);
            telemetry.addData("Turning Power: ", turningPower);
            telemetry.addData("Orientation: ", currentAngle);
            telemetry.update();
            idle();
        }

        stopDriveMotors();
    }

    // Tell the robot to turn to a specified angle.  This method is different from turnTo in that
    // we can tell the robot not to turn faster than a given speed
    public void adjustableTurnTo(double targetAngle, double maxPower)
    {
        double turningPower;
        currentAngle = getAngularOrientationWithOffset();
        double angleDiff = normalizeRotationTarget(targetAngle, currentAngle);

        // Robot only stops turning when it is within angle tolerance
        while(Math.abs(angleDiff) >= Constants.ANGLE_TOLERANCE_DEG && opModeIsActive())
        {
            currentAngle = getAngularOrientationWithOffset();

            // Give robot raw value for turning power
            angleDiff = normalizeRotationTarget(targetAngle, currentAngle);

            // Send raw turning power through PID filter to adjust range and minimize oscillation
            rotationFilter.roll(angleDiff);
            turningPower = rotationFilter.getFilteredValue();

            // Make sure turningPower doesn't go above maximum power
            if (Math.abs(turningPower) > maxPower)
            {
                turningPower = maxPower * Math.signum(turningPower);
            }

            // Makes sure turningPower doesn't go below minimum power
            if(Math.abs(turningPower) < Constants.MINIMUM_TURNING_POWER)
            {
                turningPower = Math.signum(turningPower) * Constants.MINIMUM_TURNING_POWER;
            }

            // Turns robot
            driveMecanum(0.0, 0.0, turningPower);

            telemetry.addData("angleDiff: ", angleDiff);
            telemetry.addData("Turning Power: ", turningPower);
            telemetry.addData("Orientation: ", currentAngle);
            telemetry.update();
            idle();
        }

        stopDriveMotors();
    }


    // Specialized method for driving the robot in autonomous
    void moveRobot(double driveAngle, double drivePower, double pause) throws InterruptedException
    {
        driveMecanum(driveAngle, drivePower, 0.0);
        pauseWhileUpdating(pause);
        stopDriveMotors();
    }

    // todo Add global coordinates (not a priority)
    // Uses encoders to make the robot drive to a specified relative position.  Also makes use of the
    // imu to keep the robot at a constant heading during navigation.
    void driveToPosition(double initDeltaX, double initDeltaY, double maxPower) throws InterruptedException
    {
        // Variables set every loop-------------------
        double deltaX = initDeltaX;
        double deltaY = initDeltaY;
        double headingDiff = 0;

        double driveAngle;
        double drivePower;
        double adjustedDrivePower;
        double rotationPower;

         // Find distance between robot and its destination
        double distanceToTarget = calculateDistance(deltaX, deltaY);
        //---------------------------------------------
        double initHeading = getAngularOrientationWithOffset();

        motorFL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        motorFL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorFR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorBL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorBR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Check to see if robot has arrived at destination within tolerances
        while (((distanceToTarget > Constants.POSITION_TOLERANCE_MM) || (headingDiff > Constants.ANGLE_TOLERANCE_DEG))&& opModeIsActive())
        {
            deltaX = initDeltaX - Constants.MM_PER_ANDYMARK_TICK * (-motorFL.getCurrentPosition() +
                    motorBL.getCurrentPosition() - motorFR.getCurrentPosition() + motorBR.getCurrentPosition()) / (4 * Math.sqrt(2));
            deltaY = initDeltaY - Constants.MM_PER_ANDYMARK_TICK * (-motorFL.getCurrentPosition() -
                    motorBL.getCurrentPosition() + motorFR.getCurrentPosition() + motorBR.getCurrentPosition()) / 4;

            // Calculate how far off robot is from its initial heading
            headingDiff = normalizeRotationTarget(getAngularOrientationWithOffset(), initHeading);

            // Recalculate drive angle and distance remaining every loop
            distanceToTarget = calculateDistance(deltaX, deltaY);
            driveAngle = Math.toDegrees(Math.atan2(deltaY, deltaX));

            // Transform position and heading diffs to linear and rotation powers using filters----
            translationFilter.roll(distanceToTarget);
            drivePower = translationFilter.getFilteredValue();

            // Ensure robot doesn't approach target position too slowly
            if (Math.abs(drivePower) < Constants.MINIMUM_DRIVE_POWER)
            {
                drivePower = Math.signum(drivePower) * Constants.MINIMUM_DRIVE_POWER;
            }
            // Ensure robot doesn't ever drive faster than we want it to
            else if (Math.abs(drivePower) > maxPower)
            {
                drivePower = Math.signum(drivePower) * maxPower;
            }

             // Additional factor is necessary to ensure turning power is large enough
            rotationFilter.roll(-1.5 * headingDiff);
            rotationPower = rotationFilter.getFilteredValue();
            //-------------------------------------------------------------------------------------

            driveMecanum(driveAngle, drivePower, rotationPower);

//            telemetry.addData("Encoder Diff x: ", deltaX);
//            telemetry.addData("Encoder Diff y: ", deltaY);
//            telemetry.addData("Drive Power: ", drivePower);
//            telemetry.addData("Rotation Power: ", rotationPower);
//            telemetry.update();
            idle();
        }
        stopDriveMotors();
    }

    // todo Implement global coordinates (not a priority)
    // Updates robot's coordinates and angle
    public void updateLocation()
    {
        currentAngle = getAngularOrientationWithOffset();

        /*
        // Calculate how much drive motors have turned since last update
        int deltaFL = motorFL.getCurrentPosition() - lastEncoderFL;
        int deltaFR = motorFR.getCurrentPosition() - lastEncoderFR;
        int deltaBL = motorBL.getCurrentPosition() - lastEncoderBL;
        int deltaBR = motorBR.getCurrentPosition() - lastEncoderBR;

        // Average encoder ticks to find translational x and y components. deltaFR and deltaBL are
        // negative because they turn differently when translating
        double deltaX = (deltaFL - deltaFR - deltaBL + deltaBR) / 4;
        double deltaY = (deltaFL + deltaFR + deltaBL + deltaBR) / 4;

        // Convert to mm
        deltaX *= Constants.MM_PER_ANDYMARK_TICK;
        deltaY *= Constants.MM_PER_ANDYMARK_TICK;
        */

        /*
         Delta x and y are local values, so they need to be converted to global.
         Each local component has 2 global components, which are added to find the
         total global components of displacement. The global displacement components
         are then added to the previous position to set the new coordinates
        */
        /*
        robotX += deltaX * Math.sin(Math.toRadians(robotAngle)) + deltaY * Math.cos(Math.toRadians(robotAngle));
        robotY += deltaX * -Math.cos(Math.toRadians(robotAngle)) + deltaY * Math.sin(Math.toRadians(robotAngle));
        */

        telemetry.addData("currentAngle: ", currentAngle);
        telemetry.update();
    }
}
