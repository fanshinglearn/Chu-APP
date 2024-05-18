package com.example.chuapp.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.chuapp.Adapter.EventsAdapter;
import com.example.chuapp.Domain.EventsDomain;
import com.example.chuapp.R;
import com.example.chuapp.databinding.ActivityEventsBinding;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class EventsActivity extends AppCompatActivity {
    ActivityEventsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initRecyclerView();

        Window window = EventsActivity.this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(EventsActivity.this, R.color.white));
    }

    private void initRecyclerView() {
        new AsyncTask<Void, Void, ArrayList<EventsDomain>>() {
            @Override
            protected ArrayList<EventsDomain> doInBackground(Void... voids) {
                ArrayList<EventsDomain> itemsArrayList = new ArrayList<>();
                try {
                    String url = "https://event.chu.edu.tw/ca/default.aspx";
                    Document doc = Jsoup.connect(url).get();

                    Element table = doc.select("table#GV1").first();
                    Elements rows = table.select("tr");

                    for (int i = 1; i < rows.size(); i++) { //first row is the col names so skip it.
                        Element row = rows.get(i);
                        Elements cols = row.select("td");

                        String dateText = cols.get(2).text().trim();
                        String date1 = dateText.substring(2, 18);
                        String date2 = dateText.substring(25, 41);
                        String fullDate = date1 + " ~ " + date2;

                        String fullTitle = cols.get(0).text().trim();
                        String location = cols.get(1).text().trim();
                        String newUrl = "";

                        Elements aElements = cols.get(0).select("a");
                        if (!aElements.isEmpty()) {
                            newUrl = "https://event.chu.edu.tw/ca/" + aElements.first().attr("href");
                        }

                        itemsArrayList.add(new EventsDomain(fullDate, fullTitle, location, newUrl));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return itemsArrayList;
            }

            @Override
            protected void onPostExecute(ArrayList<EventsDomain> itemsArrayList) {
                super.onPostExecute(itemsArrayList);
                binding.eventsView.setLayoutManager(new LinearLayoutManager(EventsActivity.this));
                binding.eventsView.setAdapter(new EventsAdapter(itemsArrayList));
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

}
