package org.almiso.collageapp.android.ui.collage.drawers;

import org.almiso.collageapp.android.ui.collage.BaseDrawer;
import org.almiso.collageapp.android.ui.collage.CollageObject;

/**
 * Created by Alexandr Sosorev on 01.08.2014.
 */
public class DrawerTwo extends BaseDrawer {

    public static final int FRAMES_COUNT = 6;

    public DrawerTwo(CollageObject object) {
        super(object);
    }

    public void draw() {
        switch (frameId) {
            case 0:
                draw2v1();
                break;
            case 1:
                draw2v2();
                break;
            case 2:
                draw2v3();
                break;
            case 3:
                draw2v4();
                break;
            case 4:
                draw2v5();
                break;
            case 5:
                draw2v6();
                break;
            default:
                break;
        }
    }

    private void draw2v1() {

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
                contentArea.top + contentArea.height() / 2 + indentPhoto,
                contentArea.left + contentArea.width() - indentPhoto,
                contentArea.top + contentArea.height() - indentPhoto);
        if (images[1] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[1], null, rectF1, photoPaint);
        }
    }

    private void draw2v2() {

        rectF1.set(
                contentArea.left + contentArea.width() / 2 + indentPhoto,
                contentArea.top + indentPhoto,
                contentArea.left + contentArea.width() - indentPhoto,
                contentArea.top + contentArea.height() / 2 - indentPhoto);
        if (images[0] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[0], null, rectF1, photoPaint);
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
    }

    private void draw2v3() {

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
                contentArea.left + indentPhoto,
                contentArea.top + contentArea.height() / 2 + indentPhoto,
                contentArea.left + contentArea.width() / 2 - indentPhoto,
                contentArea.top + contentArea.height() - indentPhoto);
        if (images[1] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[1], null, rectF1, photoPaint);
        }
    }

    private void draw2v4() {

        rectF1.set(
                contentArea.left + contentArea.width() / 2 + indentPhoto,
                contentArea.top + indentPhoto,
                contentArea.left + contentArea.width() - indentPhoto,
                contentArea.top + contentArea.height() / 2 - indentPhoto);
        if (images[0] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[0], null, rectF1, photoPaint);
        }

        rectF1.set(
                contentArea.left + contentArea.width() / 2 + indentPhoto,
                contentArea.top + contentArea.height() / 2 + indentPhoto,
                contentArea.left + contentArea.width() - indentPhoto,
                contentArea.top + contentArea.height() - indentPhoto);
        if (images[1] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[1], null, rectF1, photoPaint);
        }
    }

    private void draw2v5() {
        rectF1.set(
                contentArea.left + indentPhoto,
                contentArea.top + indentPhoto,
                contentArea.left + contentArea.width() - indentPhoto,
                contentArea.top + contentArea.height() / 2 - indentPhoto);
        canvas.drawBitmap(((images[0] == null) ? emptyBitmap : scaleCenterCrop(images[0], (int) rectF1.height(), (int) rectF1.width())),
                null, rectF1, photoPaint);


        rectF1.set(
                contentArea.left + indentPhoto,
                contentArea.top + contentArea.height() / 2 + indentPhoto,
                contentArea.left + contentArea.width() - indentPhoto,
                contentArea.top + contentArea.height() - indentPhoto);
        canvas.drawBitmap(((images[1] == null) ? emptyBitmap : scaleCenterCrop(images[1], (int) rectF1.height(), (int) rectF1.width())),
                null, rectF1, photoPaint);
    }

    private void draw2v6() {
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
                contentArea.top + contentArea.height() - indentPhoto);
        canvas.drawBitmap(((images[1] == null) ? emptyBitmap : scaleCenterCrop(images[1], (int) rectF1.height(), (int) rectF1.width())),
                null, rectF1, photoPaint);

    }

}
