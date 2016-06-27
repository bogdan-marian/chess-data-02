package eu.chessdata.ui.club;

import android.support.v4.app.DialogFragment;

import java.util.ArrayList;
import java.util.Map;

import eu.chessdata.model.Club;
import eu.chessdata.utils.Constants;

/**
 * Created by Bogdan Oloeriu on 6/26/2016.
 */
public class AddExistingClubFragment extends DialogFragment{
    private String tag = Constants.LOG_TAG;
    private ArrayList<Club> mClubs;
    private Map<String, Club> mClubsMap;
    private ClubAdapter mClubAdapter;


}
