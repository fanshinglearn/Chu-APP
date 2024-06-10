package com.example.chuapp.Else;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import com.example.chuapp.Activity.BuildingInformationActivity;
import com.example.chuapp.Activity.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class FloorPlanImageView extends AppCompatImageView {
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;

    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;

    private static final float MIN_SCALE = 0.1f;
    private static final float MAX_SCALE = 0.4f;
    private static final float INITIAL_SCALE = 1.1f;

    public FloorPlanImageView(Context context) {
        super(context);
        init();
    }

    public FloorPlanImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setScaleType(ScaleType.MATRIX);

        setOnTouchListener(new OnTouchListener() {
            private float startX;
            private float startY;
            private boolean isClick;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        isClick = true;
                        savedMatrix.set(matrix);
                        start.set(event.getX(), event.getY());
                        mode = DRAG;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (Math.abs(startX - event.getX()) > 5 || Math.abs(startY - event.getY()) > 5) {
                            isClick = false;
                        }
                        if (mode == DRAG) {
                            matrix.set(savedMatrix);
                            float dx = event.getX() - start.x;
                            float dy = event.getY() - start.y;

                            // 檢查邊界
                            RectF imageRect = getImageRect(matrix);
                            if (imageRect.width() <= getWidth()) {
                                dx = 0;
                            } else {
                                if (imageRect.left + dx > 0) {
                                    dx = -imageRect.left;
                                } else if (imageRect.right + dx < getWidth()) {
                                    dx = getWidth() - imageRect.right;
                                }
                            }
                            if (imageRect.height() <= getHeight()) {
                                dy = 0;
                            } else {
                                if (imageRect.top + dy > 0) {
                                    dy = -imageRect.top;
                                } else if (imageRect.bottom + dy < getHeight()) {
                                    dy = getHeight() - imageRect.bottom;
                                }
                            }
                            matrix.postTranslate(dx, dy);
                        } else if (mode == ZOOM) {
                            // Zooming logic...
                        }
                        break;
                }

                setImageMatrix(matrix);
                return true;
            }
        });

        // 初始化縮放
        matrix.postScale(INITIAL_SCALE, INITIAL_SCALE);
        setImageMatrix(matrix);
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    private RectF getImageRect(Matrix matrix) {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return new RectF();
        }
        RectF rectF = new RectF(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        matrix.mapRect(rectF);
        return rectF;
    }

    private static class ClickableArea {
        String name;
        RectF rect;

        ClickableArea(String name, RectF rect) {
            this.name = name;
            this.rect = rect;
        }
    }
}
