package studyBuddy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SessionUpdateReceiver extends BroadcastReceiver {

    private Session session;

    /**
     * Associates the current broadcast receiver with a particular session.
     * @param session - The session which is currently running.
     */
    public void setSession(Session session) {
        this.session = session;
    }

    @Override
    public void onReceive(Context ctx, Intent intent) {
        // call self in a minute
    }
}
