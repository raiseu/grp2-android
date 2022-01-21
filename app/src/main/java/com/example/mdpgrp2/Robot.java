package com.example.mdpgrp2;

import android.util.Log;

public class Robot implements ICoordinate{
    public static final double ROBOT_LENGTH = 3.0;
    public static final double TURNING_RADIUS = 1.0;
    public static final double DISTANCE_TO_BACK_WHEEL = 7.6;
    public static final int FIELD_OF_VIEW = 30;
    private float x;
    private float y;
    private String status;
    private char direction; //Redundant variable
    private int theta;

    // Localisation
    public static final double DISTANCE_PER_CLICK = 20.1 / (1100 * 10);
    public static final double WHEEL_BASE = 10;
    private int leftEncoderTicks;
    private int rightEncoderTicks;
    private double gyroX;
    private double gyroY;
    private double gyroZ;

    public Robot(){
        direction = 'N';
        theta = 0;
        x = -1;
        y = -1;
    }

    // To be confirmed
    public void localise(int left, int right, double gyroX, double gyroY, double gyroZ){
        // Find how many ticks since previous sample
        int leftTicks = left - this.leftEncoderTicks;
        int rightTicks = right - this.rightEncoderTicks;

        // Update left and right ticks
        this.leftEncoderTicks = left;
        this.rightEncoderTicks = right;

        // Convert ticks to distance
        float leftDistance = (float) (leftTicks * DISTANCE_PER_CLICK);
        float rightDistance = (float) (rightTicks * DISTANCE_PER_CLICK);

        // Calculate distance travelled since last sample
        float distanceTravelled = (float) ((leftDistance + rightDistance) / 2.0);

        // Update rotation of robot
        this.theta += (leftDistance - rightDistance) / WHEEL_BASE;

        // Bring rotation to within allowed range
        // TODO: Convert to within range

        // Calculate and accumulate position
        this.x += distanceTravelled * Math.cos(this.theta);
        this.y += distanceTravelled * Math.sin(this.theta);

    }

    // To be confirmed
    public void resetEncoders(){
        this.leftEncoderTicks = 0;
        this.rightEncoderTicks = 0;
        this.gyroX = 0;
        this.gyroY = 0;
        this.gyroZ = 0;
    }

    @Override
    public float getX() {
        return this.x;
    }

    public void setX(float x){
        this.x = x;
    }

    @Override
    public float getY() {
        return this.y;
    }

    public void setY(float y){
        this.y = y;
    }

    @Override
    public void setCoordinates(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean containsCoordinate(float x, float y) {
        Log.d("ROBOT:", "" + this.x + ", " + this.y);
//        if((this.x == x || this.x == x-1 ||this.x==x-2)&&(this.y==y|| this.y==y-1 ||this.y==y-2)){
//            return true;
//        }
        return (this.x - ROBOT_LENGTH / 2 <= x && x <= this.x + ROBOT_LENGTH / 2) && (this.y - ROBOT_LENGTH / 2 <= y && y <= this.y + ROBOT_LENGTH / 2);
    }

    public String getStatus(){
        return status;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public char getDirection(){
        return direction;
    }

    public void setDirection(char direction){
        this.direction = direction;
    }

    public int getTheta(){
        return theta;
    }

    public void setTheta(int theta){
        this.theta = theta;
    }

    // Move Forward robot
//    public void moveRobotForward(){
//        int robotDir = this.getTheta();
//        double newEdge;
//        if (this.x != -1 && this.y != -1){
//            switch (robotDir){
//                case 0:
//                    newEdge = (this.y + ROBOT_LENGTH/2) + 1;
//                    if (!(newEdge > 20)){
//                        this.setY(this.y + 1);
//                    }
//                    break;
//                case 180:
//                    newEdge = (this.y - ROBOT_LENGTH/2) - 1;
//                    if (!(newEdge < 0)){
//                        this.setY(this.y - 1);
//                    }
//                    break;
//                case 90:
//                    newEdge = (this.x + ROBOT_LENGTH/2) + 1;
//                    if (!(newEdge > 20)){
//                        this.setX(this.x + 1);
//                    }
//                    break;
//                case -90:
//                    newEdge = (this.x - ROBOT_LENGTH/2) - 1;
//                    if (!(newEdge < 0)){
//                        this.setX(this.x - 1);
//                    }
//                    break;
//                default:
//                    break;
//            }
//        }
//    }

    // Drive forward given a displacement
    public void moveRobotForward(double displacement){
        int robotDir = this.getTheta();
        double xChange = 0;
        double yChange = 0;
        if (this.x != -1 && this.y != -1){
            if (robotDir >= 0 && robotDir <= 90){
                xChange = displacement * Math.sin(convertToRadians(robotDir));
                yChange = displacement * Math.cos(convertToRadians(robotDir));
            } else if (robotDir > 90 && robotDir <= 180){
                robotDir -= 90;
                xChange = displacement * Math.cos(convertToRadians(robotDir));
                yChange = -1 * displacement * Math.sin(convertToRadians(robotDir));
            } else if (robotDir < 0 && robotDir >= -90){
                robotDir = -1 * robotDir;
                xChange = -1 * displacement * Math.sin(convertToRadians(robotDir));
                yChange = displacement * Math.cos(convertToRadians(robotDir));
            } else if (robotDir < -90 && robotDir > -180){
                robotDir = (-1 * robotDir) - 90;
                xChange = -1 * displacement * Math.cos(convertToRadians(robotDir));
                yChange = -1 * displacement * Math.sin(convertToRadians(robotDir));
            }
            this.setCoordinates(this.x + (float) xChange, this.y + (float) yChange);
        }
    }


    // Robot rotate left (press button)
    public void moveRobotTurnLeft(){
        if (this.x != -1 && this.y != -1){
            int degree = this.theta;
            if (degree == 0){
                this.setTheta(-90);
            }
            else if (degree == 90){
                this.setTheta(0);
            }
            else if (degree == 180){
                this.setTheta(90);
            }
            else if (degree == -90){
                this.setTheta(180);
            }
        }
    }

    // Robot rotate right (press button)
    public void moveRobotTurnRight(){
        if (this.x != -1 && this.y != -1){
            int degree = this.theta;
            if (degree == 0){
                this.setTheta(90);
            }
            else if (degree == 90){
                this.setTheta(180);
            }
            else if (degree == 180){
                this.setTheta(-90);
            }
            else if (degree == -90){
                this.setTheta(0);
            }
        }
    }

    public void reset(){
        setCoordinates(-1,-1);
        this.direction = 'N';
        this.theta = 0;
    }

    // Convert degree to radians
    private double convertToRadians(int degree){
        return degree * Math.PI/180;
    }

}
