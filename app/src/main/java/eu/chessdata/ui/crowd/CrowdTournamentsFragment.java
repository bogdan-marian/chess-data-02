package eu.chessdata.ui.crowd;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import eu.chessdata.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CrowdTournamentsFragment.OnCrowdFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CrowdTournamentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CrowdTournamentsFragment extends Fragment {
    View mView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USER_ID = "arg_user_id";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mUserId;
    private String mParam2;

    private OnCrowdFragmentInteractionListener mListener;

    public CrowdTournamentsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * <p>
     * param param1 Parameter 1.
     * param param2 Parameter 2.
     *
     * @return A new instance of fragment CrowdTournamentsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CrowdTournamentsFragment newInstance() {
        CrowdTournamentsFragment fragment = new CrowdTournamentsFragment();
        Bundle args = new Bundle();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        args.putString(ARG_USER_ID, userId);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserId = getArguments().getString(ARG_USER_ID);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_crowd_tournaments, container, false);
        Toast.makeText(getActivity().getApplicationContext(), "User id = " + mUserId, Toast.LENGTH_SHORT).show();
        return mView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCrowdFragmentInteractionListener) {
            mListener = (OnCrowdFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnCrowdFragmentInteractionListener {
        // TODO: Update argument type and name
        void onNavToCrowdTournament(String crowdTournamentId);
    }
}
