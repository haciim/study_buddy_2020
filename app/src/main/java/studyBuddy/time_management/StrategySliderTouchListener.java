package studyBuddy.time_management;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;

import java.util.Arrays;

public class StrategySliderTouchListener extends SliderTouchListener {

    private SliderView duration;

    public StrategySliderTouchListener(SliderView duration) {
        this.duration = duration;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean touch = super.onTouch(v, event);
        // super method calls performclick
        SliderView strategy = (SliderView)v;
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (strategy.getCurrentEntry().equals("Pomodoro")) {
                duration.setStringEntries(Arrays.asList(TimeSelectView.time_durations_pomodoro));
            } else {
                duration.setStringEntries(Arrays.asList(TimeSelectView.time_durations));
            }
            duration.postInvalidate();
            return true;
        }

        return touch;
    }
}
