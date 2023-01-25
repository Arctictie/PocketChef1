package com.example.pocketchef1;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.pocketchef1.databinding.ActivityHomePageBinding;



public class homePage extends AppCompatActivity {

    ActivityHomePageBinding binding;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityHomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeContentFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    replaceFragment(new HomeContentFragment());
                    break;

                case R.id.create_meal:
                    replaceFragment(new CreateMealFragment());
                    break;
                case R.id.calender:
                    replaceFragment(new CalendarFragment());
                    break;
                case R.id.profile:
                    replaceFragment(new ProfileFragment());
                    break;


            }


            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.FLfragment, fragment);
        fragmentTransaction.commit();
    }


}