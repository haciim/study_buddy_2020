package studyBuddy.time_management;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.example.studdybuddy.R;

public class EndSessionView extends LinearLayout {

    final Path clippingPath = new Path();
    private int[] buttonCoords;

    public EndSessionView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        buttonCoords = new int[2];
        inflateLayout(ctx);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (changed) {
            clippingPath.reset();
            View button = findViewById(R.id.fob);

            button.getLocationOnScreen(buttonCoords);

            int buttonWidth = right - buttonCoords[0];
            System.out.println("button width: " + buttonWidth);
            clippingPath.addRect(0, 0, (right - left) - (buttonWidth / 2.0f), (bottom - top), Path.Direction.CW);
            // add circle for button
            clippingPath.addCircle((buttonCoords[0] - left) + (buttonWidth / 2.0f), (buttonWidth / 2.0f), buttonWidth / 2.0f, Path.Direction.CW);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.clipPath(clippingPath);
        super.dispatchDraw(canvas);
    }

    /**
     * Inflates the EndSessionView.
     * @param ctx - Application context.
     */
    public void inflateLayout(Context ctx) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View v = inflater.inflate(R.layout.end_session_view, this, false);
        addView(v);
    }
}
