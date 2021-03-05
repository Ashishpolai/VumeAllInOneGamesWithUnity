package com.vume.allinonegames.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vume.allinonegames.ContestDetailsActivity;
import com.vume.allinonegames.CreateTeamActivity;
import com.vume.allinonegames.JoinContestSelectTeamActivity;
import com.vume.allinonegames.Model.FantasyContestsForMatchResponseData;
import com.vume.allinonegames.R;
import com.vume.allinonegames.Util.JoshApplication;

import java.math.BigDecimal;
import java.util.List;

;

public class FantasyAllContestsAdapter extends RecyclerView.Adapter<FantasyAllContestsAdapter.ViewHolder> implements View.OnClickListener {
    private List<FantasyContestsForMatchResponseData> listdata;
    private Context context;

    private int teamsJoinedProgress = 0, totalWinners = 0;
    private Double firstPrizeAmount = 0.0, totalPrizeAmount = 0.0;
    private String firstRankExtraPrize = "";

    // RecyclerView recyclerView;
    public FantasyAllContestsAdapter(Context context, List<FantasyContestsForMatchResponseData> listdata) {
        this.listdata = listdata;
        this.context= context;
    }

    public void setListdata(List<FantasyContestsForMatchResponseData> listdata){
        this.listdata = listdata;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItemView= layoutInflater.inflate(R.layout.all_contests_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final FantasyContestsForMatchResponseData currentData = listdata.get(position);

        holder.mContestTag.setText(currentData.getmNote());
        holder.mContestEntryFee.setText(String.valueOf(context.getResources().getString(R.string.rupee_sign)+currentData.getmEntryFees()));
        holder.mContestTotalTeamsJoined.setText(context.getResources().getString(R.string.teams_joined_data,
                String.valueOf(currentData.getmNumTeams()), String.valueOf(currentData.getmMaxTeams())));

        Double teamsJoinedProgressDouble = ((double)currentData.getmNumTeams()/currentData.getmMaxTeams()) * 100;
        teamsJoinedProgress = (int) Math.round(teamsJoinedProgressDouble);
        holder.mContestFillProgressBar.setProgress(teamsJoinedProgress);

        setupPrizes(currentData);

        holder.mContestTotalPrizeMoney.setText(context.getResources().getString(R.string.rupee_sign)+ new BigDecimal(Math.round(totalPrizeAmount)));
        holder.mContestFirstprizeMoney.setText(context.getResources().getString(R.string.rupee_sign)+ new BigDecimal(Math.round(firstPrizeAmount)));
        holder.mContestTotalWinners.setText(context.getResources().getString(R.string.total_winners, String.valueOf(totalWinners)));
        if(firstRankExtraPrize != null && (!firstRankExtraPrize.equalsIgnoreCase(""))){
            holder.mContestExtraPrize.setText(firstRankExtraPrize);
            holder.mContestExtraPrize.setVisibility(View.VISIBLE);
            holder.mPlusIcon.setVisibility(View.VISIBLE);
        }
        else{
            holder.mContestExtraPrize.setVisibility(View.GONE);
            holder.mPlusIcon.setVisibility(View.GONE);
        }

        View.OnClickListener winnersDetailsOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWinnersPrizeAmountDetails(currentData);
            }
        };

        holder.mContestTotalWinners.setOnClickListener(winnersDetailsOnClick);
        holder.mContestTotalWinnersDropdown.setOnClickListener(winnersDetailsOnClick);

