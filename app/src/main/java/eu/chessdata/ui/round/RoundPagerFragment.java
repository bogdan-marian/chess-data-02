package eu.chessdata.ui.round;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.ArrayMap;
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

import java.util.Map;

import eu.chessdata.R;
import eu.chessdata.utils.Constants;
import eu.chessdata.utils.MyFabInterface;

/**
 * Created by Bogdan Oloeriu on 6/12/2016.
 */
public class RoundPagerFragment extends Fragment {
    private final String tag = Constants.LOG_TAG;

    private ViewPager mViewPager;
    private String mTournamentKey;
    private String mClubKey;
    private Map<String, RoundStateFragment> mStateFragmentMap = new ArrayMap<>();

    public static Bundle getBundle(String tournamentKey, String clubKey) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOURNAMENT_KEY, tournamentKey);
        bundle.putString(Constants.CLUB_KEY,clubKey);
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
        View fragmentView = inflater.inflate(R.layout.fragment_round_pager, container, false);
        SectionPagerAdapter sectionPagerAdapter = new SectionPagerAdapter(getFragmentManager());


        mViewPager = (ViewPager) fragmentView.findViewById(R.id.container_round_pager);
        mViewPager.setAdapter(sectionPagerAdapter);
        ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                configureFab();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        mViewPager.addOnPageChangeListener(onPageChangeListener);
        setRound1Fab();
        return fragmentView;
    }

    private class SectionPagerAdapter extends FragmentStatePagerAdapter {
        private final FragmentManager mFragmentManager;
        private int roundsWithData = 2;

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragmentManager = fm;
        }

        @Override
        public Fragment getItem(int position) {
            int round = position + 1;
            Bundle bundle = RoundStateFragment.getBundle(mTournamentKey, round, mClubKey);
            RoundStateFragment roundStateFragment = new RoundStateFragment();
            roundStateFragment.setArguments(bundle);

            mStateFragmentMap.put(getTag(round), roundStateFragment);

            return roundStateFragment;
        }

        @Override
        public int getCount() {
            return roundsWithData;
        }
    }

    private String getTag(int round) {
        return "stateFragment" + round;
    }

    /**
     * todo Please make sure that is the round state that sets the fab;
     */
    public void configureFab() {
        int round = mViewPager.getCurrentItem() + 1;
        String tag = getTag(round);
        RoundStateFragment stateFragment = mStateFragmentMap.get(tag);
        stateFragment.configureFab();
    }

    /**
     * It is to messy to detect when round 1 finishes the creation process for the first time only
     * and then have it
     * configure the fab. It is way more simple to just assume round 0 all the time.
     * when implementing rotation wee will have to think of a different strategy.
     * Maybe disable screen rotation.
     */
    public void setRound1Fab() {
        Log.d(tag, "RoundPagerFragment setRound1Fab");
        final MyFabInterface myFabInterface = (MyFabInterface) getActivity();
        myFabInterface.disableFab();
        String gamesLoc = Constants.LOCATION_ROUND_GAMES
                .replace(Constants.TOURNAMENT_KEY, mTournamentKey)
                .replace(Constants.ROUND_NUMBER, String.valueOf(1));
        DatabaseReference gamesRef = FirebaseDatabase.getInstance().getReference(gamesLoc);
        gamesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    //create and activate fab listener and show add players
                    myFabInterface.enableFab(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RoundAddPlayerDialog roundAddPlayerDialog = RoundAddPlayerDialog.newInstance(mTournamentKey, 1);
                            roundAddPlayerDialog.show(getActivity().getSupportFragmentManager(), "roundAddPlayerDialog");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(tag, "Firebase error: " + databaseError.getMessage());
            }
        });
    }
}
