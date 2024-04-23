package com.example.chuapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chuapp.Domain.ReportsDomain;
import com.example.chuapp.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.ViewHolder>{
    ArrayList<ReportsDomain> items;
    DecimalFormat formatter;
    Context context;

    public ReportsAdapter(ArrayList<ReportsDomain> items) {
        this.items = items;
        formatter = new DecimalFormat("###,###,###,###.##");
    }

    @NonNull
    @Override
    public ReportsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_reports,parent,false);
        context = parent.getContext();
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportsAdapter.ViewHolder holder, int position) {
        holder.dateTxt.setText(items.get(position).getDate());
        holder.titleTxt.setText(items.get(position).getTitle());

        ReportsDomain currentItem = items.get(position);

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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTxt = itemView.findViewById(R.id.dateTxt);
            titleTxt = itemView.findViewById(R.id.titleTxt);
        }
    }
}
