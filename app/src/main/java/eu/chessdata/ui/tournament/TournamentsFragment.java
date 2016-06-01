package eu.chessdata.ui.tournament;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.chessdata.R;

/**
 * Created by Bogdan Oloeriu on 6/1/2016.
 */
public class TournamentsFragment extends Fragment{
    View mView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_tournaments, container,false);
        return mView;
    }
}
