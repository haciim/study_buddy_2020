package studyBuddy.time_management;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.studdybuddy.R;

import java.util.List;

import studyBuddy.main_activity.MainActivity;
import studyBuddy.session_activity.Session;
import studyBuddy.session_activity.SessionActivity;

public class SessionBroadcastReceiver extends BroadcastReceiver {

    private static long EPS_INTERVAL = 1000;
    public static String DELETE_INTENT = "deleteIntent";
    public static String NOTIFICATION_CHANNEL = "studySession";
    static public String NOTIFICATION_ID = "notificationID";
    static public String SESSION_END = "sessionEnd";
    static public String SESSION_START = "sessionStart";

    // handle reopening
    static public String REOPEN_SESSION = "reopenSession";

    /**
     * Create a notification builder based on build version.
     * @param ctx - The application context.
     * @return - A new notification builder.
     */
    private Notification.Builder getBuilder(Context ctx) {
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= 26) {
            builder = new Notification.Builder(ctx, NOTIFICATION_CHANNEL);
        } else {
            builder = new Notification.Builder(ctx);
        }

        return builder;
    }

    /**
     * On receiving the desired intent, creates a new notification representing the new number of
     * seconds remaining, and sets up an alarm to ping itself in a minute.
     * @param ctx - The current context.
     * @param intent - The intent received.
     */
    @Override
    public void onReceive(Context ctx, Intent intent) {
        // if intent dismissed: clear
        // clear alarm in
        intent.getAction();

        // get alarm manager
        AlarmManager alarmMGR = (AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE);
        assert alarmMGR != null;

        // get information from intent
        int notificationID = intent.getIntExtra(NOTIFICATION_ID, 0);
        long sessionEndTime = intent.getLongExtra(SESSION_END, System.currentTimeMillis());
        long sessionStartTime = intent.getLongExtra(SESSION_START, System.currentTimeMillis());
        String sessionName = intent.getStringExtra(SessionActivity.SESSION_NAME_KEY);
        Strategy strategy = StrategyFactory.getStrategy(SessionType.POMODORO, intent.getLongExtra(SessionActivity.SESSION_STRATEGY_KEY, -1));
        long duration = sessionEndTime - System.currentTimeMillis();

        // set up the intent which will trigger the broadcastreceiver to update in a minute
        Intent nextBroadcastIntent = new Intent(ctx, SessionBroadcastReceiver.class);
        nextBroadcastIntent.putExtra(NOTIFICATION_ID, notificationID);
        nextBroadcastIntent.putExtra(SESSION_END, sessionEndTime);
        nextBroadcastIntent.putExtra(SESSION_START, sessionStartTime);
        nextBroadcastIntent.putExtra(SessionActivity.SESSION_NAME_KEY, sessionName);
        if (strategy != null) {
            nextBroadcastIntent.putExtra(SessionActivity.SESSION_STRATEGY_KEY, strategy.getDuration());
        }

        PendingIntent broadIntent = PendingIntent.getBroadcast(ctx, SessionActivity.INTENT_ID, nextBroadcastIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // set up the intent which triggers our session activity again
        Intent activityIntent = new Intent(ctx, MainActivity.class);
        activityIntent.putExtra(DELETE_INTENT, broadIntent);
        activityIntent.putExtra(REOPEN_SESSION, true);
        activityIntent.putExtra(SESSION_END, sessionEndTime);
        activityIntent.putExtra(SESSION_START, sessionStartTime);
        activityIntent.putExtra(SessionActivity.SESSION_NAME_KEY, sessionName);
        if (strategy != null) {
            nextBroadcastIntent.putExtra(SessionActivity.SESSION_STRATEGY_KEY, strategy.getDuration());
            activityIntent.putExtra(SessionActivity.SESSION_STRATEGY_KEY, strategy.getDuration());
        }
        NotificationManager mgr = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        assert mgr != null;

        // build a notification based on our incoming intent values
        Notification.Builder builder = getBuilder(ctx);
        builder.setContentIntent(PendingIntent.getActivity(ctx, SessionActivity.INTENT_ID, activityIntent, PendingIntent.FLAG_CANCEL_CURRENT));
        builder.setSmallIcon(R.drawable.cattronsuperscale);
        builder.setContentTitle(ctx.getResources().getString(R.string.app_name));
        builder.setAutoCancel(true);

        long nextNotificationDuration = (long)(60000 * Math.ceil(duration / 60000.0) - 60000);
        // difference between current "time remaining" and when we send the notif
        long timeToNextUpdate = (duration - nextNotificationDuration);
        if (strategy == null) {
            // standard session
            if (sessionStartTime >= sessionEndTime) {
                String timeBuilder = "You are " + (int) Math.floor(-duration / 60000.0) + " minute(s) into your session!";
                builder.setContentText(timeBuilder);
                builder.setOngoing(true);
                alarmMGR.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeToNextUpdate, broadIntent);
            } else if (duration > EPS_INTERVAL) {
                // session is incomplete -- remind the user that it is in progress
                // duration rounded down -- time at which we will send the next notification

                String timeBuilder = (int) Math.ceil(duration / 60000.0) +
                        " minute(s) remaining!";
                builder.setContentText(timeBuilder);
                builder.setOngoing(true);
                alarmMGR.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeToNextUpdate, broadIntent);
            } else {
                // session is over -- let the user know
                builder.setOngoing(false);
                builder.setContentText("Session complete!");
                // let them know that we're done
            }
        } else {
            // pomodoro session
            if (duration > EPS_INTERVAL) {
                // figure out whether we're in a break or not
                long timePassed = (sessionEndTime - sessionStartTime) - duration;
                List<StudyInterval> intervals = strategy.getTimeTable();
                String message = "Default message :)";
                for (StudyInterval interval : intervals) {
                    timePassed -= (interval.end - interval.start);
                    if (timePassed < 0) {
                        // build the notification
                        // break
                        int ceil = (int)Math.ceil((-timePassed) / 60000.0);
                        if (interval.isActive) {
                            message = "Only " + ceil + " minute(s) left until break time!";
                        } else {
                            message = "Take " + ceil + " more minute(s) to reflect.";
                        }

                        break;
                    }
                }

                builder.setContentText(message);
                builder.setOngoing(true);
                alarmMGR.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeToNextUpdate, broadIntent);
            } else {
                builder.setOngoing(false);
                builder.setContentText("Session complete!");
            }
        }

        mgr.notify(notificationID, builder.build());
    }
}
