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
import com.example.chuapp.databinding.ActivityBuildingsInformationBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;

public class BuildingInformationActivity extends AppCompatActivity {
    ActivityBuildingsInformationBinding binding;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBuildingsInformationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        String IntentBuildingAbbreviation = getIntent().getStringExtra("buildingAbbreviation");

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareBuildingInformation(IntentBuildingAbbreviation.toUpperCase());
            }
        });


        getBuildingInformation(IntentBuildingAbbreviation);

        Window window = BuildingInformationActivity.this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(BuildingInformationActivity.this, R.color.black));

    }

    private void shareBuildingInformation(String buildingAbbreviation) {
        // 創建要分享的文本內容
        String shareText = "這是中華大學 " + buildingAbbreviation + "棟 的信息！";

        // 創建 Intent 來執行分享操作
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        // 啟動分享操作
        startActivity(Intent.createChooser(shareIntent, "分享建築物信息"));
    }

    private void getBuildingInformation(String buildingAbbreviation) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("buildings").document(buildingAbbreviation);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String buildingName = documentSnapshot.getString("building_name");
                    Map<String, String> businessHours = (Map<String, String>) documentSnapshot.get("business_hours");
                    String startTime = businessHours.get("start_time");
                    String endTime = businessHours.get("end_time");
                    String buildingPhoneNumber = documentSnapshot.getString("phone_number");
                    String buildingDescription = documentSnapshot.getString("description");

                    String imagePath = "buildings/" + buildingAbbreviation + ".jpg";

                    updateUIWithBuildingInformation(buildingName, startTime, endTime, buildingPhoneNumber, buildingDescription, imagePath);
                } else {
                    // 處理文檔不存在的情況
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // 處理讀取資料失敗的情況
            }
        });
    }


    private void updateUIWithBuildingInformation(String buildingName, String startTime, String endTime, String buildingPhoneNumber, String buildingDescription, String imagePath) {
        // 找到建築物名稱和建築物資訊的 TextView
        TextView buildingNameTextView = findViewById(R.id.buildingNameTxt);
        TextView buildingBusinessHoursTextView = findViewById(R.id.businessHoursTxt);
        TextView buildingPhoneNumberTextView = findViewById(R.id.phoneNumberTxt);
        TextView buildingInfoTextView = findViewById(R.id.DescriptionTxt);

        // 將建築物名稱和建築物資訊設置到 TextView 上
        buildingNameTextView.setText(buildingName);
        buildingBusinessHoursTextView.setText(startTime + " ~ " + endTime);

        // 檢查 buildingPhoneNumber 是否為空
        if (buildingPhoneNumber.isEmpty()) {
            // 如果 buildingPhoneNumber 為空，則將相應的 ConstraintLayout 從父佈局中移除
            // 找到電話號碼的 ConstraintLayout
            ConstraintLayout phoneNumberConstraintLayout = findViewById(R.id.phoneNumberConstraintLayout);
            phoneNumberConstraintLayout.setVisibility(View.GONE);
        } else {
            // 如果 buildingPhoneNumber 不為空，則設置建築物的電話號碼
            buildingPhoneNumberTextView.setText(formatPhoneNumber(buildingPhoneNumber));
        }

        buildingDescription = buildingDescription.replace("\\n", "\n\n");
        buildingInfoTextView.setText(buildingDescription);

        ImageView buildingImageView = findViewById(R.id.buildingImageView);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child(imagePath);
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // 下載成功，使用圖片載入庫（例如 Glide、Picasso 等）將圖片設置到 ImageView 上
                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(buildingImageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // 下載失敗，可能圖片不存在或其他錯誤，你可以在這裡處理失敗的情況
                buildingImageView.setImageResource(R.drawable.default_image);
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
