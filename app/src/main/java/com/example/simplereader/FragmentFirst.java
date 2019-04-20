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
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.simplereader.adapter.BookshelfRecyclerAdapter;
import com.example.simplereader.bookshelf.BaseBook;
import com.example.simplereader.bookshelf.LocalBook;
import com.example.simplereader.bookshelf.WebBook;
import com.example.simplereader.util.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class FragmentFirst extends Fragment implements View.OnClickListener{

    private RecyclerView recyclerView;
    private TextView textView;
    private List<BaseBook> bookList = new ArrayList<>();
    private BookshelfRecyclerAdapter adapter;
    private PopupWindow window;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_local, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerView = getActivity().findViewById(R.id.bookshelf);
        textView = getActivity().findViewById(R.id.null_text);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        adapter = new BookshelfRecyclerAdapter(bookList);
        adapter.setOnItemClickListener((v, position) -> {
            BaseBook baseBook = bookList.get(position);
            String str;
            Intent intent = new Intent(getActivity(), ReaderActivity.class);
            if(baseBook instanceof LocalBook){
                str = ((LocalBook) baseBook).getPath();
                intent.putExtra("book_ori", DBHelper.LOCAL);
            } else {
                intent.putExtra("book_ori", DBHelper.NET);
                str = ((WebBook)baseBook).getUrl();
            }
            intent.putExtra("book_str", str);
            startActivity(intent);
        });
        adapter.setOnItemLongClickListener((v, position) -> {
            showPopupWindow(position);
            return true;
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        bookList.clear();
        bookList.addAll(DBHelper.getInstance().getWebBookshelf());
        bookList.addAll(DBHelper.getInstance().getLocalBookshelf());
        if(bookList.size() == 0){
            textView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        } else {
            textView.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
    }

    private void showPopupWindow(int position){
        //window外部变暗
        WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
        params.alpha = 0.4f;
        getActivity().getWindow().setAttributes(params);

        View view;
        TextView text;

        if(bookList.get(position) instanceof WebBook){
            view = LayoutInflater.from(getActivity()).inflate(R.layout.popup_bookoptions, null);
            ((TextView)(view.findViewById(R.id.name))).setText(bookList.get(position).getName());
            text = view.findViewById(R.id.info);
            text.setTag(position);
            text.setOnClickListener(this);
            text = view.findViewById(R.id.load);
            text.setTag(position);
            text.setOnClickListener(this);
        } else {
            view = LayoutInflater.from(getActivity()).inflate(R.layout.popup_bookoptions_s, null);
            ((TextView)(view.findViewById(R.id.name))).setText(bookList.get(position).getName());
        }

        text = view.findViewById(R.id.catalogues);
        text.setTag(position);
        text.setOnClickListener(this);
        text = view.findViewById(R.id.delete);
        text.setTag(position);
        text.setOnClickListener(this);

        window = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT, true);
        window.setBackgroundDrawable(new ColorDrawable(0x00ffffff));
        window.setOnDismissListener(() -> {
            WindowManager.LayoutParams layoutParams = getActivity().getWindow().getAttributes();
            layoutParams.alpha = 1f;
            getActivity().getWindow().setAttributes(layoutParams);
        });
        window.showAtLocation(getActivity().findViewById(R.id.view_group),
                Gravity.START|Gravity.BOTTOM, 0, 0);
    }

    @Override
    public void onClick(View v) {
        window.dismiss();
        switch (v.getId()){
            case R.id.info :
                int i = (int)v.getTag();
                Intent intent = new Intent(getActivity(), BookActivity.class);
                intent.putExtra("book_data", (WebBook)bookList.get(i));
                startActivity(intent);
        }
    }
}
