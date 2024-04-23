package com.example.chuapp.Adapter;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chuapp.Domain.NewsDomain;
import com.example.chuapp.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder>{
    ArrayList<NewsDomain> items;
    DecimalFormat formatter;
    Context context;

    public NewsAdapter(ArrayList<NewsDomain> items) {
        this.items = items;
        formatter = new DecimalFormat("###,###,###,###.##");
    }

    @NonNull
    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_news,parent,false);
        context = parent.getContext();
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsAdapter.ViewHolder holder, int position) {
        NewsDomain currentItem = items.get(position);

        holder.dateTxt.setText(items.get(position).getDate());
        holder.titleTxt.setText(items.get(position).getTitle());
        holder.detailTxt.setText(items.get(position).getDetail());

        Picasso.get().load(currentItem.getImageUrl()).into(holder.pic);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = currentItem.getLink();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView dateTxt;
        TextView titleTxt;
        TextView detailTxt;
        ImageView pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTxt = itemView.findViewById(R.id.dateTxt);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            detailTxt = itemView.findViewById(R.id.detailTxt);
            pic = itemView.findViewById(R.id.pic);
        }
    }
}
