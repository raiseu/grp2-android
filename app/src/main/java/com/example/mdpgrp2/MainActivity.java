package com.example.mdpgrp2;

import android.annotation.SuppressLint;
import android.content.Context;
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

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private static BluetoothChatFragment fragment;
    //Toolbar bottomSheetToolbar;

    private LinearLayout mBottomSheetLayout;
    private BottomSheetBehavior sheetBehavior;

    static Robot robot = new Robot();
    static FastestPathTimerFragment FPT = new FastestPathTimerFragment();
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
    //BluetoothChatFragment fragment;

    public boolean tiltChk = false;
    MutableLiveData<String> listen = new MutableLiveData<>();

    private BottomSheetBehavior bottomSheetBehavior;
    TabLayout tabLayout; //bottom_sheet_tabs
    ViewPager viewPager; //bottom_sheet_viewpager

    TextView timerText;
    Button startbutton;
    boolean timerStarted = false;
    Timer timer;
    TimerTask timerTask;
    Double time = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //bottomSheetToolbar = (Toolbar) this.findViewById(R.id.toolbar);

        LinearLayout linearLayout = findViewById(R.id.design_bottom_sheet);

        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);


        timerText = (TextView) findViewById(R.id.txtTimer);
        startbutton = (Button) findViewById(R.id.startbutton);
        timer = new Timer();

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        // add the tabs
        tabLayout.addTab(tabLayout.newTab().setText("FastestPathTimer"));
        tabLayout.addTab(tabLayout.newTab().setText("ImgRecTimer"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final bottomSheetAdapters adapter = new bottomSheetAdapters(this, getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

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
                String navi = "r";
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


/*
        findViewById(R.id.startbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimer();
            }
        });
*/


    }
/*
    // fastest path
    public void startTapped(View view){
        if(timerStarted == false){
            timerStarted = true;
            startbutton.setText("STOP");
            startbutton.setTextColor(ContextCompat.getColor(this, R.color.baby_blue));

            outgoingMessage("i am moving now"); // check if this is the correct message
            startTimer();

        }
        else{
            timerStarted = false;
            startbutton.setText("START");
            startbutton.setTextColor(ContextCompat.getColor(this, R.color.darkBlue));

            timerTask.cancel();
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

    private String getTimerTask() {
        int rounded = (int) Math.round(time);

        int seconds = ((rounded % 86400) % 3600) % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);

        return formatTime(seconds, minutes, hours);
    }

    private String formatTime(int seconds, int minutes, int hours) {
        return String.format("%02d", hours) + " : " + String.format("%02d", minutes) + " : " +  String.format("%02d", seconds);
    }
*/


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