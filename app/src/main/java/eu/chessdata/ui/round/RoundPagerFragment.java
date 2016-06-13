package eu.chessdata.ui.round;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.chessdata.R;
import eu.chessdata.utils.Constants;

/**
 * Created by Bogdan Oloeriu on 6/12/2016.
 */
public class RoundPagerFragment extends Fragment {
    private final String tag = Constants.LOG_TAG;

    private ViewPager mViewPager;
    private String mTournamentKey;
    private int mTotalRounds = 1;

    public static Bundle getBundle(String tournamentKey){
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOURNAMENT_KEY,tournamentKey);
        return bundle;
    }

    private void setParameters(){
        mTournamentKey = getArguments().getString(Constants.TOURNAMENT_KEY);
        mTotalRounds = getArguments().getInt(Constants.TOTAL_ROUNDS);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setParameters();
        Log.d(tag,"Round pager fragment: " + mTournamentKey + " / " + mTotalRounds);
        View fragmentView = inflater.inflate(R.layout.fragment_round_pager,container,false);
        return fragmentView;
    }
}
