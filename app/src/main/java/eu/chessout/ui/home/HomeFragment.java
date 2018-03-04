package eu.chessout.ui.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import eu.chessout.R;

/**
 * Created by Bogdan Oloeriu on 5/25/2016.
 */
public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View fragmentView = inflater.inflate(R.layout.fragment_home, container, false);

        String quote = "Wee aim to make chess more social and fun. Wee are currently in beta. Wee also strongly encourage you to  reach out play chess in the real world have fun and let us know how wee are doing. ";

        TextView textView = (TextView) fragmentView.findViewById(R.id.home_quote);
        textView.setText(quote);

        return fragmentView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main, menu);
    }
}
