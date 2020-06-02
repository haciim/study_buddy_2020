package studyBuddy.session_activity;

import android.app.Activity;
import android.view.View;

// Generalized this in case we need more of them?
public class DoneButtonListener implements View.OnClickListener{

    private Activity finishActivity;

    public DoneButtonListener(Activity a) {
        finishActivity = a;
    }

    @Override
    public void onClick(View v) {
        finishActivity.finish();
    }

}
