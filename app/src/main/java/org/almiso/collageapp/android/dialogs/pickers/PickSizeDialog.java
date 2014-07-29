package org.almiso.collageapp.android.dialogs.pickers;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import org.almiso.collageapp.android.R;
import org.almiso.collageapp.android.ui.views.BaseCollageView;

/**
 * Created by Alexandr Sosorev on 28.07.2014.
 */
public class PickSizeDialog extends Dialog implements android.view.View.OnClickListener {

    protected static final String TAG = "PickSizeDialog";
    private static final int ANIMATION_APPEAR_DURATION = 400;

    private boolean isCanceling = false;

    private Context context;
    View rootView;
    private BaseCollageView collageView;


    public PickSizeDialog(Context context, BaseCollageView collageView) {
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

        rootView = View.inflate(getContext(), R.layout.dialog_chooser_size, null);

        rootView.findViewById(R.id.layoutRoot).setOnClickListener(this);
        rootView.findViewById(R.id.layoutContainer).setOnClickListener(this);

        SeekBar seekBarSize = (SeekBar) rootView.findViewById(R.id.seekBarSize);
        seekBarSize.setProgress((int) ((collageView.getIndentContentAreaFactor() - 1) * 40.01f));
        seekBarSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                collageView.getSizeChooserListener().onSizeChanged(progress);
            }
        });

        SeekBar seekBarSizePhoto = (SeekBar) rootView.findViewById(R.id.seekBarSizePhoto);
        seekBarSizePhoto.setProgress((int) collageView.getIndentPhoto());

        seekBarSizePhoto.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                collageView.getSizeChooserListener().onSizePhotoChanged(progress);
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
                        PickSizeDialog.this.superCancel();
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

            default:
                break;
        }
    }

}
