package com.example.mdpgrp2;

import android.util.Log;

public class Robot implements ICoordinate{
    public static final double ROBOT_LENGTH = 3.0;
    private float x;
    private float y;
    private String status;
    private char direction;
    private int theta;

    public Robot(){
        direction = 'N';
        theta = 0;
        x = -1;
        y = -1;
    }

    @Override
    public float getX() {
        return this.x;
    }

    @Override
    public float getY() {
        return this.y;
    }

    @Override
    public void setCoordinates(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean containsCoordinate(float x, float y) {
        Log.d("ROBOT:", "" + this.x + ", " + this.y);
        return (this.x - ROBOT_LENGTH / 2 <= x && x <= this.x + ROBOT_LENGTH / 2) && (this.y - ROBOT_LENGTH / 2 <= y && y <= this.y + ROBOT_LENGTH / 2);
    }

    public String getStatus(){
        return status;
    }

    public void setStatus(String status){
        this.status = status;
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
            this.setCoordinates(this.x + (float) xChange , this.y + (float) yChange);
        }
    }

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

    private double convertToRadians(int degree){
        return degree * Math.PI/180;
    }

}
