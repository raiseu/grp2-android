package com.example.mdpgrp2;

public interface ICoordinate {
    float getX();

    float getY();

    void setCoordinates(float x, float y);

    boolean containsCoordinate(float x, float y);
}
