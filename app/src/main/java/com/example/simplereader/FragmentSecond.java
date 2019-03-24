package com.example.simplereader;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.simplereader.adapter.ResultRecyclerAdapter;
import com.example.simplereader.sitebook.Book;

import com.example.simplereader.siteparser.ZhuishuRecommend;
import com.example.simplereader.util.StateCallBack;

import java.util.ArrayList;
import java.util.List;

public class FragmentSecond extends Fragment {

    private RecyclerView recyclerView;
    private RelativeLayout loading;
    private List<Book> recommendList = new ArrayList<>();
    private ResultRecyclerAdapter adapter;
    private List<View> views = new ArrayList<>(3);
    private StateCallBack callBack = new StateCallBack() {
        @Override
        public void onProcess() {

        }

        @Override
        public void onSuccess(List<Book> bookList) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loading.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    recommendList = bookList;
                    adapter.updataData(recommendList);
                }
            });
        }

        @Override
        public void onFailed() {

        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend, null);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //初始化顶部标签
        LinearLayout linearLayout = getActivity().findViewById(R.id.recommend_tag);
        for(int i = 0; i< linearLayout.getChildCount(); i++){
            View view = linearLayout.getChildAt(i);
            views.add(view);
            view.setTag(ZhuishuRecommend.WEEK + i );
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for(View view : views){
                        view.setBackgroundColor(Color.WHITE);
                    }
                    recyclerView.setVisibility(View.INVISIBLE);
                    loading.setVisibility(View.VISIBLE);
                    view.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorBlue));
                    ZhuishuRecommend.getRecommend((int)view.getTag(), callBack);
                }
            });
        }

        loading = getActivity().findViewById(R.id.loading);
        recyclerView = getActivity().findViewById(R.id.book_recommend);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ResultRecyclerAdapter(recommendList);
        adapter.setOnItemClickListener(new ResultRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(View v, int position) {
                //跳转到BookActivity
                Intent intent = new Intent(getActivity(), BookActivity.class);
                intent.putExtra("book_url", recommendList.get(position).getUrl());
                intent.putExtra("book_source", "追书神器");
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.INVISIBLE);
        views.get(0).performClick();
    }

}
