package com.example.chuapp.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.chuapp.Adapter.AnnouncementsAdapter;
import com.example.chuapp.Domain.AnnouncementsDomain;
import com.example.chuapp.R;
import com.example.chuapp.databinding.ActivityAnnouncementsBinding;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class AnnouncementsActivity extends AppCompatActivity {
    ActivityAnnouncementsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnnouncementsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AnnouncementsActivity.this, MainActivity.class));
            }
        });

        initRecyclerView();

        Window window = AnnouncementsActivity.this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(AnnouncementsActivity.this, R.color.white));
    }

    private void initRecyclerView() {
        new AsyncTask<Void, Void, ArrayList<AnnouncementsDomain>>() {
            @Override
            protected ArrayList<AnnouncementsDomain> doInBackground(Void... voids) {
                ArrayList<AnnouncementsDomain> itemsArrayList = new ArrayList<>();
                try {
                    String url = "https://www1.chu.edu.tw/app/index.php?Action=mobileloadmod&Type=mobile_rcg_mstr&Nbr=273";
                    Document doc = Jsoup.connect(url).get();
                    Elements titles = doc.select("div.mtitle a");

                    Elements dates = doc.select("i.mdate.after");

                    for (int i = 0; i < titles.size(); i++) {
                        Element title = titles.get(i);
                        Element date = dates.get(i);
                        String fullTitle = title.text().trim();
//                        String subType = fullTitle.substring(1, 5);
//                        String subTitle = fullTitle.substring(6);
                        String newUrl = title.attr("href");
                        String dateText = date.text().trim();
                        itemsArrayList.add(new AnnouncementsDomain(dateText, fullTitle, newUrl));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return itemsArrayList;
            }


            @Override
            protected void onPostExecute(ArrayList<AnnouncementsDomain> itemsArrayList) {
                super.onPostExecute(itemsArrayList);
                binding.announcementsView.setLayoutManager(new LinearLayoutManager(AnnouncementsActivity.this));
                binding.announcementsView.setAdapter(new AnnouncementsAdapter(itemsArrayList));
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

}
