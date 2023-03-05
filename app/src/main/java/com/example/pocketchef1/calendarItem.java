package com.example.pocketchef1;

public class calendarItem {
    private String mealName,date,month,year;

    public calendarItem() {
        //needed for firebase
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public calendarItem(String mealName,String date, String month, String year) {
        this.mealName = mealName;
        this.date = date;
        this.month = month;
        this.year = year;
    }
}
