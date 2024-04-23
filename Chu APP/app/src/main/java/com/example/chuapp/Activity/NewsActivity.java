package com.example.chuapp.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.chuapp.Adapter.NewsAdapter;
import com.example.chuapp.Domain.NewsDomain;
import com.example.chuapp.R;
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
                    String url = "https://news.chu.edu.tw/p/403-1001-14-1.php?Lang=zh-tw";
                    Document doc = Jsoup.connect(url).get();
                    Elements titles = doc.select("div.d-txt a");
                    Elements dates = doc.select("div.d-txt i");
                    Elements details = doc.select("div.mdetail");
                    Elements imgUrls = doc.select("div.d-img img");

                    for (int i = 0; i < titles.size(); i++) {
                        Element title = titles.get(i);
                        Element date = dates.get(i);
                        Element detail = details.get(i);
                        Element imgUrl = imgUrls.get(i);

                        String fullTitle = title.text().trim();
//                        String subType = fullTitle.substring(1, 5);
//                        String subTitle = fullTitle.substring(6);
                        String detailText = detail.text().trim();
                        String newUrl = title.attr("href");
                        String dateText = date.text().trim();

                        String src = imgUrl.attr("src");
                        String fullImgUrl = "https://news.chu.edu.tw/" + src;
                        itemsArrayList.add(new NewsDomain(dateText, fullTitle, newUrl, detailText, fullImgUrl));
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
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

}
