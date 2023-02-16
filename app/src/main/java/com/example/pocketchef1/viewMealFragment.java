package com.example.pocketchef1;

import static com.google.android.gms.tasks.Tasks.await;

import android.content.Intent;
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

    public static final String[] mealListNames = new String[]{
            "Default 1", "Default 2", "Default 3", "Default 4"
    };
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser userAuth;
    //private DocumentReference defaultMealsRef = db.document("defaultMeals/defaultlist/DefaultMeals");
    private CollectionReference mealsColRef = db.collection("defaultMeals/defaultlist/DefaultMeals");
    private FirebaseAuth mAuth;
    public static ArrayList<mealItem> mealArrayList;
    private static final String KEY_MEAL_NAME ="meal_name";
    private static final String KEY_DESCRIPTION ="description";
    private String[] mealTitles;
    private static final String TAG = "MainActivity";
    private String[] mealDescriptions;
    private String itemSelected;
    private ArrayAdapter<String> adapter = null;
    private static RecyclerView recyclerView;
    public  AutoCompleteTextView textEdit;
    public String UID;
    populateMealList listCallback;
    public View passedView;
    public static  mealViewAdapter newMealViewAdapter;
    public static Bundle passedSavedInstanceState;
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

        if (savedInstanceState == null)
        {
            itemSelected = "Default 1";
            textEdit = requireView().findViewById(R.id.meal_search);
            adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mealListNames);
            textEdit.setText(adapter.getItem(0), false);
        }
        else
        {
            if (savedInstanceState!= null) {
                newMealViewAdapter = (mealViewAdapter) savedInstanceState.getSerializable("mealItems");
                itemSelected = savedInstanceState.getString("itemSelected");
                System.out.println("savedInstanceState");
                System.out.println(itemSelected);
                adapter = (ArrayAdapter<String>) savedInstanceState.getSerializable(("adapter"));
                textEdit = (AutoCompleteTextView) savedInstanceState.getSerializable("textEdit");

            }
        }

        super.onViewCreated(view, savedInstanceState);
        passedView = view;
        passedSavedInstanceState =savedInstanceState;
        textEdit.setAdapter(adapter);
        textEdit.setOnItemClickListener(this);

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
            updateRecyclerView();
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
                if(newMealViewAdapter == null) {
                        newMealViewAdapter = new mealViewAdapter(requireContext(), mealArrayList);
                }
                recyclerView.setAdapter(newMealViewAdapter);
                textEdit.setText(itemSelected, false);
                newMealViewAdapter.notifyDataSetChanged();
            }
        }
        catch (Exception e)
        {
            Log.w("updateRecyclerView", "couldnt update recycler view",e);
        }

    }
    private void  initialiseMealData (final populateMealList listCallback ) {

        if(mealArrayList  == null) {
            mealArrayList = new ArrayList<>();
        }
        try {
            System.out.println("called");
            System.out.println(itemSelected);
            if (Objects.equals(itemSelected, "Default 2")) {

                mealsColRef.get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    mealItem dfMealItem = documentSnapshot.toObject(mealItem.class);

                                    mealArrayList.add(dfMealItem);

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
                // }
            }
            else
            {
                mealArrayList.clear();
                listCallback.onCallback(mealArrayList);
            }
            textEdit.setText(itemSelected, false);
        }

        catch (Exception e)
        {
            Log.e(TAG, "could not run code in item selected ",e);
        }


    }


      @Override
      public void onItemClick (AdapterView < ? > adapterView, View view,int i, long l){
          itemSelected = adapter.getItem(i);
          System.out.println(itemSelected);


          initialiseMealData(new populateMealList() {
              @Override
              public void onCallback(ArrayList<mealItem> item) {
                  updateRecyclerView();
              }

          });



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
            outstate.putSerializable("mealItems", (Serializable) newMealViewAdapter);
            outstate.putSerializable("adapter", (Serializable) adapter);
            outstate.putSerializable("textedit", (Serializable) textEdit);
            outstate.putSerializable("itemSelected", (Serializable) itemSelected);
        }

    }
  }



