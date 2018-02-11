package eu.chessout.ui.standings;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.chessout.R;
import eu.chessout.utils.Constants;


public class StandingsPagerFragment extends Fragment {
    private final String tag = Constants.LOG_TAG;

    private ViewPager mViewPager;
    private String mTournamentKey;
    private int mTotalrounds  = 1;
    private int mRoundsWithData = 1;
    private String mClubKey;
    private boolean roundZero = false;



    public static Bundle getBundle(String tournamentKey, String clubKey) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOURNAMENT_KEY, tournamentKey);
        bundle.putString(Constants.CLUB_KEY, clubKey);
        return bundle;
    }

    private void setParameters(){
        mTournamentKey = getArguments().getString(Constants.TOURNAMENT_KEY);
        mClubKey = getArguments().getString(Constants.CLUB_KEY);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setParameters();
        View fragmentView = inflater.inflate(R.layout.fragment_standings_pager, container,false);
        return fragmentView;
    }


    private class SectionPagerAdapter extends FragmentStatePagerAdapter{
        private final FragmentManager mFragmentManager;

        public SectionPagerAdapter(FragmentManager fm){
            super(fm);
            mFragmentManager = fm;
        }

        @Override
        public Fragment getItem(int position) {
            int round = position + 1;

            return null;
        }


        @Override
        public int getCount() {
            return 0;
        }
    }
}
