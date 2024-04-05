package com.example.chuapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chuapp.Domain.NewsDomain;
import com.example.chuapp.R;

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
        holder.typeTxt.setText(items.get(position).getType());
        holder.titleTxt.setText(items.get(position).getTitle());

        NewsDomain currentItem = items.get(position);

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
        TextView typeTxt;
        TextView titleTxt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            typeTxt = itemView.findViewById(R.id.typeTxt);
            titleTxt = itemView.findViewById(R.id.titleTxt);
        }
    }
}
