package eu.chessdata.ui.tournament;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import eu.chessdata.R;
import eu.chessdata.model.Tournament;
import eu.chessdata.utils.Constants;
import eu.chessdata.utils.MyFabInterface;

/**
 * Created by Bogdan Oloeriu on 6/1/2016.
 */
public class TournamentsFragment extends Fragment {
    String mClubKey;
    View mView;
    ListView mListView;
    DatabaseReference mReference;
    FirebaseListAdapter<Tournament> mAdapter;

    /**
     * A public interface for activities that contain this fragment
     */
    public interface TournamentsCallback {

        /**
         * This function identifies what item was pressed inside the @TournamentDetailsFragment
         *
         *
         */
        public void onTournamentSelected(String clubKey, String tournamentKey, String tournamentName);
    }

    public static TournamentsFragment newInstance(String clubKey){
        TournamentsFragment fragment = new TournamentsFragment();
        fragment.mClubKey = clubKey;
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_tournaments, container, false);
        ((MyFabInterface)getActivity()).disableFab();

        //firebase reference
        String locTournaments = Constants.LOCATION_TOURNAMENTS
                .replace(Constants.CLUB_KEY, mClubKey);
        mReference = FirebaseDatabase.getInstance().getReference(locTournaments);
        Query order = mReference.orderByChild("reversedDateCreated");//name
        mAdapter = new FirebaseListAdapter<Tournament>(
                getActivity(),
                Tournament.class,
                R.layout.list_item_text,
                order
        ) {
            @Override
            protected void populateView(View v, Tournament model, int position) {
                ((TextView) v.findViewById(R.id.list_item_text_simple_view)).setText(model.getName());
            }
        };

        mListView = (ListView) mView.findViewById(R.id.list_view_tournaments);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tournament tournament = mAdapter.getItem(position);
                String tournamentName = tournament.getName();
                String tournamentKey = mAdapter.getRef(position).getKey();
                ((TournamentsCallback) getActivity()).onTournamentSelected(
                        mClubKey,tournamentKey,tournamentName
                );
            }
        });
        return mView;
    }
}
