package eu.chessdata.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

import eu.chessdata.ui.club.ClubPlayersFragment;
import eu.chessdata.ui.club.PlayerCreateDialogFragment;
import eu.chessdata.ui.home.HomeFragment;
import eu.chessdata.ui.open.OpenMainActivity;
import eu.chessdata.ui.tournament.TournamentAddPlayerDialog;
import eu.chessdata.ui.tournament.TournamentCreateDialogFragment;
import eu.chessdata.ui.tournament.TournamentPlayersFragment;
import eu.chessdata.ui.userspecific.AllMyFollowedPlayersFragment;
import eu.chessdata.utils.MyFirebaseUtils;
import eu.chessdata.R;
import eu.chessdata.model.DefaultClub;
import eu.chessdata.ui.club.ClubCreateDialogFragment;
import eu.chessdata.ui.club.MyClubsFragment;
import eu.chessdata.ui.round.RoundPagerFragment;
import eu.chessdata.ui.standings.StandingsPagerFragment;
import eu.chessdata.ui.tournament.TournamentDetailsFragment;
import eu.chessdata.ui.tournament.TournamentsFragment;
import eu.chessdata.utils.Constants;
import eu.chessdata.utils.MyFabInterface;
import eu.chessdata.utils.Utils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener,
        MyFirebaseUtils.OnOneTimeResultsListener,
        TournamentsFragment.TournamentsCallback,
        TournamentDetailsFragment.TournamentDetailsCallback,
        Utils.VipMap, MyFabInterface{




    public enum ACTION {
        SHOW_TOURNAMENTS,
        SHOW_PLAYERS,
        SHOW_TOURNAMENT_PLAYERS,
        SHOW_ROUND
    }



    public static final String ANONYMOUS = "anonymous";
    private static final String TAG = Constants.LOG_TAG;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    private final Map<String, String> mVipMap = new HashMap<>();

    //
    private String mUsername;
    private String mPhotoUrl;
    private String mEmail;
    private SharedPreferences mSharedPreferences;
    private GoogleApiClient mGoogleApiClient;

    //
    private FloatingActionButton mFab;

    @Override
    public void updateVipValue(String key, String value) {
        mVipMap.put(key,value);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPreferences = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        //<firebase auth>
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            initializeScreen();
        }
        //</firebase auth>
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setVisibility(View.INVISIBLE);
        /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //
        Log.d(TAG,"MainActivity: firebase instance id = " + FirebaseInstanceId.getInstance().getToken());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.sign_out_menu) {
            mFirebaseAuth.signOut();
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            mUsername = ANONYMOUS;
            startActivity(new Intent(this, SignInActivity.class));
            return true;
        }
        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            HomeFragment homeFragment = new HomeFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, homeFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            mFab.setVisibility(View.INVISIBLE);
            getSupportActionBar().setTitle("chess-data");
        } else if (id == R.id.nav_clubs) {
            //show fragment
            MyClubsFragment myClubsFragment = new MyClubsFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, myClubsFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            getSupportActionBar().setTitle("my clubs");
            //show fab
            mFab.setVisibility(View.VISIBLE);
            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            mFab.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    (new ClubCreateDialogFragment()).show(getSupportFragmentManager(), "ClubCreateDialogFragment");
                    return true;
                }
            });
        } else if (id == R.id.nav_tournaments) {
            MyFirebaseUtils.getDefaultClub(this, ACTION.SHOW_TOURNAMENTS);
            MyFirebaseUtils.isManagerForDefaultClub(this, ACTION.SHOW_TOURNAMENTS);
        } else if (id == R.id.nav_members) {
            MyFirebaseUtils.getDefaultClub(this, ACTION.SHOW_PLAYERS);
            MyFirebaseUtils.isManagerForDefaultClub(this, ACTION.SHOW_PLAYERS);
        } else if (id == R.id.nav_followed_players){
            AllMyFollowedPlayersFragment allMyFollowedPlayersFragment = new AllMyFollowedPlayersFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container,allMyFollowedPlayersFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            disableFab();
            getSupportActionBar().setTitle("Followed players");
        }else if (id == R.id.nav_open_tournaments){
            Intent intent = new Intent(this, OpenMainActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    private void initializeScreen() {
        mUsername = mFirebaseUser.getDisplayName();
        if (mFirebaseUser.getPhotoUrl() != null) {
            mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
        }
        mEmail = mFirebaseUser.getEmail();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        ((TextView) header.findViewById(R.id.user_name)).setText(mUsername);
        ((TextView) header.findViewById(R.id.user_email)).setText(mEmail);

        //show home fragment
        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, homeFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onClubValue(DefaultClub club, ACTION action) {
        if (club != null) {
            if (action == ACTION.SHOW_TOURNAMENTS) {
                TournamentsFragment tournamentsFragment = TournamentsFragment.newInstance(club.getClubKey());
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, tournamentsFragment);
                transaction.addToBackStack(null);
                transaction.commit();

                getSupportActionBar().setTitle("Club: " + club.getClubName());
            } else if (action == ACTION.SHOW_PLAYERS) {
                ClubPlayersFragment clubPlayersFragment = ClubPlayersFragment.newInstance(club.getClubKey());
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, clubPlayersFragment);
                transaction.addToBackStack(null);
                transaction.commit();

                getSupportActionBar().setTitle("Club: " + club.getClubName());
            } else if (action == ACTION.SHOW_ROUND){

            }

        } else {
            Toast.makeText(getApplicationContext(),
                    "No default club! Please go to clubs section and long pres the desired club",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClubValue(DefaultClub defaultClub) {
        if (defaultClub != null) {
            TournamentsFragment tournamentsFragment = TournamentsFragment.newInstance(defaultClub.getClubKey());
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, tournamentsFragment);
            transaction.addToBackStack(null);
            transaction.commit();

            getSupportActionBar().setTitle("Club: " + defaultClub.getClubName());

        } else {
            Toast.makeText(getApplicationContext(),
                    "No default club! Please go to clubs section and long pres the desired club",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * by design this method should only be called when wee have the confirmation that
     * the user is a manager of the default club. sets the fab visibility and updates the
     * onClickListener
     */
    @Override
    public void onUserIsClubManager(final Map<String,String> myMap, ACTION action) {
        //todo create reset fab
        if (action == ACTION.SHOW_TOURNAMENTS) {
            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TournamentCreateDialogFragment dialogFragment = TournamentCreateDialogFragment.newInstance(myMap.get(Constants.CLUB_KEY) );
                    dialogFragment.show(getSupportFragmentManager(), "tournamentCreateDialogFragment");
                }
            });
            mFab.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return false;
                }
            });
            mFab.setVisibility(View.VISIBLE);
        } else if (action == ACTION.SHOW_PLAYERS) {
            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlayerCreateDialogFragment dialogFragment = PlayerCreateDialogFragment.newInstance(myMap.get(Constants.CLUB_NAME),myMap.get(Constants.CLUB_KEY) );
                    dialogFragment.show(getSupportFragmentManager(), "playerCreateDialogFragment");
                }
            });
            mFab.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return false;
                }
            });
        } else if (action == ACTION.SHOW_TOURNAMENT_PLAYERS){
            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String clubKey = mVipMap.get(Constants.CLUB_KEY);
                    String tournamentKey = mVipMap.get(Constants.TOURNAMENT_KEY);
                    TournamentAddPlayerDialog addPlayerDialog = TournamentAddPlayerDialog.newInstance(tournamentKey,clubKey);
                    addPlayerDialog.show(getSupportFragmentManager(),"TournamentAddPlayerDialog");
                }
            });

            mFab.setOnLongClickListener(new View.OnLongClickListener(){

                @Override
                public boolean onLongClick(View v) {
                    String clubKey = mVipMap.get(Constants.CLUB_KEY);
                    String tournamentKey = mVipMap.get(Constants.TOURNAMENT_KEY);
                    Toast.makeText(getApplicationContext(), "Time to add all players", Toast.LENGTH_SHORT);

                    return false;
                }
            });
            mFab.setVisibility(View.VISIBLE);
        } else if (action == ACTION.SHOW_ROUND){
            //for the moment no fab
        }
    }

    /**
     * use this function to run fragment transactions. It will make the code more readable
     * @param containerViewId
     * @param fragment
     */
    private void runFragmentTransaction(int containerViewId, Fragment fragment, String fragmentTag){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(containerViewId,fragment,fragmentTag);
        transaction.addToBackStack(fragmentTag);
        transaction.commit();
    }



    @Override
    public void onTournamentSelected(String clubKey, String tournamentKey, String tournamentName) {

        getSupportActionBar().setTitle(tournamentName);

        TournamentDetailsFragment detailsFragment = TournamentDetailsFragment.newInstance(clubKey, tournamentKey, tournamentName);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, detailsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void enableFab(View.OnClickListener onClickListener) {
        mFab.setOnClickListener(onClickListener);
        mFab.setVisibility(View.VISIBLE);
    }

    @Override
    public void disableFab() {
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        mFab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        mFab.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onTournamentDetailsItemSelected(String clubKey, String tournamentKey, String tournamentName, int position) {

        updateVipValue(Constants.CLUB_KEY,clubKey);
        updateVipValue(Constants.TOURNAMENT_KEY,tournamentKey);

        disableFab();
        if (position == 1) {//players

            TournamentPlayersFragment tournamentPlayersFragment = TournamentPlayersFragment.newInstance(tournamentKey);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, tournamentPlayersFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            MyFirebaseUtils.isManagerForClubKey(clubKey,this,ACTION.SHOW_TOURNAMENT_PLAYERS);
        }else if (position == 2){//rounds
            Bundle bundle = RoundPagerFragment.getBundle(tournamentKey, clubKey);
            RoundPagerFragment roundPagerFragment = new RoundPagerFragment();
            roundPagerFragment.setArguments(bundle);
            runFragmentTransaction(R.id.fragment_container,roundPagerFragment,"RoundPagerFragment");
        }else if (position == 3){//standings
            Bundle bundle = StandingsPagerFragment.getBundle(tournamentKey, clubKey);
            StandingsPagerFragment standingsPagerFragment = new StandingsPagerFragment();
            standingsPagerFragment.setArguments(bundle);
            runFragmentTransaction(R.id.fragment_container,standingsPagerFragment,"StandingsPagerFragment");
        }
    }
}
