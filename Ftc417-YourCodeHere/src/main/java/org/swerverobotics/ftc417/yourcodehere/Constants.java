package org.swerverobotics.ftc417.yourcodehere;

/**
 * Created by Curtis on 1/24/16.
 */
public class Constants {

    //IMU and encoder constants
    public static final int ANDYMARK_ENC_TICKS = 1120;
    public static final int TETRIX_ENC_TICKS = 1440;

    //motion variable declaration
    public static final double FORWARDS = 1.0;
    public static final double BACKWARDS = -FORWARDS;
    public static final double SLOW_FORWARDS = 0.3 * FORWARDS;
    public static final double SLOW_BACKWARDS = 0.3 * BACKWARDS;

    // in centimeters
    public static final double BACK_LEFT_WHEEL_DIAMETER = 20.32;

    public static final double SLOW_MODE_DEADZONE = 0.15;

    public static final double SLOW_MODE_MULTIPLIER = 0.1;

    //CRservo positions
    public static final double TILT_LEFT = 1;
    public static final double TILT_RIGHT = 0;
    public static final double FLAT = .5;

    //Servo positions
    public static final double LEFT_CLOSED = .1;
    public static final double LEFT_OPEN = 1;
    public static final double RIGHT_OPEN = 0;
    public static final double RIGHT_CLOSED = 1;


}