package eu.chessout.ui.standings;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import eu.chessout.R;
import eu.chessout.model.Tournament;
import eu.chessout.utils.Constants;


public class StandingsPagerFragment extends Fragment {
    private final String tag = Constants.LOG_TAG;

    private ViewPager mViewPager;
    private String mTournamentKey;
    private int mTotalRounds = 1;
    private int mRoundsWithData = 1;
    private String mClubKey;
    private boolean roundZero = false;
    private SectionPagerAdapter mSectionPagerAdapter;


    public static Bundle getBundle(String tournamentKey, String clubKey) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOURNAMENT_KEY, tournamentKey);
        bundle.putString(Constants.CLUB_KEY, clubKey);
        return bundle;
    }

    private void setParameters() {
        mTournamentKey = getArguments().getString(Constants.TOURNAMENT_KEY);
        mClubKey = getArguments().getString(Constants.CLUB_KEY);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setParameters();
        View fragmentView = inflater.inflate(R.layout.fragment_standings_pager, container, false);

        mSectionPagerAdapter = new SectionPagerAdapter(getFragmentManager());
        mViewPager = (ViewPager) fragmentView.findViewById(R.id.container_standings_pager);
        mViewPager.setAdapter(mSectionPagerAdapter);


        (new ExtractTotalRounds()).execute();


        return fragmentView;
    }


    private class SectionPagerAdapter extends FragmentStatePagerAdapter {
        private final FragmentManager mFragmentManager;

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragmentManager = fm;
        }

        @Override
        public Fragment getItem(int position) {
            int roundNumber = position + 1;
            StandingsFragment standingsFragment = StandingsFragment.newInstance(mTournamentKey, roundNumber, mClubKey);
            Log.d(tag,"Get item section pager for page nr " + roundNumber);
            return standingsFragment;
        }


        @Override
        public int getCount() {
            return mTotalRounds;
        }
    }


    /**
     * It computes totalRounds
     */
    private class ExtractTotalRounds extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            //get tournament total rounds
            String tournamentLoc = Constants.LOCATION_TOURNAMENT
                    .replace(Constants.CLUB_KEY, mClubKey)
                    .replace(Constants.TOURNAMENT_KEY, mTournamentKey);
            DatabaseReference tournamentRef = FirebaseDatabase.getInstance().getReference(tournamentLoc);
            tournamentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Tournament tournament = dataSnapshot.getValue(Tournament.class);
                    if (tournament != null) {

                        mTotalRounds = tournament.getTotalRounds();
                        mSectionPagerAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return null;
        }
    }
}
