package vis.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import vision.fastfiletransfer.R;

/**
 * LoadingView 加载等待
 */
public class LoadingView extends View {

//    private String mExampleString; // TO DO: use a default from R.string...
//    private int mExampleColor = Color.RED; // TO DO: use a default from R.color...
//    private float mExampleDimension = 0; // TO DO: use a default from R.dimen...
//    private Drawable mExampleDrawable;

    //    private TextPaint mTextPaint;
//    private float mTextWidth;
//    private float mTextHeight;
    private Drawable mProgressImage;
    private Drawable mCenterIcon;
    private float mCenterIconDimension = 70;
    private float mProgressAngle = 0;
    private boolean isRunningProgress = false;
    private int mDuration = 1000;
    private Thread mThread;

    public LoadingView(Context context) {
        super(context);
        init(null, 0);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.loadingView, defStyle, 0);

//        mExampleString = a.getString(
//                R.styleable.loadingView_exampleString);
//        mExampleColor = a.getColor(
//                R.styleable.loadingView_exampleColor,
//                mExampleColor);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
//        mExampleDimension = a.getDimension(
//                R.styleable.loadingView_exampleDimension,
//                mExampleDimension);
//
//        if (a.hasValue(R.styleable.loadingView_exampleDrawable)) {
//            mExampleDrawable = a.getDrawable(
//                    R.styleable.loadingView_exampleDrawable);
//            mExampleDrawable.setCallback(this);
//        }

        //--------------------------------------------------------------------

        mCenterIconDimension = a.getDimension(
                R.styleable.loadingView_centerIconDimension,
                mCenterIconDimension);

        mDuration = a.getInt(
                R.styleable.loadingView_duration,
                mDuration);

        if (a.hasValue(R.styleable.loadingView_progressImage)) {
            mProgressImage = a.getDrawable(
                    R.styleable.loadingView_progressImage);
            if (mProgressImage != null) {
                mProgressImage.setCallback(this);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mProgressImage = getResources().getDrawable(R.mipmap.loading_circle_yidont_2, null);
            } else {
                mProgressImage = getResources().getDrawable(R.mipmap.loading_circle_yidont_2);
            }
        }

        if (a.hasValue(R.styleable.loadingView_centerIcon)) {
            mCenterIcon = a.getDrawable(
                    R.styleable.loadingView_centerIcon);
            if (mCenterIcon != null) {
                mCenterIcon.setCallback(this);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mCenterIcon = getResources().getDrawable(R.mipmap.ico_yidont, null);
            } else {
                mCenterIcon = getResources().getDrawable(R.mipmap.ico_yidont);
            }
        }

        a.recycle();

        // Set up a default TextPaint object
//        mTextPaint = new TextPaint();
//        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
//        mTextPaint.setTextAlign(Paint.Align.LEFT);

        // Update TextPaint and text measurements from attributes
//        invalidateTextPaintAndMeasurements();
    }

