package eu.chessdata.ui.club;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import eu.chessdata.R;
import eu.chessdata.model.Club;
import eu.chessdata.utils.Constants;

/**
 * Created by Bogdan Oloeriu on 5/25/2016.
 */
public class MyClubsFragment extends Fragment {
    FirebaseApp mApp;
    FirebaseDatabase mDatabaseClubs;
    DatabaseReference mClubsReference;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    Firebase legacyRef;
    View mView;
    ListView mListView;
    MyClubsItemAdapter mMyClubsItemAdapter;

    FirebaseRecyclerAdapter<Club,ClubViewHolder> mFirebaseAdapter;



    public static class ClubViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;

        public ClubViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.list_item_text_simple_view);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mApp = FirebaseApp.getInstance();
        mDatabaseClubs = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mView = inflater.inflate(R.layout.fragment_my_clubs, container,false);

        //Firebase reference
        String myClubsLocation = Constants.LOCATION_MY_CLUBS
                .replace(Constants.USER_ID,"ZWw9LeF7NTUdUlsrKR9VuqJInzp1");

        mDatabaseClubs.getReference(myClubsLocation);
        mClubsReference = mDatabaseClubs.getReference();

        //initialize screen
        RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.list_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        //use recycler adapter
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Club, ClubViewHolder>(
                Club.class,
                R.layout.list_item_text,
                ClubViewHolder.class,
                mClubsReference
        ) {
            @Override
            protected void populateViewHolder(ClubViewHolder viewHolder, Club model, int position) {
                viewHolder.textView.setText(model.getShortName());
            }
        };

        recyclerView.setAdapter(mFirebaseAdapter);

        return mView;
    }
}
