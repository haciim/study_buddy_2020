package studyBuddy;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;

import com.example.studdybuddy.R;

import java.util.List;

public class SliderView extends LinearLayoutCompat {

    private List<String> entries;
    private float offsetValue;
    private int offsetHeight;

    private static Paint textPaintPrimary = new Paint();
    private static Paint textPaintBackground = new Paint();

    public SliderView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        entries = null;
        offsetHeight = 32;
        textPaintPrimary.setColor(ContextCompat.getColor(ctx, R.color.black));
        textPaintBackground.setColor(ContextCompat.getColor(ctx, R.color.gray_50_percent_alpha));
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
        int centerIndex = Math.round(offsetValue);
        centerIndex += entries.size();
        int offset = (int)(offsetHeight * (offsetValue - centerIndex + 0.5));
        for (int i = -1; i < 2; i++) {
            canvas.drawText(entries.get((centerIndex + i) % entries.size()), 0, offset + (i + 1) * offsetHeight, textPaintPrimary);
        }
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

    /**
     * Modifies the offset of the slider view in response to (for instance) a touch event.
     * @param deltaY - The change in position along the y axis.
     */
    public void setOffset(float deltaY) {
        // figure out how the delta
        float deltaOffsetValue = deltaY / offsetHeight;
        offsetValue += deltaOffsetValue;
        postInvalidate();
    }

    /**
     * Directly sets the slider value to a new index.
     * @param index - The index which we are setting the slider value to.
     */
    public void setIndex(int index) {
        offsetValue = offsetHeight * (index % entries.size());
    }

}