        holder.btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(JoshApplication.mySelectedMatchSavedTeamList.size()>0) {
                    currentData.setmTotalWinnings(Math.round(totalPrizeAmount));
                    JoshApplication.selectedContest = currentData;
                    Intent selectTeamForContestActivity = new Intent(context, JoinContestSelectTeamActivity.class);
                    selectTeamForContestActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(selectTeamForContestActivity);
                    ((Activity)context).overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
                }
                else{
                    Toast.makeText(context, context.getResources().getString(R.string.no_saved_teams_found), Toast.LENGTH_SHORT).show();
                    Intent createTeamIntent = new Intent(context, CreateTeamActivity.class);
                    createTeamIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(createTeamIntent);
                    ((Activity)context).overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentData.setmTotalWinnings(Math.round(totalPrizeAmount));
                JoshApplication.selectedContest = currentData;
                Intent contestDetailsIntent = new Intent(context, ContestDetailsActivity.class);
                contestDetailsIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                context.startActivity(contestDetailsIntent);
                ((Activity)context).overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
            }
        });
    }

    private void setupPrizes(final FantasyContestsForMatchResponseData currentData){
        final List<FantasyContestsForMatchResponseData.FantasyprizesData> prizeDataList =  currentData.getmPrizesList();
        totalPrizeAmount = 0.0;
        for(int x =0; x<prizeDataList.size(); x++){
            final FantasyContestsForMatchResponseData.FantasyprizesData prizeData = prizeDataList.get(x);
            if(prizeData.getmMinRank() == 1){
                firstPrizeAmount = prizeData.getmPrizeAmount();
                firstRankExtraPrize = prizeData.getmExtraPrize();
            }

            if(x == (prizeDataList.size()-1)){
                totalWinners = prizeData.getmMaxrank();
            }

            if(prizeData.getmMaxrank() > prizeData.getmMinRank()) {
                totalPrizeAmount += prizeData.getmPrizeAmount() * (prizeData.getmMaxrank() - prizeData.getmMinRank());
            }
            else if(prizeData.getmMaxrank() == prizeData.getmMinRank()){
                totalPrizeAmount += prizeData.getmPrizeAmount();
            }
        }
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
    }

    private void showWinnersPrizeAmountDetails(FantasyContestsForMatchResponseData currentData){
        final Dialog errorDialog = new Dialog(context, R.style.ErrorThemeDialogCustom);
        errorDialog.setContentView(R.layout.winners_details_dialog);
        errorDialog.setCanceledOnTouchOutside(true);
        errorDialog.setCancelable(true);
        final Window window = errorDialog.getWindow();

        RecyclerView recyclerview = (RecyclerView) errorDialog.findViewById(R.id.prizeamount_recyclerview);
        FantasyAllPrizesInDialogAdapter allPrizesAdapter = new FantasyAllPrizesInDialogAdapter(context, currentData.getmPrizesList());
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(context));
        recyclerview.setAdapter(allPrizesAdapter);

        ((Button) errorDialog.findViewById(R.id.btn_right)).setText(context.getResources().getString(R.string.got_it));
        ((Button) errorDialog.findViewById(R.id.btn_right)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(errorDialog !=null){
                    errorDialog.dismiss();
                    //errorDialog = null;
                }
            }
        });
        DisplayMetrics displayMetrics = new DisplayMetrics();
        window.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int displayWidth = displayMetrics.widthPixels;
        final int displayHeight = displayMetrics.heightPixels;
        final WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(window.getAttributes());
        // Set alert dialog width equal to screen width 70%
        int dialogWindowWidth = (int) (displayWidth * 0.9f);
        // Set alert dialog height equal to screen height 70%
        int dialogWindowHeight = (int) (displayHeight * 0.4f);

        layoutParams.width = dialogWindowWidth;

        // Apply the newly created layout parameters to the alert dialog window
        window.setAttributes(layoutParams);
        errorDialog.show();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View itemView;
        public TextView mContestTag, mContestTotalPrizeMoney, mContestExtraPrize, mContestEntryFee, mContestFirstprizeMoney,
                    mContestTotalWinners, mContestTotalTeamsJoined;
        public ImageView mPlusIcon, mContestTotalWinnersDropdown;
        public ProgressBar mContestFillProgressBar;
        public Button btnJoin;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;

            this.mContestTag = itemView.findViewById(R.id.contest_tag);
            this.mContestTotalPrizeMoney = itemView.findViewById(R.id.contest_prize_money);
            this.mContestExtraPrize = itemView.findViewById(R.id.contest_extra_prize);
            this.mContestEntryFee = itemView.findViewById(R.id.contest_entry_fee);
            this.mContestFirstprizeMoney = itemView.findViewById(R.id.contest_first_prize);
            this.mContestTotalWinners = itemView.findViewById(R.id.contest_total_winners);
            this.mContestTotalWinnersDropdown = itemView.findViewById(R.id.contest_total_winners_dropdown);
            this.mContestTotalTeamsJoined = itemView.findViewById(R.id.contest_teamjoined_details);
            this.btnJoin = itemView.findViewById(R.id.btn_join);

            mPlusIcon = itemView.findViewById(R.id.icon_plus);
            mContestFillProgressBar = itemView.findViewById(R.id.contest_teams_progressbar);
        }
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
    }
}