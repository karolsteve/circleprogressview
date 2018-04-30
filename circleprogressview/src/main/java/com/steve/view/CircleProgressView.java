package com.steve.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

/**
 * Created by Steve Tchatchouang on 12/01/2018
 */

public class CircleProgressView extends View {

    public static final String PERCENT = "%";

    private static final int DEFAULT_BACKGROUND_COLOR = Color.LTGRAY;
    private static final int DEFAULT_FOREGROUND_COLOR = Color.DKGRAY;
    private static final int DEFAULT_PROGRESS         = 30;
    private static final int DEFAULT_PROGRESS_MAX     = 100;
    private static final int DEFAULT_STROKE_WIDTH     = 4;
    private static final int DEFAULT_TEXT_SIZE        = 30;

    private int       foregroundColor;
    private int       backgroundColor;
    private int       textColor;
    private int       percentColor;
    private float     mStrokeWidth;
    private float     mTextSize;
    private boolean   showText;
    private boolean   fillBackground;
    private Paint     mBackgroundPaint;
    private Paint     mForegroundPaint;
    private TextPaint mTextPaint;
    private TextPaint mPercentPaint;


    private int   mProgress;
    private int   mMax;
    private RectF rectF;

    public CircleProgressView(Context context) {
        this(context, null);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressView);
        backgroundColor = a.getColor(R.styleable.CircleProgressView_backgroundProgressColor, DEFAULT_BACKGROUND_COLOR);
        foregroundColor = a.getColor(R.styleable.CircleProgressView_foregroundProgressColor, DEFAULT_FOREGROUND_COLOR);
        textColor = a.getColor(R.styleable.CircleProgressView_textColor, foregroundColor);
        percentColor = a.getColor(R.styleable.CircleProgressView_percentColor, textColor);
        mProgress = a.getInteger(R.styleable.CircleProgressView_progress, DEFAULT_PROGRESS);
        mMax = a.getInteger(R.styleable.CircleProgressView_max, DEFAULT_PROGRESS_MAX);
        mTextSize = a.getDimension(R.styleable.CircleProgressView_textSize, DEFAULT_TEXT_SIZE);
        mStrokeWidth = a.getInt(R.styleable.CircleProgressView_strokeWidth, DEFAULT_STROKE_WIDTH);
        mStrokeWidth = convertDpToPixel(mStrokeWidth,context);
        showText = a.getBoolean(R.styleable.CircleProgressView_showText, false);
        fillBackground = a.getBoolean(R.styleable.CircleProgressView_fillBackground, false);
        a.recycle();
        rectF = new RectF();
        initPaints();
    }

    private float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        Log.e("Utils", "convertDpToPixel: "+metrics.density );
        Log.e("Utils", "convertDpToPixel: "+metrics.densityDpi );
        return dp * (metrics.densityDpi / 160f);
    }

    private void initPaints() {
        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setColor(backgroundColor);
        mBackgroundPaint.setStrokeCap(Paint.Cap.ROUND);
        mBackgroundPaint.setStrokeWidth(mStrokeWidth);
        mBackgroundPaint.setStyle(fillBackground ? Paint.Style.FILL_AND_STROKE : Paint.Style.STROKE);
        mForegroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mForegroundPaint.setStyle(Paint.Style.STROKE);
        mForegroundPaint.setColor(foregroundColor);
        mForegroundPaint.setStrokeCap(Paint.Cap.ROUND);
        mForegroundPaint.setStrokeWidth(mStrokeWidth);

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(textColor);
        mTextPaint.setStrokeWidth(1f);
        mTextPaint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Thin.ttf"));

        mPercentPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mPercentPaint.setStyle(Paint.Style.FILL);
        mPercentPaint.setStrokeWidth(1f);
        mPercentPaint.setTextSize(mTextSize / 3);
        mPercentPaint.setColor(percentColor);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int defaultWidth = 100;
        int defaultHeight = 100;
        int widthSpec = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpec = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSpec;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(widthSpec, defaultWidth);
        } else {
            width = defaultWidth;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSpec;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(heightSpec, defaultHeight);
        } else {
            height = defaultHeight;
        }
        int size = Math.min(width, height);
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int middleStroke = (int) (mBackgroundPaint.getStrokeWidth() / 2);
        rectF.set(middleStroke, middleStroke, getWidth() - middleStroke, getHeight() - middleStroke);
        canvas.drawOval(rectF, mBackgroundPaint);
        float angle = 360 * mProgress / mMax;
        float startAngle = -90;
        canvas.drawArc(rectF, startAngle, angle, false, mForegroundPaint);
        if (showText) {
            drawText(canvas, String.valueOf(mProgress));
        }
    }

    Rect r = new Rect();

    private void drawText(Canvas canvas, String text) {

        canvas.getClipBounds(r);
        int cHeight = r.height();
        int cWidth = r.width();
        mTextPaint.getTextBounds(text, 0, text.length(), r);

        float x = cWidth / 2f - r.width() / 2f - r.left;
        int textHeight = r.height();
        float y = cHeight / 2f + textHeight / 2f - r.bottom;
        canvas.drawText(text, x, y, mTextPaint);
        mPercentPaint.getTextBounds(PERCENT, 0, PERCENT.length(), r);
        x = cWidth / 2f - r.width() / 2f - r.left;
        y = cHeight / 2f + textHeight / 2 + convertDpToPixel(20,getContext());
        canvas.drawText(PERCENT, x, y, mPercentPaint);
    }

    public int getForegroundColor() {
        return foregroundColor;
    }

    public void setForegroundColor(int foregroundColor) {
        if (this.foregroundColor == foregroundColor) return;
        this.foregroundColor = foregroundColor;
        invalidate();
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        if (backgroundColor == this.backgroundColor) return;
        this.backgroundColor = backgroundColor;
        invalidate();
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        if (this.textColor == textColor) return;
        this.textColor = textColor;
        invalidate();
    }

    public int getPercentColor() {
        return percentColor;
    }

    public void setPercentColor(int percentColor) {
        if (this.percentColor == percentColor) return;
        this.percentColor = percentColor;
        invalidate();
    }

    public float getStrokeWidth() {
        return mStrokeWidth;
    }

    public void setStrokeWidth(float mStrokeWidth) {
        if (this.mStrokeWidth == mStrokeWidth) return;
        this.mStrokeWidth = mStrokeWidth;
        invalidate();
    }

    public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(float mTextSize) {
        if (this.mTextSize == mTextSize) return;
        this.mTextSize = mTextSize;
        invalidate();
    }

    public int getProgress() {
        return mProgress;
    }

    public void setProgress(int mProgress) {
        if (this.mProgress == mProgress) return;
        this.mProgress = mProgress;
        invalidate();
    }

    public int getMax() {
        return mMax;
    }

    public void setMax(int mMax) {
        if (this.mMax == mMax) return;
        this.mMax = mMax;
        invalidate();
    }
}
