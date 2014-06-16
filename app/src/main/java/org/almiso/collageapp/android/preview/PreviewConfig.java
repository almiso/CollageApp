package org.almiso.collageapp.android.preview;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by almiso on 10.06.2014.
 */
public class PreviewConfig {


    private static final int MIN_MEDIA_PREVIEW_MARGIN_DP = 2;
    private static final int MAX_MEDIA_PREVIEW_DP = 220;

    public static final int MEDIA_ROW_COUNT = 3;
    public static int MEDIA_PREVIEW = MAX_MEDIA_PREVIEW_DP;
    public static int MEDIA_SPACING = MIN_MEDIA_PREVIEW_MARGIN_DP;


    public static void init(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float density = metrics.density;

        int side = Math.min(metrics.widthPixels, metrics.heightPixels);

        int margin = (int) (MIN_MEDIA_PREVIEW_MARGIN_DP * metrics.density);
        int cellWidth = ((side - (MEDIA_ROW_COUNT - 1) * margin) / MEDIA_ROW_COUNT);

        if (cellWidth >= (MAX_MEDIA_PREVIEW_DP * metrics.density)) {
            MEDIA_PREVIEW = (int) (MAX_MEDIA_PREVIEW_DP * metrics.density);
            MEDIA_SPACING = (int) (MIN_MEDIA_PREVIEW_MARGIN_DP * metrics.density);
        } else {
            MEDIA_PREVIEW = cellWidth;
            MEDIA_SPACING = margin;
        }
    }
}
