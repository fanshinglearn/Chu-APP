package com.example.chuapp.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.chuapp.Adapter.ReportsAdapter;
import com.example.chuapp.Domain.ReportsDomain;
import com.example.chuapp.R;
import com.example.chuapp.databinding.ActivityReportsBinding;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class ReportsActivity extends AppCompatActivity {
    ActivityReportsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReportsActivity.this, MainActivity.class));
            }
        });

        initRecyclerView();

        Window window = ReportsActivity.this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(ReportsActivity.this, R.color.white));
    }

    private void initRecyclerView() {
        new AsyncTask<Void, Void, ArrayList<ReportsDomain>>() {
            @Override
            protected ArrayList<ReportsDomain> doInBackground(Void... voids) {
                ArrayList<ReportsDomain> itemsArrayList = new ArrayList<>();
                try {
                    String url = "https://news.chu.edu.tw/p/403-1001-30-1.php?Lang=zh-tw";
                    Document doc = Jsoup.connect(url).get();
                    Elements titles = doc.select("div.d-txt a");
                    Elements dates = doc.select("div.d-txt i");

                    for (int i = 0; i < titles.size(); i++) {
                        Element title = titles.get(i);
                        Element date = dates.get(i);
                        String fullTitle = title.text().trim();
//                        String subType = fullTitle.substring(1, 5);
//                        String subTitle = fullTitle.substring(6);
                        String newUrl = title.attr("href");
                        String dateText = date.text().trim();
                        itemsArrayList.add(new ReportsDomain(dateText, fullTitle, newUrl));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return itemsArrayList;
            }


            @Override
            protected void onPostExecute(ArrayList<ReportsDomain> itemsArrayList) {
                super.onPostExecute(itemsArrayList);
                binding.reportsView.setLayoutManager(new LinearLayoutManager(ReportsActivity.this));
                binding.reportsView.setAdapter(new ReportsAdapter(itemsArrayList));
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

}
