package eu.chessdata.ui.tournament;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import eu.chessdata.R;
import eu.chessdata.model.Player;

/**
 * Created by Bogdan Oloeriu on 6/12/2016.
 */
public class PlayerAdapter extends ArrayAdapter<Player>{
    public PlayerAdapter(Context context, List<Player> players) {
        super(context, 0,players);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Player player = getItem(position);
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_text,parent,false);
        }
        TextView listItem = (TextView)convertView.findViewById(R.id.list_item_text_simple_view);
        listItem.setText(player.getName());
        return convertView;
    }
}
