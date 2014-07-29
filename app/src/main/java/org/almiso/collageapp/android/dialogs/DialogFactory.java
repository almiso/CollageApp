package org.almiso.collageapp.android.dialogs;

import android.content.Context;

import org.almiso.collageapp.android.dialogs.pickers.PickBackgroundDialog;
import org.almiso.collageapp.android.dialogs.pickers.PickSizeDialog;
import org.almiso.collageapp.android.ui.views.BaseCollageView;

/**
 * Created by Alexandr Sosorev on 28.07.2014.
 */
public class DialogFactory {
    private Context context;

    public DialogFactory(Context context) {
        this.context = context;
    }

    public void requestBackgroundChooser(BaseCollageView collageView) {
        PickBackgroundDialog res = new PickBackgroundDialog(context, collageView);
        res.show();
    }

    public void requestSizeChooser(BaseCollageView collageView) {
        PickSizeDialog res = new PickSizeDialog(context, collageView);
        res.show();
    }
}
