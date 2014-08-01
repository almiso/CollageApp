package org.almiso.collageapp.android.ui.collage.drawers;

import org.almiso.collageapp.android.ui.collage.BaseDrawer;
import org.almiso.collageapp.android.ui.collage.CollageObject;

/**
 * Created by Alexandr Sosorev on 01.08.2014.
 */
public class DrawerThree extends BaseDrawer {

    public static final int FRAMES_COUNT = 4;


    public DrawerThree(CollageObject object) {
        super(object);
    }

    public void draw() {
        switch (frameId) {
            case 0:
                draw3v1();
                break;
            case 1:
                draw3v2();
                break;
            case 2:
                draw3v3();
                break;
            case 3:
                draw3v4();
                break;
            default:
                break;
        }
    }

    private void draw3v1() {

        rectF1.set(
                contentArea.left + indentPhoto,
                contentArea.top + indentPhoto,
                contentArea.left + contentArea.width() / 2 - indentPhoto, contentArea.top + contentArea.height() / 2
                        - indentPhoto);
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
                contentArea.left + contentArea.width() / 4 + indentPhoto,
                contentArea.top + contentArea.height() / 2 + indentPhoto,
                contentArea.left + contentArea.width() / 2 + contentArea.width() / 4 - indentPhoto,
                contentArea.top + contentArea.height() - indentPhoto);
        if (images[2] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[2], null, rectF1, photoPaint);
        }

    }

    private void draw3v2() {

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
                contentArea.top + contentArea.width() / 2 + +indentPhoto,
                contentArea.left + contentArea.width() - indentPhoto,
                contentArea.top + contentArea.height() - indentPhoto);

        if (images[2] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(scaleCenterCrop(images[2], (int) rectF1.height(), (int) rectF1.width()),
                    null, rectF1, photoPaint);
        }
    }

    private void draw3v3() {
        rectF1.set(
                contentArea.left + indentPhoto,
                contentArea.top + +indentPhoto,
                contentArea.left + contentArea.width() - indentPhoto,
                contentArea.top + contentArea.height() / 2 - indentPhoto);

        if (images[0] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(scaleCenterCrop(images[0], (int) rectF1.height(), (int) rectF1.width()),
                    null, rectF1, photoPaint);
        }

        rectF1.set(
                contentArea.left + indentPhoto,
                contentArea.top + contentArea.height() / 2 + indentPhoto,
                contentArea.left + contentArea.width() / 2 - indentPhoto,
                contentArea.top + contentArea.height() - indentPhoto);
        if (images[1] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[1], null, rectF1, photoPaint);
        }

        rectF1.set(
                contentArea.left + contentArea.width() / 2 + indentPhoto,
                contentArea.top + contentArea.height() / 2 + indentPhoto,
                contentArea.left + contentArea.width() - indentPhoto,
                contentArea.top + contentArea.height() - indentPhoto);
        if (images[2] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[2], null, rectF1, photoPaint);
        }
    }

    private void draw3v4() {
        rectF1.set(
                contentArea.left + indentPhoto,
                contentArea.top + +indentPhoto,
                contentArea.left + contentArea.width() / 2 - indentPhoto,
                contentArea.top + contentArea.height() - indentPhoto);
        canvas.drawBitmap(((images[0] == null) ? emptyBitmap : scaleCenterCrop(images[0], (int) rectF1.height(), (int) rectF1.width())),
                null, rectF1, photoPaint);


        rectF1.set(
                contentArea.left + contentArea.width() / 2 + indentPhoto,
                contentArea.top + indentPhoto,
                contentArea.left + contentArea.width() - indentPhoto,
                contentArea.top + contentArea.height() / 2 - indentPhoto);
        canvas.drawBitmap(((images[1] == null) ? emptyBitmap : images[1]), null, rectF1, photoPaint);

        rectF1.set(
                contentArea.left + contentArea.width() / 2 + indentPhoto,
                contentArea.top + contentArea.height() / 2 + indentPhoto,
                contentArea.left + contentArea.width() - indentPhoto,
                contentArea.top + contentArea.height() - indentPhoto);
        canvas.drawBitmap(((images[2] == null) ? emptyBitmap : images[2]), null, rectF1, photoPaint);
    }
}
