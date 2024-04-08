package com.example.chuapp.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.chuapp.Adapter.BuildingsAdapter;
import com.example.chuapp.Domain.BuildingsDomain;
import com.example.chuapp.R;
import com.example.chuapp.databinding.ActivityBuildingsBinding;
import com.example.chuapp.databinding.ActivityBuildingsImformationBinding;
import com.example.chuapp.databinding.ActivityFavoritesBinding;
import com.example.chuapp.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class BuildingInformationActivity extends AppCompatActivity {
    ActivityBuildingsImformationBinding binding;
    private FirebaseFirestore db;
    String IntentBuildingAbbreviation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBuildingsImformationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        IntentBuildingAbbreviation = getIntent().getStringExtra("buildingAbbreviation");

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BuildingInformationActivity.this, MainActivity.class));
            }
        });

        initRecyclerView();

        Window window = BuildingInformationActivity.this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(BuildingInformationActivity.this, R.color.black));

    }

    private void initRecyclerView() {
        ArrayList<BuildingsDomain> itemsArraylist = new ArrayList<>();

        db.collection("buildings")
                .whereEqualTo("building_abbreviation", IntentBuildingAbbreviation)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            StringBuilder result = new StringBuilder();
                            for (DocumentSnapshot document : task.getResult()) {
                                String buildingName = document.getString("building_name");
                                String buildingAbbreviation = document.getString("building_abbreviation");

                                binding.buildingName.setText(buildingName);
                                binding.test.setText(buildingAbbreviation);
                                };
                        } else {
                            // 处理失败情况
                        }
                    }
                });
    }
}
