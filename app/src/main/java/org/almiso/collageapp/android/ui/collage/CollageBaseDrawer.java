package org.almiso.collageapp.android.ui.collage;

import android.content.Context;

import org.almiso.collageapp.android.R;
import org.almiso.collageapp.android.ui.collage.drawers.DrawerDefault;
import org.almiso.collageapp.android.ui.collage.drawers.DrawerFive;
import org.almiso.collageapp.android.ui.collage.drawers.DrawerFour;
import org.almiso.collageapp.android.ui.collage.drawers.DrawerOne;
import org.almiso.collageapp.android.ui.collage.drawers.DrawerSeven;
import org.almiso.collageapp.android.ui.collage.drawers.DrawerSix;
import org.almiso.collageapp.android.ui.collage.drawers.DrawerThree;
import org.almiso.collageapp.android.ui.collage.drawers.DrawerTwo;

/**
 * Created by Alexandr Sosorev on 31.07.2014.
 */
public class CollageBaseDrawer {
    private Context context;

    public CollageBaseDrawer(Context context) {
        this.context = context;
    }

    public void draw(CollageObject object) {

        switch (object.getImages().length) {
            case 1:
                DrawerOne drawerOne = new DrawerOne(object);
                drawerOne.draw();
                break;
            case 2:
                DrawerTwo drawerTwo = new DrawerTwo(object);
                drawerTwo.draw();
                break;
            case 3:
                DrawerThree drawerThree = new DrawerThree(object);
                drawerThree.draw();
                break;
            case 4:
                DrawerFour drawerFour = new DrawerFour(object);
                drawerFour.draw();
                break;
            case 5:
                DrawerFive drawerFive = new DrawerFive(object);
                drawerFive.draw();
                break;
            case 6:
                DrawerSix drawerSix = new DrawerSix(object);
                drawerSix.draw();
                break;
            case 7:
                DrawerSeven drawerSeven = new DrawerSeven(object);
                drawerSeven.draw();
                break;
            default:
                DrawerDefault drawerDefault = new DrawerDefault(object);
                drawerDefault.draw();


                break;
        }
    }

    public CharSequence[] getFramesTitles(int size) {
        switch (size) {
            case 1:
                return getItems(DrawerOne.FRAMES_COUNT);
            case 2:
                return getItems(DrawerTwo.FRAMES_COUNT);
            case 3:
                return getItems(DrawerThree.FRAMES_COUNT);
            case 4:
                return getItems(DrawerFour.FRAMES_COUNT);
            case 5:
                return getItems(DrawerFive.FRAMES_COUNT);
            case 6:
                return getItems(DrawerSix.FRAMES_COUNT);
            case 7:
                return getItems(DrawerSeven.FRAMES_COUNT);
            default:
                return getItems(DrawerDefault.FRAMES_COUNT);
        }
    }

    private CharSequence[] getItems(int size) {
        CharSequence[] items = new CharSequence[size];
        items[0] = context.getResources().getString(R.string.st_default);
        if (size == 0) {
            return items;
        }
        for (int i = 1; i < size; i++) {
            items[i] = context.getResources().getString(R.string.st_variation) + " " + i;
        }
        return items;
    }

}
