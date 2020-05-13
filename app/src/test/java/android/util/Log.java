package android.util;

// trick to create log class without mockito.
// https://stackoverflow.com/questions/36787449/how-to-mock-method-e-in-log
// wish i was using this for handler lol

public class Log {
    public static int d(String tag, String msg) {
        System.out.println("DEBUG: " + tag + ": " + msg);
        return 0;
    }
}