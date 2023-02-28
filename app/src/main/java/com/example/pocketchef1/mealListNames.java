package com.example.pocketchef1;

import java.util.List;

public class mealListNames {
    List<String> listNames;

    public List<String> getListNames() {
        return listNames;
    }

    public void setListNames(List<String> listNames) {
        this.listNames = listNames;
    }

    public mealListNames() {
    }

    public String getItemAtPos(int position) {
        return listNames.get(position);
    }

    public mealListNames(List<String> listNames) {
        this.listNames = listNames;
    }

}
