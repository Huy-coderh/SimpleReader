package com.example.simplereader.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.simplereader.MyApplication;
import com.example.simplereader.R;
import com.example.simplereader.bookshelf.BaseBook;
import com.example.simplereader.bookshelf.LocalBook;
import com.example.simplereader.bookshelf.WebBook;

import java.util.List;

public class BookshelfRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener, View.OnLongClickListener{

    private class BookViewHolder extends RecyclerView.ViewHolder{
        ImageView bookImg;
        TextView bookText;

        public BookViewHolder(View itemView){
            super(itemView);
            bookImg = itemView.findViewById(R.id.bookshelf_book_img);
            bookText = itemView.findViewById(R.id.bookshelf_book_text);
        }
    }

    private List<BaseBook> baseBookList;
    private OnItemClickListener mOnItemClickListener = null;
    private OnItemLongClickListener mOnItemLongClickListener = null;

    public BookshelfRecyclerAdapter(List<BaseBook> baseBooks){
        this.baseBookList = baseBooks;
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener){
        this.mOnItemLongClickListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_book, viewGroup, false);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setTag(position);
        BaseBook baseBook = baseBookList.get(position);
        ((BookViewHolder)holder).bookText .setText(baseBook.getName());
        if(baseBook instanceof LocalBook){
            ((BookViewHolder)holder).bookImg.setImageResource(R.drawable.img_default);
        }
        if(baseBook instanceof WebBook){
            Glide.with(MyApplication.getContext()).load(((WebBook) baseBook).getImage())
                    .into(((BookViewHolder)holder).bookImg);
        }
    }

    @Override
    public int getItemCount() {
        return baseBookList.size();
    }

    @Override
    public void onClick(View v) {
        if(mOnItemClickListener != null){
            mOnItemClickListener.onClick(v, (int)v.getTag());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if(mOnItemLongClickListener != null){
            return mOnItemLongClickListener.onLongClick(v, (int)v.getTag());
        }
        return false;
    }

    public interface OnItemClickListener{
        void onClick(View v, int position);
    }

    public interface OnItemLongClickListener{
        boolean onLongClick(View v, int position);
    }
}
