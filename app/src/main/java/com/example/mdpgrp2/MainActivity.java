package com.example.mdpgrp2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.mdpgrp2.bluetoothchat.BluetoothChatFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static Robot robot = new Robot();
    @SuppressLint("StaticFieldLeak")
    public static TextView txtX;
    @SuppressLint("StaticFieldLeak")
    public static TextView txtY;
    @SuppressLint("StaticFieldLeak")
    public static TextView txtDir;
    @SuppressLint("StaticFieldLeak")
    public static TextView txtRobotStatus;
    private static MapGrid mapGrid;
    BluetoothChatFragment fragment;

    public boolean tiltChk = false;
    private Gyroscope gyroscope;
    MutableLiveData<String> listen = new MutableLiveData<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listen.setValue("Default");
        gyroscope = new Gyroscope(this);

        //drawing of map grid
        mapGrid = findViewById(R.id.map);

        //Update Robot Pos
        txtX = findViewById(R.id.txtX);
        txtY = findViewById(R.id.txtY);

        //Update Robot Direction
        txtDir = findViewById(R.id.txtDirection);

        //Update Robot Status
        txtRobotStatus = findViewById(R.id.txtRobotStatus);

        // Remove shadow of action bar
        getSupportActionBar().setElevation(0);
        // Set layout to shift up when soft keyboard is open
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            fragment = new BluetoothChatFragment();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }

        //Reset Robot
        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mapGrid.removeAllObstacles();
                robot.reset();
                mapGrid.invalidate();
                updateRobotPositionText();
            }
        });

        //To move forward
        findViewById(R.id.btnUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    robot.moveRobotForward(1.0);
                    mapGrid.invalidate();
                    String navi = "f";
                    outgoingMessage(navi);
                    Toast.makeText(MainActivity.this, "Move forward",
                        Toast.LENGTH_SHORT).show();
                    updateRobotPositionText();
            }
        });

        //To move backwards
        findViewById(R.id.btnDown).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                robot.moveRobotForward(-1.0);
                mapGrid.invalidate();
                String navi = "r";
                outgoingMessage(navi);
                Toast.makeText(MainActivity.this, "Move backward",
                        Toast.LENGTH_SHORT).show();
                updateRobotPositionText();
            }
        });

        //Turn left
        findViewById(R.id.btnLeft).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                robot.moveRobotTurnLeft();
                mapGrid.invalidate();
                String navi = "tl";
                outgoingMessage(navi);
                Toast.makeText(MainActivity.this, "Turn Left",
                        Toast.LENGTH_SHORT).show();
                updateRobotPositionText();
            }
        });

        //Turn right
        findViewById(R.id.btnRight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                robot.moveRobotTurnRight();
                mapGrid.invalidate();
                String navi = "tr";
                outgoingMessage(navi);
                Toast.makeText(MainActivity.this, "Turn Right",
                        Toast.LENGTH_SHORT).show();
                updateRobotPositionText();
            }
        });

        // GYROSCOPE AND TILT SWITCH
        Switch sw = (Switch) findViewById(R.id.tiltSwitch);

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    tiltChk =true;
                    onResume();
                    gyroscope.register();
                } else {
                    // The toggle is disabled
                    tiltChk=false;
                    onPause();
                }
            }
        });


        gyroscope.setListener(new Gyroscope.Listener() {

            @Override
            public void onRotation(float rx, float ry, float rz) {
                if(rx<-1.0f){
                    if(listen.getValue() !="Move" ){
                        listen.setValue("Move");
                    }
                }
                else if (rx>1.0f){
                    if(listen.getValue() != "Default" ){
                        listen.setValue("Default");
                    }
                }
                else if(rz<-1.0f){
                    if(listen.getValue() !="Right" ){
                        listen.setValue("Right");
                    }
                }
                else if (rz>1.0f){
                    if(listen.getValue() !="Left" ){
                        listen.setValue("Left");
                    }
                }
            }
        });

        listen.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(s == "Move"){
                    if (robot.getX() != -1 && robot.getY() != -1) {
                        robot.moveRobotForward(1.0);
                        mapGrid.invalidate();
                        txtX.setText(String.valueOf(robot.getX()));
                        txtY.setText(String.valueOf(robot.getY()));
                        txtDir.setText(String.valueOf(robot.getDirection()));
                    }
                    Log.d("MainActivity", "MOVE ");
                }else if (s=="Left"){
                    if (robot.getX() != -1 && robot.getY() != -1) {
                        robot.moveRobotTurnLeft();
                        mapGrid.invalidate();
                        txtX.setText(String.valueOf(robot.getX()));
                        txtY.setText(String.valueOf(robot.getY()));
                        txtDir.setText(String.valueOf(robot.getDirection()));
                    }
                    Log.d("MainActivity", "LEFT");
                }else{
                    Log.d("MainActivity", "CHANGE VALUE: "+s);
                }
            }
        });

    }


    public void outgoingMessage(String sendMsg) {
        fragment.sendMsg(sendMsg);
        //Toast.makeText(getApplicationContext(),sendMsg,Toast.LENGTH_SHORT).show();
    }

    public static void showObstaclePopup(Context c, View view, Obstacle obstacle) {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) c.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.obstacle_popup_window, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        Button btnN = (Button) popupView.findViewById(R.id.obstacleN);
        Button btnS = (Button) popupView.findViewById(R.id.obstacleS);
        Button btnE = (Button) popupView.findViewById(R.id.obstacleE);
        Button btnW = (Button) popupView.findViewById(R.id.obstacleW);

        // show the popup window
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        MapGrid mapGrid = view.findViewById(R.id.map);

        btnN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapGrid.setObstacleSide(obstacle, 'N');
                popupWindow.dismiss();
            }
        });

        btnS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapGrid.setObstacleSide(obstacle, 'S');
                popupWindow.dismiss();
            }
        });

        btnE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapGrid.setObstacleSide(obstacle, 'E');
                popupWindow.dismiss();
            }
        });

        btnW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapGrid.setObstacleSide(obstacle, 'W');
                popupWindow.dismiss();
            }
        });

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }



    // Set robot position based on bluetooth string received
    public static boolean setRobotPosition(float x, float y, char direction){
        if (0 <= x && x <= 200 && 0 <= y && y <= 200 && (direction == 'N' || direction == 'S' || direction == 'E' || direction == 'W')){
            robot.setCoordinates(x, y);
            robot.setDirection(direction);
            switch (direction){
                case 'N':
                    robot.setTheta(0);
                    break;
                case 'S':
                    robot.setTheta(180);
                    break;
                case 'E':
                    robot.setTheta(90);
                    break;
                case 'W':
                    robot.setTheta(-90);
                    break;
                default:
                    break;
            }
            updateRobotPositionText();
            mapGrid.invalidate();
            return true;
        }
        return false;
    }

    // Update the targetID of the obstacle once image recognised
    public static boolean exploreTarget(int obstacleNumber, int targetID){
        // if obstacle number exists in map
        if (1 <= obstacleNumber && obstacleNumber <= Map.getInstance().getObstacles().size()){
            Obstacle obstacle = Map.getInstance().getObstacles().get(obstacleNumber - 1);
            obstacle.explore(targetID);
            mapGrid.invalidate();
            return true;
        }
        return false;
    }

    // Update the robot status
    public static void updateRobotStatus(String status){
        robot.setStatus(status);
        txtRobotStatus.setText(robot.getStatus());
    }

    // Update the coordinates and direction of the robot in textView
    @SuppressLint("SetTextI18n")
    public static void updateRobotPositionText(){
        if (robot.getX() != -1 && robot.getY() != -1){
            txtX.setText(String.valueOf((int) ((robot.getX() *10 - 15)/10)));
            txtY.setText(String.valueOf((int) ((robot.getY()*10 -15)/10)));
            switch (robot.getTheta()){
                case 0:
                    txtDir.setText("N");
                    break;
                case 90:
                    txtDir.setText("E");
                    break;
                case 180:
                    txtDir.setText("S");
                    break;
                case -90:
                    txtDir.setText("W");
                    break;
                default:
                    txtDir.setText(robot.getTheta() + " \u00B0");
                    break;
            }
        } else {
            txtX.setText("-");
            txtY.setText("-");
            txtDir.setText("-");
        }
    }

    // Make robot move straight forward or reverse
    public static void driveRobotStraight(double displacement){

    }

    @Override
    protected void onResume() {
        super.onResume();
        gyroscope.register();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gyroscope.unregister();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
//        Resources res = getResources();
//        String[] menuOptions = res.getStringArray(R.array.bluetooth_menu);
//        for (int i = 0; i<menuOptions.length; i++){
//            menu.add(0, i, 0, menuOptions[i]).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
//        }
        return true;
    }

}