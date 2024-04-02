package com.example.chuapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.chuapp.Adapter.BuildingsAdapter;
import com.example.chuapp.Domain.BuildingsDomain;
import com.example.chuapp.R;
import com.example.chuapp.databinding.ActivityBuildingsBinding;

import java.util.ArrayList;

public class BuildingsActivity extends AppCompatActivity {
    ActivityBuildingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBuildingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

        itemsArraylist.add(new BuildingsDomain("工程一館", "e"));
        itemsArraylist.add(new BuildingsDomain("工程二館", "s"));
        itemsArraylist.add(new BuildingsDomain("研發大樓", "i"));
        itemsArraylist.add(new BuildingsDomain("國際會議聽", "n"));
        itemsArraylist.add(new BuildingsDomain("管理一館", "m1"));
        itemsArraylist.add(new BuildingsDomain("綜合一館", "m2"));
        itemsArraylist.add(new BuildingsDomain("圖書與資訊大樓", "l"));
        itemsArraylist.add(new BuildingsDomain("建築一館", "a"));
        itemsArraylist.add(new BuildingsDomain("體育館暨活動中心", "s"));
        itemsArraylist.add(new BuildingsDomain("學生宿舍1", "d1"));
        itemsArraylist.add(new BuildingsDomain("學生宿舍2", "d2"));
        itemsArraylist.add(new BuildingsDomain("學生宿舍3", "d3"));
        itemsArraylist.add(new BuildingsDomain("學生宿舍4", "d4"));

        int numberOfColumns = 2;
        binding.buildingsView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        binding.buildingsView.setAdapter(new BuildingsAdapter(itemsArraylist));
    }
}
