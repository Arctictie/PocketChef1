package com.example.pocketchef1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class mealViewAdapter extends RecyclerView.Adapter<mealViewAdapter.mealViewHolder> {
     Context context;
    ArrayList<mealItem> mealsList;

    public mealViewAdapter(Context context, ArrayList<mealItem> mealsList) {
        this.context = context;
        this.mealsList = mealsList;
    }

    @NonNull
    @Override
    public mealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View newView = LayoutInflater.from(context).inflate(R.layout.meal_item, parent, false);
        return new mealViewHolder(newView);
    }

    @Override
    public void onBindViewHolder(@NonNull mealViewHolder holder, int position) {
        mealItem mealitem = mealsList.get(position);

        holder.mealTitle.setText(mealitem.getMeal_name());
        holder.mealDescription.setText(mealitem.getDescription());
    }

    @Override
    public int getItemCount() {
        return mealsList.size();
    }

    public static class mealViewHolder extends RecyclerView.ViewHolder {
        TextView mealTitle;
        TextView mealDescription;
        ImageView mealImage;

        public mealViewHolder(@NonNull View itemView) {
            super(itemView);

            mealTitle = itemView.findViewById(R.id.mealTitle);
            mealDescription = itemView.findViewById(R.id.mealDescription);
            mealImage = itemView.findViewById(R.id.mealImg);
        }
    }
}




//public static class mealViewHolder extends RecyclerView.ViewHolder{
//    TextView mealTitle;
//    TextView mealDescription;
//    ImageView mealImage;
//    private CreateMealFragment adapter;
//
//    public mealViewHolder(@NonNull View itemView)
//    {
//       super(itemView);
//        mealTitle = itemView.findViewById(R.id.mealTitle);
//        mealDescription = itemView.findViewById(R.id.mealDescription);
//        mealImage = itemView.findViewById(R.id.mealImg);
//    }
//    private mealViewHolder linkAdapter(CreateMealFragment adapter){
//        this.adapter = adapter;
//        return this;
//    }
//}