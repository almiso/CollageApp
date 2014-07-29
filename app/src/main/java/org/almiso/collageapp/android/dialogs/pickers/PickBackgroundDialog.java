package org.almiso.collageapp.android.dialogs.pickers;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import com.yskang.colorpicker.ColorPicker;
import com.yskang.colorpicker.OnColorSelectedListener;

import org.almiso.collageapp.android.R;
import org.almiso.collageapp.android.media.util.ImageShape;
import org.almiso.collageapp.android.media.util.VersionUtils;
import org.almiso.collageapp.android.ui.views.BaseCollageView;

import java.util.ArrayList;

/**
 * Created by Alexandr Sosorev on 28.07.2014.
 */
public class PickBackgroundDialog extends Dialog implements android.view.View.OnClickListener {

    protected static final String TAG = "PickBackgroundDialog";
    private static final int ANIMATION_APPEAR_DURATION = 400;
    private boolean isCanceling = false;
    private Context context;
    private View rootView;
    private BaseCollageView collageView;

    public PickBackgroundDialog(Context context, BaseCollageView collageView) {
        super(context, R.style.PickDialog_Theme);
        this.context = context;
        this.collageView = collageView;
        init();
    }

    private void init() {
        WindowManager manager = (WindowManager) getContext().getSystemService(Activity.WINDOW_SERVICE);
        int width, height;

        if (VersionUtils.hasHoneycombMR2()) {
            width = manager.getDefaultDisplay().getWidth();
            height = manager.getDefaultDisplay().getHeight() - getStatusBarHeight();
        } else {
            Point point = new Point();
            manager.getDefaultDisplay().getSize(point);
            width = point.x;
            height = point.y - getStatusBarHeight();
        }

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = width;
        lp.height = height;
        getWindow().setAttributes(lp);

        rootView = View.inflate(getContext(), R.layout.dialog_chooser_bg, null);

        rootView.findViewById(R.id.layoutRoot).setOnClickListener(this);
        rootView.findViewById(R.id.layoutContainer).setOnClickListener(this);
        rootView.findViewById(R.id.buttonRect).setOnClickListener(this);
        rootView.findViewById(R.id.buttonCircle).setOnClickListener(this);
        rootView.findViewById(R.id.buttonFrame).setOnClickListener(this);

        // Color picker
        ColorPicker colorPicker_2;
        ColorPicker colorPicker_1;
        {
            int color_2 = Color.argb(128, 128, 128, 255);
            ArrayList<Integer> presetColors = new ArrayList<Integer>();
            presetColors.add(Color.BLUE);
            presetColors.add(Color.CYAN);
            presetColors.add(Color.argb(255, 222, 100, 18));
            presetColors.add(Color.argb(128, 222, 100, 18));
            presetColors.add(Color.argb(10, 128, 128, 128));

            OnColorSelectedListener button2ColorSelectedListener = new OnColorSelectedListener() {
                @Override
                public void onSelected(int selectedColor) {
                    collageView.getBackgroundChooserListener().onContentColorSelected(selectedColor);
                }
            };
            colorPicker_2 = new ColorPicker(context, color_2, button2ColorSelectedListener, presetColors);
            OnColorSelectedListener button2ColorSelectedListener1 = new OnColorSelectedListener() {
                @Override
                public void onSelected(int selectedColor) {
                    collageView.getBackgroundChooserListener().onStrokeColorSelected(selectedColor);
                }
            };
            colorPicker_1 = new ColorPicker(context, color_2, button2ColorSelectedListener1, presetColors);
        }

        rootView.findViewById(R.id.buttonBgColor).setOnClickListener(new OnStartButton(colorPicker_2.getDialog()));
        rootView.findViewById(R.id.buttonStrokeColor).setOnClickListener(new OnStartButton(colorPicker_1.getDialog()));
        // Color picker


        SeekBar seekBarStrokeWidth = (SeekBar) rootView.findViewById(R.id.seekBarStrokeSize);
        seekBarStrokeWidth.setProgress(collageView.getStrokeWidth() * 2);
        seekBarStrokeWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                collageView.getBackgroundChooserListener().onStrokeWidthSelected(progress / 2);
            }
        });

        initItems(context);

        FrameLayout container = new FrameLayout(getContext());
        rootView.setLayoutParams(new FrameLayout.LayoutParams(width, height));
        container.addView(rootView);

        ObjectAnimator animator = ObjectAnimator.ofFloat(rootView, "translationY", 2000, 2000).setDuration(0);
        animator.start();
        setContentView(container);

        ViewCompat.postOnAnimation(rootView, new Runnable() {
            @Override
            public void run() {
                ObjectAnimator animator = ObjectAnimator.ofFloat(rootView, "translationY", rootView.getHeight(), 0)
                        .setDuration(ANIMATION_APPEAR_DURATION);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.start();
            }
        });
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getContext().getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void cancel() {
        if (isCanceling) {
            return;
        }

        isCanceling = true;

        ViewCompat.postOnAnimation(rootView, new Runnable() {
            @Override
            public void run() {
                ObjectAnimator animator = ObjectAnimator.ofFloat(rootView, "translationY", 0, rootView.getHeight())
                        .setDuration(ANIMATION_APPEAR_DURATION);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.start();
                rootView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        PickBackgroundDialog.this.superCancel();
                    }
                }, ANIMATION_APPEAR_DURATION);
            }
        });
    }

    private void superCancel() {
        super.cancel();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutRoot:
                cancel();
                break;
            case R.id.layoutContainer:
                break;
            case R.id.buttonFrame:
                openColorPicker(context);
                break;
            case R.id.buttonRect:
                if (collageView.getShape() != ImageShape.SHAPE_RECTANGLE) {
                    collageView.getBackgroundChooserListener().onShapeSelected(ImageShape.SHAPE_RECTANGLE);
                }
                break;
            case R.id.buttonCircle:
                if (collageView.getShape() != ImageShape.SHAPE_CIRCLE) {
                    collageView.getBackgroundChooserListener().onShapeSelected(ImageShape.SHAPE_CIRCLE);
                }
                break;

            default:
                break;
        }
    }

    private void openColorPicker(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.st_select_frame);
        builder.setItems(getItems(), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                collageView.getBackgroundChooserListener().onFrameSelected(item);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    CharSequence[] itemsDefault;
    CharSequence[] items2;
    CharSequence[] items4;

    private void initItems(Context context) {
        itemsDefault = new CharSequence[]{context.getResources().getString(R.string.st_default)};
        items2 = new String[]{context.getResources().getString(R.string.st_default),
                "Variation 1"};
        items4 = new String[]{context.getResources().getString(R.string.st_default),
                context.getResources().getString(R.string.st_variation) + "1",
                context.getResources().getString(R.string.st_variation) + "2",
                context.getResources().getString(R.string.st_variation) + "3",
        };
    }

    private CharSequence[] getItems() {
        switch (collageView.getPhotos().size()) {
            case 1:
                return itemsDefault;
            case 2:
                return items4;
            case 3:
                return itemsDefault;
            case 4:
                return itemsDefault;
            default:
                return itemsDefault;
        }
    }


    public class OnStartButton implements View.OnClickListener {

        private Dialog dialog;

        public OnStartButton(Dialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public void onClick(View v) {
            dialog.show();
        }

    }

}
