package com.example.pocketchef1;

public class mealItem {

    private String meal_name;
    private String description;
    private String ingredients;
    private String instructions;


    public mealItem(){
        //needed for firebase.
    }

    public String getMeal_name() {
        return meal_name;
    }

    public String getDescription() {
        return description;
    }

    public String getIngredients() {return ingredients;}

    public String getInstructions() {return instructions;}

    public mealItem(String meal_name, String description, String ingredients, String instructions) {
        this.meal_name = meal_name;
        this.description = description;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

}
