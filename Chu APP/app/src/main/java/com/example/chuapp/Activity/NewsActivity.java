package com.example.chuapp.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.chuapp.Adapter.BuildingsAdapter;
import com.example.chuapp.Adapter.NewsAdapter;
import com.example.chuapp.Domain.NewsDomain;
import com.example.chuapp.Domain.NewsDomain;
import com.example.chuapp.R;
import com.example.chuapp.databinding.ActivityBuildingsBinding;
import com.example.chuapp.databinding.ActivityNewsBinding;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class NewsActivity extends AppCompatActivity {
    ActivityNewsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewsActivity.this, MainActivity.class));
            }
        });

        initRecyclerView();

        Window window = NewsActivity.this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(NewsActivity.this, R.color.white));
    }


    private void initRecyclerView() {
        new AsyncTask<Void, Void, ArrayList<NewsDomain>>() {
            @Override
            protected ArrayList<NewsDomain> doInBackground(Void... voids) {
                ArrayList<NewsDomain> itemsArrayList = new ArrayList<>();
                try {
                    String url = "https://www.chu.edu.tw/p/403-1000-273-1.php?Lang=zh-tw";
                    Document doc = Jsoup.connect(url).get();
                    Elements titles = doc.select("div.mtitle a");

                    for (Element title : titles) {
                        String fullTitle = title.attr("title");
                        String subType = fullTitle.substring(1, 5);
                        String subTitle = fullTitle.substring(6);
                        String newUrl = title.attr("href");
                        itemsArrayList.add(new NewsDomain(subType, subTitle, newUrl)); // 將標題添加到列表中
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return itemsArrayList;
            }

            @Override
            protected void onPostExecute(ArrayList<NewsDomain> itemsArrayList) {
                super.onPostExecute(itemsArrayList);
                binding.newsView.setLayoutManager(new LinearLayoutManager(NewsActivity.this));
                binding.newsView.setAdapter(new NewsAdapter(itemsArrayList));
            }
        }.execute();
    }
}
