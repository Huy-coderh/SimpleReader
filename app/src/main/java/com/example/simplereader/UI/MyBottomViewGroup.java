package com.example.simplereader.UI;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class MyBottomViewGroup extends LinearLayout
        implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private int childCount;
    private ViewPager viewPager;
    private int currentPosition = 0;
    private boolean isClick = false;

    public MyBottomViewGroup(Context context) {
        super(context);
    }

    public MyBottomViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setViewPager(ViewPager viewPager){
        this.viewPager = viewPager;
        this.viewPager.addOnPageChangeListener(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        childCount = getChildCount();
        for(int i=0; i<childCount; i++){
            View child = getChildAt(i);
            child.setTag(i);
            child.setOnClickListener(this);
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {
        if(! isClick){
            ((MyBottomView)getChildAt(i)).setProgress(1 - v);
            if(i+1 < childCount){
                ((MyBottomView)getChildAt(i+1)).setProgress(v);
            }
        }
    }

    @Override
    public void onPageSelected(int i) {
        currentPosition = i;
    }

    @Override
    public void onPageScrollStateChanged(int i) {
        if(i == 0){
            isClick = false;
        }
    }

    @Override
    public void onClick(View v) {
        isClick = true;
        ((MyBottomView)getChildAt(currentPosition)).setProgress(0);
        if(v instanceof MyBottomView){
            ((MyBottomView) v).setProgress(1);
        }
        int position = (int)v.getTag();
        viewPager.setCurrentItem(position);
        currentPosition = position;
    }

}
