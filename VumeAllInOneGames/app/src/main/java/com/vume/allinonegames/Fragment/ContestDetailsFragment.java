package com.vume.allinonegames.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vume.allinonegames.Adapters.FantasyAllContestPrizesAdapter;
import com.vume.allinonegames.R;
import com.vume.allinonegames.Util.JoshApplication;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContestDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContestDetailsFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = ContestDetailsFragment.class.getName();

    private static final String ARG_PAGE = "page";

    private int mPage;
    private Context mActivityContext;
    View view;
    private RecyclerView prizeAmountRecyclerView;
    private FantasyAllContestPrizesAdapter allContestPrizesAdapter;
    private int selectedContestId;

    public ContestDetailsFragment() {

    }

    public static ContestDetailsFragment newInstance(int page) {
        ContestDetailsFragment fragment = new ContestDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityContext = getContext();
        if (getArguments() != null) {
            mPage = getArguments().getInt(ARG_PAGE);
        }
        selectedContestId = JoshApplication.selectedContest.getmId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contestprizes_indetails, container, false);
        prizeAmountRecyclerView = view.findViewById(R.id.prizeamount_recyclerview);
        allContestPrizesAdapter =
                new FantasyAllContestPrizesAdapter(mActivityContext,
                        JoshApplication.selectedContestWithAllDetails.getmPrizesList());
        prizeAmountRecyclerView.setHasFixedSize(true);
        prizeAmountRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        prizeAmountRecyclerView.setAdapter(allContestPrizesAdapter);
        return view;
    }

    @Override
    public void onClick(View v) {

    }
}