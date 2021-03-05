package com.vume.allinonegames.Fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vume.allinonegames.Adapters.FantasyAllMatchesAdapter;
import com.vume.allinonegames.Model.FantasyAllMatchesResponseData;
import com.vume.allinonegames.Model.JoshErrorResponse;
import com.vume.allinonegames.R;
import com.vume.allinonegames.Util.ErrorUtils;
import com.vume.allinonegames.Util.JoshApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link com.vume.allinonegames.Fragment.MatchesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MatchesFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = MatchesFragment.class.getName();

    private static final String ARG_PAGE = "page";
    public static final int ALL_MATCHES_TAB_INDEX = 0;
    public static final int MY_MATCHES_TAB_INDEX = 1;

    private int mPage;
    private Context mActivityContext;

    private TextView btnUpcomingMatches, btnLiveMatches, btnCompletedMatches, noMatchesError;
    private RecyclerView allMatchesRecyclerView;
    private FantasyAllMatchesAdapter allMatchesAdapter;
    private List<FantasyAllMatchesResponseData> mFantasyAllMatchesdatumResponses = new ArrayList<>();
    View view;

    public MatchesFragment() {

    }

    public static MatchesFragment newInstance(int page) {
        MatchesFragment fragment = new MatchesFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_matches, container, false);
        btnUpcomingMatches = view.findViewById(R.id.upcoming_matches);
        btnLiveMatches = view.findViewById(R.id.live_matches);
        btnCompletedMatches = view.findViewById(R.id.completed_matches);
        noMatchesError = view.findViewById(R.id.no_matches_error);
        allMatchesRecyclerView = view.findViewById(R.id.allmatches_recyclerview);

        btnUpcomingMatches.setOnClickListener(this);
        btnLiveMatches.setOnClickListener(this);
        btnCompletedMatches.setOnClickListener(this);

        getAllMatchesDetails();
        return view;
    }

    private void getAllMatchesDetails() {
        JoshApplication.startSpinnerProgress(mActivityContext, mActivityContext.getResources().getString(R.string.loading_match_details));
        if (JoshApplication.isInternetAvailable(mActivityContext)) {
            JoshApplication.getFantasyAllMatchesApiCall(getResources().getString(R.string.josh_server_api_key),
                    JoshApplication.currentLoginAuthTokenFromServer(mActivityContext),
                    JoshApplication.ALLMATCHES_LIMIT, "0", JoshApplication.FANTASY_MATCH_STATUS_ALL).
                    enqueue(new Callback<List<FantasyAllMatchesResponseData>>() {
                        @Override
                        public void onResponse(Call<List<FantasyAllMatchesResponseData>> call, Response<List<FantasyAllMatchesResponseData>> response) {
                            if (response.isSuccessful()) {
                                //JoshApplication.toast(mActivityContext, "Buyin success. got to webview");
                                mFantasyAllMatchesdatumResponses.addAll(response.body());
                                if(mFantasyAllMatchesdatumResponses.size()>0) {
                                    new SortMatchData().execute();
                                    noMatchesError.setVisibility(View.GONE);
                                    allMatchesRecyclerView.setVisibility(View.VISIBLE);
                                }
                                else{
                                    noMatchesError.setVisibility(View.VISIBLE);
                                    allMatchesRecyclerView.setVisibility(View.GONE);
                                }
                                JoshApplication.closeSpinnerProgress(mActivityContext);
                            } else {
                                JoshApplication.closeSpinnerProgress(mActivityContext);
                                JoshErrorResponse errorResponseObj = ErrorUtils.parseError(response);
                                String errorMsg, errorCode;
                                if (errorResponseObj != null) {
                                    errorMsg = errorResponseObj.getmMessage();
                                    errorCode = errorResponseObj.getmErrorCode();
                                    Log.d(TAG, "getKycDetailsApiCall UnSuccessfull - " + errorResponseObj.getmErrorString());
                                } else {
                                    errorMsg = getResources().getString(R.string.server_not_responding);
                                    errorCode = String.valueOf(response.code());
                                }

                                if(!errorCode.equalsIgnoreCase(ErrorUtils.MISSING_KYC_ERRORCODE)) {//No error dialogs for no kyc found errors
                                    JoshApplication.showFantasyErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.server_not_responding_title),
                                            errorMsg);
                                    JoshApplication.getFantasyErrorDialogOkButton(mActivityContext).setText(mActivityContext.getResources().getString(R.string.ok_heading));
                                    JoshApplication.getFantasyErrorDialog().setCancelable(false);
                                    JoshApplication.getFantasyErrorDialogOkButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            getActivity().finish();
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<List<FantasyAllMatchesResponseData>> call, Throwable t) {
                            JoshApplication.closeSpinnerProgress(mActivityContext);
                            Log.d(TAG, "getChangePasswordApiCall UnSuccessfull - " + t.getLocalizedMessage());
                            JoshApplication.showFantasyErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.server_not_responding_title),
                                    mActivityContext.getResources().getString(R.string.server_not_responding));

                            JoshApplication.getFantasyErrorDialogOkButton(mActivityContext).setText(mActivityContext.getResources().getString(R.string.ok_heading));
                            JoshApplication.getFantasyErrorDialog().setCancelable(false);
                            JoshApplication.getFantasyErrorDialogOkButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    getActivity().finish();
                                }
                            });
                        }
                    });
        }
        else{//NO INTERNET CONDITION
            JoshApplication.closeSpinnerProgress(mActivityContext);
            JoshApplication.showFantasyErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.no_internet_err_title),
                    mActivityContext.getResources().getString(R.string.no_internet_err));
            JoshApplication.getFantasyErrorDialogOkButton(mActivityContext).setText(mActivityContext.getResources().getString(R.string.ok_heading));
            JoshApplication.getFantasyErrorDialogOkButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        if(view == btnUpcomingMatches || view == btnLiveMatches || view == btnCompletedMatches){
            showMatches(view);
        }
    }

    private void showMatches(View view){
        List<FantasyAllMatchesResponseData> allMatchesResponseData = new ArrayList<>();
        if(view == btnUpcomingMatches){
            switchTabsVisual(btnUpcomingMatches);
            if(mFantasyAllMatchesdatumResponses !=null && mFantasyAllMatchesdatumResponses.size()>0) {
                allMatchesResponseData = getFilteredData(JoshApplication.FANTASY_MATCH_STATUS_UPCOMING);
                allMatchesAdapter.setListdata(getFilteredData(JoshApplication.FANTASY_MATCH_STATUS_UPCOMING));
                allMatchesAdapter.notifyDataSetChanged();
            }
        }
        else if(view == btnLiveMatches){
            switchTabsVisual(btnLiveMatches);
            if(mFantasyAllMatchesdatumResponses !=null && mFantasyAllMatchesdatumResponses.size()>0) {
                allMatchesResponseData = getFilteredData(JoshApplication.FANTASY_MATCH_STATUS_LIVE);
                allMatchesAdapter.setListdata(getFilteredData(JoshApplication.FANTASY_MATCH_STATUS_LIVE));
                allMatchesAdapter.notifyDataSetChanged();
            }
        }
        else if(view == btnCompletedMatches){
            switchTabsVisual(btnCompletedMatches);
            if(mFantasyAllMatchesdatumResponses !=null && mFantasyAllMatchesdatumResponses.size()>0) {
                allMatchesResponseData = getFilteredData(JoshApplication.FANTASY_MATCH_STATUS_COMPLETED);
                allMatchesAdapter.setListdata(getFilteredData(JoshApplication.FANTASY_MATCH_STATUS_COMPLETED));
                allMatchesAdapter.notifyDataSetChanged();
            }
        }

        if(allMatchesResponseData!=null && allMatchesResponseData.size()>0){
            allMatchesRecyclerView.setVisibility(View.VISIBLE);
            noMatchesError.setVisibility(View.GONE);
        }
        else{
            allMatchesRecyclerView.setVisibility(View.GONE);
            noMatchesError.setVisibility(View.VISIBLE);
        }
    }

    private void switchTabsVisual(final TextView activeTabView){
        TextView tabviews[] = {btnLiveMatches, btnCompletedMatches, btnUpcomingMatches};
        for(TextView view : tabviews){
            if(view == activeTabView){
                view.setTextColor(getResources().getColor(R.color.white));
                view.setBackground(getResources().getDrawable(R.drawable.flat_red_button_bg_without_corner));
            }
            else {
                view.setTextColor(getResources().getColor(R.color.black));
                view.setBackgroundColor(getResources().getColor(R.color.white));
            }
        }
    }

    private ArrayList<FantasyAllMatchesResponseData> getFilteredData(String matchStatus){
        ArrayList<FantasyAllMatchesResponseData> filteredData = new ArrayList<>();
        if(mFantasyAllMatchesdatumResponses !=null && mFantasyAllMatchesdatumResponses.size()>0) {
            for(FantasyAllMatchesResponseData fantasyMatchData : mFantasyAllMatchesdatumResponses){
                if(fantasyMatchData.getmStatus().equalsIgnoreCase(matchStatus))
                    filteredData.add(fantasyMatchData);
            }
        }
        return filteredData;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(allMatchesRecyclerView!=null)
            allMatchesRecyclerView.setAdapter(null);
    }

    private final class SortMatchData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Collections.sort(mFantasyAllMatchesdatumResponses, new Comparator<FantasyAllMatchesResponseData>() {
                @Override
                public int compare(FantasyAllMatchesResponseData o1, FantasyAllMatchesResponseData o2) {
                    return o1.getmStartDateTimestampInSeconds()>o2.getmStartDateTimestampInSeconds()?1:-1;
                }
        });

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            JoshApplication.closeSpinnerProgress(mActivityContext);
            final List<FantasyAllMatchesResponseData> allMatchesResponseData = getFilteredData(JoshApplication.FANTASY_MATCH_STATUS_UPCOMING);
            if(allMatchesResponseData.size()>0) {
                allMatchesAdapter = new FantasyAllMatchesAdapter(mActivityContext, allMatchesResponseData);
                allMatchesRecyclerView.setHasFixedSize(true);
                allMatchesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                allMatchesRecyclerView.setAdapter(allMatchesAdapter);
                allMatchesRecyclerView.setVisibility(View.VISIBLE);
                noMatchesError.setVisibility(View.GONE);
            }
            else{
                allMatchesRecyclerView.setVisibility(View.GONE);
                noMatchesError.setVisibility(View.VISIBLE);
            }
        }
    }
}