package com.example.pocketchef1;

public class mealItem {

    private String meal_name;
    private String description;

    public mealItem(){
        //needed for firebase.
    }

    public String getMeal_name() {
        return meal_name;
    }

    public String getDescription() {
        return description;
    }

    public mealItem(String meal_name, String description) {
        this.meal_name = meal_name;
        this.description = description;
    }

}
