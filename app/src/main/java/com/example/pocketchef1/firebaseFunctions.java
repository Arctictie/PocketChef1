package com.example.pocketchef1;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class firebaseFunctions {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser userAuth;
    FirebaseAuth mAuth;
    private static final String TAG = "firebaseFunctions";
    String UID;
    DocumentReference userRef;

    private static final String KEY_MEAL_NAME ="meal_name";
    private static final String KEY_DESCRIPTION ="description";

    public firebaseFunctions(String uid)
    {
        mAuth = FirebaseAuth.getInstance();
        userAuth = mAuth.getCurrentUser();
    }

    public boolean getUserInfo()
    {
        userRef = db.collection("users").document(userAuth.getUid());
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    Log.d(TAG,"failed to retrieve user info", (Throwable) document.getData());
                }
                else
                {
                    Log.d(TAG,"failed to retrieve user info", task.getException());
                }

            }

        });
        return true;
    }

}
