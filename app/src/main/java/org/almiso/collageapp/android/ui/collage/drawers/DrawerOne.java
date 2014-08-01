package org.almiso.collageapp.android.ui.collage.drawers;

import org.almiso.collageapp.android.ui.collage.BaseDrawer;
import org.almiso.collageapp.android.ui.collage.CollageObject;

/**
 * Created by Alexandr Sosorev on 31.07.2014.
 */
public class DrawerOne extends BaseDrawer {

    public static final int FRAMES_COUNT = 1;

    public DrawerOne(CollageObject object) {
        super(object);
    }

    public void draw() {
        switch (frameId) {
            case 0:
                draw1v1();
                break;
            default:
                break;
        }
    }

    private void draw1v1() {
        rectF1.set(
                contentArea.left + indentPhoto,
                contentArea.top + indentPhoto,
                contentArea.left + contentArea.width() - indentPhoto,
                contentArea.top + contentArea.height() - indentPhoto);
        if (images[0] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[0], null, rectF1, photoPaint);
        }
    }
}
