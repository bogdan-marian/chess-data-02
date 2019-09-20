package eu.chessdata.ui.tournament.players;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import eu.chessdata.R;

public class PlayerDataListItemViewHolder extends RecyclerView.ViewHolder {

    private TournamentPlayersFragment.TournamentPlayersItemSelected fragment;

    public PlayerDataListItemViewHolder(View itemView, TournamentPlayersFragment.TournamentPlayersItemSelected tournamentPlayersItemSelected) {
        super(itemView);
        this.fragment = tournamentPlayersItemSelected;
    }

    public void bindTo(PlayerData playerData) {
        TextView tv = (TextView) itemView;
        tv.setText("" + playerData.tournamentInitialOrder + playerData.playerName);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.playerSingleClicked(playerData);
            }
        });
    }

    public static final PlayerDataListItemViewHolder create(ViewGroup parent, TournamentPlayersFragment.TournamentPlayersItemSelected tournamentPlayersItemSelected) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_text, parent, false);
        return new PlayerDataListItemViewHolder(view, tournamentPlayersItemSelected);
    }
}
