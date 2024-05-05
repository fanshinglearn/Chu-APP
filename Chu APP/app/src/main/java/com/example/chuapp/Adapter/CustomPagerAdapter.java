package com.example.chuapp.Adapter;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.chuapp.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomPagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<String> mImageUrls = new ArrayList<>();
    private List<String> mHyperLinks = new ArrayList<>();
    private ViewPager mViewPager;
    private Handler mHandler;
    private int mCurrentPage = 0;
    private boolean mIsAutoPlay = false;

    public CustomPagerAdapter(Context context, ViewPager viewPager) {
        mContext = context;
        mViewPager = viewPager;
        mHandler = new Handler(Looper.getMainLooper());
        new ImageUrlAsyncTask().execute();
    }

    @Override
    public int getCount() {
        return mImageUrls.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View itemView = inflater.inflate(R.layout.item_image, container, false);

        ImageView imageView = itemView.findViewById(R.id.imageView);
        Glide.with(mContext)
                .load(mImageUrls.get(position))
                .into(imageView);

        // 監聽圖片點擊事件
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 點擊後跳轉到連接的超連結
                String hyperlink = mHyperLinks.get(position);
                if (!hyperlink.isEmpty()) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(hyperlink));
                    mContext.startActivity(browserIntent);
                }
            }
        });

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public void startAutoPlay() {
        if (!mIsAutoPlay) {
            mIsAutoPlay = true;
            mHandler.postDelayed(autoPlayRunnable, 1000);
        }
    }

    public void stopAutoPlay() {
        if (mIsAutoPlay) {
            mIsAutoPlay = false;
            mHandler.removeCallbacks(autoPlayRunnable);
        }
    }

    private Runnable autoPlayRunnable = new Runnable() {
        @Override
        public void run() {
            if (mIsAutoPlay) {
                mCurrentPage = (mCurrentPage + 1) % getCount();
                mViewPager.setCurrentItem(mCurrentPage, true);
                mHandler.postDelayed(this, 3000);
            }
        }
    };

    private class ImageUrlAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect("https://www1.chu.edu.tw/app/index.php?Action=mobileloadmod&Type=mobile_sz_mstr&Nbr=9").get();
                Elements imgElements = doc.select("img");
                Elements aElements = doc.select("a");
                for (int i = 0; i < imgElements.size(); i++) {
                    Element imgElement = imgElements.get(i);
                    String imageUrl = "https://www1.chu.edu.tw" + imgElement.attr("src");
                    mImageUrls.add(imageUrl);

                    // 獲取圖片對應的超連結
                    if (i < aElements.size()) {
                        Element aElement = aElements.get(i);
                        String hyperlink = "https://www1.chu.edu.tw" + aElement.attr("href");
                        mHyperLinks.add(hyperlink);
                    } else {
                        mHyperLinks.add("");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            notifyDataSetChanged();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startAutoPlay(); // 開始自動播放
                }
            }, 5000); // 延遲5秒後開始自動播放
        }
    }
}
