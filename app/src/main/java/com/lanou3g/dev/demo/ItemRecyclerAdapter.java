package com.lanou3g.dev.demo;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Risky57 on 2017/4/8.
 */

public class ItemRecyclerAdapter extends RecyclerView.Adapter {

    private int count;
    private List<String> objects;

    public ItemRecyclerAdapter(List<String> objects) {
        this.objects = objects;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("ItemRecyclerAdapter", "++count:" + ++count);
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new RecyclerView.ViewHolder(view) {};
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextView textView = (TextView) holder.itemView;
        textView.setText(objects.get(position));
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }
}
