package com.example.mdpgrp2;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapGrid extends View {
    // dimensions of canvas
    private int width;
    private int height;
    private float cellHeight;
    private float cellWidth;
    private float offsetX;
    private float offsetY;
    private float sidebar;
    private float obstacleSideWidth = 4;

    // grid properties
    private static final int numColumns = 20;
    private static final int numRows = 20;
    private static final int padding = 20;
    private static final int border = 5;

    // Paint - coloring
    private final Paint whitePaint = new Paint();
    private final Paint bluePaint = new Paint();
    private final Paint blackPaint = new Paint();
    private final Paint redPaint = new Paint();
    private final Paint coordinatesPaint = new Paint();
    private final Paint whiteNumber = new Paint();
    private final Paint whiteNumberTwo = new Paint();
    private final Paint yellowPaint = new Paint();
    private final Paint greenPaint = new Paint();
    private final Paint fieryPink  = new Paint();
    private final Paint neonBluePaint = new Paint();
    private final Paint electricBlue = new Paint();

    // Images
    private final Bitmap robotBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.robotdrawn);
    private final Bitmap robotBoxBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.robotshadow);
    private final Bitmap obstacleBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.obstacles);

    // Handle motion
    private float initialX;
    private float initialY;
    private ICoordinate objectToMove;

    // Sidebar buttons
    public static final int sideBarLeft = 21;
    public static final int sideBarRight = sideBarLeft + 3;
    public static final int robotBoxBottom = 17;
    public static final int robotBoxTop = robotBoxBottom + 3;
    public static final int obstacleBoxTop = 16;
    public static final int obstacleBoxBottom = obstacleBoxTop - 3;

    KonfettiView konfettiView;

    public MapGrid(Context context){
        this(context, null);
    }

    public MapGrid(Context context, AttributeSet attrs){
        super(context, attrs);

        whitePaint.setColor(Color.WHITE);
        whitePaint.setShadowLayer(border, 0, 0, Color.GRAY);
        bluePaint.setColor(Color.BLUE);
        blackPaint.setColor(Color.BLACK);
        coordinatesPaint.setColor(Color.BLACK);
        coordinatesPaint.setTextSize(20);
        coordinatesPaint.setTextAlign(Paint.Align.CENTER);
        whiteNumber.setColor(Color.WHITE);
        whiteNumber.setTextSize(20);
        whiteNumber.setTextAlign(Paint.Align.CENTER);
        whiteNumberTwo.setColor(Color.WHITE);
        whiteNumberTwo.setTextSize(35);
        whiteNumberTwo.setTextAlign(Paint.Align.CENTER);
        yellowPaint.setColor(Color.RED);
        fieryPink.setColor(Color.rgb(255,0, 127));
        redPaint.setColor(Color.RED);
        //yellowPaint.setColor(Color.rgb(255,253,141));
        greenPaint.setColor(Color.rgb(0, 153, 51));
        neonBluePaint.setColor(Color.rgb(17,255,238));
        electricBlue.setColor(Color.rgb(122, 215, 240));
    }


    /*Paint paint = new Paint();
    Rect r = new Rect(70, 739, 249, 918);*/
    RectF rectF = new RectF(70, 739, 249, 918);

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        calculateDimensions();
        // draw white area of canvas
        canvas.drawRoundRect(border, border, width - border - sidebar, height - border, 20, 20, whitePaint);

        /*paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(137, 207, 240));
        canvas.drawRect(r, paint);*/
        // draw start Area
        drawStartArea(canvas);
        // draw grid lines and coordinates
        drawCoordinates(canvas);
        // draw obstacle box
        drawObstacleBox(canvas);
        // draw robot box
        drawRobotBox(canvas);
        // draw obstacles
        drawObstacles(canvas);
        // draw robot
        drawRobot(canvas, MainActivity.robot.getX(), MainActivity.robot.getY(), MainActivity.robot.getTheta());
    }
    private void drawStartArea(Canvas canvas) {
        int width = 4;
        canvas.drawRect(offsetX, offsetY + (numRows - width) * cellHeight, offsetX + width * cellWidth, offsetY + numRows * cellHeight, electricBlue);
    }

    private void calculateDimensions(){
        int sidebarNumOfCells = 4;
        this.width = getWidth();
        this.height = getHeight();
        this.cellWidth = (float) (width - padding*2 - border*2) / (numColumns + sidebarNumOfCells + 1);
        this.cellHeight = this.cellWidth;
        this.offsetX = padding + border + cellWidth;
        this.offsetY = padding + border;
        this.sidebar = sidebarNumOfCells * cellWidth;
    }

    private void drawCoordinates(Canvas canvas){
        float offsetX = padding + border + cellWidth;
        float offsetY = padding + border;
        for (int i = 0; i <= numColumns; i++){
            canvas.drawLine(offsetX + i * cellWidth, offsetY, offsetX + i * cellWidth, offsetY + cellHeight * numRows, blackPaint);
        }
        for (int i = 0; i <= numRows; i++){
            canvas.drawLine(offsetX, offsetY + i * cellHeight, offsetX + cellWidth * (numColumns), offsetY + i * cellHeight, blackPaint);
        }

        float textSize = this.coordinatesPaint.getTextSize();
        //canvas.drawText("0", offsetX - this.cellWidth/2, offsetY + this.cellHeight * (float) (numRows + 0.7), this.coordinatesPaint);
        for (int i = 0; i < numColumns; i++){
            canvas.drawText(String.valueOf(i), (float) (offsetX + this.cellWidth * (i + 0.5)), offsetY + this.cellHeight * (float) (numRows + 0.7), this.coordinatesPaint);
        }
        for (int i = 0; i < numRows; i++){
            canvas.drawText(String.valueOf(i), offsetX - this.cellWidth/2, (float) (offsetY + this.cellHeight * (numRows - (i + 0.5)) + textSize/2), this.coordinatesPaint);
        }
    }

    private void drawRobot(Canvas canvas, float x, float y, int degree){
        if (y == -1 || x == -1){
            y = ((float) robotBoxTop + robotBoxBottom)/2;
            x = ((float) sideBarRight + sideBarLeft)/2;
            degree = 0;
        }
        // Set up bitmap to draw
        Matrix matrix = new Matrix();
        matrix.postScale((float) com.example.mdpgrp2.Robot.ROBOT_LENGTH * cellWidth / robotBitmap.getWidth(), (float) com.example.mdpgrp2.Robot.ROBOT_LENGTH * cellHeight / robotBitmap.getHeight());
        matrix.postRotate((float) degree);
        Bitmap rotatedBitmap = Bitmap.createBitmap(robotBitmap,0,0, robotBitmap.getWidth(), robotBitmap.getHeight(), matrix, true);

        // Determine where to draw bitmap
        double radian =     convertToRadians(degree) * -1;
        double halfRobotLength = com.example.mdpgrp2.Robot.ROBOT_LENGTH / 2;
        double left;
        double top;
        if (degree >= 0 && degree <= 90){
            left = findRobotXAfterRotation(x, -halfRobotLength, -halfRobotLength, radian);
            top = findRobotYAfterRotation(y, -halfRobotLength, halfRobotLength, radian);
        } else if (degree > 90 && degree <= 180){
            left = findRobotXAfterRotation(x, halfRobotLength, -halfRobotLength, radian);
            top = findRobotYAfterRotation(y, -halfRobotLength, -halfRobotLength, radian);
        } else if (degree < 0 && degree > -90){
            left = findRobotXAfterRotation(x, -halfRobotLength, halfRobotLength, radian);
            top = findRobotYAfterRotation(y, halfRobotLength, halfRobotLength, radian);
        } else{
            left = findRobotXAfterRotation(x, halfRobotLength, halfRobotLength, radian);
            top = findRobotYAfterRotation(y, halfRobotLength, -halfRobotLength, radian);
        }

        canvas.drawBitmap(rotatedBitmap, offsetX + cellWidth * (float) left, offsetY + cellHeight * (numRows - (float) top), null);

    }

    private double findRobotXAfterRotation(float x, double dX, double dY, double radian){
        return x + (dX * Math.cos(radian)) - (dY * Math.sin(radian));
    }

    private double findRobotYAfterRotation(float y, double dX, double dY, double radian){
        return y + (dX * Math.sin(radian)) + (dY * Math.cos(radian));
    }

    private void drawObstacles(Canvas canvas){
        float textSize = this.whiteNumber.getTextSize();
        com.example.mdpgrp2.Map map = com.example.mdpgrp2.Map.getInstance();
        for (com.example.mdpgrp2.Obstacle obstacle: map.getObstacles()){
            float x = obstacle.getX();
            float y = obstacle.getY();
            float left = (float) (offsetX + (x - 0.5) * cellWidth);
            float top = (float) (offsetY + cellHeight * (numRows - y - 0.5));
            float right = (float) (offsetX + (x + 0.5) * cellWidth);
            float bottom = (float) (offsetY + cellHeight * (numRows - y + 0.5));
            if (obstacle.isExplored()){
                canvas.drawRect(left, top, right, bottom, greenPaint);
                canvas.drawText(String.valueOf(obstacle.getTargetID()),
                        (float) (left + 0.5 * cellWidth),
                        (float) (top + (cellHeight - textSize)/2 + 1 + textSize), whiteNumberTwo);

                konfettiView = findViewById(R.id.konfettiView);
                konfettiView.build()
                        .addColors(Color.WHITE, Color.GRAY, Color.BLUE)
                        .setDirection(0.0, 359.0)
                        .setSpeed(1f, 5f)
                        .setFadeOutEnabled(true)
                        .setTimeToLive(1000L)
                        .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                        .addSizes(new Size(8, 4f))
                        .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                        .streamFor(300, 5000L);


            } else{
                canvas.drawRect(left, top, right, bottom, blackPaint);
                canvas.drawText(String.valueOf(obstacle.getNumber()),
                        (float) (left + 0.5 * cellWidth),
                        (float) (top + (cellHeight - textSize)/2 - 3 + textSize), whiteNumber);
            }
            switch (obstacle.getSide()){
                case 'N':
                    canvas.drawRect(left, top, right, top + obstacleSideWidth, redPaint);
                    break;
                case 'S':
                    canvas.drawRect(left, bottom - obstacleSideWidth, right, bottom, redPaint);
                    break;
                case 'E':
                    canvas.drawRect(right - obstacleSideWidth, top, right, bottom, redPaint);
                    break;
                case 'W':
                    canvas.drawRect(left, top, left + obstacleSideWidth, bottom, redPaint);
                    break;
                default:
                    break;
            }
        }
    }

    private void drawObstacleBox(Canvas canvas){
        //Matrix matrix = new Matrix();
        //Bitmap bm = Bitmap.createBitmap(obstacleBitmap,0,0, obstacleBitmap.getWidth(), obstacleBitmap.getHeight(), matrix, true);
        //canvas.drawBitmap(bm, null, new RectF(offsetX + (sideBarLeft - 1) * cellWidth, offsetY + (numRows - obstacleBoxTop) * cellHeight, offsetX + sideBarRight * cellWidth,offsetY + (numRows - obstacleBoxBottom + 1) * cellHeight), null);
        canvas.drawBitmap(obstacleBitmap, null, new RectF(offsetX + sideBarLeft * cellWidth,
                        offsetY + (numRows - obstacleBoxTop) * cellHeight,
                        offsetX + sideBarRight * cellWidth,
                        offsetY + (numRows - obstacleBoxBottom) * cellHeight),
                null);
    }

    private void drawRobotBox(Canvas canvas){
        // draw robot box in sidebar
        canvas.drawBitmap(robotBoxBitmap, null, new RectF(offsetX + sideBarLeft * cellWidth, offsetY + (numRows - robotBoxTop) * cellHeight, offsetX + sideBarRight * cellWidth,offsetY + (numRows - robotBoxBottom) * cellHeight), null);

    }

    // Gesture detector for handling long presses
    final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
        public void onLongPress(MotionEvent event) {
            int selectedX = (int) (((event.getX() - offsetX)/cellWidth) + 1);
            int selectedY = (int) (numRows - ((event.getY() - offsetY)/cellHeight) + 1);
            ICoordinate obstacle = Map.getInstance().findObstacle(selectedX, selectedY);
            if (obstacle != null){
                MainActivity.showObstaclePopup(getContext(), getRootView(), (Obstacle) obstacle);
            }
        }
    });

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                objectToMove = null;
                // Determine which point in the map is the user touching
                initialX = (event.getX() - offsetX)/cellWidth;
                initialY = (numRows - ((event.getY() - offsetY)/cellHeight));

                // Touch robot on map
                if (MainActivity.robot.containsCoordinate(initialX, initialY)){
                    objectToMove = MainActivity.robot;
                    // Robot not on map and touch robot button
                } else if ((MainActivity.robot.getX() == -1 || MainActivity.robot.getY() == -1) && (sideBarLeft <= initialX && initialX <= sideBarRight && robotBoxBottom <= initialY && initialY <= robotBoxTop)){
                    objectToMove = MainActivity.robot;
                    // Touch obstacle button
                } else if (sideBarLeft <= initialX && initialX <= sideBarRight && obstacleBoxBottom <= initialY && initialY <= obstacleBoxTop){
                    objectToMove = com.example.mdpgrp2.Map.getInstance().addObstacle();
                } else {
                    // Returns obstacle on map that is being touched, returns null if no obstacle
                    objectToMove = com.example.mdpgrp2.Map.getInstance().findObstacle(initialX, initialY);
                }
                Log.d("MainActivity", "Current coordinates= row:" + initialX + ", col:" + initialY);
                break;
            case MotionEvent.ACTION_MOVE:
                // Determine which point in the map is the user touching
                float movingX = (event.getX() - offsetX)/cellWidth;
                float movingY = (numRows - ((event.getY() - offsetY)/cellHeight));
                // Convert X and Y coordinates to be the center of a grid box
                movingX = (float) (((int) movingX + 1) - 0.5);
                movingY = (float) (((int) movingY + 1) - 0.5);

                if (objectToMove instanceof com.example.mdpgrp2.Robot){
                    if (checkIfRobotInMap(movingX, movingY, com.example.mdpgrp2.Robot.ROBOT_LENGTH)){
                        Log.d("MainActivity", "Moving coordinates= X:" + movingX +", Y:" + movingY);
                        MainActivity ma = (MainActivity) this.getContext();
                        /*
                        try {
                            JSONObject obj = new JSONObject();
                            obj.put("type","origin");
                            obj.put("payload",(int) (MainActivity.robot.getX()) + "," + (int) (MainActivity.robot.getY()) + "," + MainActivity.robot.getDirection());
                            ma.outgoingMessage(obj.toString());
                        } catch (JSONException e) {
                            //some exception handler code.
                        }*/

                        MainActivity.robot.setCoordinates(movingX, movingY);
                        MainActivity.updateRobotPositionText();
                        invalidate();
                    }
                } else if (objectToMove instanceof com.example.mdpgrp2.Obstacle){
                    if ((movingX >= 0 && movingX <= numColumns) && (movingY >= 0 && movingY <= numRows)){
                        if (com.example.mdpgrp2.Map.getInstance().findObstacle(movingX, movingY) == null){
                            objectToMove.setCoordinates(movingX, movingY);
                        }
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                // Determine which point in the map is the user touching
                float finalX = (event.getX() - offsetX)/cellWidth;
                float finalY = (numRows - ((event.getY() - offsetY)/cellHeight));

                finalX = (float) (((int) finalX + 1) - 0.5);
                finalY = (float) (((int) finalY + 1) - 0.5);

                // when robot is drag and drop
                if (objectToMove instanceof com.example.mdpgrp2.Robot){
                    if (checkIfRobotInMap(finalX, finalY, com.example.mdpgrp2.Robot.ROBOT_LENGTH)){
                        MainActivity.robot.setCoordinates(finalX, finalY);
                        MainActivity.updateRobotPositionText();
                        Log.d("MainActivity", "ROBOT = row:"+MainActivity.robot.getX()+", col:"+MainActivity.robot.getY());
                        Log.d("MainActivity", "From ("+initialX+","+initialY+") to ("+finalX+","+finalY+")"+"\n");

                        // Send new robot coordinates via bluetooth
                        MainActivity ma = (MainActivity) this.getContext();
                        //ma.outgoingMessage("Robot " + ": (" + (int) (MainActivity.robot.getX()) + ", " + (int) (MainActivity.robot.getY()) + ")");
                        try {
                            JSONObject obj = new JSONObject();
                            obj.put("type","origin");
                            obj.put("payload",(int) (MainActivity.robot.getX()) + "," + (int) (MainActivity.robot.getY()) + "," + MainActivity.robot.getDirection());
                            ma.outgoingMessage(obj.toString());
                        } catch (JSONException e) {
                            //some exception handler code.
                        }
                    }
                    else{
                        MainActivity.robot.reset();
                        MainActivity.updateRobotPositionText();
                    }
                    invalidate();
                } else if (objectToMove instanceof com.example.mdpgrp2.Obstacle){
                    if ((finalX < 0 || finalX > numColumns) || (finalY < 0 || finalY > numRows)){
                        com.example.mdpgrp2.Map.getInstance().removeObstacle((com.example.mdpgrp2.Obstacle) objectToMove);
                        MainActivity ma = (MainActivity) this.getContext();
                        //ma.outgoingMessage("Removed Obstacle " + ((com.example.mdpgrp2.Obstacle) objectToMove).getNumber());
                    } else {
                        // If finger is released at a square
                        if (!com.example.mdpgrp2.Map.getInstance().isOccupied(finalX, finalY, (com.example.mdpgrp2.Obstacle) objectToMove)) {
                            objectToMove.setCoordinates(finalX, finalY);
                        }
                        MainActivity ma = (MainActivity) this.getContext();
                        //ma.outgoingMessage("Obstacle " + ((com.example.mdpgrp2.Obstacle) objectToMove).getNumber() + ": (" + (int) (objectToMove.getX()) + ", " + (int) (objectToMove.getY()) + ")");
                    }
                    invalidate();
                }
                break;
        }
        // handle long presses on obstacles
        gestureDetector.onTouchEvent(event);
        return true;
    }

    public void setObstacleSide(Obstacle obstacle, char side){
        obstacle.setSide(side);
        invalidate();
        MainActivity ma = (MainActivity) this.getContext();
        //ma.outgoingMessage("Obstacle " + obstacle.getNumber() + ": (" + obstacle.getX() + ", " + obstacle.getY() + "), " + obstacle.getSide());
        //fragment.sendMsg("Obstacle " + obstacle.getNumber() + ": (" + obstacle.getX() + ", " + obstacle.getY() + "), " + obstacle.getSide());
    }

    public void removeAllObstacles(){
        com.example.mdpgrp2.Map map = com.example.mdpgrp2.Map.getInstance();
        com.example.mdpgrp2.Map.getInstance().removeAllObstacles();
    }

    // Checks if Robot will be in the map given the new coordinates of x and y
    private boolean checkIfRobotInMap(float x, float y, double robotLength){
        double top = y + robotLength/2;
        double bottom = y - robotLength/2;
        double left = x - robotLength/2;
        double right = x + robotLength/2;
        return top <= numRows && bottom >= 0 && left >= 0 && right <= numColumns;
    }

    // Convert degree to radians
    private double convertToRadians(int degree){
        return degree * Math.PI/180;
    }

}

