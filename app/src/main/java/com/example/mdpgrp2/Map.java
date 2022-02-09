package com.example.mdpgrp2;

import java.util.ArrayList;

public class Map {
    private static Map map;
    public static Map getInstance(){
        if (map == null){
            map = new Map();
        }
        return map;
    }
    private Map(){}

    private final ArrayList<com.example.mdpgrp2.Obstacle> obstacles = new ArrayList<>();
    public ArrayList<com.example.mdpgrp2.Obstacle> getObstacles(){
        return this.obstacles;
    }

    public com.example.mdpgrp2.Obstacle addObstacle(){
        int number = this.obstacles.size() + 1;
        com.example.mdpgrp2.Obstacle newObstacle = new com.example.mdpgrp2.Obstacle(number);
        this.obstacles.add(newObstacle);
        return newObstacle;
    }

    public void removeObstacle(com.example.mdpgrp2.Obstacle obstacle){
        int indexToRemove = obstacle.getNumber() - 1;
        this.obstacles.remove(indexToRemove);
        for (int i = indexToRemove; i < this.obstacles.size(); i++){
            this.obstacles.get(i).setNumber(i + 1);
        }
    }

    public void removeAllObstacles(){
        this.obstacles.clear();
    }

    public ICoordinate findObstacle(float x, float y){
        for (ICoordinate obstacle: this.obstacles){
            if (obstacle.containsCoordinate(x, y)){
                return obstacle;
            }
        }
        return null;
    }

    public boolean isOccupied(float x, float y, com.example.mdpgrp2.Obstacle obstacle){
        for (com.example.mdpgrp2.Obstacle o: this.obstacles){
            if (o.containsCoordinate(x, y) && o != obstacle){
                return true;
            }
        }
        return false;
    }
}
