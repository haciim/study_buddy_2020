package studyBuddy.session_activity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.studdybuddy.R;

import java.util.Locale;

public class PiePercentageView extends View implements View.OnTouchListener {

    private int percentage = 0;
    private Paint circleBorder;
    private Paint circleBackground;
    private Paint pieFill;
    private Paint pieHighlight;

    private TextView percentageText;

    private static final float PIE_SWEEP_INCREMENT = 36.0f;
    private static final float PIE_SWEEP_INCREMENT_RADS = (float)(Math.PI / 5);

    public PiePercentageView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        circleBorder = new Paint();
        circleBackground = new Paint();
        pieFill = new Paint();
        pieHighlight = new Paint();
        circleBorder.setColor(ContextCompat.getColor(ctx, R.color.pie_border));
        circleBackground.setColor(ContextCompat.getColor(ctx, R.color.pie_background));
        pieFill.setColor(ContextCompat.getColor(ctx, R.color.pie_fill));
        pieHighlight.setColor(ContextCompat.getColor(ctx, R.color.pie_highlight));
        setOnTouchListener(this);
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        int radius = Math.min(centerX, centerY);
        // draw the pie slices a bit out and a bit smaller
        canvas.drawCircle(centerX, centerY, radius, circleBackground);
        // 0 and 50

        // draw arcs
        canvas.drawArc(centerX - radius, centerY - radius, centerX + radius, centerY + radius, -90, PIE_SWEEP_INCREMENT * percentage, true, pieFill);
        canvas.drawArc(centerX - radius, centerY - radius, centerX + radius, centerY + radius, PIE_SWEEP_INCREMENT * (percentage) - 90, PIE_SWEEP_INCREMENT, true, pieHighlight);

        for (int j = 0; j < 5; j++) {
            circleBorder.setAlpha(16);
            circleBorder.setStrokeWidth(12.0f / (j + 1));
            for (int i = 0; i < 5; i++) {
                // draw border lines
                float xFlip = (float)(radius * Math.sin((i * 36) / (180 / Math.PI)));
                float yFlip = (float)(radius * Math.cos((i * 36) / (180 / Math.PI)));
                canvas.drawLine(centerX + xFlip, centerY + yFlip, centerX - xFlip, centerY - yFlip, circleBorder);
            }
        }
    }

    public void setTextPercentageView(TextView view) {
        this.percentageText = view;
    }

    public double getPercentage() {
        return (percentage + 1) / 10.0;
    }

    public boolean onTouch(View v, MotionEvent e) {
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        int newPercentage = (int)((-Math.atan2(e.getX() - cx, e.getY() - cy) + Math.PI) / PIE_SWEEP_INCREMENT_RADS);
        if (newPercentage != percentage) {
            percentage = newPercentage;
            percentageText.setText(String.format(Locale.US, "%d%%", (percentage + 1) * 10));
            postInvalidate();
        }

        return true;
    }
}
