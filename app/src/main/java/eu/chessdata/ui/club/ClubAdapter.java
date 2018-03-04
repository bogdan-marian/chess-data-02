package eu.chessdata.ui.club;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import eu.chessdata.R;
import eu.chessdata.model.Club;

/**
 * Created by Bogdan Oloeriu on 6/27/2016.
 */
public class ClubAdapter extends ArrayAdapter<Club>{
    public ClubAdapter(Context context, List<Club> clubs) {
        super(context, 0,clubs);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Club club = getItem(position);
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_text,parent,false);
        }
        TextView listItem = (TextView) convertView.findViewById(R.id.list_item_text_simple_view);
        listItem.setText(club.getName());
        return convertView;
    }
}
