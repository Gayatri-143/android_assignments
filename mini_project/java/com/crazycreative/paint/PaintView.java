package com.crazycreative.paint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PaintView extends View {

    private int currentColor = Color.BLACK;
    private int strokeWidth = 12;
    private boolean eraserEnabled = false;
    private ToolMode currentToolMode = ToolMode.BRUSH;
    private boolean isNeon = false;
    private boolean isDashed = false;
    private final List<DrawingAction> actions = new ArrayList<>();
    private final List<DrawingAction> redoActions = new ArrayList<>();
    private String currentEmoji = "😊";
    private DrawingAction activeAction;

    public PaintView(Context context) {
        super(context);
    }

    public PaintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PaintView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);

        for (DrawingAction action : actions) {
            renderAction(canvas, action);
        }

        if (activeAction != null) {
            renderAction(canvas, activeAction);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                activeAction = createAction(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                updateActiveAction(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                updateActiveAction(touchX, touchY);
                commitActiveAction();
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

    public void setBrushColor(@ColorInt int color) {
        currentColor = color;
        eraserEnabled = false;
        if (currentToolMode == ToolMode.ERASER) {
            currentToolMode = ToolMode.BRUSH;
        }
    }

    public int getCurrentColor() {
        return currentColor;
    }

    public void setStrokeWidth(int width) {
        strokeWidth = width;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void enableEraser() {
        eraserEnabled = true;
        currentToolMode = ToolMode.ERASER;
    }

    public void disableEraser() {
        if (currentToolMode == ToolMode.ERASER) {
            currentToolMode = ToolMode.BRUSH;
        }
        eraserEnabled = false;
    }

    public void clearCanvas() {
        actions.clear();
        redoActions.clear();
        activeAction = null;
        invalidate();
    }

    public void undo() {
        if (!actions.isEmpty()) {
            redoActions.add(actions.remove(actions.size() - 1));
            invalidate();
        }
    }

    public void redo() {
        if (!redoActions.isEmpty()) {
            actions.add(redoActions.remove(redoActions.size() - 1));
            invalidate();
        }
    }

    public void setToolMode(ToolMode toolMode) {
        currentToolMode = toolMode;
        eraserEnabled = toolMode == ToolMode.ERASER;
        // Reset effects when switching to shapes or eraser
        if (toolMode != ToolMode.BRUSH) {
            isNeon = false;
            isDashed = false;
        }
        invalidate();
    }

    public void setNeon(boolean enabled) {
        this.isNeon = enabled;
        if (enabled) {
            this.currentToolMode = ToolMode.BRUSH;
            this.isDashed = false;
        }
        invalidate();
    }

    public void setDashed(boolean enabled) {
        this.isDashed = enabled;
        if (enabled) {
            this.currentToolMode = ToolMode.BRUSH;
            this.isNeon = false;
        }
        invalidate();
    }

    public ToolMode getToolMode() {
        return currentToolMode;
    }

    public void setCurrentEmoji(String emoji) {
        this.currentEmoji = emoji;
        this.currentToolMode = ToolMode.EMOJI;
        invalidate();
    }

    public Bitmap exportBitmap() {
        if (getWidth() <= 0 || getHeight() <= 0) {
            return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        }
        Bitmap exportedBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas exportCanvas = new Canvas(exportedBitmap);
        draw(exportCanvas);
        return exportedBitmap;
    }

    private DrawingAction createAction(float startX, float startY) {
        DrawingAction action = new DrawingAction();
        action.toolMode = currentToolMode;
        action.color = eraserEnabled ? Color.WHITE : currentColor;
        action.strokeWidth = strokeWidth;
        action.startX = startX;
        action.startY = startY;
        action.endX = startX;
        action.endY = startY;
        action.isNeon = isNeon;
        action.isDashed = isDashed;
        if (currentToolMode == ToolMode.BRUSH || currentToolMode == ToolMode.ERASER) {
            action.path = new Path();
            action.path.moveTo(startX, startY);
        } else if (currentToolMode == ToolMode.EMOJI) {
            action.emoji = currentEmoji;
        }
        return action;
    }

    private void updateActiveAction(float x, float y) {
        if (activeAction == null) {
            return;
        }
        activeAction.endX = x;
        activeAction.endY = y;
        if (activeAction.path != null) {
            activeAction.path.lineTo(x, y);
        }
    }

    private void commitActiveAction() {
        if (activeAction == null) {
            return;
        }
        if (isMeaningfulAction(activeAction)) {
            actions.add(activeAction);
            redoActions.clear();
        }
        activeAction = null;
    }

    private boolean isMeaningfulAction(DrawingAction action) {
        if (action.path != null) {
            return true;
        }
        return action.startX != action.endX || action.startY != action.endY;
    }

    private void renderAction(Canvas canvas, DrawingAction action) {
        Paint paint = createPaint(action.color, action.strokeWidth);
        
        if (action.isNeon) {
            paint.setShadowLayer(action.strokeWidth, 0, 0, action.color);
            setLayerType(LAYER_TYPE_SOFTWARE, paint); // Required for shadow layer
        }
        
        if (action.isDashed) {
            paint.setPathEffect(new android.graphics.DashPathEffect(new float[]{action.strokeWidth * 2, action.strokeWidth}, 0));
        }

        switch (action.toolMode) {
            case BRUSH:
            case ERASER:
                if (action.path != null) {
                    canvas.drawPath(action.path, paint);
                }
                break;
            case LINE:
                canvas.drawLine(action.startX, action.startY, action.endX, action.endY, paint);
                break;
            case RECTANGLE:
                canvas.drawRect(
                        Math.min(action.startX, action.endX),
                        Math.min(action.startY, action.endY),
                        Math.max(action.startX, action.endX),
                        Math.max(action.startY, action.endY),
                        paint
                );
                break;
            case OVAL:
                canvas.drawOval(
                        Math.min(action.startX, action.endX),
                        Math.min(action.startY, action.endY),
                        Math.max(action.startX, action.endX),
                        Math.max(action.startY, action.endY),
                        paint
                );
                break;
            case TRIANGLE:
                drawTriangle(canvas, action, paint);
                break;
            case STAR:
                drawStar(canvas, action, paint);
                break;
            case HEART:
                drawHeart(canvas, action, paint);
                break;
            case EMOJI:
                if (action.emoji != null) {
                    Paint textPaint = new Paint();
                    textPaint.setAntiAlias(true);
                    textPaint.setTextSize(action.strokeWidth * 4f); // Scale emoji with stroke width
                    textPaint.setTextAlign(Paint.Align.CENTER);
                    canvas.drawText(action.emoji, action.endX, action.endY, textPaint);
                }
                break;
        }
    }

    private void drawTriangle(Canvas canvas, DrawingAction action, Paint paint) {
        Path path = new Path();
        path.moveTo((action.startX + action.endX) / 2, action.startY);
        path.lineTo(action.startX, action.endY);
        path.lineTo(action.endX, action.endY);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawStar(Canvas canvas, DrawingAction action, Paint paint) {
        float cx = (action.startX + action.endX) / 2;
        float cy = (action.startY + action.endY) / 2;
        float radius = Math.min(Math.abs(action.startX - action.endX), Math.abs(action.startY - action.endY)) / 2;
        Path path = new Path();
        double angle = Math.PI / 5;
        for (int i = 0; i < 10; i++) {
            float r = (i % 2 == 0) ? radius : radius / 2;
            float x = (float) (cx + r * Math.sin(i * angle));
            float y = (float) (cy - r * Math.cos(i * angle));
            if (i == 0) path.moveTo(x, y);
            else path.lineTo(x, y);
        }
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawHeart(Canvas canvas, DrawingAction action, Paint paint) {
        float left = Math.min(action.startX, action.endX);
        float top = Math.min(action.startY, action.endY);
        float right = Math.max(action.startX, action.endX);
        float bottom = Math.max(action.startY, action.endY);
        float width = right - left;
        float height = bottom - top;
        Path path = new Path();
        path.moveTo(left + width / 2, top + height / 4);
        path.cubicTo(left + width / 4, top, left, top + height / 4, left, top + height / 2);
        path.cubicTo(left, top + 3 * height / 4, left + width / 2, bottom, left + width / 2, bottom);
        path.cubicTo(left + width / 2, bottom, right, top + 3 * height / 4, right, top + height / 2);
        path.cubicTo(right, top + height / 4, right - width / 4, top, left + width / 2, top + height / 4);
        canvas.drawPath(path, paint);
    }

    private Paint createPaint(@ColorInt int color, int width) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(width);
        return paint;
    }

    public enum ToolMode {
        BRUSH,
        ERASER,
        LINE,
        RECTANGLE,
        OVAL,
        TRIANGLE,
        STAR,
        HEART,
        EMOJI
    }

    private static class DrawingAction {
        private ToolMode toolMode;
        private int color;
        private int strokeWidth;
        private float startX;
        private float startY;
        private float endX;
        private float endY;
        private Path path;
        private String emoji;
        private boolean isNeon;
        private boolean isDashed;
    }
}
