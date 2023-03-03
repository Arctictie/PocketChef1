package com.example.pocketchef1;

public class mealImage {
    private String imageURl;
    private String mealName;

    public mealImage() {
        //needed for firebase.
    }

    public mealImage(String imageURl,String mealName)
    {
        this.imageURl = imageURl;
        this.mealName = mealName;
    }

    public String getImageUrl() {
        return imageURl;
    }
    public void setImageUrl(String imageURl) {
        this.imageURl = imageURl;
    }

    public String getMealName() {
        return mealName;
    }
}
