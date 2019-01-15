package com.reactnativenavigation.views.bottomTabs;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class CurvedNotchCenter extends Drawable {
    private Path mPath;
    private Paint mPaint;
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

    public CurvedNotchCenter(int color, int notchRadius) {
        CURVE_CIRCLE_RADIUS = notchRadius;
        mPath = new Path();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(color);
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
        mPath.moveTo(0, 0);
        mPath.lineTo(mFirstCurveStartPoint.x, mFirstCurveStartPoint.y);

        mPath.cubicTo(mFirstCurveStartPoint.x, mFirstCurveStartPoint.y,
                mFirstCurveControlPoint2.x, mFirstCurveControlPoint2.y,
                mFirstCurveEndPoint.x, mFirstCurveEndPoint.y);

        mPath.cubicTo(mFirstCurveEndPoint.x, mFirstCurveEndPoint.y,
                mSecondCurveControlPoint1.x, mSecondCurveControlPoint1.y,
                mSecondCurveEndPoint.x, mSecondCurveEndPoint.y);

        mPath.lineTo(mNavigationBarWidth, 0);
        mPath.lineTo(mNavigationBarWidth, mNavigationBarHeight);
        mPath.lineTo(0, mNavigationBarHeight);

        mPath.close();

        canvas.drawPath(mPath, mPaint);

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