package com.example.mdpgrp2;

public class Obstacle implements ICoordinate{
    public static final double OBSTACLE_LENGTH = 1.0;
    private float x;
    private float y;
    private int number;
    private int targetID;
    private char side;
    private boolean isExplored;

    public Obstacle(int number){
        this.x = -1;
        this.y = -1;
        this.number = number;
        this.isExplored = false;
    }

    public float getX(){
        return this.x;
    }

    public float getY(){
        return this.y;
    }

    @Override
    public void setCoordinates(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean containsCoordinate(float x, float y) {
        if ((this.x - OBSTACLE_LENGTH/2 <= x && x <= this.x + OBSTACLE_LENGTH/2) && (this.y - OBSTACLE_LENGTH/2 <= y && y <= this.y + OBSTACLE_LENGTH/2)){
            return true;
        }
        return false;
    }

    public int getNumber(){
        return this.number;
    }

    public void setNumber(int number){
        this.number = number;
    }

    public int getTargetID(){
        return this.targetID;
    }

    public char getSide(){
        return this.side;
    }

    public boolean setSide(char side){
        if (side != 'N' && side != 'S' && side != 'E' && side != 'W'){
            return false;
        } else {
            this.side = side;
            return true;
        }
    }

    public void explore(int targetID){
        this.targetID = targetID;
        isExplored = true;
    }

    public boolean isExplored(){
        return this.isExplored;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
        if (o instanceof Obstacle){
            Obstacle obstacle = (Obstacle) o;
            return x == obstacle.getX() && y == obstacle.getY();
        } else {
            return false;
        }
    }

}
