package com.example.chuapp.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.chuapp.Adapter.BuildingsAdapter;
import com.example.chuapp.Adapter.NewsAdapter;
import com.example.chuapp.Domain.NewsDomain;
import com.example.chuapp.Domain.NewsDomain;
import com.example.chuapp.R;
import com.example.chuapp.databinding.ActivityBuildingsBinding;
import com.example.chuapp.databinding.ActivityNewsBinding;
import com.example.chuapp.databinding.ActivityTransportationBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class TransportationActivity extends AppCompatActivity {
    ActivityTransportationBinding binding;
    ImageView testPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransportationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        testPic = binding.testPic;

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TransportationActivity.this, MainActivity.class));
            }
        });

        // 获取FirebaseStorage实例
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // 创建StorageReference指向你的文件
        StorageReference storageRef = storage.getReference().child("buildings/l.jpg");

        // 下载文件到本地
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // 使用Glide加载图像到ImageView
                Glide.with(getApplicationContext())
                        .load(uri)
                        .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)) // 缓存策略，可根据需要设置
                        .into(testPic);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // 处理失败情况
                Toast.makeText(TransportationActivity.this, "下载图片失败", Toast.LENGTH_SHORT).show();
            }
        });

        Window window = TransportationActivity.this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(TransportationActivity.this, R.color.white));
    }
}
