package com.example.pocketchef1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class signIn extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

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


    public void signIn(String email, String password) {
        try {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(signIn.this, "Authentication success. Use an intent to move to a new activity",
                                        Toast.LENGTH_SHORT).show();
                                try {
                                    Intent i = new Intent(getApplicationContext(), homePage.class);
                                    startActivity(i);
                                } catch (Exception e) {
                                    Log.w("Signin", "log in intent move failure", e);
                                    Toast.makeText(signIn.this, "intent failed.",
                                            Toast.LENGTH_SHORT).show();
                                }


                            } else {
// If sign in fails, display a message to the user.
                                Log.w("signIn", "signInWithEmail:failure",
                                        task.getException());
                                Toast.makeText(signIn.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }catch (Exception e )
        {

        }



    }

    public void signinButtonClicked(View view) {
        try {
            EditText email = findViewById(R.id.editTextTextEmailAddress);
            EditText password = findViewById(R.id.editTextTextPassword);
            String sEmail = email.getText().toString();
            String sPassword = password.getText().toString();
            signIn(sEmail, sPassword);
        } catch (IllegalArgumentException e) {
            Toast.makeText(signIn.this,
                    "Enter username and password",
                    Toast.LENGTH_SHORT).show();
            Log.w("MainActivity",
                    "signupClicked:failure", e);
        }
    }
}
