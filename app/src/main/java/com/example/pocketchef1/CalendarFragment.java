package com.example.pocketchef1;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import org.checkerframework.checker.units.qual.A;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class CalendarFragment extends Fragment  {
    static final int maxCalendarDays = 42;
    LayoutInflater inflater;
    ViewGroup container;
    String mealToAdd;
    ImageButton calBack,calForward,calAddMeal;
    calendarGridAdapter calGridAdapter;
    mealListNames meaListObj;
    String itemSelected;
    ListView calListView;
    public static ArrayList<String> mealListNames = new ArrayList<String>();
    TextView CurrentDate;
    public AutoCompleteTextView calendarAutoTextEdit;
    GridView gridView;
    FirebaseUser userAuth;
    DocumentReference userListNamesRef;
    FirebaseAuth mAuth;
    firebaseFunctions fbFunctions= new firebaseFunctions();
    private ArrayAdapter<String> adapter = null;
    AlertDialog alertDialog;
    Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
    Context context;
    CollectionReference userList;
    FirebaseFirestore db;
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy",Locale.ENGLISH);
    SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM",Locale.ENGLISH);
    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy",Locale.ENGLISH);
    private ArrayAdapter<String> listAdapter;
    List<Date> dates = new ArrayList<>();
    List<String> meals = new ArrayList<>();
    List<calendarItem> calendarItemList = new ArrayList<>();
    public CalendarFragment() {
        this.context = getContext();
    }

    public static CalendarFragment newInstance(String param1, String param2) {

        CalendarFragment fragment = new CalendarFragment();

        Bundle args = new Bundle();
               fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userAuth = mAuth.getCurrentUser();
        userListNamesRef = db.document("users/"+userAuth.getUid()+"/root/userListNames");
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        calBack = view.findViewById(R.id.calenderBack);
        calForward = view.findViewById(R.id.calenderForward);
        CurrentDate = view.findViewById(R.id.currentDate);
        gridView = view.findViewById(R.id.gridView);
        initialiseCalendar();

        calBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH,-1);
                initialiseCalendar();
            }
        });
        calForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH,1);
                initialiseCalendar();
            }
        });
         gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.w("CalendarFragment","pressed GridItem");
                AlertDialog.Builder adBuilder = new AlertDialog.Builder(getContext());
                adBuilder.setCancelable(true);

                    final View addItemPopup = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_calendar_item, null);

                    calendarAutoTextEdit = addItemPopup.findViewById(R.id.calTextInput);
                    calListView = addItemPopup.findViewById(R.id.calenderMealList);
                    calAddMeal = addItemPopup.findViewById(R.id.calAddMeal);
                    adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, mealListNames);
                    itemSelected = adapter.getItem(0);
                    calendarAutoTextEdit.setAdapter(adapter);
                    calendarAutoTextEdit.setText(itemSelected, false);

                    calendarAutoTextEdit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            itemSelected = adapter.getItem(position);
                            calendarAutoTextEdit.setText(itemSelected, false);
                            meals = fbFunctions.getMealNames(itemSelected);
                            getMealNames(itemSelected);
                            Log.d("CalendarFragment", "autocomplete Pressed");
                        }
                    });



                    calListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            mealToAdd = calListView.getAdapter().getItem(position).toString();
                            System.out.println("meal to Add" + mealToAdd);
                        }
                    });

                        String date = dateFormat.format(dates.get(position));
                        String month = dateFormat.format(dates.get(position));
                        String year = dateFormat.format(dates.get(position));
                        calAddMeal.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (itemSelected != null && mealToAdd != null) {
                                    calendarItem calItem = new calendarItem(mealToAdd, date, month, year);
                                    calendarItemList.add(calItem);
                                    fbFunctions.saveCalendarItem(calItem);
                                    initialiseCalendar();
                                    alertDialog.dismiss();
                                }


                            }
                        });
                        adBuilder.setView(addItemPopup);
                        alertDialog = adBuilder.create();
                        alertDialog.show();
                    }
                });
    }


    public void getLists()
    {
        mealListNames.clear();
        userListNamesRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                meaListObj = documentSnapshot.toObject(mealListNames.class);
                System.out.println(meaListObj.getListNames());
                mealListNames.addAll(meaListObj.getListNames());
            }
        });
    }
    public void getMealNames(String mealListName)
    {

        userList = db.collection("users/" + userAuth.getUid() + "/root/userLists/" + mealListName);
        userList.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                        {
                            meals.add( documentSnapshot.getId());
                        }
                        listAdapter = new ArrayAdapter<String> (getContext(),android.R.layout.simple_list_item_single_choice,meals);
                        calListView.setAdapter(listAdapter);
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getLists();
        this.inflater = inflater;
        this.container = container;
        // Inflate the layout for this fragment
        View calenderView = inflater.inflate(R.layout.calendar, container, false);
        return calenderView;

    }


    private void initialiseCalendar()
    {
        this.context = getActivity().getBaseContext();
        String currentDate = dateFormat.format(calendar.getTime());
        CurrentDate.setText(currentDate);
        dates.clear();
        Calendar monthCalendar = (Calendar)calendar.clone();
        monthCalendar.set(Calendar.DAY_OF_MONTH,1);
        int firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK)-2 ;
        monthCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth);
        while(dates.size() < maxCalendarDays)
        {
            dates.add(monthCalendar.getTime());
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        calGridAdapter = new calendarGridAdapter(context,dates,calendar,calendarItemList);
        gridView.setAdapter(calGridAdapter);
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }
    @Override
    public void onPause()
    {
        super.onPause();

    }


}