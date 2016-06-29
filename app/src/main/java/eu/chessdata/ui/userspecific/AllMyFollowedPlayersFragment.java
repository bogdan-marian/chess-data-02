package eu.chessdata.ui.userspecific;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.chessdata.R;

/**
 * Created by Bogdan Oloeriu on 6/29/2016.
 */
public class AllMyFollowedPlayersFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_my_followed_players,container,false);

        return view;
    }
}
