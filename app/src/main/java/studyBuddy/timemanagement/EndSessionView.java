package studyBuddy.timemanagement;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.example.studdybuddy.R;

public class EndSessionView extends LinearLayout {

    final Path clippingPath = new Path();

    public EndSessionView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        inflateLayout(ctx);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        clippingPath.reset();
        View button = findViewById(R.id.fob);

        // fix this once it works
        @SuppressLint("DrawAllocation") int[] buttonCoords = new int[2];
        button.getLocationOnScreen(buttonCoords);

        int buttonWidth = right - buttonCoords[0];
        System.out.println("button width: " + buttonWidth);
        clippingPath.addRect(0, 0, (right - left) - (buttonWidth / 2.0f), (bottom - top), Path.Direction.CW);
        // add circle for button
        clippingPath.addCircle((buttonCoords[0] - left) + (buttonWidth / 2.0f), (buttonWidth / 2.0f), buttonWidth / 2.0f, Path.Direction.CW);
    }

    // todo: animate "endsession" text
    //  callback should be a bit larger
    //  end session only on second press
    //  hide again after like five seconds or so

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.clipPath(clippingPath);
        super.dispatchDraw(canvas);
//        Paint p = new Paint();
//        p.setColor(Color.BLUE);
//        canvas.drawPath(clippingPath, p);
    }

    public void inflateLayout(Context ctx) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View v = inflater.inflate(R.layout.end_session_view, this, false);
        addView(v);
    }
}
