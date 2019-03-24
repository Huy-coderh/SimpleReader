package com.example.simplereader;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simplereader.adapter.FileRecyclerAdapter;
import com.example.simplereader.bookshelf.LocalBook;
import com.example.simplereader.local.LocalTxt;
import com.example.simplereader.local.LocalDirectory;
import com.example.simplereader.local.LocalFile;
import com.example.simplereader.util.BookshelfHelper;
import com.example.simplereader.util.Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileBrowserActivity extends BaseActivity{

    private List<LocalFile> dataList = new ArrayList<>();
    private String filePath;
    private LinearLayout pathInfoLayout;
    private FileRecyclerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_filebrower);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        filePath = getIntent().getStringExtra("FilePath");
        openDirectory(filePath);
        initViews();
    }

    private void initViews(){
        adapter = new FileRecyclerAdapter(dataList);
        adapter.setOnItemClickListener(new FileRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(View v, int position) {
                if(dataList.get(position) instanceof LocalDirectory){
                    filePath = dataList.get(position).getPath();
                    openDirectory(filePath);
                } else {
                    LocalBook book = new LocalBook(dataList.get(position).getName(),
                            dataList.get(position).getPath());
                    //加入书架
                    switch (BookshelfHelper.getInstance().addLocalBook(book)){
                        case BookshelfHelper.ADD_FINISHED :
                            Toast.makeText(FileBrowserActivity.this,
                                    "已加入书架", Toast.LENGTH_SHORT).show();
                            finish();
                            break;
                        case BookshelfHelper.ADD_FAILED :
                            Toast.makeText(FileBrowserActivity.this,
                                    "添加失败", Toast.LENGTH_SHORT).show();
                            break;
                        case BookshelfHelper.BOOK_EXIST :
                            Toast.makeText(FileBrowserActivity.this,
                                    "书籍已存在", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        });
        /**
         * 初始化recyclerView并设置为线性，默认为竖向
         */
        RecyclerView recyclerView = findViewById(R.id.txt_recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        /**
         * 为recyclerView添加分界线
         */
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.recycler_view_divider));
        recyclerView.addItemDecoration(itemDecoration);
        /**
         * 初始化控件
         */
        pathInfoLayout = findViewById(R.id.file_path_layout);
        pathInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToParentDirectory();
            }
        });
        ImageView imageView = findViewById(R.id.back_image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToParentDirectory();
            }
        });
    }

    private void backToParentDirectory(){
        if(! Environment.getExternalStorageDirectory().getPath().equals(filePath)){
            filePath = filePath.substring(0, filePath.lastIndexOf("/"));
            openDirectory(filePath);
        }
    }

    private void openDirectory(String filePath){
        dataList.clear();
        String info;
        File directory = new File(filePath);
        File[] files = directory.listFiles();
        for(File file : files){
            if(!file.isDirectory()){
                info = Utility.getFileLength(file.length());
                if(file.getName().endsWith(".txt")){
                    dataList.add(new LocalTxt(file.getName(), info, file.getPath()));
                }
            }
        }
        for(File file : files){
            if(file.isDirectory()){
                File[] nFiles = file.listFiles();
                int count = 0;
                for(File aFile : nFiles){
                    if(aFile.isDirectory()){
                        count++;
                    } else if(aFile.getName().endsWith(".txt")){
                        count++;
                    }
                }
                info = count + "项";
                dataList.add(new LocalDirectory(file.getName(), info, file.getPath()));
            }
        }if(adapter != null){
            adapter.updateData(dataList);
            updateFilePathInfo();
        }
    }

    private void updateFilePathInfo(){
        LinearLayout.LayoutParams params;
        String str = Environment.getExternalStorageDirectory().getPath();
        str = filePath.substring(str.length());
        String[] strs = str.split("/");

        pathInfoLayout.removeAllViews();

        TextView preText = new TextView(this);
        preText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        preText.setText("根目录");
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        pathInfoLayout.addView(preText, params);

        ImageView preImage = new ImageView(this);
        //获取屏幕密度
        float density = getResources().getDisplayMetrics().density;
        preImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_right_arrow));
        params = new LinearLayout.LayoutParams((int)density*20, (int)density*20);
        pathInfoLayout.addView(preImage, params);

        for(int i=1; i<strs.length; i++){
            TextView textView = new TextView(this);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            textView.setText(strs[i]);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_VERTICAL;
            pathInfoLayout.addView(textView, params);
            ImageView imageView = new ImageView(this);
            imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_right_arrow));
            params = new LinearLayout.LayoutParams((int)density*20, (int)density*20);
            pathInfoLayout.addView(imageView, params);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.file_brower_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.scan_file:
                Intent intent = new Intent(FileBrowserActivity.this, TxtFileScanActivity.class);
                startActivity(intent);
                default:
        }
        return true;
    }
}
