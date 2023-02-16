package com.example.pocketchef1;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String KEY_MEAL_NAME ="meal_name";
    private static final String KEY_DESCRIPTION ="description";
    private static final String KEY_EMAIL = "Email";
    private static final String KEY_CREATED_MEALS = "created_meals";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser userAuth;
    FirebaseAuth mAuth;
    private String encrypedEmail;

    String UID ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
    }


    @Override
    public void onStart() {
        super.onStart();
// Check if user is signed in (non-null) and update accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
//User is signed in use an intent to move to another activity
        }
    }



    public void signup(String email, String password) {


        mAuth.createUserWithEmailAndPassword(email, password)


                .addOnCompleteListener(this, new
                        OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    mAuth.signInWithEmailAndPassword(email,password);
                                    Log.d("MainActivity",
                                            "createUserWithEmail:success");
                                    userAuth = mAuth.getCurrentUser();
                                     userAuth = FirebaseAuth.getInstance().getCurrentUser();
                                    Map<String, Object> user = new HashMap<>();
                                    Boolean createdMeals = false;
                                    user.put(KEY_EMAIL, email);
                                    user.put(KEY_CREATED_MEALS, createdMeals);

                                    db.collection("users").document(String.valueOf(userAuth.getUid()))
                                            .set(user)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(MainActivity.this, "added user  to database", Toast.LENGTH_SHORT).show();
                                                    UID = userAuth.getUid();
                                                    Intent i = new Intent(getApplicationContext(), homePage.class);
                                                    i.putExtra("uid",UID);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(MainActivity.this, "Failed to add user to database", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                    assert userAuth != null;


                                    Toast.makeText(MainActivity.this,
                                            "Authentication success. Use an intent to move to a new activity",
                                            Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(getApplicationContext(), homePage.class);
                                    i.putExtra("uid",UID);
                                    startActivity(i);


                                } else {


                                    Log.w(TAG,
                                            "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(MainActivity.this,
                                            "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }

                            }


                        });


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createUserDir(String sEmail) {
        Map<String, Object> user = new HashMap<>();
        Boolean createdMeals = false;
        user.put(KEY_EMAIL, sEmail);
        user.put(KEY_CREATED_MEALS, createdMeals);
        String encodedEmail = encodeEmail(sEmail);
        db.collection("users").document(encodedEmail)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MainActivity.this, "added user  to database", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Failed to add user to database", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void signupClicked(View view) {
        try {
            EditText email = findViewById(R.id.signupEmail);
            EditText password = findViewById(R.id.signupPassword);
            String sEmail = email.getText().toString();
            String sPassword = password.getText().toString();


            signup(sEmail, sPassword);
            generateBaseMeals();

        } catch (IllegalArgumentException e) {
            Toast.makeText(MainActivity.this,
                    "Enter username and password",
                    Toast.LENGTH_SHORT).show();
            Log.w("MainActivity",
                    "signupClicked:failure", e);
        }


    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public String encodeEmail(String email)
    {
          byte[] Passbyte = email.getBytes(StandardCharsets.UTF_8);
            byte[] Base64pass = Base64.getEncoder().encode(Passbyte);
            return new String (Base64pass,StandardCharsets.UTF_8);

    }



    public void generateBaseMeals() {

        String[] mealNames = {"Pepper steak with noodles"
               , "Chicken chow mein"
                , "Healthy Lasagna"
                , "One-Pot garlic chicken"
                , "Vegetarian chili"
                , "Oven baked pork chops"};

        String[] mealDescriptions = { "Try our pepper steak with noodles for a simple, balanced, midweek meal that's full of flavour. Top with sesame seeds and crushed peppercorns to serve"
                , "This taiwanese-style chow mein uses a combination of sir frying and steaming so theres less oil involed and pork is used instead of chicken"
                , "lighter version of the family classic – lasagne. It has bags of flavour, but comes in at under 500 calories a portion, as well as packing in four of your five-a-day"
                , "Save on the washing-up with this easy one-pot garlic chicken. Creamy and comforting, you can adjust the amount of garlic to suit your own tastes."
                , "Rustle up our easy vegetarian chilli. It's a great recipe for batch-cooking – you can easily double it if you have a pan big enough, and freeze the rest"
                , "Enjoy oven-baked pork chops cooked in a honey and wholegrain mustard glaze with new potatoes for a deliciously easy dinner, just add your favourite veg"};

        for (int i =0 ;i < mealNames.length; i++) {

            Map<String, Object> meal = new HashMap<>();
            meal.put(KEY_MEAL_NAME, mealNames[i]);
            meal.put(KEY_DESCRIPTION, mealDescriptions[i]);

            db.collection("defaultMeals").document("defaultlist").collection("DefaultMeals").document(mealNames[i])
                    .set(meal);
        }

    }

    public void generateMealList() {

        String[] meals = {"Pepper steak with noodles", "Try our pepper steak with noodles for a simple, balanced, midweek meal that's full of flavour. Top with sesame seeds and crushed peppercorns to serve", "Chicken chow mein", "This taiwanese-style chow mein uses a combination of sir frying and steaming so theres less oil involed and pork is used instead of chicken"
                , "Healthy Lasagna", "lighter version of the family classic – lasagne. It has bags of flavour, but comes in at under 500 calories a portion, as well as packing in four of your five-a-day"
                , "One-Pot garlic chicken", "Save on the washing-up with this easy one-pot garlic chicken. Creamy and comforting, you can adjust the amount of garlic to suit your own tastes."
                , "Vegetarian chili", "Rustle up our easy vegetarian chilli. It's a great recipe for batch-cooking – you can easily double it if you have a pan big enough, and freeze the rest"
                , "Oven baked pork chops", "Enjoy oven-baked pork chops cooked in a honey and wholegrain mustard glaze with new potatoes for a deliciously easy dinner, just add your favourite veg"};
        for (int i = 0; i < meals.length - 1; i++) {
            Map<String, Object> meal = new HashMap<>();
            meal.put("Meal Name", meals[0]);
            meal.put("Description", meals[i + 1]);

            db.collection("DefaultMeals").document().collection("mealLists")
                    .add(meal)

                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(MainActivity.this, "Meals added to database", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Failed to add meals to database", Toast.LENGTH_SHORT).show();
                        }
                    });


            // use coding flow android studio
        }

    }


}