package com.example.viewlearn;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private XRecyclerView refreshView;
    private List<DataTest> dataTests = new ArrayList<>();
    private Handler msgHandler;
    private View headView;
    public static final int UPDATEFINISH = 0x01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        refreshView = findViewById(R.id.refresh_view);
        init();
    }

    private void init(){
        initData();
        msgHandler = new Handler(getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case UPDATEFINISH:
                        refreshView.refreshComplete();
                }
            }
        };
        LinearLayoutManager xManager = new LinearLayoutManager(this);
        xManager.setOrientation(LinearLayoutManager.VERTICAL);
        refreshView.setLayoutManager(xManager);
        refreshView.setAdapter(new MyAdapter(dataTests));
        refreshView.setLoadingMoreEnabled(false);
        refreshView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                Thread thread = new Thread(new TestRun(msgHandler));
                thread.start();
            }

            @Override
            public void onLoadMore() {

            }
        });
        headView = LayoutInflater.from(this).inflate(R.layout.head_view,null);
        refreshView.addHeaderView(headView);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private int addInsex;
    private class TestRun implements Runnable{

        private Handler msgSendHandler;

        public TestRun(Handler handler){
            msgSendHandler = handler;
        }
        @Override
        public void run() {
            try {
                Thread.sleep(2000);
                dataTests.add(new DataTest("我是新来的：" + addInsex++));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            msgSendHandler.sendEmptyMessage(UPDATEFINISH);

        }
    }


    private void initData(){
        for (int i = 0 ; i<20;i++){
            DataTest dataTest = new DataTest("我是" + i);
            dataTests.add(dataTest);
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder{
        TextView itemName;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
        }
    }


    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder>{
        private List<DataTest> mdatas;

        public MyAdapter(List<DataTest> mdatas){
            this.mdatas = mdatas;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view,parent,false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            DataTest itemData = mdatas.get(position);
            holder.itemName.setText(itemData.getName());
        }

        @Override
        public int getItemCount() {
            return mdatas.size();
        }
    }

}
