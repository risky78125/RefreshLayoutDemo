package com.lanou3g.dev.demo;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lanou3g.dev.demo.widget.LoadingLayout;
import com.lanou3g.dev.demo.widget.RefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "TAG_MainActivity";

    private RefreshLayout mRefreshLayout;
    private TextView mTextView;
//    private ListView mListView;
    private RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRefreshLayout = (RefreshLayout) findViewById(R.id.refresh_layout);
//        mTextView = (TextView) findViewById(R.id.text_view);
//        mListView = (ListView) findViewById(R.id.list_view);
//        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<String> objects = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            objects.add("ITEM: " + i);
        }
//        mRecyclerView.setAdapter(new ItemRecyclerAdapter(objects));
//        mListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, objects));

        mRefreshLayout.setOnRefreshOrLoadListener(new RefreshLayout.OnRefreshOrLoadListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.loadingFinished();
                        Log.d(TAG, "刷新完成");
                    }
                }, 3000);
            }

            @Override
            public void onLoad() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.loadingFinished();
                        Log.d(TAG, "加载完成");
                    }
                }, 3000);
            }
        });
    }


    public void finish(View view) {
        mRefreshLayout.loadingFinished();
    }
}
