package studyBuddy.timemanagement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SessionBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context ctx, Intent intent) {
        // intent carries start time, notification, notifID, and end time
        // modify the notification to show the new time left
        // make it so that the notification reopens the activity

    }
}
