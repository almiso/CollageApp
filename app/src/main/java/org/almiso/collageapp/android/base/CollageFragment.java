package org.almiso.collageapp.android.base;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;

/**
 * Created by almiso on 07.06.2014.
 */
public class CollageFragment extends CollageBaseFragment {
    private boolean saveInStack = true;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("collage:saveInStack", saveInStack);
    }

    public void setSaveInStack(boolean saveInStack) {
        this.saveInStack = saveInStack;
    }

    public boolean isSaveInStack() {
        return saveInStack;
    }

    public boolean onBackPressed() {
        return false;
    }

    public boolean isParentFragment(CollageFragment fragment) {
        return true;
    }

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
