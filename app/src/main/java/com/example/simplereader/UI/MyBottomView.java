package com.example.simplereader.UI;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.example.simplereader.R;

public class MyBottomView extends View {
    private int textColor;
    private int processColor;
    private Bitmap defaultBitmap;
    private Bitmap pitchBitmap;
    private float textSize;
    private String text;
    private boolean defaultState;

    private int currentAlpha;

    private Paint bitmapPaint;
    private Paint textPaint;

    private Rect srcRect;
    private RectF dstRectf;

    private int textX;
    private int textY;

    private ValueAnimator colorAnimator;

    public MyBottomView(Context context) {
        super(context);
    }

    public MyBottomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyBottomView);
        textColor = typedArray.getColor(R.styleable.MyBottomView_default_text_color,
                getResources().getColor(R.color.default_text_color));
        processColor = typedArray.getColor(R.styleable.MyBottomView_process_color,
                getResources().getColor(R.color.default_process_color));
        defaultBitmap = ((BitmapDrawable)typedArray.getDrawable(R.styleable.MyBottomView_default_ic))
                .getBitmap();
        pitchBitmap = ((BitmapDrawable)typedArray.getDrawable(R.styleable.MyBottomView_pitch_ic))
                .getBitmap();
        textSize = typedArray.getDimension(R.styleable.MyBottomView_text_size,
                getResources().getDimension(R.dimen.default_text_size));
        text = typedArray.getString(R.styleable.MyBottomView_text);
        defaultState = typedArray.getBoolean(R.styleable.MyBottomView_default_state, false);
        typedArray.recycle();
        init();
    }

    private void init(){
        currentAlpha = defaultState ? 255 : 0;
        bitmapPaint = new Paint();
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
        //设置字体样式
        textPaint.setTypeface(Typeface.SANS_SERIF);
        initProcessTextColorAnimator();
    }

    /**
     * 设置属性动画，改变textColor
     */
    private void initProcessTextColorAnimator(){
        colorAnimator = ValueAnimator.ofInt(textColor, processColor);
        colorAnimator.setDuration(100);
        //设置线性插值器
        colorAnimator.setInterpolator(new LinearInterpolator());
        //设置颜色估值器
        colorAnimator.setEvaluator(new ArgbEvaluator());
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                textColor = (Integer) animation.getAnimatedValue();
                //Log.d("current textColor", "  " + textColor);
            }
        });
    }


    /**
     * 在控件大小发生变化时调用，初始化会被调用一次，可以获取控件的width和height
     * @param w 控件的新宽度
     * @param h 控件的新高度
     * @param oldw 控件的旧宽度
     * @param oldh 控件的旧高度
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int width = getWidth();
        int height = getHeight();
        if(srcRect == null){
            srcRect = new Rect();
            srcRect.left = 0;
            srcRect.right = defaultBitmap.getWidth();
            srcRect.top = 0;
            srcRect.bottom = defaultBitmap.getHeight();
        }
        if(dstRectf == null){
            dstRectf = new RectF();
            dstRectf = new RectF() ;
            if (text == null ) {
                double ratio = 0.2 ;
                dstRectf.top = (float) (height * ratio) ;
                dstRectf.bottom = (float) (height * (1- ratio ));
            }else {
                dstRectf.top = height / 8f;
                dstRectf.bottom = (float) (height * 10 / 16);
            }
            int  bitmapWidth = (int) (defaultBitmap.getWidth() * ( dstRectf.bottom - dstRectf.top)
                    / defaultBitmap.getHeight()) ;
            dstRectf.left = (width - bitmapWidth ) / 2 ;
            dstRectf.right = dstRectf.left + bitmapWidth;
        }
        if (text != null ) {
            Rect rect = new Rect();
            textPaint.getTextBounds(text, 0, text.length(), rect );
            int textWidth = rect.width();
            int textHeight = rect.height();
            textX = (width - textWidth) / 2;
            textY =(int) (( height * 0.2  - textHeight) / 2  + height * 0.9 ) ;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        bitmapPaint.setAlpha(255 - currentAlpha);
        canvas.drawBitmap(defaultBitmap, srcRect, dstRectf, bitmapPaint);
        bitmapPaint.setAlpha(currentAlpha);
        canvas.drawBitmap(pitchBitmap, srcRect, dstRectf, bitmapPaint);
        if(text != null){
            textPaint.setColor(textColor);
            canvas.drawText(text, textX, textY, textPaint);
        }
        canvas.restore();
    }

    /*@Override
    public boolean onTouchEvent(MotionEvent motionEvent){
        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN :
                setProgress(1);
                break;
            case MotionEvent.ACTION_MOVE :
                if( ! isTouchPointInView(this,
                        (int)motionEvent.getRawX(), (int)motionEvent.getRawY())){
                    setProgress(0);
                }
                break;
            case MotionEvent.ACTION_UP :
                if( ! isTouchPointInView(this,
                        (int)motionEvent.getRawX(), (int)motionEvent.getRawY())){
                    setProgress(0);
                } else {
                    setProgress(1);
                }
                break;
        }
        invalidate();
        return true;
    }

    private boolean isTouchPointInView(View targetView, int xAxis, int yAxis) {
        if (targetView== null) {
            return false;
        }
        int[] location = new int[2];
        targetView.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + targetView.getMeasuredWidth();
        int bottom = top + targetView.getMeasuredHeight();
        if (yAxis >= top && yAxis <= bottom && xAxis >= left
                && xAxis <= right) {
            return true;
        }
        return false;
    }*/

    public void setProgress(float progress){
        if (progress > 1) {
            throw new RuntimeException("progress do not > 100") ;
        }
        currentAlpha = (int) (255 * progress) ;
        colorAnimator.setCurrentPlayTime((long) (progress * 100));
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(colorAnimator != null){
            colorAnimator.removeAllUpdateListeners();
            colorAnimator.cancel();
            colorAnimator = null;
        }
    }

}
