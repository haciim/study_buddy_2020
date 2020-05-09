package studyBuddy.timemanagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.studdybuddy.R;

public class TimelineView extends LinearLayout {

    private static final int TIMELINE_RANGE = 150;

    public TimelineView(Context ctx) {
        super(ctx);
        // https://stackoverflow.com/questions/3820401/how-to-load-an-xml-inside-a-view-in-android
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View v = inflater.inflate(R.layout.session_timeline_view, this, false);
        this.addView(v);
        View marker = findViewById(R.id.time_marker);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)marker.getLayoutParams();
        params.setMarginStart(-120);
        marker.setLayoutParams(params);
    }
}
