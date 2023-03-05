package com.example.pocketchef1;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class firebaseFunctions {
    private String mealListName;
    private String mealName;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser userAuth;
    FirebaseAuth mAuth;
    private static final String TAG = "firebaseFunctions";
    public boolean addMealReturnState = false;
    String UID;
    public String tempMealItem;
    DocumentReference userListNamesRef;
    CollectionReference userList;
    private StorageReference storageref;
    private DatabaseReference imageDatabaseRef;
    public static ArrayList<mealItem> mealArrayList;
    private DocumentReference userMealListRef;
    Map<String, Object> meal = new HashMap<>();
    private static final String KEY_MEAL_NAME = "meal_name";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_INSTRUCTIONS = "instructions";
    private static final String KEY_INGREDIENTS = "ingredients";
    public ArrayList<String> listNamesArr;
    public ArrayList<String> tempMeal;
    mealListNames listNamesObj = new mealListNames();
    mealListNames listNamesObjRet = new mealListNames();
    getMealListNamesCallback getListNamesCallback = new getMealListNamesCallback() {
        @Override
        public void onCallback(mealListNames mealListObj) {

        }
    };


    public firebaseFunctions() {

        mAuth = FirebaseAuth.getInstance();
        userAuth = mAuth.getCurrentUser();
        System.out.println("UID in firebaseFunctions=" + userAuth.getUid());

        userListNamesRef = db.document("users/" + userAuth.getUid() + "/root/userListNames");
        //fbAddMealList("list2");


    }

    public interface getMealListNamesCallback {
        void onCallback(mealListNames mealListObj);
    }

    // public ArrayList fbGetMealsFromList(String mealListSelected)
    //{
//
    // }

    public boolean fbAddMealList(String listName) {
        String tempString = "temp";
        Map<String, Object> temp = new HashMap<>();
        temp.put("temp", tempString);
        db.collection("users/" + userAuth.getUid() + "/root/userLists/" + listName).document("temp")
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


    public mealListNames fbGetMealListNames() {

        userListNamesRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                listNamesObj = documentSnapshot.toObject(mealListNames.class);

                //
                //
                System.out.println("listnames" + documentSnapshot.get("listNames"));
                //System.out.println(listNamesObj.getListNames().get(0));
                //System.out.println(listNamesObj.getListNames().size());


                //   listNamesArr.addAll(listNamesObj.getListNames());

                System.out.println("listNames ARR" + listNamesArr);
                getListNamesCallback.onCallback(listNamesObj);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("firebaseFunctions", "Couldn't get meal list names");
            }
        }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

            }

        });
        return listNamesObj;


    }

    public void addMeal(String listName, mealItem mealItem, Uri mealImage) {

        Map<String, Object> meal = new HashMap<>();
        mealListName = listName;
        mealName = mealItem.getMeal_name();
        userMealListRef = db.document("users/" + userAuth.getUid() + "/root/userLists/" + mealListName + "/" + mealName);
        storageref = FirebaseStorage.getInstance().getReference("images/" + userAuth.getUid() + "/" + mealListName + "/" + mealName);
        imageDatabaseRef = FirebaseDatabase.getInstance("https://pocketchef-b2697-default-rtdb.europe-west1.firebasedatabase.app").getReference("images/" + userAuth.getUid() + "/" + mealListName + "/" + mealName);

        //meal.put(mealName);
        userMealListRef.set(mealItem);

        StorageReference imageStorageRef = storageref.child(mealName + ".jpg");
        imageStorageRef.putFile(mealImage)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.i("firebaseFunctions", "DownloadUri successfull.!");
                                String sUrl = uri.toString();
                                mealImage mealImage = new mealImage(sUrl, mealName);
                                String uploadID = imageDatabaseRef.push().getKey();
                                imageDatabaseRef.child(uploadID).setValue(mealImage);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("createMeal", "Writing Image to database failed", e);
                    }
                });

    }


    public mealListNames getMealListNames() {
        //  fbGetMealListNames (new getMealListNamesCallback() {
        //      @Override
        //      public void onCallback(mealListNames mealListObj) {

        //          System.out.println("callback response" +mealListObj.getListNames());
        //           listNamesObjRet = mealListObj;
        //        }
        //   });

        return listNamesObjRet;
    }
    public void saveCalendarItem(calendarItem calendarItem)
    {

    }
    public List getMealNames(String mealListName)
    {
        List<String> mealNames = new ArrayList<>();
        this.mealListName = mealListName;
        userList = db.collection("users/" + userAuth.getUid() + "/root/userLists/" + mealListName);
        userList.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                        {
                            mealNames.add( documentSnapshot.getId());
                        }
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
        return mealNames;

    }

    public void generateBaseMeals() {
        userListNamesRef.update("listNames", FieldValue.arrayUnion("BBC Meals"));

        String[] mealNames = {"Pepper steak with noodles"
                , "Chicken chow mein"
                , "Healthy Lasagna"
                , "One-Pot garlic chicken"
                , "Vegetarian chili"
                , "Oven baked pork chops"};

        String[] mealDescriptions = {"Try our pepper steak with noodles for a simple, balanced, midweek meal that's full of flavour. Top with sesame seeds and crushed peppercorns to serve"
                , "This taiwanese-style chow mein uses a combination of sir frying and steaming so theres less oil involed and pork is used instead of chicken"
                , "lighter version of the family classic – lasagne. It has bags of flavour, but comes in at under 500 calories a portion, as well as packing in four of your five-a-day"
                , "Save on the washing-up with this easy one-pot garlic chicken. Creamy and comforting, you can adjust the amount of garlic to suit your own tastes."
                , "Rustle up our easy vegetarian chilli. It's a great recipe for batch-cooking – you can easily double it if you have a pan big enough, and freeze the rest"
                , "Enjoy oven-baked pork chops cooked in a honey and wholegrain mustard glaze with new potatoes for a deliciously easy dinner, just add your favourite veg"};

        String[] mealInstructions = {" Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type"
                , " Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type"
                , " Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type"
                , " Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type"
                , " Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type"
                , " Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type"};

        String[] mealIngredients = {"Sage " +
                "Potatoes" +
                "Mince" +
                "Onion" +
                "Garlic" +
                "cheddar" +
                "Green Beans",
                "Sage " +
                        "Potatoes" +
                        "Mince" +
                        "Onion" +
                        "Garlic" +
                        "cheddar" +
                        "Green Beans",
                "Sage " +
                        "Potatoes" +
                        "Mince" +
                        "Onion" +
                        "Garlic" +
                        "cheddar" +
                        "Green Beans",
                "Sage " +
                        "Potatoes" +
                        "Mince" +
                        "Onion" +
                        "Garlic" +
                        "cheddar" +
                        "Green Beans",
                "Sage " +
                        "Potatoes" +
                        "Mince" +
                        "Onion" +
                        "Garlic" +
                        "cheddar" +
                        "Green Beans",
                "Sage " +
                        "Potatoes" +
                        "Mince" +
                        "Onion" +
                        "Garlic" +
                        "cheddar" +
                        "Green Beans"};
        for (int i = 0; i < mealNames.length; i++) {

            Map<String, Object> meal = new HashMap<>();
            meal.put(KEY_MEAL_NAME, mealNames[i]);
            meal.put(KEY_DESCRIPTION, mealDescriptions[i]);
            meal.put(KEY_INSTRUCTIONS, mealInstructions[i]);
            meal.put(KEY_INGREDIENTS, mealInstructions[i]);
            userAuth = FirebaseAuth.getInstance().getCurrentUser();
            db.collection("users/" + userAuth.getUid() + "/root/" + "userLists" + "/" + "BBC Meals").document(mealNames[i])
                    .set(meal);
        }
    }
}

