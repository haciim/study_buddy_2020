package studyBuddy.timemanagement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.example.studdybuddy.R;

import java.time.Duration;
import java.util.Arrays;

public class TimeSelectView extends LinearLayout {

    private SliderView duration;
    private SliderView strategy;
    public static String[] time_durations = {"∞", "5", "10", "20", "30", "40", "50", "60"};
    public static String[] time_durations_pomodoro = {"30", "60", "90"};
    private static String[] strategies = {"Standard", "Pomodoro"};

    private float oldY;

    public TimeSelectView(Context ctx) {
        super(ctx);
        setupSliders(ctx);

    }

    public TimeSelectView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        setupSliders(ctx);
    }

    private void setupSliders(Context ctx) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View select = inflater.inflate(R.layout.time_select_view, this, false);
        addView(select);

        duration = findViewById(R.id.minute_counter);
        duration.setStringEntries(Arrays.asList(time_durations));
        duration.setTextHeight(64.0f);
        duration.setTextIntervalDistance(96);
        duration.setOnTouchListener(new SliderTouchListener());
        duration.setSliderName("session length");

        strategy = findViewById(R.id.strategy_selector);
        strategy.setStringEntries(Arrays.asList(strategies));
        strategy.setTextHeight(64.0f);
        strategy.setTextIntervalDistance(96);
        strategy.setOnTouchListener(new StrategySliderTouchListener(duration));
        strategy.setSliderName("session type");
    }

    /**
     * @return the number of minutes long our session is.
     */
    public int getDuration() {
        String time = duration.getCurrentEntry();
        if (time.equals("∞")) {
            return 0;
        }

        return Integer.parseInt(time);

    }

    public Strategy getStrategy() {
        if (strategy.getCurrentEntry().equals("Standard")) {
            return null;
        } else {
            // should be ok
            return new PomodoroStrategy(getDuration() * 60 * 1000);
        }
    }

}