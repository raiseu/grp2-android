package com.example.mdpgrp2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;

import com.example.mdpgrp2.bluetoothchat.BluetoothChatFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private static BluetoothChatFragment fragment;
    //Toolbar bottomSheetToolbar;

    private LinearLayout mBottomSheetLayout;
    private BottomSheetBehavior sheetBehavior;

    static Robot robot = new Robot();
    static Obstacle obstacle = new Obstacle(1);
    @SuppressLint("StaticFieldLeak")
    public static TextView txtX;
    @SuppressLint("StaticFieldLeak")
    public static TextView txtY;
    @SuppressLint("StaticFieldLeak")
    public static TextView txtDir;
    @SuppressLint("StaticFieldLeak")
    public static TextView txtRobotStatus, bluetoothTVStatus;
    private static MapGrid mapGrid;

    TextView timerText;
    TextView timerText2;
    TextView fastestPT;
    TextView imgRecT;
    Button startbutton;
    Button startbutton2;
    boolean timerStarted = false;
    boolean timerStarted2 = false;
    Timer timer;
    Timer timer2;
    TimerTask timerTask;
    TimerTask timerTask2;
    Double time = 0.0;
    Double time2 = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //bottomSheetToolbar = (Toolbar) this.findViewById(R.id.toolbar);

        LinearLayout linearLayout = findViewById(R.id.design_bottom_sheet);

        timerText = (TextView) findViewById(R.id.txtTimer);
        timerText2 = (TextView) findViewById(R.id.txtTimer2);
        startbutton = (Button) findViewById(R.id.startbutton);
        startbutton2 = (Button) findViewById(R.id.startbutton2);
        fastestPT = (TextView) findViewById(R.id.fastestTV);
        imgRecT = (TextView) findViewById(R.id.ImageTV);

        timer = new Timer();
        timer2 = new Timer();


        //drawing of map grid
        mapGrid = findViewById(R.id.map);

        //Update Robot Pos
        txtX = findViewById(R.id.txtX);
        txtY = findViewById(R.id.txtY);

        bluetoothTVStatus = findViewById(R.id.bluetoothTV);

        //Update Robot Direction
        txtDir = findViewById(R.id.txtDirection);

        //Update Robot Status
        txtRobotStatus = findViewById(R.id.txtRobotStatus);


        // Remove shadow of action bar
        //getSupportActionBar().setElevation(0);
        // Define ActionBar object
        ActionBar actionBar;
        actionBar = getSupportActionBar();

        // Define ColorDrawable object and parse color
        // using parseColor method
        // with color hash code as its parameter
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#FF0C57A8"));

        // Set BackgroundDrawable
        actionBar.setBackgroundDrawable(colorDrawable);


        // Set layout to shift up when soft keyboard is open
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        ConstraintLayout constraintLayout = findViewById(R.id.mainlayout);

        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            fragment = new BluetoothChatFragment();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }

        //Reset Robot
        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
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
                //if(robot.getX()+30 != obstacle.getX()  && robot.getY()+30 != obstacle.getY()){
                robot.moveRobotForward(1.0);
                mapGrid.invalidate();
                String navi = "f";
                outgoingMessage(navi);
                //Toast.makeText(MainActivity.this, "Move forward", Toast.LENGTH_SHORT).show();
                updateRobotPositionText();
                //}
            }
        });

        //To move backwards
        findViewById(R.id.btnDown).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                robot.moveRobotForward(-1.0);
                mapGrid.invalidate();
                String navi = "b";
                outgoingMessage(navi);
                //Toast.makeText(MainActivity.this, "Move backward", Toast.LENGTH_SHORT).show();
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
                //Toast.makeText(MainActivity.this, "Turn Left", Toast.LENGTH_SHORT).show();
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
                //Toast.makeText(MainActivity.this, "Turn Right", Toast.LENGTH_SHORT).show();
                updateRobotPositionText();
            }
        });

    }



    // fastest path
    public void resetTapped(View view)
    {
        AlertDialog.Builder resetAlert = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        //AlertDialog alertDialog = resetAlert.create();
        resetAlert.setTitle("Reset Timer");
        resetAlert.setMessage("Are you sure you want to reset the timer?");
        resetAlert.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(timerTask != null)
                {
                    timerTask.cancel();
                    startButtonUI("START", R.color.white);
                    timerTVUI("Fastest Path Timer", R.color.white);
                    time = 0.0;
                    timerStarted = false;
                    timerText.setText(formatTime(0,0,0));
                    timerText.setTextColor(getResources().getColor(R.color.white));
                }

            }
        });

        resetAlert.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //do nothing

            }
        });

        resetAlert.show();


    }

    public void resetTapped2(View view)
    {
        AlertDialog.Builder resetAlert = new AlertDialog.Builder(this, R.style.AlertDialogTheme) ;
        resetAlert.setTitle("Reset Timer");
        resetAlert.setMessage("Are you sure you want to reset the timer?");
        resetAlert.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(timerTask2 != null)
                {
                    timerTask2.cancel();
                    startButtonUI2("START", R.color.white);
                    timerTVUI("Image Recognition", R.color.white);
                    time2 = 0.0;
                    timerStarted2 = false;
                    timerText2.setText(formatTime(0,0,0));
                    timerText2.setTextColor(getResources().getColor(R.color.white));

                }

            }
        });

        resetAlert.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //do nothing

            }
        });

        resetAlert.show();

    }



    public void startTapped(View view){
        if(timerStarted == false){
            timerStarted = true;
            startButtonUI("STOP", R.color.white);
            timerTxtUI("timerText", R.color.nenonBlue);
            timerTVUI("Fastest Path Timer",  R.color.oceanBreeze);
            Toast.makeText(MainActivity.this, "Timer Stopped", Toast.LENGTH_SHORT).show();

            //outgoingMessage("START");
            try {
                JSONObject obj = new JSONObject();
                obj.put("type","instruction");
                obj.put("payload", "start");
                outgoingMessage(obj.toString()); // check if this is the correct message
            } catch (JSONException e) {
                //some exception handler code.
            }

            startTimer();
        }
        else{
            timerStarted = false;
            startButtonUI("START", R.color.blueWhite);
            timerTxtUI("timerText", R.color.fiery_coral);
            timerTVUI("Fastest Path Timer",  R.color.fiery_coral);


            timerTask.cancel();
        }
    }

    public void startTapped2(View view){
        try {
            JSONObject objj = new JSONObject();
            JSONArray array = new JSONArray();
        if(timerStarted2 == false){
            StringBuilder msg = new StringBuilder();
            ArrayList<Obstacle> obstacles = Map.getInstance().getObstacles();
            if(obstacles.size() != 5){
                Toast.makeText(MainActivity.this, obstacles.size() + " obstacles selected ",
                        Toast.LENGTH_SHORT).show();
            }
            else{
                boolean setDir = true;
                StringBuilder unset = new StringBuilder();
                char dir = ' ';
                for (int i = 0; i < obstacles.size(); i++){
                    if (i != 0){
                        msg.append("|");
                    }
                    dir = obstacles.get(i).getSide();
                    if (dir != 'N' && dir != 'S' && dir != 'E' && dir != 'W') {
                        setDir = false;
                        if (!unset.toString().equals("")) {
                            unset.append(",");
                        }
                        unset.append(obstacles.get(i).getNumber());
                    }
                    /*
                    msg.append(obstacles.get(i).getNumber()).append(",")
                            .append((int) (obstacles.get(i).getX() )).append(",")
                            .append((int) (obstacles.get(i).getY() )).append(",")
                            .append(obstacles.get(i).getSide());
                        */
                        array.put(obstacles.get(i).getNumber() + "," +
                                (int)obstacles.get(i).getX() + "," +
                                (int)obstacles.get(i).getY()  + "," +
                                obstacles.get(i).getSide());
                }
                if (setDir) {
                    timerStarted2 = true;
                    startButtonUI2("STOP", R.color.white);
                    timerTxtUI("timerText2", R.color.nenonBlue);
                    timerTVUI("Image Recognition",  R.color.oceanBreeze);
                    //Toast.makeText(MainActivity.this, "Timer Stopped", Toast.LENGTH_SHORT).show();

                    startTimer2();

                    objj.put("type","coordinates");
                    objj.put("payload",array);
                    outgoingMessage(objj.toString());
                }
                else {
                    Toast.makeText(MainActivity.this, unset + " side not selected",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
        else{
            timerStarted2 = false;
            startButtonUI2("START", R.color.blueWhite);
            timerTxtUI("timerText2", R.color.fiery_coral);
            timerTVUI("Image Recognition",  R.color.fiery_coral);

            timerTask2.cancel();
        }
        } catch (JSONException e) {
            //some exception handler code.
        }
    }

    private void startButtonUI(String start, int color) {
        startbutton.setText(start);
        startbutton.setTextColor(ContextCompat.getColor(this, color));
    }

    private void startButtonUI2(String start, int color) {
        startbutton2.setText(start);
        startbutton2.setTextColor(ContextCompat.getColor(this, color));
    }

    private void timerTxtUI(String start, int color) {
        if(start == "timerText")
            timerText.setTextColor(ContextCompat.getColor(this, color));
        else
            timerText2.setTextColor(ContextCompat.getColor(this, color));
    }

    private void timerTVUI(String start, int color) {
        if(start == "Fastest Path Timer"){
            fastestPT.setTextColor(ContextCompat.getColor(this, color));
            fastestPT.setText("Fastest Path Timer");
        }

        else if(start == "Image Recognition"){
            imgRecT.setTextColor(ContextCompat.getColor(this, color));
            imgRecT.setText("Image Recognition");
        }
    }

    private void startTimer() {
        timerTask  = new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        time++;
                        timerText.setText(getTimerTask());

                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000); // 1000ms = 1s

    }

    private void startTimer2() {
        timerTask2  = new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        time2++;
                        timerText2.setText(getTimerTask2());

                    }
                });


            }
        };
        timer2.scheduleAtFixedRate(timerTask2, 0, 1000); // 1000ms = 1s

    }

    private String getTimerTask() {
        int rounded = (int) Math.round(time);

        int seconds = ((rounded % 86400) % 3600) % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);

        return formatTime(seconds, minutes, hours);
    }

    private String getTimerTask2() {
        int rounded = (int) Math.round(time2);

        int seconds = ((rounded % 86400) % 3600) % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);

        return formatTime(seconds, minutes, hours);
    }

    private String formatTime(int seconds, int minutes, int hours) {
        return String.format("%02d", hours) + " : " + String.format("%02d", minutes) + " : " +  String.format("%02d", seconds);
    }



    public static void outgoingMessage(String sendMsg) {
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
    public static boolean setRobotPosition(float x, float y, char direction) {
        if (0 <= x && x <= 19 && 0 <= y && y <= 19 && (direction == 'N' || direction == 'S' || direction == 'E' || direction == 'W')) {
            x = (float) (x + 0.5);
            y = (float) (y + 0.5);
            robot.setCoordinates(x, y);
            robot.setDirection(direction);
            switch (direction) {
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

    // Update the robot status
    public static void updateBluetoothStatus(String status) {
        bluetoothTVStatus.setText(status);
    }

    // Update the targetID of the obstacle once image recognised
    public static boolean exploreTarget(int obstacleNumber, int targetID) {
        // if obstacle number exists in map
        if (1 <= obstacleNumber && obstacleNumber <= Map.getInstance().getObstacles().size()) {
            Obstacle obstacle = Map.getInstance().getObstacles().get(obstacleNumber - 1);
            obstacle.explore(targetID);
            mapGrid.invalidate();
            return true;
        }
        return false;
    }

    // Update the robot status
    public static void updateRobotStatus(String status) {
        robot.setStatus(status);
        txtRobotStatus.setText(robot.getStatus());
    }

    // Update the coordinates and direction of the robot in textView
    @SuppressLint("SetTextI18n")
    public static void updateRobotPositionText() {
        if (robot.getX() != -1 && robot.getY() != -1) {
            txtX.setText(String.valueOf((int) (robot.getX())));
            txtY.setText(String.valueOf((int) (robot.getY())));
            switch (robot.getTheta()) {
                case 0:
                    txtDir.setText("NORTH");
                    break;
                case 90:
                    txtDir.setText("EAST");
                    break;
                case 180:
                    txtDir.setText("SOUTH");
                    break;
                case -90:
                    txtDir.setText("WEST");
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

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}