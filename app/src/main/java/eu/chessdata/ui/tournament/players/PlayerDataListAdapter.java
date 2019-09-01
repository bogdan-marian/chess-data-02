package eu.chessdata.ui.tournament.players;

import android.support.v7.widget.RecyclerView;

public abstract class PlayerDataListAdapter extends RecyclerView.Adapter<PlayerDataListItemViewHolder> {
    protected abstract void addPlayer (PlayerData playerData);
    protected abstract void removePlayer (PlayerData playerData);
}
