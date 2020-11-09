package com.example.justificanteemulsa.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SignatureView extends View {

  private static final float STROKE_WIDTH = 5f;
  private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;

  private Paint paint = new Paint();
  private Path path = new Path();

  /**
   * Optimiza el pintado invaliando la menor area posible
   */

  private float lastTouchX;
  private float lastTouchY;
  private final RectF dirtyRect = new RectF();

  public SignatureView(Context context, AttributeSet attrs) {
    super(context, attrs);

    paint.setAntiAlias(true);
    paint.setColor(Color.BLACK);
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeJoin(Paint.Join.ROUND);
    paint.setStrokeWidth(STROKE_WIDTH);

    this.setDrawingCacheEnabled(true);
  }

  /**
   * Elimina la firma
   */
  public void clear() {
    path.reset();
    invalidate(); // Redibuja la vista entera
  }

  @Override
  protected void onDraw(Canvas canvas) {
    canvas.drawPath(path, paint);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    float eventX = event.getX();
    float eventY = event.getY();

    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        path.moveTo(eventX, eventY);
        lastTouchX = eventX;
        lastTouchY = eventY;
        return true;
      case MotionEvent.ACTION_MOVE:
      case MotionEvent.ACTION_UP:
        /**
         * Cuando el Hardware recibe mas eventos de los que son enviados
         * el evento contendra una lista con los puntos saltados.
         */
        int historySize = event.getHistorySize();
        for (int i = 0; i < historySize; i++) {
          float historicalX = event.getHistoricalX(i);
          float historicalY = event.getHistoricalY(i);
          expandDirtyRect(historicalX, historicalY);
          path.lineTo(historicalX, historicalY);
        }

        // Despues de reproducir la historia, conecta la linea a todos los puntos
        path.lineTo(eventX, eventY);
        break;
      default:
        //Debug("Ignored Touch Event: " + event.toString());
        return false;
    }

    invalidate(
            (int) (dirtyRect.left - HALF_STROKE_WIDTH),
            (int) (dirtyRect.top - HALF_STROKE_WIDTH),
            (int) (dirtyRect.right + HALF_STROKE_WIDTH),
            (int) (dirtyRect.bottom + HALF_STROKE_WIDTH)
    );

    return true;
  }

  public boolean isEmpty() {
    return path.isEmpty();
  }

  /**
   * Llamado cuando se reproduce la historia para asegurar que la region sucia
   * incluye todos los puntos
   */
  private void expandDirtyRect(float historicalX, float historicalY) {
    if (historicalX < dirtyRect.left) {
      dirtyRect.left = historicalX;
    } else if (historicalX > dirtyRect.right) {
      dirtyRect.right = historicalX;
    }
    if (historicalY < dirtyRect.top) {
      dirtyRect.top = historicalY;
    } else if (historicalY > dirtyRect.bottom) {
      dirtyRect.bottom = historicalY;
    }
  }

  /**
   * Resetea la zona sucia donde el evento de mocion tiene lugar
   */
  private void resetDirtyRect(float eventX, float eventY) {
    dirtyRect.left = Math.min(lastTouchX, eventX);
    dirtyRect.right = Math.max(lastTouchX, eventX);
    dirtyRect.top = Math.min(lastTouchY, eventY);
    dirtyRect.bottom = Math.max(lastTouchY, eventY);
  }

  public Bitmap GetSignatureBitmap() {
    Bitmap bitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    this.draw(canvas);
    return bitmap;
  }
}
