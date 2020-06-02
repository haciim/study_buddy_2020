package studyBuddy;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.studdybuddy.R;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class PrimaryColorPicker {

    public static final long HOUR_DURATION = 60 * 60 * 1000;
    public static final long DAY_DURATION = HOUR_DURATION * 24;


    public static int getDayColorInt(Context ctx) {
        int curHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        Log.e("test2", String.valueOf(curHour));
        if (curHour < 5) {
            return ContextCompat.getColor(ctx, R.color.color_evening);
        } else if (curHour < 10) {
            return ContextCompat.getColor(ctx, R.color.color_morning);
        } else if (curHour < 14) {
            return ContextCompat.getColor(ctx, R.color.color_midday);
        } else if (curHour < 17) {
            return ContextCompat.getColor(ctx, R.color.color_afternoon);
        } else if (curHour < 21) {
            return ContextCompat.getColor(ctx, R.color.color_sunset);
        } else {
            return ContextCompat.getColor(ctx, R.color.color_evening);
        }
    }

    public static ColorMatrixColorFilter getDayColorMatrixFilter(Context ctx) {
        int color = getDayColorInt(ctx);

        // https://stackoverflow.com/questions/11376516/change-drawable-color-programmatically/47299270
        float r = Color.red(color) / 255f;
        float g = Color.green(color) / 255f;
        float b = Color.blue(color) / 255f;

        ColorMatrix result = new ColorMatrix();

        result.setScale(r, g, b, 1);
        return new ColorMatrixColorFilter(result);
    }
}
