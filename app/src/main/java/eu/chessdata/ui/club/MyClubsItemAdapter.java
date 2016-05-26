package eu.chessdata.ui.club;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseListAdapter;

import eu.chessdata.R;
import eu.chessdata.model.Club;

/**
 * Created by Bogdan Oloeriu on 5/26/2016.
 */
public class MyClubsItemAdapter  extends FirebaseListAdapter<Club>{
    private Club mClub;
    private String mClubId;

    public MyClubsItemAdapter(Activity activity, Class<Club> modelClass, int modelLayout, Firebase ref) {
        super(activity, modelClass, modelLayout, ref);
    }


    /*public MyClubsItemAdapter(Activity activity, Class<Club> modelClass, int modelLayout, Query ref, String clubId) {
        super(activity, modelClass, modelLayout, ref);
        this.mActivity = activity;
        this.mClubId = clubId;
    }*/

    //use this to pass club object when it is loaded in ValueEventListener
    public void setClub(Club club){
        this.mClub = club;
        this.notifyDataSetChanged();
    }

    @Override
    protected void populateView(View view, final Club club, int position) {
        TextView textView = (TextView) view.findViewById(R.id.list_item_text_simple_view);
        textView.setText(club.getShortName());
    }
}
