package eu.chessdata.ui.tournament.players;

import android.support.v7.util.SortedList;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.view.ViewGroup;

public class PlayerDataSortedListAdapter extends PlayerDataListAdapter {

    private SortedList<PlayerData> sortedList;
    TournamentPlayersFragment.TournamentPlayersItemSelected tournamentPlayersItemSelected;


    public PlayerDataSortedListAdapter(TournamentPlayersFragment.TournamentPlayersItemSelected tournamentPlayersItemSelected) {
        this.tournamentPlayersItemSelected = tournamentPlayersItemSelected;
        this.sortedList = new SortedList<>(PlayerData.class, new SortedListAdapterCallback<PlayerData>(this) {
            @Override
            public int compare(PlayerData o1, PlayerData o2) {
                //wee compare by initial order
                int order1 = o1.tournamentInitialOrder;
                int order2 = o2.tournamentInitialOrder;
                return order1 - order2;
            }

            @Override
            public boolean areContentsTheSame(PlayerData oldItem, PlayerData newItem) {
                boolean sameKey = oldItem.playerKey.equals(newItem.playerKey);
                boolean sameInitialOrder = oldItem.tournamentInitialOrder == newItem.tournamentInitialOrder;
                return sameKey && sameInitialOrder;
            }

            @Override
            public boolean areItemsTheSame(PlayerData item1, PlayerData item2) {
                boolean sameKey = item1.playerKey.equals(item2.playerKey);
                boolean sameInitialOrder = item1.tournamentInitialOrder == item2.tournamentInitialOrder;
                return sameKey && sameInitialOrder;
            }
        });
    }


    @Override
    protected void addPlayer(PlayerData playerData) {
        sortedList.add(playerData);
    }

    @Override
    protected void removePlayer(PlayerData playerData) {
        sortedList.remove(playerData);
    }

    @Override
    public PlayerDataListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return PlayerDataListItemViewHolder.create(parent, tournamentPlayersItemSelected);
    }

    @Override
    public void onBindViewHolder(PlayerDataListItemViewHolder holder, int position) {
        holder.bindTo(sortedList.get(position));
    }

    @Override
    public int getItemCount() {
        return sortedList.size();
    }
}
