import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.Arrays;


// The purpose of this class is to empirically determine the step response of a motor.  This is
// accomplished by setting 4 motors to full power and sampling the power value of one after
// preset time increments.  The resulting data is placed in an array to be used for further analysis.

@Autonomous(name = "Motor Step Response Test", group = "Autonomous")
public class MotorStepResponseTest extends LinearOpMode
{
    @Override
    public void runOpMode() throws InterruptedException
    {
        final int NUM_LOOPS = 200;
        ElapsedTime loopTime = new ElapsedTime();
        double[] encoderSpeeds = new double[200];
        double sampleTime = 10;     // This number is in milliseconds

        // Encoder values that will be stored from last loop
        double oldEncValBL = 0;
        double oldEncValBR = 0;
        double oldEncValFL = 0;
        double oldEncValFR = 0;

        // Encoder values that will be retrieved in the current loop and compared to old values
        double newEncValBL = 0;
        double newEncValBR = 0;
        double newEncValFL = 0;
        double newEncValFR = 0;

        // Set up motors in configuration
        DcMotor motorBackLeft;
        DcMotor motorBackRight;
        DcMotor motorFrontLeft;
        DcMotor motorFrontRight;

        motorBackLeft = hardwareMap.dcMotor.get("motorBackLeft");
        motorBackRight = hardwareMap.dcMotor.get("motorBackRight");
        motorFrontLeft = hardwareMap.dcMotor.get("motorFrontLeft");
        motorFrontRight = hardwareMap.dcMotor.get("motorFrontRight");

        motorBackLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorBackRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorFrontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorFrontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        waitForStart();


        motorBackLeft.setPower(-1.0);
        motorBackRight.setPower(1.0);
        motorFrontLeft.setPower(-1.0);
        motorFrontRight.setPower(1.0);

        loopTime.reset();

        // Go through the specified number of loops while sampling encoders and calculating rotational
        // velocity at set increments
        for (int i = 0; i < NUM_LOOPS; i++)
        {
            loopTime.reset();

            oldEncValBL = newEncValBL;
            newEncValBL = motorBackLeft.getCurrentPosition();

            // Calculate time rate of change of encoder values
            encoderSpeeds[i] = (-newEncValBL + oldEncValBL) / (0.001 * sampleTime);
            //encoderSpeeds[i] = ((-newEncValBL + newEncValBR - newEncValFL + newEncValFR) / 4
            //        -(-oldEncValBL + oldEncValBR - oldEncValFL + oldEncValFR) / 4) / (0.001 * sampleTime);

            while (loopTime.milliseconds() < sampleTime && opModeIsActive())
                idle();
        }

        motorBackLeft.setPower(0.0);
        motorBackRight.setPower(0.0);
        motorFrontLeft.setPower(0.0);
        motorFrontRight.setPower(0.0);

        // Display the data
        System.out.println(Arrays.toString(encoderSpeeds));
    }
}