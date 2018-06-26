package eu.chessdata.utils;

import android.view.View;

/**
 * Created by Bogdan Oloeriu on 6/14/2016.
 */
public interface MyFabInterface {
    public void enableFab(View.OnClickListener onClickListener);
    public void enableFab(View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener);
    public void disableFab();
}
