package com.example.chuapp.Activity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chuapp.Adapter.RestaurantsAdapter;
import com.example.chuapp.Domain.RestaurantsDomain;
import com.example.chuapp.R;
import com.example.chuapp.databinding.ActivityRestaurantsBinding;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

public class RestaurantsActivity extends AppCompatActivity {
    ActivityRestaurantsBinding binding;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRestaurantsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initRecyclerView(binding.restaurantsView1, "第一餐廳");
        initRecyclerView(binding.restaurantsView2, "第二餐廳");
        initRecyclerView(binding.restaurantsView3, "其他");

        Window window = RestaurantsActivity.this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(RestaurantsActivity.this, R.color.white));
    }

    private void initRecyclerView(RecyclerView recyclerView, String collectionName) {
        ArrayList<RestaurantsDomain> itemsArrayList = new ArrayList<>();

        db.collection("restaurant_locations")
                .document(collectionName)
                .collection("restaurants")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int totalItems = task.getResult().size();
                            AtomicInteger completedItems = new AtomicInteger(0);

                            for (DocumentSnapshot document : task.getResult()) {
                                String restaurantName = document.getId();

                                // Firebase Storage 圖像路徑
                                String imagePath = "restaurants/" + restaurantName + ".jpg";

                                // Firebase Storage 圖片的下載 URL
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference storageRef = storage.getReference().child(imagePath);
                                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                // 將下載的 URL 傳給 RestaurantsDomain
                                                itemsArrayList.add(new RestaurantsDomain(restaurantName, uri));

                                                if (completedItems.incrementAndGet() == totalItems) {
                                                    // 在所有圖片載入完成後進行排序與設置 RecyclerView
                                                    Collections.sort(itemsArrayList, new Comparator<RestaurantsDomain>() {
                                                        @Override
                                                        public int compare(RestaurantsDomain rd1, RestaurantsDomain rd2) {
                                                            return rd1.getTitle().compareTo(rd2.getTitle());
                                                        }
                                                    });
                                                    setRecyclerView(recyclerView, itemsArrayList);
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // 找不到圖片，使用預設圖片
                                                Uri defaultImageUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.default_image);
                                                itemsArrayList.add(new RestaurantsDomain(restaurantName, defaultImageUri));

                                                if (completedItems.incrementAndGet() == totalItems) {
                                                    // 在所有圖片載入完成後進行排序與設置 RecyclerView
                                                    Collections.sort(itemsArrayList, new Comparator<RestaurantsDomain>() {
                                                        @Override
                                                        public int compare(RestaurantsDomain rd1, RestaurantsDomain rd2) {
                                                            return rd1.getTitle().compareTo(rd2.getTitle());
                                                        }
                                                    });
                                                    setRecyclerView(recyclerView, itemsArrayList);
                                                }
                                            }
                                        });
                            }
                        } else {
                            // 處理失敗情況
                        }
                    }
                });
    }

    private void setRecyclerView(RecyclerView recyclerView, ArrayList<RestaurantsDomain> itemsArrayList) {
        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(RestaurantsActivity.this, numberOfColumns));
        RestaurantsAdapter adapter = new RestaurantsAdapter(itemsArrayList);
        recyclerView.setAdapter(adapter);
    }
}

