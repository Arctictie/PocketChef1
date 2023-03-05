package com.example.pocketchef1;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class calendarGridAdapter extends ArrayAdapter {
    List<Date> dates;
    Calendar currentDate;
    List<calendarItem> calendarItems;
    LayoutInflater inflater;

    public calendarGridAdapter(@NonNull Context context,List<Date> dates,Calendar currentDate,List<calendarItem> calendarItems) {
        super(context, R.layout.calendar_cell);
        this.dates = dates;
        this.currentDate = currentDate;
        this.calendarItems = calendarItems;
        inflater = LayoutInflater.from(context);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Date monthDate = dates.get(position);
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(monthDate);
        int DayNo = dateCalendar.get(Calendar.DAY_OF_MONTH);
        int displayMonth = dateCalendar.get(Calendar.MONTH)+1;
        int displayYear = dateCalendar.get(Calendar.YEAR);
        int currentMonth = currentDate.get(Calendar.MONTH)+1;
        int currentYear = currentDate.get(Calendar.YEAR);
        View view = convertView;
        if(view ==null)
        {
            view = inflater.inflate(R.layout.calendar_cell,parent,false);
        }
        if(displayMonth == currentMonth && displayYear == currentYear)
        {
            view.setBackgroundColor(getContext().getResources().getColor(R.color.blue3));
        }
        else
        {
            view.setBackgroundColor(Color.parseColor("#7aa7eb"));
        }
        TextView dayNum = view.findViewById(R.id.day);
        dayNum.setText(String.valueOf(DayNo));
        return view;
    }

    @Override
    public int getCount() {
        return dates.size();
    }

    @Override
    public int getPosition(@Nullable Object item) {
        return dates.indexOf(item);
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return dates.get(position);
    }
}
