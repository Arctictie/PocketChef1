package com.example.pocketchef1;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class createMeal extends AppCompatActivity  {
    private static final int pic_id = 101;
    private static final int permissionRequest = 100;
    Button  addImageBtnID;
    Button  createList;
    Button  removeList;
    Button  createMealBtn;
    ImageView  mealImageID;
    TextView title;
    TextView description;
    TextView instructions;
    ImageButton back;
    TextView ingredients;
    FirebaseUser userAuth;
    FirebaseAuth mAuth;
    private Uri imageUri;
    private DocumentReference userListNamesRef;
    String listText;
    Matrix matrix = new Matrix();
    public String selectedList;
    mealItem  cMealItem = new mealItem();
    public AutoCompleteTextView cmTextEdit;
    public static ArrayList<String> mealListNames = new ArrayList<String>();
    mealListNames meaListObj;
    ActivityResultLauncher<Intent> activityResultLauncher;




    private ArrayAdapter<String> adapter = null;


    public static  firebaseFunctions firebaseFunctions;

    public interface getMealData {
        void onCallback(mealListNames mealLists);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userAuth = mAuth.getCurrentUser();

        userListNamesRef = db.document("users/"+userAuth.getUid()+"/root/userListNames");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meal);

        cmTextEdit = findViewById(R.id.cm_meal_search);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cmTextEdit.setForeground(null);
        }

        firebaseFunctions = new firebaseFunctions();
        getLists();


       // populateList();

        addImageBtnID = findViewById(R.id.addMealImage);
        mealImageID = findViewById(R.id.mealImage);
        back = findViewById(R.id.cmBack);
        createMealBtn = findViewById(R.id.createCompletedMealBtn);
        createList = findViewById(R.id.createList);
        removeList = findViewById(R.id.removeList);
        title = findViewById(R.id.createMealTitle);
        description = findViewById(R.id.createMealDescription);
        instructions = findViewById(R.id.createMealInstructions);
        ingredients = findViewById((R.id.createMealIngredients));


        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == Activity.RESULT_OK) {
                    matrix.postRotate(90);

                    Bundle extras = result.getData().getExtras();
                    Bitmap input = (Bitmap) extras.get("data");
                    Bitmap scale = Bitmap.createScaledBitmap(input,input.getWidth(),input.getHeight(),true);
                    Bitmap photo = Bitmap.createBitmap(scale,0,0,scale.getWidth(),scale.getHeight(),matrix,true);
                    WeakReference<Bitmap> res = new WeakReference<>(Bitmap.createScaledBitmap(photo,
                            photo.getHeight(), photo.getWidth(), false).copy(
                            Bitmap.Config.RGB_565, true));
                    Bitmap photoClear = res.get();

                    imageUri = bitmapToUri(photo, createMeal.this);
                    loadImage();

                }
                else
                {
                    Log.d("createMeal" , "result from taking picture not valid.");
                }
            }

        });
        createMealBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sTitle = title.getText().toString();
                String sDescription = description.getText().toString();
                String sIngredients = ingredients.getText().toString();
                String sInstructions = instructions.getText().toString();

                if (imageUri != null && sTitle.length() > 3 && sDescription.length() > 10 && sInstructions.length() > 10 && sIngredients.length() > 10) {
                    cMealItem = new mealItem(sTitle, sDescription, sIngredients, sInstructions);

                    if (selectedList == null) {selectedList = adapter.getItem(0);
                    }
                    firebaseFunctions.addMeal(selectedList, cMealItem, imageUri);
                    onBackPressed();
                }
                else{
                    Toast.makeText(createMeal.this, "Add all details before creating meal", Toast.LENGTH_SHORT).show();
                }

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Camera_open button is for open the camera and add the setOnClickListener in this button
        createList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String listText = cmTextEdit.getText().toString();

                firebaseFunctions.fbAddMealList(listText);
                getLists();
            }
        });
        removeList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listText = cmTextEdit.getText().toString();
                firebaseFunctions.fbRemoveMealList(listText);
                getLists();
            }
        });

        addImageBtnID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listText = cmTextEdit.getText().toString();
                System.out.println("hello");

                checkPermissions();

            }
        });
        this.cmTextEdit.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("CreateMeal","on item click textEdit (meals)");
                selectedList = adapter.getItem(position);

            }
        });


    }
    private void loadImage()
    {
       // mealImageID.setRotation(90);
        Picasso.with(this).load(imageUri).into(mealImageID);
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
                 populateList();

            }

        });
    }
    private void populateList()
    {
       System.out.println("meal list names !"+meaListObj.getListNames());
        adapter =new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mealListNames);
        cmTextEdit.setAdapter(adapter);

        cmTextEdit.setText(adapter.getItem(0), false);
    }

    private void checkPermissions()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, permissionRequest);
                System.out.println("hello2");
            }
            else
            {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
               openCamera(cameraIntent);
            }
        }
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == permissionRequest)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();

                //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                openCamera(cameraIntent);
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void openCamera(Intent cameraIntent)
    {
        activityResultLauncher.launch(cameraIntent);

    }


   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == pic_id && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            //imageUri = data.getData();
            //imageUri = Utils.getUri(this, photo);

            //mealImageID.setImageBitmap(photo);
           // mealImageID.setRotation(90);
        }
    }*/

    private Uri bitmapToUri(Bitmap mealImage, createMeal context) {
        File tempImage = new File(context.getCacheDir(),"images");
        Uri tempUri = null;

        try{
            tempImage.mkdir();
            File file = new File(tempImage,"temp_image.jpg");
            FileOutputStream  stream = new FileOutputStream(file);
            mealImage.compress(Bitmap.CompressFormat.JPEG,75,stream);
            stream.flush();
            stream.close();
            tempUri = FileProvider.getUriForFile(context.getApplicationContext(),"com.example.pocketchef1"+".provider",file);
        }
        catch (FileNotFoundException e)
        {
            Log.e("createMeal","Could not convert bitmap to uri",e);
        }
        catch (IOException e)
        {
            Log.e("createMeal","TempFile save issue",e);
        }
        return tempUri;
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }


}
