package studyBuddy.time_management;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class SliderTouchListener implements View.OnTouchListener {

    private float oldY;

    public boolean onTouch(View v, MotionEvent event) {
        Log.d("View selected", String.valueOf(v.getId()));
        SliderView slider = (SliderView)v;
        int eventType = event.getAction();
        if (eventType == MotionEvent.ACTION_DOWN) {
            oldY = event.getY();
            return true;
        } else if (eventType == MotionEvent.ACTION_MOVE) {
            float deltaY = event.getY() - oldY;
            oldY = event.getY();
            slider.setOffset(deltaY);
            return true;
        } else if (eventType == MotionEvent.ACTION_UP) {
            v.performClick();
            slider.roundIndex();
            return true;
        }
        return false;
    }
}
