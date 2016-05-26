package eu.chessdata.ui.club;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.firebase.client.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import eu.chessdata.R;
import eu.chessdata.model.Club;
import eu.chessdata.utils.Constants;

/**
 * Created by Bogdan Oloeriu on 5/25/2016.
 */
public class MyClubsFragment extends Fragment {
    FirebaseApp mApp;
    FirebaseDatabase mDatabase;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    Firebase mMyClubsRef;
    View mView;
    ListView mListView;
    MyClubsItemAdapter mMyClubsItemAdapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mApp = FirebaseApp.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mView = inflater.inflate(R.layout.fragment_my_clubs, container,false);

        //Firebase reference
        String myClubsLocation = Constants.LOCATION_MY_CLUBS
                .replace(Constants.USER_ID,"ZWw9LeF7NTUdUlsrKR9VuqJInzp1");
        myClubsLocation = Constants.FIREBASE_URL+"/"+myClubsLocation;
        mMyClubsRef = new Firebase(myClubsLocation);
        // mDatabase.getReference(myClubsLocation);


        //initialize screen
        mListView = (ListView) mView.findViewById(R.id.list_view_my_clubs);

        //set the adapter
        mMyClubsItemAdapter = new MyClubsItemAdapter(getActivity(),Club.class,
                R.layout.list_item_text,mMyClubsRef);
        mListView.setAdapter(mMyClubsItemAdapter);

        return mView;
    }
}
