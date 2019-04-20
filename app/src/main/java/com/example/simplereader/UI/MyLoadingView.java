package com.example.simplereader.UI;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.example.simplereader.R;


public class MyLoadingView extends View {

    private int ovalColor;

    private int arcColor;

    private int strokeWidth;

    private Paint ovalPaint;

    private Paint arcPaint;

    private RectF ovalRect;

    private float startAngle = 90;

    private float arcAngle = 90;

    private ValueAnimator valueAnimator;

    public MyLoadingView(Context context) {
        super(context);
    }

    public MyLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyLoadingView);
        ovalColor = typedArray.getColor(R.styleable.MyLoadingView_oval_color,
                getResources().getColor(R.color.default_oval_color));
        arcColor = typedArray.getColor(R.styleable.MyLoadingView_arc_color,
                getResources().getColor(R.color.default_arc_color));
        strokeWidth = typedArray.getDimensionPixelOffset(R.styleable.MyLoadingView_stroke_width,
                3);
        typedArray.recycle();
        init();
    }

    /**
     *当wrap_content和match_parent,采用默认宽高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //默认值
        int width = 60;
        int height = 60;

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if(widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST){
            setMeasuredDimension(Math.min(width, widthSpecSize), Math.min(height, heightSpecSize));//测量数据保存
        } else if(widthSpecMode == MeasureSpec.AT_MOST){
            setMeasuredDimension(Math.min(width, widthSpecSize), heightSpecSize);
        } else if(heightSpecMode == MeasureSpec.AT_MOST){
            setMeasuredDimension(widthSpecSize, Math.min(height, heightSpecSize));
        }
    }

    private void init(){
        ovalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ovalPaint.setColor(ovalColor);
        ovalPaint.setStrokeWidth(strokeWidth);
        ovalPaint.setStyle(Paint.Style.STROKE);

        arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcPaint.setColor(arcColor);
        arcPaint.setStrokeWidth(strokeWidth);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setDither(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float ovalWidth = w * 3 / 4;
        float ovalHeight = h * 3 / 4;
        ovalRect = new RectF(w/2 - ovalWidth /2, h/2 - ovalHeight /2,
                w/2 + ovalWidth /2, h/2 + ovalHeight /2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.drawOval(ovalRect, ovalPaint);
        canvas.drawArc(ovalRect, startAngle, arcAngle, false, arcPaint);
        canvas.drawArc(ovalRect, -startAngle, arcAngle, false, arcPaint);
        canvas.restore();
    }

    private void initAnimation() {
        valueAnimator = ValueAnimator.ofFloat(0, 1.F);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                startAngle = 360 * Float.valueOf(valueAnimator.getAnimatedValue().toString());
                //arcAngle = 360 * Float.valueOf(valueAnimator.getAnimatedValue().toString());
                invalidate();
            }
        });
        valueAnimator.setDuration(1000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initAnimation();
        if(valueAnimator != null){
            valueAnimator.start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(valueAnimator != null){
            valueAnimator.removeAllUpdateListeners();
            valueAnimator.cancel();
            valueAnimator = null;
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        switch(visibility){
            case View.VISIBLE :
                valueAnimator.start();
                break;
            case View.INVISIBLE :
                valueAnimator.cancel();
                break;
        }
    }
}
