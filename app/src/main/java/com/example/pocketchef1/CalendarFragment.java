package com.example.pocketchef1;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class CalendarFragment extends Fragment {
    ImageButton calBack,calForward;
    TextView CurrentDate;
    GridView gridView;
    Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
    Context context;
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy",Locale.ENGLISH);
    SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM",Locale.ENGLISH);
    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy",Locale.ENGLISH);
    List<Date> dates = new ArrayList<>();
    List<calendarItem> calendarItemList = new ArrayList<>();
    public CalendarFragment() {
        this.context = getContext();
    }

    public static CalendarFragment newInstance(String param1, String param2) {

        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
               fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        calBack = view.findViewById(R.id.calenderBack);
        calForward = view.findViewById(R.id.calenderForward);
        CurrentDate = view.findViewById(R.id.currentDate);
        gridView = view.findViewById(R.id.gridView);
        initialiseCalendar();

        calBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH,-1);
                initialiseCalendar();
            }
        });
        calForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH,1);
                initialiseCalendar();
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.calendar, container, false);
    }


    private void initialiseCalendar()
    {
        String currentDate = dateFormat.format(calendar.getTime());
        CurrentDate.setText(currentDate);
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }
    @Override
    public void onPause()
    {
        super.onPause();

    }
}