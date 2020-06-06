package studyBuddy.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class GlideGifLoader {
    public static void loadGifIntoView(Context context, ImageView view, int gifID) {
        Glide.with(context).asGif().load(gifID).into(view);
    }
}
