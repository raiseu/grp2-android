package com.example.mdpgrp2;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FastestPathTimerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FastestPathTimerFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    TextView timerText;
    Button startbutton;
    boolean timerStarted = false;
    Timer timer;
    TimerTask timerTask;
    Double time = 0.0;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FastestPathTimerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BottomSheetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FastestPathTimerFragment newInstance(String param1, String param2) {
        FastestPathTimerFragment fragment = new FastestPathTimerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        final View contentView = View.inflate(getContext(), R.layout.fragment_fastest_path_timer, null);
        timerText = contentView.findViewById(R.id.txtTimer);
        startbutton = (Button) contentView.findViewById(R.id.startbutton);
        timer = new Timer();

    }


    // fastest path
    public void startTapped(View view) {
        if (timerStarted == false) {
            timerStarted = true;
            startbutton.setText("STOP");
            startbutton.setTextColor(Color.WHITE);
            startTimer();


            MainActivity.outgoingMessage("i am moving now"); // check if this is the correct message

        } else {
            timerStarted = false;
            startbutton.setText("START");
            startbutton.setTextColor(Color.MAGENTA);

            timerTask.cancel();
        }


    }

    private void startTimer() {

        timerTask = new TimerTask() {

            @Override
            public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            time++;
                            timerText.setText(getTimerTask());
                            Log.d("hello", getTimerTask());

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
        return String.format("%02d", hours) + " : " + String.format("%02d", minutes) + " : " + String.format("%02d", seconds);
    }

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_fastest_path_timer, container, false);

        view = inflater.inflate(R.layout.fragment_fastest_path_timer, container, false);
        Button startbutton = (Button) view.findViewById(R.id.startbutton);
        startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTapped(view);
            }
        });
        return view;

    }


/*
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button button = (Button) view.findViewById(R.id.startbutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //place your action here
                if (timerStarted == false) {
                    timerStarted = true;
                    startbutton.setText("STOP");
                    startbutton.setTextColor(Color.WHITE);

                    startTimer();

                    MainActivity.outgoingMessage("i am moving now"); // check if this is the correct message


                } else {
                    timerStarted = false;
                    startbutton.setText("START");
                    startbutton.setTextColor(Color.MAGENTA);

                    timerTask.cancel();
                }
            }
        });
    }*/
/*
    @Override
    public void onClick(View view) {

    }
  */
/*
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.startbutton:
                startTapped(view);
                break;

        }
    }*/
}