//    private void invalidateTextPaintAndMeasurements() {
//        mTextPaint.setTextSize(mExampleDimension);
//        mTextPaint.setColor(mExampleColor);
//        mTextWidth = mTextPaint.measureText(mExampleString);
//
//        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
//        mTextHeight = fontMetrics.bottom;
//    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int specWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int specHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        int specHeightSize;
        int specWidthSize;
        if (specHeightMode == MeasureSpec.AT_MOST) {
//            specHeightSize = MeasureSpec.getSize(heightMeasureSpec);
            specHeightSize = 70;
        } else {
            specHeightSize = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        }

        if (specWidthMode == MeasureSpec.AT_MOST) {
//            specWidthSize = MeasureSpec.getSize(widthMeasureSpec);
            specWidthSize = 70;
        } else {
            specWidthSize = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        }
        setMeasuredDimension(specWidthSize, specHeightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TO DO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        // Draw the text.
//        canvas.drawText(mExampleString,
//                paddingLeft + (contentWidth - mTextWidth) / 2,
//                paddingTop + (contentHeight + mTextHeight) / 2,
//                mTextPaint);

        // Draw the example drawable on top of the text.
//        if (mExampleDrawable != null) {
//            mExampleDrawable.setBounds(paddingLeft, paddingTop,
//                    paddingLeft + contentWidth, paddingTop + contentHeight);
//            mExampleDrawable.draw(canvas);
//        }


        if (mCenterIcon != null) {
            int hpWidth = getWidth() / 2;
            int hpHeight = getHeight() / 2;
            int hpCenterIconDimension = (((int) mCenterIconDimension) / 2);
            mCenterIcon.setBounds(hpWidth - hpCenterIconDimension, hpHeight - hpCenterIconDimension,
                    hpWidth + hpCenterIconDimension, hpHeight + hpCenterIconDimension);
            mCenterIcon.draw(canvas);
        }
        canvas.rotate(mProgressAngle, contentWidth / 2, contentHeight / 2);
        if (mProgressImage != null) {
            int content;
            if (contentHeight >= contentWidth) {
                content = contentWidth;
                paddingTop += (contentHeight - contentWidth) / 2;
            } else {
                content = contentHeight;
                paddingLeft += (contentWidth - contentHeight) / 2;
            }

            mProgressImage.setBounds(paddingLeft, paddingTop,
                    paddingLeft + content, paddingTop + content);
            mProgressImage.draw(canvas);
        }
        if (mThread == null) {
            //启动线程
            isRunningProgress = true;
            mThread = new Thread(new RunningProgress());
            mThread.start();
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == GONE || visibility == INVISIBLE) {
            //关闭线程
            isRunningProgress = false;
        }
    }

    class RunningProgress implements Runnable {

        @Override
        public void run() {
//            Log.d("", "Thread is start.");
            while (isRunningProgress) {
                mProgressAngle = (mProgressAngle + 1) % 360;
                postInvalidate();//View里面的方法, 让重画, 及重新调用onDraw方法. 因为y已经更新.
                try {
                    Thread.sleep(mDuration / 360);  //慢点更新
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mThread = null;
//            Log.d("", "Thread is end.");
        }
    }

    public void setProgressImage(Drawable progressImage) {
        this.mProgressImage = progressImage;
    }

    public void setCenterIcon(Drawable centerIcon) {
        this.mCenterIcon = centerIcon;
    }

    /**
     * Gets the example string attribute value.
     *
     * @return The example string attribute value.
     */
//    public String getExampleString() {
//        return mExampleString;
//    }

    /**
     * Sets the view's example string attribute value. In the example view, this string
     * is the text to draw.
     *
     * @param exampleString The example string attribute value to use.
     */
//    public void setExampleString(String exampleString) {
//        mExampleString = exampleString;
//        invalidateTextPaintAndMeasurements();
//    }

    /**
     * Gets the example color attribute value.
     *
     * @return The example color attribute value.
     */
//    public int getExampleColor() {
//        return mExampleColor;
//    }

    /**
     * Sets the view's example color attribute value. In the example view, this color
     * is the font color.
     *
     * @param exampleColor The example color attribute value to use.
     */
//    public void setExampleColor(int exampleColor) {
//        mExampleColor = exampleColor;
//        invalidateTextPaintAndMeasurements();
//    }

    /**
     * Gets the example dimension attribute value.
     *
     * @return The example dimension attribute value.
     */
//    public float getExampleDimension() {
//        return mExampleDimension;
//    }

    /**
     * Sets the view's example dimension attribute value. In the example view, this dimension
     * is the font size.
     *
     * @param exampleDimension The example dimension attribute value to use.
     */
//    public void setExampleDimension(float exampleDimension) {
//        mExampleDimension = exampleDimension;
//        invalidateTextPaintAndMeasurements();
//    }

    /**
     * Gets the example drawable attribute value.
     *
     * @return The example drawable attribute value.
     */
//    public Drawable getExampleDrawable() {
//        return mExampleDrawable;
//    }

    /**
     * Sets the view's example drawable attribute value. In the example view, this drawable is
     * drawn above the text.
     *
     * @param exampleDrawable The example drawable attribute value to use.
     */
//    public void setExampleDrawable(Drawable exampleDrawable) {
//        mExampleDrawable = exampleDrawable;
//    }
}
