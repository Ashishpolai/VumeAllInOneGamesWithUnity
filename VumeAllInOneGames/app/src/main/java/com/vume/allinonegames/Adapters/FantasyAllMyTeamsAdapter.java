package com.vume.allinonegames.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vume.allinonegames.Model.FantasyGetMyTeamsResponseData;
import com.vume.allinonegames.R;
import com.vume.allinonegames.Util.JoshApplication;

import java.util.List;

;

public class FantasyAllMyTeamsAdapter extends RecyclerView.Adapter<FantasyAllMyTeamsAdapter.ViewHolder> implements View.OnClickListener {
    private List<FantasyGetMyTeamsResponseData> listdata;
    private Context context;

    private int teamsJoinedProgress = 0, totalWinners = 0;
    private Double firstPrizeAmount = 0.0, totalPrizeAmount = 0.0;
    private String firstRankExtraPrize = "";

    int selectedTeamId = 0;

    public interface SelectedTeamForContest {
        void setSelectedTeamIdForContest(int selectedTeamIdForContest);
    }

    // RecyclerView recyclerView;
    public FantasyAllMyTeamsAdapter(Context context, List<FantasyGetMyTeamsResponseData> listdata) {
        this.listdata = listdata;
        this.context= context;
    }

    public void setListdata(List<FantasyGetMyTeamsResponseData> listdata){
        this.listdata = listdata;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItemView= layoutInflater.inflate(R.layout.all_myteams_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final FantasyGetMyTeamsResponseData currentData = listdata.get(position);

        holder.txtTeamName.setText(currentData.mName);
        if(currentData.getCaptainPlayer()!=null)
            holder.txtCaptainName.setText(currentData.getCaptainPlayer().mPlayerName);
        if(currentData.getViceCaptainPlayer()!=null)
            holder.txtViceCaptainName.setText(currentData.getViceCaptainPlayer().mPlayerName);

        if(selectedTeamId == currentData.mMyTeamId){
            holder.teamSelectionCheckbox.setImageResource(R.mipmap.checkbox_checked);
        }
        else{
            holder.teamSelectionCheckbox.setImageResource(R.mipmap.checkbox_unchecked);
        }

        holder.btnTeamPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JoshApplication.toast(context, "Team preview");
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTeamId = currentData.mMyTeamId;
                ((SelectedTeamForContest)context).setSelectedTeamIdForContest(selectedTeamId);
                notifyDataSetChanged();
            }
        });
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View itemView;
        public ImageView teamSelectionCheckbox;
        public TextView txtTeamName, txtCaptainName, txtViceCaptainName;
        public LinearLayout btnTeamPreview;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.teamSelectionCheckbox = itemView.findViewById(R.id.selectteamradiobutton);
            this.txtTeamName = itemView.findViewById(R.id.teamname);
            this.txtCaptainName = itemView.findViewById(R.id.txt_captainname);
            this.txtViceCaptainName = itemView.findViewById(R.id.txt_vicecaptainname);
            this.btnTeamPreview = itemView.findViewById(R.id.team_preview_layout);
        }
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
    }
}