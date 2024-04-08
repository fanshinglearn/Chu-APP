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

import com.bumptech.glide.Glide;
import com.example.chuapp.Activity.BuildingInformationActivity;
import com.example.chuapp.Domain.BuildingsDomain;
import com.example.chuapp.R;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class BuildingsAdapter extends RecyclerView.Adapter<BuildingsAdapter.ViewHolder>{
    ArrayList<BuildingsDomain> items;
    DecimalFormat formatter;
    Context context;

    public BuildingsAdapter(ArrayList<BuildingsDomain> items) {
        this.items = items;
        formatter = new DecimalFormat("###,###,###,###.##");
    }

    @NonNull
    @Override
    public BuildingsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_buildings,parent,false);
        context = parent.getContext();
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull BuildingsAdapter.ViewHolder holder, int position) {
        holder.titleTxt.setText(items.get(position).getTitle());

        Uri imageUrl = items.get(position).getImageUrl();
        // 使用Glide加载图像到ImageView
        Glide.with(context)
                .load(imageUrl)
                .into(holder.pic);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在点击事件中启动新的活动
                Context context = holder.itemView.getContext();
                Intent intent = new Intent(context, BuildingInformationActivity.class);
                // 传递buildingAbbreviation到BuildingInformationActivity
                intent.putExtra("buildingAbbreviation", items.get(position).getBuildingAbbreviation());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView titleTxt;
        ImageView pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            pic = itemView.findViewById(R.id.pic);
        }
    }
}
