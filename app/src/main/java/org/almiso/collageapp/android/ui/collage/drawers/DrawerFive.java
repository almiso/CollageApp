package org.almiso.collageapp.android.ui.collage.drawers;

import org.almiso.collageapp.android.ui.collage.BaseDrawer;
import org.almiso.collageapp.android.ui.collage.CollageObject;

/**
 * Created by Alexandr Sosorev on 01.08.2014.
 */
public class DrawerFive extends BaseDrawer {

    public static final int FRAMES_COUNT = 3;

    public DrawerFive(CollageObject object) {
        super(object);
    }

    public void draw() {
        switch (frameId) {
            case 0:
                draw5v1();
                break;
            case 1:
                draw5v2();
                break;
            case 2:
                draw5v3();
                break;
            default:
                break;
        }
    }

    private void draw5v1() {

        rectF1.set(
                contentArea.left + indentPhoto,
                contentArea.top + indentPhoto,
                contentArea.left + contentArea.width() / 2 - indentPhoto,
                contentArea.top + contentArea.height() / 2 - indentPhoto);
        if (images[0] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[0], null, rectF1, photoPaint);
        }


        rectF1.set(
                contentArea.left + contentArea.width() / 2 + indentPhoto,
                contentArea.top + indentPhoto,
                contentArea.left + contentArea.width() - indentPhoto,
                contentArea.top + contentArea.height() / 2 - indentPhoto);
        if (images[1] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[1], null, rectF1, photoPaint);
        }

        rectF1.set(
                contentArea.left + indentPhoto,
                contentArea.top + contentArea.height() / 2 + indentPhoto,
                contentArea.left + contentArea.width() / 2 - indentPhoto,
                contentArea.top + contentArea.height() - indentPhoto);
        if (images[2] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[2], null, rectF1, photoPaint);
        }

        rectF1.set(
                contentArea.left + contentArea.width() / 2 + indentPhoto,
                contentArea.top + contentArea.height() / 2 + indentPhoto,
                contentArea.left + contentArea.width() - indentPhoto,
                contentArea.top + contentArea.height() - indentPhoto);
        if (images[3] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[3], null, rectF1, photoPaint);
        }

        rectF1.set(
                contentArea.left + contentArea.width() / 4 + indentPhoto,
                contentArea.top + contentArea.height() / 4 + indentPhoto,
                contentArea.left + contentArea.width() - contentArea.height() / 4 - indentPhoto,
                contentArea.top + contentArea.height() - contentArea.height() / 4 - indentPhoto);
        if (images[4] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[4], null, rectF1, photoPaint);
        }
    }

    private void draw5v2() {
        rectF1.set(
                contentArea.left + indentPhoto,
                contentArea.top + +indentPhoto,
                contentArea.left + contentArea.width() / 2 - indentPhoto,
                contentArea.top + contentArea.height() / 2 - indentPhoto);
        canvas.drawBitmap(((images[0] == null) ? emptyBitmap : images[0]), null, rectF1, photoPaint);

        rectF1.set(
                contentArea.left + contentArea.width() / 2 + indentPhoto,
                contentArea.top + +indentPhoto,
                contentArea.left + contentArea.width() - indentPhoto,
                contentArea.top + contentArea.height() / 2 - indentPhoto);
        canvas.drawBitmap(((images[0] == null) ? emptyBitmap : images[0]), null, rectF1, photoPaint);

        rectF1.set(
                contentArea.left + indentPhoto,
                contentArea.top + contentArea.height() / 2 + indentPhoto,
                contentArea.left + contentArea.width() / 2 - indentPhoto,
                contentArea.top + contentArea.height() - indentPhoto);
        canvas.drawBitmap(((images[2] == null) ? emptyBitmap : images[2]), null, rectF1, photoPaint);

        rectF1.set(
                contentArea.left + contentArea.width() / 2 + indentPhoto,
                contentArea.top + contentArea.height() / 2 + indentPhoto,
                contentArea.left + contentArea.width() - indentPhoto,
                contentArea.top + contentArea.height() / 2 + contentArea.height() / 4 - indentPhoto);
        canvas.drawBitmap(((images[3] == null) ? emptyBitmap : scaleCenterCrop(images[3], (int) rectF1.height(), (int) rectF1.width())),
                null, rectF1, photoPaint);

        rectF1.set(
                contentArea.left + contentArea.width() / 2 + indentPhoto,
                contentArea.top + contentArea.height() / 2 + contentArea.height() / 4 + indentPhoto,
                contentArea.left + contentArea.width() - indentPhoto,
                contentArea.top + contentArea.height() - indentPhoto);
        canvas.drawBitmap(((images[4] == null) ? emptyBitmap : scaleCenterCrop(images[4], (int) rectF1.height(), (int) rectF1.width())),
                null, rectF1, photoPaint);
    }


    private void draw5v3() {
        rectF1.set(
                contentArea.left + indentPhoto,
                contentArea.top + +indentPhoto,
                contentArea.left + contentArea.width() * 2 / 3 - indentPhoto,
                contentArea.top + contentArea.height() / 4 - indentPhoto);
        canvas.drawBitmap(((images[0] == null) ? emptyBitmap : scaleCenterCrop(images[0], (int) rectF1.height(), (int) rectF1.width())),
                null, rectF1, photoPaint);

        rectF1.set(
                contentArea.left + contentArea.width() * 2 / 3 + indentPhoto,
                contentArea.top + +indentPhoto,
                contentArea.left + contentArea.width() - indentPhoto,
                contentArea.top + contentArea.height() / 4 - indentPhoto);
        canvas.drawBitmap(((images[1] == null) ? emptyBitmap : scaleCenterCrop(images[1], (int) rectF1.height(), (int) rectF1.width())),
                null, rectF1, photoPaint);

        rectF1.set(
                contentArea.left + indentPhoto,
                contentArea.top + contentArea.height() / 4 + indentPhoto,
                contentArea.left + contentArea.width() / 3 - indentPhoto,
                contentArea.top + contentArea.height() / 2 - indentPhoto);
        canvas.drawBitmap(((images[2] == null) ? emptyBitmap : scaleCenterCrop(images[2], (int) rectF1.height(), (int) rectF1.width())),
                null, rectF1, photoPaint);

        rectF1.set(
                contentArea.left + contentArea.width() / 3 + indentPhoto,
                contentArea.top + contentArea.height() / 4 + indentPhoto,
                contentArea.left + contentArea.width() - indentPhoto,
                contentArea.top + contentArea.height() / 2 + contentArea.height() / 4 - indentPhoto);
        canvas.drawBitmap(((images[3] == null) ? emptyBitmap : scaleCenterCrop(images[3], (int) rectF1.height(), (int) rectF1.width())),
                null, rectF1, photoPaint);

        rectF1.set(
                contentArea.left + indentPhoto,
                contentArea.top + contentArea.height() / 2 + indentPhoto,
                contentArea.left + contentArea.width() - indentPhoto,
                contentArea.top + contentArea.height() - indentPhoto);
        canvas.drawBitmap(((images[4] == null) ? emptyBitmap : scaleCenterCrop(images[4], (int) rectF1.height(), (int) rectF1.width())),
                null, rectF1, photoPaint);
    }

}
