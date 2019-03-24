package com.example.simplereader.UI;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.example.simplereader.R;

public class MyReaderView extends View {

    private Bitmap bitmap;

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public MyReaderView(Context context) {
        super(context);
    }

    public MyReaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Paint paint = new Paint();
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MyReaderView);
        int textColor = array.getColor(R.styleable.MyReaderView_textColor, Color.BLACK);
        float textSize = array.getDimension(R.styleable.MyReaderView_textSize, 36);
        array.recycle();
    }

    /**
     * 在wrap_content下给出默认值
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = 200;
        int height = 200;
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

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if(bitmap != null){
            canvas.save();
            canvas.drawBitmap(bitmap, 0, 0, null);
            canvas.restore();
        }
    }

}
