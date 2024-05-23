package com.example.chuapp.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.chuapp.R;
import com.example.chuapp.databinding.ActivityRestaurantsInformationBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class RestaurantInformationActivity extends AppCompatActivity {
    ActivityRestaurantsInformationBinding binding;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRestaurantsInformationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        String IntentRestaurantAbbreviation = getIntent().getStringExtra("title");

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareRestaurantInformation(IntentRestaurantAbbreviation.toUpperCase());
            }
        });

        getRestaurantInformation(IntentRestaurantAbbreviation);

        Window window = RestaurantInformationActivity.this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(RestaurantInformationActivity.this, R.color.black));
    }

    private void shareRestaurantInformation(String restaurantAbbreviation) {
        // 創建要分享的文本內容
        String shareText = "這是中華大學 " + restaurantAbbreviation + " 的信息！";

        // 創建 Intent 來執行分享操作
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        // 啟動分享操作
        startActivity(Intent.createChooser(shareIntent, "分享餐廳信息"));
    }

    private void getRestaurantInformation(String restaurantAbbreviation) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        ArrayList<String> restaurantCollections = new ArrayList<>(Arrays.asList("第一餐廳", "第二餐廳", "其他"));

        for(String collection : restaurantCollections){
            DocumentReference docRef = db.collection("restaurant_locations").document(collection).collection("restaurants").document(restaurantAbbreviation);

            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        String restaurantName = restaurantAbbreviation;
                        String opening_hours = documentSnapshot.getString("opening_hours");
                        String items = documentSnapshot.getString("items");
                        String restaurantDescription = documentSnapshot.getString("description");
                        String imagePath = "restaurants/" + restaurantAbbreviation + ".jpg";

                        updateUIWithRestaurantInformation(restaurantName, opening_hours, items, restaurantDescription, imagePath);
                    } else {
                        // handle case where the document does not exist
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // handle case where the document reading fails
                }
            });
        }
    }

    private void updateUIWithRestaurantInformation(String restaurantName, String opening_hours, String items, String restaurantDescription, String imagePath) {
        // 找到餐廳名稱和建築物資訊的 TextView
        TextView restaurantNameTextView = findViewById(R.id.restaurantNameTxt);
        TextView restaurantBusinessHoursTextView = findViewById(R.id.businessHoursTxt);
        TextView restaurantItemsTextView = findViewById(R.id.itemsTxt);
        TextView restaurantInfoTextView = findViewById(R.id.DescriptionTxt);

        // 將餐廳名稱和餐廳資訊設置到 TextView 上
        restaurantNameTextView.setText(restaurantName);
        restaurantBusinessHoursTextView.setText(opening_hours);
        restaurantItemsTextView.setText(items);


        restaurantDescription = restaurantDescription.replace("\\n", "\n\n");
        restaurantInfoTextView.setText(restaurantDescription);

        ImageView restaurantImageView = findViewById(R.id.restaurantImageView);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child(imagePath);
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // 下載成功，使用圖片載入庫（例如 Glide、Picasso 等）將圖片設置到 ImageView 上
                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(restaurantImageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // 下載失敗，可能圖片不存在或其他錯誤，你可以在這裡處理失敗的情況
                restaurantImageView.setImageResource(R.drawable.default_image);
            }
        });

    }

    private String formatPhoneNumber(String phoneNumber) {
        String formattedPhoneNumber = phoneNumber.substring(0, 2) + " " +
                phoneNumber.substring(2, 5) + " " +
                phoneNumber.substring(5);

        return formattedPhoneNumber;
    }


}
