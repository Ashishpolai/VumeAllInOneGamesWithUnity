package com.vume.allinonegames.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vume.allinonegames.Model.FantasyGetMatchDetailsResponseData;
import com.vume.allinonegames.R;
import com.vume.allinonegames.Util.JoshApplication;

import java.util.ArrayList;
import java.util.List;

public class FantasyAllPlayersSelectAdapter extends RecyclerView.Adapter<FantasyAllPlayersSelectAdapter.ViewHolder> {
    private List<FantasyGetMatchDetailsResponseData.FantasyPlayer> listdata;
    private FantasyGetMatchDetailsResponseData fantasyMatchData;
    private List<FantasyGetMatchDetailsResponseData.FantasyPlayer> selectedPlayerList = new ArrayList<>();
    private Context context;
    private int selectedBatsmanCount = 0, selectedBowlerCount = 0, selectedAllrounderCount = 0, selectedKeeperCount = 0,
            selectedTeamAPlayers = 0, selectedTeamBPlayers = 0;;
    private double totalCreditsOfSelectedPlayers = 0.0;

    public interface TeamSelectionUpdateInterface {
        void setSelectedFantasyPlayerList(List<FantasyGetMatchDetailsResponseData.FantasyPlayer> fantasyPlayerList);
    }

    public FantasyAllPlayersSelectAdapter(Context context, List<FantasyGetMatchDetailsResponseData.FantasyPlayer> listdata) {
        this.listdata = listdata;
        this.context= context;
    }

    public void setListdata(List<FantasyGetMatchDetailsResponseData.FantasyPlayer> listdata){
        this.listdata = listdata;
    }

    public void setMatchDetailsData(FantasyGetMatchDetailsResponseData matchData){
        fantasyMatchData = matchData;
    }

    public void setSelectedPlayerCount(int selectedBatsmanCount, int selectedBowlerCount,
                                       int selectedKeeperCount, int selectedAllrounderCount,
                                       int selectedTeamAPlayers, int selectedTeamBPlayers,
                                       double totalCreditsOfSelectedPlayers){
        this.selectedBatsmanCount = selectedBatsmanCount;
        this.selectedBowlerCount = selectedBowlerCount;
        this.selectedKeeperCount = selectedKeeperCount;
        this.selectedAllrounderCount = selectedAllrounderCount;
        this.selectedTeamAPlayers = selectedTeamAPlayers;
        this.selectedTeamBPlayers = selectedTeamBPlayers;
        this.totalCreditsOfSelectedPlayers = totalCreditsOfSelectedPlayers;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItemView= layoutInflater.inflate(R.layout.all_players_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final FantasyGetMatchDetailsResponseData.FantasyPlayer fantasyPlayer = listdata.get(position);
        final int teamSelectionIndicator = fantasyPlayer.isTeamA()? R.mipmap.cricket_player_icon : R.mipmap.cricket_opponent_player_icon;
        final String teamName = fantasyPlayer.isTeamA()?
                fantasyMatchData.getmFantasyTeamList().get(0).getmName():fantasyMatchData.getmFantasyTeamList().get(1).getmName();

        if(selectedPlayerList.contains(fantasyPlayer)){
            holder.playerUnselectedBg.setVisibility(View.GONE);
            holder.playerSelectionIndicator.setImageResource(R.mipmap.cancel_player);
        }
        else{
            holder.playerUnselectedBg.setVisibility(View.VISIBLE);
            holder.playerSelectionIndicator.setImageResource(R.mipmap.add_player);
        }

        holder.playerTeamIndicator.setImageResource(teamSelectionIndicator);
        holder.playerName.setText(fantasyPlayer.getmFullName());
        holder.playerTeamAndPoints.setText(teamName);
        holder.playerCredit.setText(String.valueOf(fantasyPlayer.getmCreditValue()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (selectedPlayerList.contains(fantasyPlayer)) { //Selected Player, Unselect the player
                        selectedPlayerList.remove(fantasyPlayer);
                        holder.playerUnselectedBg.setVisibility(View.VISIBLE);
                        holder.playerSelectionIndicator.setImageResource(R.mipmap.add_player);
                    } else {//UnSelected Player, Select the player
                        if (fantasyPlayer.isKeeper() && selectedKeeperCount >= JoshApplication.MAX_KEEPER_REQ) {
                            Toast.makeText(context, context.getResources().getString(R.string.max_keepers_selected), Toast.LENGTH_SHORT).show();
                            return;
                        } else if (fantasyPlayer.isBatsman() && selectedBatsmanCount >= JoshApplication.MAX_BATSMAN_REQ) {
                            Toast.makeText(context, context.getResources().getString(R.string.max_batsman_selected), Toast.LENGTH_SHORT).show();
                            return;
                        } else if (fantasyPlayer.isBowler() && selectedBowlerCount >= JoshApplication.MAX_BOWLER_REQ) {
                            Toast.makeText(context, context.getResources().getString(R.string.max_bowlers_selected), Toast.LENGTH_SHORT).show();
                            return;
                        } else if (fantasyPlayer.isAllRounder() && selectedAllrounderCount >= JoshApplication.MAX_ALLROUNDER_REQ) {
                            Toast.makeText(context, context.getResources().getString(R.string.max_allrounders_selected), Toast.LENGTH_SHORT).show();
                            return;
                        }else if(selectedPlayerList.size()>=JoshApplication.MAX_PLAYERS_REQ) {//MORE PLAYERS NEED TO BE SELECTED
                            Toast.makeText(context, context.getResources().getString(R.string.max_players_selected), Toast.LENGTH_SHORT).show();
                            return;
                        }else if((totalCreditsOfSelectedPlayers+fantasyPlayer.getmCreditValue())>JoshApplication.MAX_CREDITS_AVAILABLE){
                            Toast.makeText(context, context.getResources().getString(R.string.max_credits_reached), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        selectedPlayerList.add(fantasyPlayer);
                        holder.playerUnselectedBg.setVisibility(View.GONE);
                        holder.playerSelectionIndicator.setImageResource(R.mipmap.cancel_player);
                    }
                    ((TeamSelectionUpdateInterface) context).setSelectedFantasyPlayerList(selectedPlayerList);


            }
        });
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View itemView, playerUnselectedBg;
        public ImageView playerTeamIndicator, playerSelectionIndicator;
        public TextView playerName, playerTeamAndPoints, playerSelectedPercent, playerCredit;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.playerUnselectedBg = itemView.findViewById(R.id.unselectedBg);
            this.playerTeamIndicator = itemView.findViewById(R.id.player_team_indicator);
            this.playerSelectionIndicator = itemView.findViewById(R.id.player_selection_indicator);
            this.playerName = itemView.findViewById(R.id.player_name);
            this.playerTeamAndPoints = itemView.findViewById(R.id.player_team_points_heading);
            this.playerSelectedPercent = itemView.findViewById(R.id.selected_percent_heading);
            this.playerCredit = itemView.findViewById(R.id.credits_heading);
        }

    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);

    }
}