package eu.chessdata.ui.tournament.players;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import eu.chessdata.R;

public class PlayerDataListItemViewHolder extends RecyclerView.ViewHolder {
    public PlayerDataListItemViewHolder(View itemView) {
        super(itemView);
    }

    public void bindTo(PlayerData playerData) {
        TextView tv = (TextView) itemView;
        tv.setText("" + playerData.tournamentInitialOrder + playerData.playerName);
    }

    public static final PlayerDataListItemViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_text, parent, false);
        return new PlayerDataListItemViewHolder(view);
    }
}
