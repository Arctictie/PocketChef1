package com.example.pocketchef1;

import static com.google.android.gms.tasks.Tasks.await;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link viewMealFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class viewMealFragment extends Fragment  implements View.OnClickListener ,AdapterView.OnItemClickListener  {

    public static ArrayList<String> mealListNames = new ArrayList<String>();
    public static ArrayList<String> mealListCopy = new ArrayList<String>();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser userAuth;
    //private DocumentReference defaultMealsRef = db.document("defaultMeals/defaultlist/DefaultMeals");

    private DocumentReference userListNamesRef;
    private FirebaseAuth mAuth;
    public static ArrayList<mealItem> mealArrayList = new ArrayList<mealItem>();
    private static final String KEY_MEAL_NAME ="meal_name";
    private static final String KEY_DESCRIPTION ="description";
    private String[] mealTitles;
    private static final String TAG = "MainActivity";
    private String[] mealDescriptions;
    private String itemSelected ="list1";
    private ArrayAdapter<String> adapter = null;
    private static RecyclerView recyclerView;
    public  AutoCompleteTextView textEdit;
    private CollectionReference mealsColRef;
    public String UID;
    populateMealList listCallback;
    public View passedView;
    public static  mealViewAdapter newMealViewAdapter;
    public static Bundle passedSavedInstanceState;

    mealListNames meaListObj = new mealListNames();
    private static Boolean callActivity;

    public viewMealFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Create_Meal_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static viewMealFragment newInstance(String param1, String param2) {
        viewMealFragment fragment = new viewMealFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        userAuth = FirebaseAuth.getInstance().getCurrentUser();
        mealsColRef = db.collection("users/"+userAuth.getUid()+"/root/" +"userLists"+"/"+itemSelected);
        userListNamesRef = db.document("users/"+userAuth.getUid()+"/root/userListNames");


        callActivity = false;

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createMealBtn:
                  callActivity = true;
                  Intent i = new Intent(getActivity(), createMeal.class);
                  startActivity(i);
                break;
            }

           // case R.id.textView_settings:
            //    switchFragment(SettingsFragment.TAG);
            //    break;
        }




    public interface populateMealList {
        void onCallback(ArrayList<mealItem> item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_meal, container, false);

        Button upButton = view.findViewById(R.id.createMealBtn);
        upButton.setOnClickListener(this);
        return view;

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getLists();
        textEdit = requireView().findViewById(R.id.meal_search);
        textEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLists();
            }
        });
        passedSavedInstanceState =savedInstanceState;
        super.onViewCreated(view, savedInstanceState);

        passedView = view;
        if(textEdit != null) {
            textEdit.setAdapter(adapter);
            textEdit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    itemSelected = adapter.getItem(position);

                    System.out.println("item clicked"+itemSelected);
                    initialiseMealData(new populateMealList() {
                        @Override
                        public void onCallback(ArrayList<mealItem> item) {
                            updateRecyclerView();

                        }

                    });
                }
            });


        }
        if(newMealViewAdapter == null) {
            initialiseMealData(new populateMealList() {
                @Override
                public void onCallback(ArrayList<mealItem> item) {
                    updateRecyclerView();
                }
            });
        }

        else
        {

        }



    }


    private void updateRecyclerView()
    {
        try {
            {
                textEdit.setAdapter(adapter);
                recyclerView = passedView.findViewById(R.id.recyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setHasFixedSize(true);


                 if(mealArrayList.size() != 0) {
                     newMealViewAdapter = new mealViewAdapter(requireContext(), mealArrayList);
                     recyclerView.setAdapter(newMealViewAdapter);
                 }
                textEdit.setText(itemSelected, false);
                newMealViewAdapter.notifyDataSetChanged();
                recyclerView.setClickable(true);

            }
        }
        catch (Exception e)
        {
            Log.w("updateRecyclerView", "couldnt update recycler view",e);
        }

    }



    private void getLists()
    {
        mealListNames.clear();
        userListNamesRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                meaListObj = documentSnapshot.toObject(mealListNames.class);
                System.out.println(meaListObj.getListNames());
                mealListNames.addAll(meaListObj.getListNames());


                adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mealListNames);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    textEdit.setForeground(null);
                }

                textEdit.setText(itemSelected, false);
                updateRecyclerView();
                }

        });
    }
    private void  initialiseMealData (final populateMealList listCallback ) {
        System.out.println("Item SELECTED ="+ itemSelected);
        mealsColRef = db.collection("users/"+userAuth.getUid()+"/root/" +"userLists"+"/"+itemSelected);
        mealArrayList.clear();
                mealsColRef.get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    mealItem dfMealItem = documentSnapshot.toObject(mealItem.class);
                                    if(dfMealItem.getIngredients() != null) {
                                        mealArrayList.add(dfMealItem);
                                    }
                                    System.out.println(dfMealItem.getMeal_name());
                                    // Log.d(TAG, "meal item : ",dfMealItem.getMealTitle());


                                }
                                listCallback.onCallback(mealArrayList);
                                System.out.println(mealArrayList.size());


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "could not initialise data ");
                            }
                        });

        listCallback.onCallback(mealArrayList);
    }




      @Override
      public void onItemClick (AdapterView < ? > adapterView, View view,int i, long l)
      {
      System.out.println("Hello click2");
      }
      @Override
        public void onResume()
      {
          super.onResume();
          Log.i("viewMealFragment","Fragment resumed");
          System.out.println(itemSelected);



      }
      @Override
        public void onPause()
      {
          super.onPause();

      }

    @Override
    public void onSaveInstanceState(Bundle outstate)
    {
        if (!callActivity) {
            super.onSaveInstanceState(outstate);
            try {
                outstate.putSerializable("mealItems", (Serializable) newMealViewAdapter);
                outstate.putSerializable("adapter", (Serializable) adapter);
                outstate.putSerializable("textedit", (Serializable) textEdit);
                outstate.putSerializable("itemSelected", (Serializable) itemSelected);
            }catch (ClassCastException e)
            {
                Log.e("viewMealFragment","Couldn't save instance data",e);
            }

        }

    }
  }



