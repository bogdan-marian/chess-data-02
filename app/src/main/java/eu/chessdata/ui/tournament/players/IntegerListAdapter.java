package eu.chessdata.ui.tournament.players;

import android.support.v7.widget.RecyclerView;

public abstract class IntegerListAdapter extends RecyclerView.Adapter<IntegerListItemViewHolder> {
    protected abstract void addInteger(Integer integer);

    protected abstract void removeInteger(Integer integer);
}