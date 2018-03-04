package eu.chessdata.ui.club;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import eu.chessdata.model.Club;
import eu.chessdata.model.DefaultClub;
import eu.chessdata.utils.MyFirebaseUtils;
import eu.chessdata.R;
import eu.chessdata.utils.Constants;
import eu.chessdata.utils.MyFabInterface;

/**
 * Created by Bogdan Oloeriu on 5/25/2016.
 */
public class MyClubsFragment extends Fragment {
    private final String tag = Constants.LOG_TAG;

    FirebaseApp mApp;
    DatabaseReference mClubsReference;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    View mView;
    ListView mListView;
    FirebaseListAdapter<Club> mAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mApp = FirebaseApp.getInstance();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mView = inflater.inflate(R.layout.fragment_my_clubs, container, false);

        //Firebase reference
        String myClubsLocation = Constants.LOCATION_MY_CLUBS
                .replace(Constants.USER_KEY, mUser.getUid());
        mClubsReference = FirebaseDatabase.getInstance().getReference(myClubsLocation);

        //find the listView
        mListView = (ListView) mView.findViewById(R.id.list_view_my_clubs);

        //create custom FirebaseListAdapter subclass
        mAdapter = new FirebaseListAdapter<Club>(
                getActivity(),
                Club.class,
                R.layout.list_item_text,
                mClubsReference) {
            @Override
            protected void populateView(View v, Club model, int position) {
                ((TextView) v.findViewById(R.id.list_item_text_simple_view)).setText(model.getShortName());
            }
        };
        mListView.setAdapter(mAdapter);
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Club selectedClub = mAdapter.getItem(position);
                String name = selectedClub.getShortName();
                final String clubId = mAdapter.getRef(position).getKey();
                //Toast.makeText(getContext(),name +" / " + clubId ,Toast.LENGTH_SHORT).show();

                String adminPath = Constants.CLUB_MANAGERS
                        .replace(Constants.CLUB_KEY, clubId)
                        .replace(Constants.MANAGER_KEY, mUser.getUid());

                DatabaseReference adminReference = FirebaseDatabase.getInstance().getReference(adminPath);

                adminReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        DefaultClub defaultClub = new DefaultClub(clubId, selectedClub.getShortName());
                        MyFirebaseUtils.setDefaultClub(defaultClub);
                        //if data exists then set as default club
                        if (dataSnapshot.getValue() != null) {
                            MyFirebaseUtils.setDefaultClub(defaultClub);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(tag, "Data error: " + databaseError.getMessage());
                    }
                });
                return true;
            }
        });
        configureFab();
        return mView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    private void configureFab(){
        MyFabInterface myFabInterface = (MyFabInterface) getActivity();
        myFabInterface.enableFab(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClubAddExistingDialog clubAddExistingDialog = new ClubAddExistingDialog();
                clubAddExistingDialog.show(getActivity().getSupportFragmentManager(),"ClubAddExistingDialog");
            }
        });
    }
}
