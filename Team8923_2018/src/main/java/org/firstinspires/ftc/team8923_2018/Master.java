package org.firstinspires.ftc.team8923_2018;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

abstract class Master extends LinearOpMode
{
    DcMotor motorFL;
    DcMotor motorFR;
    DcMotor motorBL;
    DcMotor motorBR;

    BNO055IMU imu;

    double slowModeDivisor = 1.0;

    void InitHardware()
    {
        motorFL = hardwareMap.get(DcMotor.class, "motorFL");
        motorFR = hardwareMap.get(DcMotor.class, "motorFR");
        motorBL = hardwareMap.get(DcMotor.class, "motorBL");
        motorBR = hardwareMap.get(DcMotor.class, "motorBR");

        motorFL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorFR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorBL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorBR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;

        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);
    }



    void driveMecanum(double driveAngle, double drivePower, double turnPower)
    {
        // Calculate x and y components of drive power, where y is forward (0 degrees) and x is right (-90 degrees)
        double x = drivePower * -Math.sin(Math.toRadians(driveAngle));
        double y = drivePower * Math.cos(Math.toRadians(driveAngle));

        /*
         * Below is an explanation of how the mecanum wheels are driven
         *
         * Each wheel has a roller on the bottom mounted at 45 degrees. Because of this, each wheel
         * can only exert a force diagonally in one dimension. Using the front left wheel as an
         * example, when it is given a positive power, it exerts a force forward and right, which
         * means positive y and x. When the front right wheel is given a positive power, it exerts
         * a force forward and left, which means positive y and negative x. This is reflected in
         * how the motor powers are set. Turning is like standard tank drive.
         */
        // Set motor powers
        double powerFL = y + x - turnPower;
        double powerFR = y - x + turnPower;
        double powerBL = y - x - turnPower;
        double powerBR = y + x + turnPower;

        // Motor powers might be set above 1, so this scales all of the motor powers to stay
        // proportional and within power range
        double scalar = Math.max(Math.abs(powerFL), Math.max(Math.abs(powerFR),
                Math.max(Math.abs(powerBL), Math.abs(powerBR))));

        // Only apply scalar if greater than 1. Otherwise we could unintentionally increase power
        // This also prevents dividing by 0
        if(scalar < 1)
            scalar = 1;

        // Apply scalar
        powerFL /= (scalar * slowModeDivisor);
        powerFR /= (scalar * slowModeDivisor);
        powerBL /= (scalar * slowModeDivisor);
        powerBR /= (scalar * slowModeDivisor);

        // Set motor powers
        /*if(reverseDrive)
        {
            // Drive backwards
            motorFL.setPower(powerBR);
            motorFR.setPower(powerBL);
            motorBL.setPower(powerFR);
            motorBR.setPower(powerFL);
            return;
        }*/

        // Drive forwards
        motorFL.setPower(powerFL);
        motorFR.setPower(powerFR);
        motorBL.setPower(powerBL);
        motorBR.setPower(powerBR);
    }


}
