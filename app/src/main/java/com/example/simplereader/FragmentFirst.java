package com.example.simplereader;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.simplereader.adapter.BookshelfRecyclerAdapter;
import com.example.simplereader.bookshelf.BaseBook;
import com.example.simplereader.bookshelf.LocalBook;
import com.example.simplereader.util.BookshelfHelper;

import java.util.ArrayList;
import java.util.List;

public class FragmentFirst extends Fragment {

    private RecyclerView recyclerView;
    private TextView textView;
    private List<BaseBook> baseBookList = new ArrayList<>();
    private BookshelfRecyclerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local, null);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerView = getActivity().findViewById(R.id.bookshelf_local);
        textView = getActivity().findViewById(R.id.null_text);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        adapter = new BookshelfRecyclerAdapter(baseBookList);
        adapter.setOnItemClickListener(new BookshelfRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(View v, int position) {
                BaseBook baseBook = baseBookList.get(position);
                String str = ((LocalBook) baseBook).getPath();
                Intent intent = new Intent(getActivity(), ReaderActivity.class);
                intent.putExtra("url_path", str);
                startActivity(intent);
            }
        });
        adapter.setOnItemLongClickListener(new BookshelfRecyclerAdapter.OnItemLongClickListener() {
            @Override
            public boolean onLongClick(View v, int position) {
                showPopupWindow();
                return true;
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        baseBookList = BookshelfHelper.getInstance().getLocalBookshelf();
        if(baseBookList.size() == 0){
            textView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        } else {
            textView.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.updateData(baseBookList);
        }
    }

    private void showPopupWindow(){
        //window外部变暗
        WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
        params.alpha = 0.4f;
        getActivity().getWindow().setAttributes(params);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.popup_bookoptions, null);
        PopupWindow window = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT, true);
        window.setBackgroundDrawable(new ColorDrawable(0x00ffffff));
        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams layoutParams = getActivity().getWindow().getAttributes();
                layoutParams.alpha = 1f;
                getActivity().getWindow().setAttributes(layoutParams);
            }
        });
        window.showAtLocation(getActivity().findViewById(R.id.view_group),
                Gravity.START|Gravity.BOTTOM, 0, 0);
    }

}
