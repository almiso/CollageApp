package org.almiso.collageapp.android.dialogs.util;

/**
 * Created by Alexandr Sosorev on 28.07.2014.
 */
public interface BackgroundChooserListener {

    public void onShapeSelected(int shape);

    public void onContentColorSelected(int colorId);

    public void onStrokeWidthSelected(int width);

    public void onStrokeColorSelected(int colorId);

    public void onFrameSelected(int frameId);
}
