package com.example.chuapp.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.chuapp.Adapter.BuildingsAdapter;
import com.example.chuapp.Domain.BuildingsDomain;
import com.example.chuapp.R;
import com.example.chuapp.databinding.ActivityBuildingsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class BuildingsActivity extends AppCompatActivity {
    ActivityBuildingsBinding binding;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBuildingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BuildingsActivity.this, MainActivity.class));
            }
        });

        initRecyclerView();

        Window window = BuildingsActivity.this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(BuildingsActivity.this, R.color.white));
    }


    private void initRecyclerView() {
        ArrayList<BuildingsDomain> itemsArraylist = new ArrayList<>();

        db.collection("buildings")
                .get()
                .addOnCompleteListener(new OnCompleteListener<com.google.firebase.firestore.QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<com.google.firebase.firestore.QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            StringBuilder result = new StringBuilder();
                            for (DocumentSnapshot document : task.getResult()) {
                                String buildingName = document.getString("building_name");
                                String buildingAbbreviation = document.getString("building_abbreviation");

                                // Firebase Storage 圖像路徑
                                String imagePath = "buildings/" + buildingAbbreviation + ".jpg";

                                // Firebase Storage 圖片的下載 URL
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference storageRef = storage.getReference().child(imagePath);
                                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        // 將下載的 URL 傳給 BuildingsDomain
                                        itemsArraylist.add(new BuildingsDomain(buildingName, uri, buildingAbbreviation));

                                        // 在完成所有圖片的加載後設置 RecyclerView
                                        if (itemsArraylist.size() == task.getResult().size()) {
                                            int numberOfColumns = 2;
                                            binding.buildingsView.setLayoutManager(new GridLayoutManager(BuildingsActivity.this, numberOfColumns));

                                            BuildingsAdapter adapter = new BuildingsAdapter(itemsArraylist);

                                            binding.buildingsView.setAdapter(adapter);
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // 處理獲取圖片 URL 失敗的情況
                                    }
                                });
                            }
                        } else {
                            // 處理失敗情況
                        }
                    }
                });
    }




}
