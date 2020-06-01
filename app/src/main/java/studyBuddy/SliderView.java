package studyBuddy;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;

import com.example.studdybuddy.R;

import java.util.ArrayList;
import java.util.List;

public class SliderView extends LinearLayoutCompat {

    private List<String> entries;
    private float offsetValue;
    private int offsetHeight;

    private float textHeight;
    private int textIntervalDistance;

    private Paint textPaintPrimary = new Paint();
    private Paint textPaintBackground = new Paint();

    public SliderView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        entries = new ArrayList<>();
        setWillNotDraw(false);
        entries.add("hello");
        entries.add("the");
        entries.add("monkeybank");
        offsetHeight = 128;
        textHeight = 96.0f + 8;
        textIntervalDistance = (int)textHeight;
        textPaintPrimary.setColor(ContextCompat.getColor(ctx, R.color.black));
        textPaintPrimary.setTextSize(textHeight);
        textPaintPrimary.setTextAlign(Paint.Align.CENTER);
        textPaintBackground.setTextSize(textHeight * 0.9f);
        textPaintBackground.setColor(ContextCompat.getColor(ctx, R.color.gray_50_percent_alpha));
        textPaintBackground.setTextAlign(Paint.Align.CENTER);
    }

    /**
     * Return the string associated with the currently selected slider entry.
     * @return String associated with current entry
     */
    public String getCurrentEntry() {
        int index = Math.round(offsetValue / offsetHeight) & entries.size();
        return entries.get(index);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // use offsetValue to determine which trio of elements should be drawn
        // offset them based on a remainder
        Log.d(String.valueOf(offsetValue), "ttt");
        int centerIndex = Math.round(offsetValue);
        int offset = (int)(offsetHeight * (centerIndex - offsetValue + 0.5));
        centerIndex += entries.size();
        int centerPoint = getHeight() / 2 - (int)(textHeight / 2);
        canvas.drawText(entries.get((centerIndex - 2) % entries.size()), getWidth() / 2.0f, centerPoint - (textIntervalDistance) * 2 + offset, textPaintBackground);
        canvas.drawText(entries.get((centerIndex - 1) % entries.size()), getWidth() / 2.0f, centerPoint - (textIntervalDistance) + offset, textPaintBackground);
        canvas.drawText(entries.get(centerIndex % entries.size()), getWidth() / 2.0f, centerPoint + offset, textPaintPrimary);
        canvas.drawText(entries.get((centerIndex + 1) % entries.size()), getWidth() / 2.0f, centerPoint + (textIntervalDistance) + offset, textPaintBackground);
        canvas.drawText(entries.get((centerIndex + 2) % entries.size()), getWidth() / 2.0f, centerPoint + (textIntervalDistance) * 2 + offset, textPaintBackground);
        super.onDraw(canvas);

        // set the top margin on the top view?
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    /**
     * Determines the contents of the slider view.
     * @param newEntries - A list of our new string entries.
     */
    public void setStringEntries(List<String> newEntries) {
        entries = newEntries;
    }

    public void setTextHeight(float textHeight) {
        this.textHeight = textHeight;
        postInvalidate();
    }

    public void setTextIntervalDistance(int textIntervalDistance) {
        this.textIntervalDistance = textIntervalDistance;
    }

    /**
     * Modifies the offset of the slider view in response to (for instance) a touch event.
     * @param deltaY - The change in position along the y axis.
     */
    public void setOffset(float deltaY) {
        // figure out how the delta
        float deltaOffsetValue = deltaY / offsetHeight;
        Log.d("the", String.valueOf(deltaOffsetValue));
        offsetValue += deltaOffsetValue;
        postInvalidate();
    }

    public void roundIndex() {
        offsetValue = Math.round(offsetValue);
    }
}
