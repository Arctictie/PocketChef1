package com.example.pocketchef1;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class firebaseFunctions {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser userAuth;
    FirebaseAuth mAuth;
    private static final String TAG = "firebaseFunctions";
    public boolean addMealReturnState = false;
    String UID;
    public String tempMealItem;
    DocumentReference userListNamesRef;
    public static ArrayList<mealItem> mealArrayList;
    private CollectionReference  userMealListRef;
    private static final String KEY_MEAL_NAME ="meal_name";
    Map<String, Object> meal = new HashMap<>();
    private static final String KEY_DESCRIPTION ="description";
    public  ArrayList<String> listNamesArr;
    public  ArrayList<String> tempMeal;
    mealListNames listNamesObj = new mealListNames();
    mealListNames listNamesObjRet = new mealListNames();
    getMealListNamesCallback getListNamesCallback =new getMealListNamesCallback() {
        @Override
        public void onCallback(mealListNames mealListObj) {

        }
    };


    public firebaseFunctions()
    {
        mAuth = FirebaseAuth.getInstance();
        userAuth = mAuth.getCurrentUser();

        System.out.println("UID in firebaseFunctions=" +userAuth.getUid());

        userMealListRef = db.collection("users/"+userAuth.getUid()+"/root/userLists/list1");
        userListNamesRef = db.document("users/"+userAuth.getUid()+"/root/userListNames");

        fbAddMealList("list2");


    }

    public interface getMealListNamesCallback {
        void onCallback(mealListNames mealListObj);
    }

    public ArrayList fbGetMealsFromList(String mealListSelected)
    {

// need to have nested function to go into the userLists DOCUMENT and get each and then for each do step bellow
        userMealListRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        DocumentSnapshot scCopy = documentSnapshot;
                        mealItem dfMealItem = documentSnapshot.toObject(mealItem.class);

                      //   dfMealItem = new mealItem((String) documentSnapshot.get("Meal Title"), (String) documentSnapshot.get("Meal Description"), (String) documentSnapshot.get("Meal Instructions"), (String) documentSnapshot.get("Meal Ingredients"));
                       // mealArrayList.add(dfMealItem);
                      //  mealListNames.add(documentSnapshot.getId());
                        System.out.println("firebase Functions "+listNamesArr);
                        System.out.println(documentSnapshot.getId());
                        // Log.d(TAG, "meal item : ",dfMealItem.getMealTitle());


                    }


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "could not initialise data ");
                }
            }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

            }
        });

        return mealArrayList;
    }

    public boolean fbAddMealList(String listName)
    {
        String tempString = "temp";
        Map<String, Object> temp = new HashMap<>();
        temp.put("temp",tempString);
        db.collection("users/"+userAuth.getUid()+"/root/userLists/"+listName).document("temp")
                        .set(temp);


        userListNamesRef.update("listNames", FieldValue.arrayUnion(listName)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                addMealReturnState = true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                addMealReturnState = false;
            }
        });

        return addMealReturnState;
    }
    public void fbRemoveMealList(String listName) {
        if (!Objects.equals(listName, "list1")) {
            userListNamesRef.update("listNames", FieldValue.arrayRemove(listName)).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    addMealReturnState = true;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    addMealReturnState = false;
                }
            });

            db.collection("users/" + userAuth.getUid() + "/root/userLists/" + listName).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        db.collection("users/" + userAuth.getUid() + "/root/userLists/" + listName).document(documentSnapshot.getId()).delete();
                    }
                }
            });

        }
    }


    public mealListNames fbGetMealListNames()
    {

        userListNamesRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                 listNamesObj = documentSnapshot.toObject(mealListNames.class);

                //
                //
                System.out.println("listnames"+documentSnapshot.get("listNames"));
                //System.out.println(listNamesObj.getListNames().get(0));
                //System.out.println(listNamesObj.getListNames().size());



             //   listNamesArr.addAll(listNamesObj.getListNames());

                System.out.println("listNames ARR"+listNamesArr);
                getListNamesCallback.onCallback(listNamesObj);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("firebaseFunctions","Couldn't get meal list names");
            }
        }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

            }

        });
        return listNamesObj;




    }


    public mealListNames getMealListNames()
    {
      //  fbGetMealListNames (new getMealListNamesCallback() {
      //      @Override
      //      public void onCallback(mealListNames mealListObj) {

     //          System.out.println("callback response" +mealListObj.getListNames());
     //           listNamesObjRet = mealListObj;
    //        }
     //   });

        return listNamesObjRet;
    }
}

