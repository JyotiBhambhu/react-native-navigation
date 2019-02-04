package com.reactnativenavigation.views.bottomTabs;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

class BottomBarNotchFill extends Drawable {
    private Path mPath;
    private Path mUpperPath;
    private Paint mPaint;
    private Paint mStrokePaint;
    private int CURVE_CIRCLE_RADIUS;

    public Point mFirstCurveStartPoint = new Point();
    public Point mFirstCurveEndPoint = new Point();
    public Point mFirstCurveControlPoint2 = new Point();
    public Point mFirstCurveControlPoint1 = new Point();

    public Point mSecondCurveStartPoint = new Point();
    public Point mSecondCurveEndPoint = new Point();
    public Point mSecondCurveControlPoint1 = new Point();
    public Point mSecondCurveControlPoint2 = new Point();
    public int mNavigationBarWidth;
    public int mNavigationBarHeight;

    public BottomBarNotchFill(int color, int notchRadius) {
        CURVE_CIRCLE_RADIUS = notchRadius;
        mPath = new Path();
        mUpperPath = new Path();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(color);

        mStrokePaint = new Paint();
        mStrokePaint.setStrokeWidth(3);
        mStrokePaint.setColor(Color.LTGRAY);
        mStrokePaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        mNavigationBarWidth = getBounds().width();
        mNavigationBarHeight = getBounds().height();
        mFirstCurveStartPoint.set((mNavigationBarWidth / 2) - (CURVE_CIRCLE_RADIUS * 2) - (10), 0);
        mFirstCurveEndPoint.set(mNavigationBarWidth / 2, 2*CURVE_CIRCLE_RADIUS + (CURVE_CIRCLE_RADIUS / 4));
        mSecondCurveStartPoint = mFirstCurveEndPoint;
        mSecondCurveEndPoint.set((mNavigationBarWidth / 2) + (CURVE_CIRCLE_RADIUS * 2) + (CURVE_CIRCLE_RADIUS / 3), 0);

        mFirstCurveControlPoint2.set(mFirstCurveEndPoint.x - (CURVE_CIRCLE_RADIUS * 2), mFirstCurveEndPoint.y);

        mSecondCurveControlPoint1.set(mSecondCurveStartPoint.x + (CURVE_CIRCLE_RADIUS * 2), mSecondCurveStartPoint.y);

        mPath.reset();
        mUpperPath.reset();
        mUpperPath.moveTo(0, 0);
        mUpperPath.lineTo((mNavigationBarWidth / 2) - (CURVE_CIRCLE_RADIUS), 0);
        mUpperPath.addArc(new RectF((mNavigationBarWidth / 2) - CURVE_CIRCLE_RADIUS, -CURVE_CIRCLE_RADIUS,
                        (mNavigationBarWidth / 2) + CURVE_CIRCLE_RADIUS, CURVE_CIRCLE_RADIUS),
                -180, -180);

        mUpperPath.lineTo(mNavigationBarWidth, 0);

        mPath.addPath(mUpperPath);
        mPath.lineTo(mNavigationBarWidth, mNavigationBarHeight);
        mPath.lineTo(0, mNavigationBarHeight);
        mPath.lineTo(0, 0);
        mPath.close();



        canvas.drawPath(mPath, mPaint);
        canvas.drawPath(mUpperPath, mStrokePaint);
    }

    @Override
    public void setAlpha(int i) {
        mPaint.setAlpha(i);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }
}

class BottomBarNotchUpperStroke extends Drawable {
    private Path mUpperPath;
    private Paint mStrokePaint;
    private int CURVE_CIRCLE_RADIUS;

    private int mNavigationBarWidth;

    public BottomBarNotchUpperStroke(int color, int notchRadius) {
        CURVE_CIRCLE_RADIUS = notchRadius;
        mUpperPath = new Path();

        mStrokePaint = new Paint();
        mStrokePaint.setStrokeWidth(3);
        mStrokePaint.setColor(Color.LTGRAY);
        mStrokePaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        mNavigationBarWidth = getBounds().width();
        mUpperPath.reset();
        mUpperPath.moveTo(0, 0);
        mUpperPath.lineTo((mNavigationBarWidth / 2) - (CURVE_CIRCLE_RADIUS), 0);
        mUpperPath.addArc(new RectF((mNavigationBarWidth / 2) - CURVE_CIRCLE_RADIUS, -CURVE_CIRCLE_RADIUS,
                (mNavigationBarWidth / 2) + CURVE_CIRCLE_RADIUS, CURVE_CIRCLE_RADIUS),
                -180, -180);
        mUpperPath.lineTo(mNavigationBarWidth, 0);
        canvas.drawPath(mUpperPath, mStrokePaint);
    }

    @Override
    public void setAlpha(int i) {
        mStrokePaint.setAlpha(i);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mStrokePaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }
}

public class CurvedNotchCenter extends LayerDrawable {

    public CurvedNotchCenter(int color, int notchRadius) {
        super(new Drawable[]{new BottomBarNotchFill(color, notchRadius), new BottomBarNotchUpperStroke(color, notchRadius)});
    }
}