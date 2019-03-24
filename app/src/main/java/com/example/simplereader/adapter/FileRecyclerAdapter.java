package com.example.simplereader.adapter;




import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.simplereader.R;
import com.example.simplereader.local.LocalTxt;
import com.example.simplereader.local.LocalDirectory;
import com.example.simplereader.local.LocalFile;
import java.util.List;

public class FileRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener{

    private final int TYPE_BOOK_VIEW = 1;
    private final int TYPE_DIRECTORY_VIEW = 2;
    private List<LocalFile> dataList;
    private OnItemClickListener mOnItemClickListener = null;

    private class BookViewHolder extends RecyclerView.ViewHolder{
        ImageView bookImage;
        TextView bookName;
        TextView bookInfo;

        public BookViewHolder(View itemView){
            super(itemView);
            bookImage = itemView.findViewById(R.id.file_image);
            bookName = itemView.findViewById(R.id.file_name);
            bookInfo = itemView.findViewById(R.id.file_info);
        }
    }

    private class DirectoryViewHolder extends RecyclerView.ViewHolder{
        ImageView directoryImage;
        TextView directoryName;
        TextView directoryInfo;
        ImageView arrowImage;

        public DirectoryViewHolder(@NonNull final View itemView) {
            super(itemView);
            directoryImage = itemView.findViewById(R.id.directory_image);
            directoryName = itemView.findViewById(R.id.directory_name);
            directoryInfo = itemView.findViewById(R.id.directory_info);
            arrowImage = itemView.findViewById(R.id.arrow_image);
        }
    }

    public FileRecyclerAdapter(List<LocalFile> fileList){
        this.dataList = fileList;
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mOnItemClickListener = listener;
    }

    public void updateData(List<LocalFile> list){
        this.dataList = list;
        this.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if(mOnItemClickListener != null){
            mOnItemClickListener.onClick(v, (int)v.getTag());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(dataList.get(position) instanceof LocalDirectory){
            return TYPE_DIRECTORY_VIEW;
        } else {
            return TYPE_BOOK_VIEW;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, int viewType) {
        if(viewType == TYPE_BOOK_VIEW){
            View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_txt, viewGroup, false);
            view.setOnClickListener(this);
            return new BookViewHolder(view);
        } else if(viewType == TYPE_DIRECTORY_VIEW){
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.recycler_directory, viewGroup, false);
            view.setOnClickListener(this);
            return new DirectoryViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        viewHolder.itemView.setTag(i);
        if(viewHolder instanceof BookViewHolder){
            LocalTxt book = (LocalTxt) dataList.get(i);
            ((BookViewHolder)viewHolder).bookName.setText(book.getName());
            ((BookViewHolder)viewHolder).bookImage.setImageResource(book.getImageId());
            ((BookViewHolder)viewHolder).bookInfo.setText(book.getInfo());
        } else if(viewHolder instanceof DirectoryViewHolder){
            LocalDirectory directory = (LocalDirectory)dataList.get(i);
            ((DirectoryViewHolder)viewHolder).directoryName.setText(directory.getName());
            ((DirectoryViewHolder)viewHolder).directoryImage.setImageResource(directory.getImageId());
            ((DirectoryViewHolder)viewHolder).directoryInfo.setText(directory.getInfo());
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public interface OnItemClickListener{
        void onClick(View v, int position);
    }

}
