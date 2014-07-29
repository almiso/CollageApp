package org.almiso.collageapp.android.dialogs.pickers;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import org.almiso.collageapp.android.R;
import org.almiso.collageapp.android.media.util.ImageShape;
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
    View rootView;

    private BaseCollageView collageView;

    public PickBackgroundDialog(Context context, BaseCollageView collageView) {
        super(context, R.style.PickDialog_Theme);
        this.context = context;
        this.collageView = collageView;
        init();
    }

    @SuppressLint("NewApi")
    private void init() {

        WindowManager manager = (WindowManager) getContext().getSystemService(Activity.WINDOW_SERVICE);
        int width, height;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
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

        // Color picker
//        ColorPicker colorPicker_2;
//        ColorPicker colorPicker_1;
        {
            int color_2 = Color.argb(128, 128, 128, 255);
            ArrayList<Integer> presetColors = new ArrayList<Integer>();
            presetColors.add(Color.BLUE);
            presetColors.add(Color.CYAN);
            presetColors.add(Color.argb(255, 222, 100, 18));
            presetColors.add(Color.argb(128, 222, 100, 18));
            presetColors.add(Color.argb(10, 128, 128, 128));
//            OnColorSelectedListener button2ColorSelectedListener = new OnColorSelectedListener() {
//                @Override
//                public void onSelected(int selectedColor) {
//                    listener.onContentColorChosed(selectedColor);
//                }
//            };
//            colorPicker_2 = new ColorPicker(context, color_2, button2ColorSelectedListener, presetColors);


//            OnColorSelectedListener button2ColorSelectedListener1 = new OnColorSelectedListener() {
//                @Override
//                public void onSelected(int selectedColor) {
//                    listener.onStrokeColorChosed(selectedColor);
//                }
//            };

//			onStrokeColorChosed
//            colorPicker_1 = new ColorPicker(context, color_2, button2ColorSelectedListener1, presetColors);

        }

//        rootView.findViewById(R.id.buttonColor).setOnClickListener(new OnStartButton(colorPicker_2.getDialog()));

//        rootView.findViewById(R.id.buttonStrokeColor).setOnClickListener(new OnStartButton(colorPicker_1.getDialog()));


        rootView.findViewById(R.id.buttonRect).setOnClickListener(this);
        rootView.findViewById(R.id.buttonCircle).setOnClickListener(this);

//        layoutShapeAngle = (LinearLayout) rootView.findViewById(R.id.layoutShapeAngle);
//        if (collageView.getShape() == BaseCollageView.SHAPE_RECT) {
//            showView(layoutShapeAngle);
//        } else {
//            goneView(layoutShapeAngle);
//        }

//        SeekBar seekBarShapeAngle = (SeekBar) rootView.findViewById(R.id.seekBarShapeAngle);
//        seekBarShapeAngle.setProgress(collageView.getBgAngle());
//        seekBarShapeAngle.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
////                listener.onShapeAngleChosed(progress);
//            }
//        });

        SeekBar seekBarStrokeSize = (SeekBar) rootView.findViewById(R.id.seekBarStrokeSize);
//        seekBarStrokeSize.setProgress(collageView.getStrokeWidth());
        seekBarStrokeSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                listener.onStrokeSizeChosed(progress / 2);
            }
        });

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
            case R.id.buttonColor:
                openColorPicker(context);
                break;
            case R.id.buttonStrokeColor:
                openStrokeColorPicker(context);
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

        // AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // builder.setTitle("Select color");
        // builder.setItems(items, new DialogInterface.OnClickListener() {
        // public void onClick(DialogInterface dialog, int item) {
        // listener.onContentChosed(String.valueOf(items[item]));
        // switch (item) {
        // case 0:
        // listener.onContentColorChosed(Color.RED);
        // break;
        // case 1:
        // listener.onContentColorChosed(Color.GREEN);
        // break;
        // case 2:
        // listener.onContentColorChosed(Color.YELLOW);
        // break;
        // case 3:
        // listener.onContentColorChosed(Color.CYAN);
        // case 4:
        // listener.onContentColorChosed(Color.BLUE);
        // break;
        // case 5:
        // listener.onContentColorChosed(Color.WHITE);
        // break;
        // case 6:
        // listener.onContentColorChosed(Color.TRANSPARENT);
        // break;
        // default:
        // break;
        // }
        // }
        // });
        // AlertDialog alert = builder.create();
        // alert.show();
    }

    private void openStrokeColorPicker(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select color");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
//                listener.onContentChosed(String.valueOf(items[item]));
//                switch (item) {
//                    case 0:
//                        listener.onStrokeColorChosed(Color.RED);
//                        break;
//                    case 1:
//                        listener.onStrokeColorChosed(Color.GREEN);
//                        break;
//                    case 2:
//                        listener.onStrokeColorChosed(Color.YELLOW);
//                        break;
//                    case 3:
//                        listener.onStrokeColorChosed(Color.CYAN);
//                    case 4:
//                        listener.onStrokeColorChosed(Color.BLUE);
//                        break;
//                    case 5:
//                        listener.onStrokeColorChosed(Color.WHITE);
//                        break;
//                    case 6:
//                        listener.onStrokeColorChosed(Color.TRANSPARENT);
//                        break;
//                    default:
//                        break;
//                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    final CharSequence[] items = {"Red", "Green", "Yellow", "CYAN", "BLUE", "WHITE", "TRANSPARENT"};

    protected void showView(View view) {
        showView(view, true);
    }

    protected void showView(View view, boolean isAnimating) {
        if (view == null) {
            return;
        }
        if (view.getVisibility() == View.VISIBLE) {
            return;
        }

        if (isAnimating) {
            AlphaAnimation alpha = new AlphaAnimation(0.0F, 1.0f);
            alpha.setDuration(250);
            alpha.setFillAfter(false);
            view.startAnimation(alpha);
        }
        view.setVisibility(View.VISIBLE);
    }

    protected void hideView(View view) {
        hideView(view, true);
    }

    protected void hideView(View view, boolean isAnimating) {
        if (view == null) {
            return;
        }
        if (view.getVisibility() != View.VISIBLE) {
            return;
        }
        if (isAnimating) {
            AlphaAnimation alpha = new AlphaAnimation(1.0F, 0.0f);
            alpha.setDuration(250);
            alpha.setFillAfter(false);
            view.startAnimation(alpha);
        }
        view.setVisibility(View.INVISIBLE);
    }

    protected void goneView(View view) {
        goneView(view, true);
    }

    protected void goneView(View view, boolean isAnimating) {
        if (view == null) {
            return;
        }
        if (view.getVisibility() != View.VISIBLE) {
            return;
        }
        if (isAnimating) {
            AlphaAnimation alpha = new AlphaAnimation(1.0F, 0.0f);
            alpha.setDuration(250);
            alpha.setFillAfter(false);
            view.startAnimation(alpha);
        }
        view.setVisibility(View.GONE);
    }

}
