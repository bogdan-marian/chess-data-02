package eu.chessdata.ui.tournament;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.chessdata.R;
import eu.chessdata.utils.Constants;

/**
 * Created by Bogdan Oloeriu on 6/5/2016.
 */
public class TournamentDetailsFragment extends Fragment {
    private final String tag = Constants.LOG_TAG;

    private String mClubKey;
    private String mTournamentKey;
    private String mTournamentName;

    String[] mValues = {"Categories", "Players", "Rounds", "Standings", "Get social"};
    private ListView mListView;
    private ArrayAdapter<String> mArrayAdapter;

    public interface TournamentDetailsCallback {

        /**
         * callback function that must be implemented by the activity that holds this fragment.
         *
         * @param clubKey
         * @param tournamentKey
         * @param tournamentName
         */
        public void onTournamentDetailsItemSelected(String clubKey, String tournamentKey, String tournamentName, int position);
    }

    public static TournamentDetailsFragment newInstance(String clubKey, String tournamentKey, String tournamentName) {
        TournamentDetailsFragment vip = new TournamentDetailsFragment();
        vip.mClubKey = clubKey;
        vip.mTournamentKey = tournamentKey;
        vip.mTournamentName = tournamentName;
        return vip;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_tournament_details, container, false);

        mListView = (ListView) fragmentView.findViewById(R.id.tournament_details_list_view);
        List<String> tournamentOptions = new ArrayList<>(Arrays.asList(mValues));
        mArrayAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_text,
                R.id.list_item_text_simple_view,
                tournamentOptions
        );
        mListView.setAdapter(mArrayAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 || position == 4) {
                    Toast.makeText(getContext(),
                            "Categories and Social section not implemented yet",
                            Toast.LENGTH_SHORT).show();
                } else {
                    ((TournamentDetailsCallback) getActivity()).onTournamentDetailsItemSelected(
                            mClubKey, mTournamentKey, mTournamentName, position
                    );
                }
            }
        });

        return fragmentView;
    }


}
