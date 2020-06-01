package studyBuddy.timemanagement;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.example.studdybuddy.R;

import java.util.Arrays;

public class TimeSelectView extends LinearLayout implements View.OnTouchListener {

    private SliderView duration;
    private static String[] time_durations = {"5", "10", "20", "30", "40", "50", "60"};

    private float oldY;

    public TimeSelectView(Context ctx) {
        super(ctx);

        // bad code. too bad :)
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View select = inflater.inflate(R.layout.time_select_view, this, false);
        addView(select);

        duration = findViewById(R.id.minute_counter);
        duration.setStringEntries(Arrays.asList(time_durations));
        duration.setTextHeight(64.0f);
        duration.setTextIntervalDistance(96);
        setOnTouchListener(this);
    }

    public TimeSelectView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View select = inflater.inflate(R.layout.time_select_view, this, false);
        addView(select);

        duration = findViewById(R.id.minute_counter);
        duration.setStringEntries(Arrays.asList(time_durations));
        duration.setTextHeight(64.0f);
        duration.setTextIntervalDistance(96);
        setOnTouchListener(this);
    }



    /**
     * @return the number of minutes long our session is.
     */
    public int getDuration() {
        String time = duration.getCurrentEntry();
        return Integer.parseInt(time);
    }

    public boolean onTouch(View v, MotionEvent event) {
        v.performClick();
        int eventType = event.getAction();
        if (eventType == MotionEvent.ACTION_DOWN) {
            oldY = event.getY();
            return true;
        } else if (eventType == MotionEvent.ACTION_MOVE) {
            float deltaY = event.getY() - oldY;
            oldY = event.getY();
            duration.setOffset(deltaY);
            return true;
        } else if (eventType == MotionEvent.ACTION_UP) {
            duration.roundIndex();
            return true;
        }
        return false;
    }

}