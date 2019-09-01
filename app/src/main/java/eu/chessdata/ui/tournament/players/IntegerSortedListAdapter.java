package eu.chessdata.ui.tournament.players;

import android.support.v7.util.SortedList;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.view.ViewGroup;

public class IntegerSortedListAdapter extends IntegerListAdapter {


    private SortedList<Integer> sortedList;

    public IntegerSortedListAdapter() {
        this.sortedList = new SortedList<>(Integer.class, new SortedListAdapterCallback<Integer>(this) {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }

            @Override
            public boolean areContentsTheSame(Integer oldItem, Integer newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areItemsTheSame(Integer item1, Integer item2) {
                return item1.intValue() == item2.intValue();
            }
        });
    }

    @Override
    protected void addInteger(Integer integer) {
        sortedList.add(integer);
    }

    @Override
    protected void removeInteger(Integer integer) {
        sortedList.remove(integer);
    }

    @Override
    public IntegerListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return IntegerListItemViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(IntegerListItemViewHolder holder, int position) {
        holder.bindTo(sortedList.get(position));
    }

    @Override
    public int getItemCount() {
        return sortedList.size();
    }
}
