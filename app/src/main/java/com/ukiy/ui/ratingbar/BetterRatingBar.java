package com.ukiy.ui.ratingbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableWrapper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.RatingBar;

/**
 * Created by ukiy on 15/12/22.
 */
public class BetterRatingBar extends RatingBar {

    private Drawable emptyDrawable;
    private Drawable fillDrawable;
    private int starSize;

    public BetterRatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BetterRatingBar);
        emptyDrawable = a.getDrawable(R.styleable.BetterRatingBar_emptyDrawable);
        fillDrawable = a.getDrawable(R.styleable.BetterRatingBar_fillDrawable);
        starSize = a.getDimensionPixelSize(R.styleable.BetterRatingBar_starSize, 20);
        if (emptyDrawable == null) {
            emptyDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_love_empty);
        }
        if (fillDrawable == null) {
            fillDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_love_fill);
        }

        updateDrawable();
        a.recycle();
    }

    public BetterRatingBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int width = starSize * getNumStars();
        final int height = starSize;
        setMeasuredDimension(ViewCompat.resolveSizeAndState(width, widthMeasureSpec, 0),
                ViewCompat.resolveSizeAndState(height, heightMeasureSpec, 0));

    }


    private Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;         //取drawable的颜色格式
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    private Drawable zoomDrawable(Drawable drawable, int w, int h) {
        Bitmap oldbmp = drawableToBitmap(drawable);
        Bitmap bitmapResized = Bitmap.createScaledBitmap(oldbmp, w, h, false);
        return new BitmapDrawable(getResources(), bitmapResized);
    }

    private Drawable tileify(Drawable drawable, boolean clip) {
        if (drawable instanceof DrawableWrapper) {
            Drawable inner = ((DrawableWrapper) drawable).getWrappedDrawable();
            if (inner != null) {
                inner = tileify(inner, clip);
                ((DrawableWrapper) drawable).setWrappedDrawable(inner);
            }
        } else if (drawable instanceof LayerDrawable) {
            LayerDrawable background = (LayerDrawable) drawable;
            final int N = background.getNumberOfLayers();
            Drawable[] outDrawables = new Drawable[N];

            for (int i = 0; i < N; i++) {
                int id = background.getId(i);
                outDrawables[i] = tileify(background.getDrawable(i),
                        (id == android.R.id.progress || id == android.R.id.secondaryProgress));
            }
            LayerDrawable newBg = new LayerDrawable(outDrawables);

            for (int i = 0; i < N; i++) {
                newBg.setId(i, background.getId(i));
            }

            return newBg;

        } else if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
            return (clip) ? new ClipDrawable(bitmapDrawable, Gravity.LEFT,
                    ClipDrawable.HORIZONTAL) : bitmapDrawable;
        }

        return drawable;
    }

    private void updateDrawable() {
        Drawable background = emptyDrawable;
        Drawable secondaryProgress = emptyDrawable;
        Drawable progress = fillDrawable;

        background = zoomDrawable(background, starSize, starSize);
        secondaryProgress = zoomDrawable(secondaryProgress, starSize, starSize);
        progress = zoomDrawable(progress, starSize, starSize);
        LayerDrawable ld = new LayerDrawable(new Drawable[]{background, secondaryProgress, progress});
        ld.setId(0, android.R.id.background);
        ld.setId(1, android.R.id.secondaryProgress);
        ld.setId(2, android.R.id.progress);
//        setProgressDrawableTiled(ld);
        setProgressDrawable(tileify(ld, false));
    }

    public Drawable getEmptyDrawable() {
        return emptyDrawable;
    }

    public void setEmptyDrawable(Drawable emptyDrawable) {
        this.emptyDrawable = emptyDrawable;
        updateDrawable();
    }

    public Drawable getFillDrawable() {
        return fillDrawable;
    }

    public void setFillDrawable(Drawable fillDrawable) {
        this.fillDrawable = fillDrawable;
        updateDrawable();
    }

    public int getStarSize() {
        return starSize;
    }

    public void setStarSize(int starSize) {
        this.starSize = starSize;
        updateDrawable();
    }
}
