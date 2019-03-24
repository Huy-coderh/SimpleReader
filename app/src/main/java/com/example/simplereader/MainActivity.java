package com.example.simplereader;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.simplereader.UI.MyBottomViewGroup;
import com.example.simplereader.adapter.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private List<Fragment> fragmentList = new ArrayList<>();
    private FragmentFirst fragment1 = new FragmentFirst();
    private FragmentSecond fragment2 = new FragmentSecond();
    private FragmentThird fragment3 = new FragmentThird();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /**
         * 设置ViewPager
         */
        ViewPager viewPager = findViewById(R.id.view_pager);
        fragmentList.add(fragment1);
        fragmentList.add(fragment2);
        fragmentList.add(fragment3);
        FragmentManager fragmentManager = getSupportFragmentManager();
        ViewPagerAdapter adapter = new ViewPagerAdapter(fragmentManager, fragmentList);
        viewPager.setAdapter(adapter);
        MyBottomViewGroup viewGroup = findViewById(R.id.view_group);
        viewGroup.setViewPager(viewPager);


        NavigationView navigationView = findViewById(R.id.nav_view);
        //navigationView.setCheckedItem(R.id.nav_vip);   //设置默认选中
        /**
         * 为NavigationView设置menu监听器
         */
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_vip:
                        Toast.makeText(MainActivity.this, "you clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_give_reward:
                        Toast.makeText(MainActivity.this, "you clicked", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });
        /**
         * 设置DrawerLayout滑动时的动画效果
         */
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout, toolbar,
                0,
                0);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    /**
     * 重写该方法来加载toolBar的menu项
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    /**
     * 重写该方法来设置toolBar的menu的监听器
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.local_load:
                if(ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE },
                            1);    //请求码为唯一值 1
                } else {
                    readExternalStorage();
                }
                break;
                default:
        }
        return true;
    }

    /**
     * 运行时权限处理回调方法
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    readExternalStorage();
                } else {
                    Toast.makeText(this, "拒绝读取内存权限，无法读取本地文件", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
                default:
        }
    }

    private void readExternalStorage(){
        Intent intent = new Intent(MainActivity.this, FileBrowserActivity.class);
        String str = Environment.getExternalStorageDirectory().getPath();
        intent.putExtra("FilePath", str);
        startActivity(intent);
    }

}
