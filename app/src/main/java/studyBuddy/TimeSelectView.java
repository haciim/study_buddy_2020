package studyBuddy;

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
    private static String[] time_durations = {"10", "20", "30", "40", "50", "60"};

    private float oldY;

    TimeSelectView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View v = inflater.inflate(R.layout.time_select_view, this, false);
        addView(v);

        duration = findViewById(R.id.minute_counter);
        duration.setStringEntries(Arrays.asList(time_durations));
        duration.setTextHeight(32.0f);
        duration.setTextIntervalDistance(64);
        setOnTouchListener(this);
    }

    /**
     * @return the number of minutes long our session is.
     */
    public int getDuration() {
        String time = duration.getCurrentEntry();
        return Integer.parseInt(time);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int eventType = event.getAction();
        // switch vs if/else lol
        if (eventType == MotionEvent.ACTION_DOWN) {
            oldY = event.getY();
            return true;
        } else if (eventType == MotionEvent.ACTION_MOVE) {
            float deltaY = event.getY() - oldY;
            duration.setOffset(deltaY);
            return true;
        } else if (eventType == MotionEvent.ACTION_UP) {
            duration.roundIndex();
            return true;
        }

        return false;
    }

}
