package studyBuddy.timemanagement;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.studdybuddy.R;

public class TimelineView extends LinearLayout {

    private int timelineRange;
    private static final double RANGE_DIVIDER = 365.0;       // as magic as it gets

    private View marker;                                     // time marker

    public TimelineView(Context ctx) {
        super(ctx);
        inflateLayouts(ctx);
        marker = findViewById(R.id.time_marker);
        timelineRange = -1;
    }

    public TimelineView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        inflateLayouts(ctx);
        marker = findViewById(R.id.time_marker);
        timelineRange = -1;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        timelineRange = (int)((getMeasuredWidth() / RANGE_DIVIDER) * 150);
        setPercentageCompletion(0.5);
        super.onDraw(canvas);
    }

    public void setPercentageCompletion(double percentageCompletion) {
        if (timelineRange > 0) {
            LinearLayoutCompat.LayoutParams param = (LinearLayoutCompat.LayoutParams)marker.getLayoutParams();
            param.setMarginStart((int)(((percentageCompletion * 2.0) - 1.0) * timelineRange));
            marker.setLayoutParams(param);
            postInvalidate();
        }
    }

    // https://stackoverflow.com/questions/3820401/how-to-load-an-xml-inside-a-view-in-android
    private void inflateLayouts(Context ctx) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View v = inflater.inflate(R.layout.session_timeline_view, this, false);
        this.addView(v);
    }
}
