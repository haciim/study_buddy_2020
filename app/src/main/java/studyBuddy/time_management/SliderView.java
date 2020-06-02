package studyBuddy.time_management;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.Toast;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;

import com.example.studdybuddy.R;

import java.util.List;

class SliderView extends LinearLayoutCompat {

    private List<String> entries;
    private float offsetValue;

    private float textHeight;
    private int textIntervalDistance;

    private String sliderName;

    private Paint textPaintPrimary = new Paint();
    private Paint textPaintBackground = new Paint();

    public SliderView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        entries = null;
        sliderName = "slider";
        setWillNotDraw(false);
        textHeight = 96.0f + 8;
        textIntervalDistance = (int)textHeight;
        textPaintPrimary.setColor(ContextCompat.getColor(ctx, R.color.black));
        textPaintPrimary.setTextSize(textHeight);
        textPaintPrimary.setTextAlign(Paint.Align.CENTER);
        textPaintBackground.setTextSize(textHeight * 0.9f);
        textPaintBackground.setColor(ContextCompat.getColor(ctx, R.color.gray_50_percent_alpha));
        textPaintBackground.setTextAlign(Paint.Align.CENTER);
    }

    public void setSliderName(String name) {
        this.sliderName = name;
    }

    /**
     * Return the string associated with the currently selected slider entry.
     * @return String associated with current entry, or null if no entries.
     */
    public String getCurrentEntry() {
        if (entries.size() > 0) {
            int index = Math.round(offsetValue) % entries.size();
            return entries.get(index);
        }

        return null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // use offsetValue to determine which trio of elements should be drawn
        // offset them based on a remainder
        if (offsetValue < 0) {
            offsetValue += (entries.size());
        }

        if (entries != null) {
            int centerIndex = Math.round(offsetValue);
            int offset = (int)(textIntervalDistance * (centerIndex - offsetValue + 0.5));
            centerIndex += entries.size();
            int centerPoint = getHeight() / 2 - (textIntervalDistance / 2);
            canvas.drawText(entries.get((centerIndex - 2) % entries.size()), getWidth() / 2.0f, centerPoint - (textIntervalDistance) * 2 + offset, textPaintBackground);
            canvas.drawText(entries.get((centerIndex - 1) % entries.size()), getWidth() / 2.0f, centerPoint - (textIntervalDistance) + offset, textPaintBackground);
            canvas.drawText(entries.get(centerIndex % entries.size()), getWidth() / 2.0f, centerPoint + offset, textPaintPrimary);
            canvas.drawText(entries.get((centerIndex + 1) % entries.size()), getWidth() / 2.0f, centerPoint + (textIntervalDistance) + offset, textPaintBackground);
            canvas.drawText(entries.get((centerIndex + 2) % entries.size()), getWidth() / 2.0f, centerPoint + (textIntervalDistance) * 2 + offset, textPaintBackground);
        }

        super.onDraw(canvas);
    }

    /**
     * Determines the contents of the slider view.
     * @param newEntries - A list of our new string entries.
     */
    public void setStringEntries(List<String> newEntries) {
        entries = newEntries;
    }

    @Override
    public boolean performClick() {
        Toast.makeText(getContext(), "Set " + sliderName + " to " + getCurrentEntry(), Toast.LENGTH_SHORT).show();
        return super.performClick();
    }

    public void setTextHeight(float textHeight) {
        this.textHeight = textHeight;
        textPaintPrimary.setTextSize(textHeight);
        textPaintBackground.setTextSize(textHeight);
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
        float deltaOffsetValue = deltaY / textIntervalDistance;
        offsetValue -= deltaOffsetValue;
        postInvalidate();
    }

    public void roundIndex() {
        offsetValue = Math.round(offsetValue);
        postInvalidate();
    }
}