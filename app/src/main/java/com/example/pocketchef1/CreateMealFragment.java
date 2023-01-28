package com.example.pocketchef1;

import static com.google.android.gms.tasks.Tasks.await;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateMealFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateMealFragment extends Fragment implements AdapterView.OnItemClickListener  {

    public static final String[] mealListNames = new String[]{
            "Default 1", "Default 2"
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
    private ArrayAdapter<String> adapter;
    private static RecyclerView recyclerView;
    public  AutoCompleteTextView textEdit;
    populateMealList listCallback;
    public View passedView;
    public static Bundle passedSavedInstanceState;

    public CreateMealFragment() {
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
    public static CreateMealFragment newInstance(String param1, String param2) {
        CreateMealFragment fragment = new CreateMealFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    public interface populateMealList {
        void onCallback(ArrayList<mealItem> item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create__meal_, container, false);

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        itemSelected = "Default 1";
        super.onViewCreated(view, savedInstanceState);
        passedView = view;
        passedSavedInstanceState =savedInstanceState;
        textEdit = requireView().findViewById(R.id.meal_search);
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_activated_1, mealListNames);
        textEdit.setText(adapter.getItem(0), true);
        textEdit.setAdapter(adapter);
        textEdit.setOnItemClickListener((AdapterView.OnItemClickListener) this);

        initialiseMealData(new populateMealList() {
            @Override
            public void onCallback(ArrayList<mealItem> item) {
                updateRecyclerView();
            }
        });




    }
    private void updateRecyclerView()
    {
        try {
            {
                textEdit.setAdapter(adapter);
                recyclerView = passedView.findViewById(R.id.recyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setHasFixedSize(true);
                mealViewAdapter mealViewAdapter = new mealViewAdapter(requireContext(), mealArrayList);
                recyclerView.setAdapter(mealViewAdapter);
                mealViewAdapter.notifyDataSetChanged();
            }
        }
        catch (Exception e)
        {
            Log.w("updateRecyclerView", "couldnt update recycler view",e);
        }

    }
    private void  initialiseMealData (final populateMealList listCallback ) {

        mealArrayList = new ArrayList<>();
        try {
            System.out.println("called");
            System.out.println(itemSelected);
            if (Objects.equals(itemSelected, "Default 1")) {

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
  }

    // On resume could be used!

