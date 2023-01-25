package com.example.pocketchef1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateMealFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateMealFragment extends Fragment implements AdapterView.OnItemClickListener {

    public static final String[] mealListNames = new String[]{
            "Default"
    };
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser userAuth;
    private FirebaseAuth mAuth;
    private ArrayList<mealItem> mealArrayList;
    private String[] mealTitles;
    private String[] mealDescriptions;
    private String itemSelected;
    private ArrayAdapter<String> adapter;
    private RecyclerView recyclerView;

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create__meal_, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AutoCompleteTextView textEdit = requireView().findViewById(R.id.meal_search);

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_activated_1, mealListNames);
        textEdit.setText(adapter.getItem(0), false);
        textEdit.setAdapter(adapter);
        textEdit.setOnItemClickListener((AdapterView.OnItemClickListener) this);
        initialiseMealData();

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        mealViewAdapter mealViewAdapter = new mealViewAdapter(getContext(), mealArrayList);
        recyclerView.setAdapter(mealViewAdapter);
        mealViewAdapter.notifyDataSetChanged();


    }


    private void initialiseMealData() {
        mealArrayList = new ArrayList<>();

        if (itemSelected == "Default") {

        }
        mealTitles = new String[]{
                "title1",
                "title2",
                "title3",
                "title4",
                "title5",
                "title6",
                "title7"

        };
        mealDescriptions = new String[]{
                "meal 1 description here",
                "meal 2 description here",
                "meal 3 description here",
                "meal 4 description here",
                "meal 5 description here",
                "meal 6 description here",
                "meal 7 description here",

        };
        for (int i = 0; i < mealTitles.length; i++) {
            mealItem mealItem = new mealItem(mealTitles[i], mealDescriptions[i]);
            mealArrayList.add(mealItem);

        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        itemSelected = adapter.getItem(i);
    }

    // On resume could be used!
}