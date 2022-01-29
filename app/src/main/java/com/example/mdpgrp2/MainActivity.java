package com.example.mdpgrp2;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

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

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    private static Context context;
    ProgressDialog myDialog;
    private static final String TAG = "Main Activity";

    Toolbar bottomSheetToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //bottomSheetToolbar = (Toolbar) this.findViewById(R.id.toolbar);

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
    }

    public void outgoingMessage(String sendMsg) {
        fragment.sendMsg(sendMsg);
        //Toast.makeText(getApplicationContext(),sendMsg,Toast.LENGTH_SHORT).show();
    }

    private BroadcastReceiver mBroadcastReceiver5 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice mDevice = intent.getParcelableExtra("Device");
            String status = intent.getStringExtra("Status");
            sharedPreferences();

            if(status.equals("connected")){
                try {
                    myDialog.dismiss();
                } catch(NullPointerException e){
                    e.printStackTrace();
                }

                Log.d(TAG, "mBroadcastReceiver5: Device now connected to "+mDevice.getName());
                Toast.makeText(MainActivity.this, "Device now connected to "+mDevice.getName(), Toast.LENGTH_LONG).show();
                editor.putString("connStatus", "Connected to " + mDevice.getName());
//                TextView connStatusTextView = findViewById(R.id.connStatusTextView);
//                connStatusTextView.setText("Connected to " + mDevice.getName());
            }
            else if(status.equals("Disconnected")){
                //Log.d(TAG, "mBroadcastReceiver5: Disconnected from "+mDevice.getName());
                //Toast.makeText(MainActivity.this, "Disconnected from "+mDevice.getName(), Toast.LENGTH_LONG).show();
//                mBluetoothConnection = new BluetoothConnectionService(MainActivity.this);
//                mBluetoothConnection.startAcceptThread();

                editor.putString("connStatus", "Disconnected");
//                TextView connStatusTextView = findViewById(R.id.connStatusTextView);
//                connStatusTextView.setText("Disconnected");

                myDialog.show();
            }
            editor.commit();
        }
    };

    public static void sharedPreferences() {
        sharedPreferences = MainActivity.getSharedPreferences(MainActivity.context);
        editor = sharedPreferences.edit();
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("Shared Preferences", Context.MODE_PRIVATE);
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

    /*
    private void setupBottomSheet() {
        bottomSheetToolbar.setTitle(R.string.message);
        final PagerAdapter sectionsPagerAdapter = new PagerAdapter(getSupportFragmentManager(), this, TabItem.CONNECTION, TabItem.MESSAGE);
        bottomSheetViewPager.setOffscreenPageLimit(1);
        bottomSheetViewPager.setAdapter(sectionsPagerAdapter);
        bottomSheetTabLayout.setupWithViewPager(bottomSheetViewPager);
        BottomSheetUtils.setupViewPager(bottomSheetViewPager);
    }

*/
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
            txtX.setText(String.valueOf((int) (robot.getX() * 10)));
            txtY.setText(String.valueOf((int) (robot.getY() * 10)));
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
        //gyroscope.register();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        gyroscope.unregister();
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