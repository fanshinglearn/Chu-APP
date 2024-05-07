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

public class ZoomableImageView extends AppCompatImageView {
    private ArrayList<ClickableArea> clickableAreas = new ArrayList<>();
    private PointF lastTouchDown = new PointF();
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
    private static final float INITIAL_SCALE = 0.2f;

    public ZoomableImageView(Context context) {
        super(context);
        init();
    }

    public ZoomableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setScaleType(ScaleType.MATRIX);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference buildingsRef = db.collection("buildings");
        buildingsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String buildingId = document.getId();
                        // 檢查 coordinates 映射是否存在並且有效
                        Object coordinatesObject = document.getData().get("coordinates");
                        if (coordinatesObject instanceof Map) {
                            Map<String, Object> coordinatesMap = (Map<String, Object>) coordinatesObject;
                            if (coordinatesMap.containsKey("left") && coordinatesMap.containsKey("top") && coordinatesMap.containsKey("right") && coordinatesMap.containsKey("bottom")) {
                                try {
                                    double left = ((Number) coordinatesMap.get("left")).doubleValue();
                                    double top = ((Number) coordinatesMap.get("top")).doubleValue();
                                    double right = ((Number) coordinatesMap.get("right")).doubleValue();
                                    double bottom = ((Number) coordinatesMap.get("bottom")).doubleValue();
                                    RectF rect = new RectF((float) left, (float) top, (float) right, (float) bottom);
                                    clickableAreas.add(new ClickableArea(buildingId, rect));
                                } catch (ClassCastException e) {
                                    Log.e(TAG, "Error casting coordinates to Double: " + e.getMessage());
                                }
                            } else {
                                Log.d(TAG, "Building " + buildingId + " does not have valid coordinates. Skipping.");
                            }
                        } else {
                            Log.d(TAG, "Building " + buildingId + " does not have coordinates. Skipping.");
                        }
                    }
                    // 在這裡更新 UI 或執行其他操作
                    invalidate();
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

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

                    case MotionEvent.ACTION_UP:
                        if (isClick) {
                            // 點擊事件處理
                            float[] pts = {event.getX(), event.getY()};
                            Matrix inverse = new Matrix();
                            matrix.invert(inverse);
                            inverse.mapPoints(pts);
                            float x = pts[0];
                            float y = pts[1];
                            // 檢查點擊位置與各個可點擊區域的交集
                            for (ClickableArea clickableArea : clickableAreas) {
                                if (clickableArea.rect.contains(x, y)) {
                                    // 在點擊區域內，傳遞相應的值到 Activity
                                    Context context = getContext();
                                    Intent intent = new Intent(context, BuildingInformationActivity.class);
                                    intent.putExtra("buildingAbbreviation", clickableArea.name); // 傳遞可點擊區域的名稱
                                    context.startActivity(intent);
                                    return true; // 如果點擊區域內，返回 true，表示已處理點擊事件
                                }
                            }
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        oldDist = spacing(event);
                        if (oldDist > 10f) {
                            savedMatrix.set(matrix);
                            midPoint(mid, event);
                            mode = ZOOM;
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;
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

//    private void drawClickableAreas(Canvas canvas) {
//        Paint paint = new Paint();
//        paint.setColor(Color.RED);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeWidth(5);
//
//        for (ClickableArea clickableArea : clickableAreas) {
//            // 將原始的可點擊區域映射到當前的圖像上
//            RectF mappedArea = new RectF();
//            matrix.mapRect(mappedArea, clickableArea.rect);
//            canvas.drawRect(mappedArea, paint);
//        }
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        drawClickableAreas(canvas);
//    }

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
