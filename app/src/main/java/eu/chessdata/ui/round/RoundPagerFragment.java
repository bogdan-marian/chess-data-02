package eu.chessdata.ui.round;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setParameters();
        View fragmentView = inflater.inflate(R.layout.fragment_round_pager,container,false);
        SectionPagerAdapter sectionPagerAdapter = new SectionPagerAdapter(getFragmentManager());
        mViewPager = (ViewPager)fragmentView.findViewById(R.id.container_round_pager);
        mViewPager.setAdapter(sectionPagerAdapter);
        return fragmentView;
    }

    private class SectionPagerAdapter extends FragmentStatePagerAdapter{
        private final FragmentManager mFragmentManager;
        private int roundsWithData = 2;

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragmentManager = fm;
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = RoundStateFragment.getBundle(mTournamentKey,position+1);
            RoundStateFragment roundStateFragment = new RoundStateFragment();
            roundStateFragment.setArguments(bundle);
            return roundStateFragment;
        }

        @Override
        public int getCount() {
            return roundsWithData;
        }

        public void appendRound() {
            roundsWithData += 1;
            notifyDataSetChanged();
        }
    }
}
