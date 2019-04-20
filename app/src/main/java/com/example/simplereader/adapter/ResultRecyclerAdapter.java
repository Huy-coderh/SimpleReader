package com.example.simplereader.adapter;

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
import com.example.simplereader.sitebean.Book;

import java.util.List;

public class ResultRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener{

    private class BookViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView name;
        TextView intro;
        TextView type;
        TextView author;
        TextView source;
        TextView tagText;

        public BookViewHolder(View itemView){
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.recommend_name);
            intro = itemView.findViewById(R.id.recommend_intro);
            type = itemView.findViewById(R.id.recommend_type);
            author = itemView.findViewById(R.id.recommend_author);
            source = itemView.findViewById(R.id.source_text);
            tagText = itemView.findViewById(R.id.tag_text);
        }
    }

    private List<Book> bookList;
    private OnItemClickListener listener = null;

    public ResultRecyclerAdapter(List<Book> bookList){
        this.bookList = bookList;
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setTag(position);
        Book book = bookList.get(position);
        Glide.with(MyApplication.getContext()).load(book.getImage()).into(((BookViewHolder)holder).image);
        if(book.getSource() != null){
            ((BookViewHolder)holder).type.setText(book.getType());
        } else{
            ((BookViewHolder)holder).type.setVisibility(View.GONE);
            ((BookViewHolder)holder).tagText.setVisibility(View.GONE);
        }
        if(book.getSource() != null){
            String source = "source:" + book.getSource();
            ((BookViewHolder)holder).source.setText(book.getSource());
        }
        ((BookViewHolder)holder).name.setText(book.getName());
        ((BookViewHolder)holder).author.setText(book.getAuthor());
        ((BookViewHolder)holder).intro.setText(book.getIntro());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_recommend, viewGroup, false);
        view.setOnClickListener(this);
        return new BookViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    @Override
    public void onClick(View v) {
        if(listener != null){
            listener.onClick(v, (int)v.getTag());
        }
    }

    public interface OnItemClickListener{
        void onClick(View v, int position);
    }
}
