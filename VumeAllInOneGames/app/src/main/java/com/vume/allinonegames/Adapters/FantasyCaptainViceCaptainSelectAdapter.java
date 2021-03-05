package com.vume.allinonegames.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vume.allinonegames.Model.FantasyGetMatchDetailsResponseData;
import com.vume.allinonegames.R;
import com.vume.allinonegames.Util.JoshApplication;

import java.util.List;

public class FantasyCaptainViceCaptainSelectAdapter extends RecyclerView.Adapter<FantasyCaptainViceCaptainSelectAdapter.ViewHolder> {
    private List<FantasyGetMatchDetailsResponseData.FantasyPlayer> listdata;
    private Context context;
    private FantasyGetMatchDetailsResponseData.FantasyPlayer captainPlayer, viceCaptainPlayer;

    public interface TeamCapAndViceCapSelectionUpdateInterface {
        void setSelectedCaptainAndViceCaptain(FantasyGetMatchDetailsResponseData.FantasyPlayer captainPlayer,
                                              FantasyGetMatchDetailsResponseData.FantasyPlayer viceCaptainPlayer);
    }

    public FantasyCaptainViceCaptainSelectAdapter(Context context, List<FantasyGetMatchDetailsResponseData.FantasyPlayer> listdata) {
        this.listdata = listdata;
        this.context= context;
    }

    public void setListdata(List<FantasyGetMatchDetailsResponseData.FantasyPlayer> listdata){
        this.listdata = listdata;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItemView= layoutInflater.inflate(R.layout.captainvicecap_selectionlistitem, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final FantasyGetMatchDetailsResponseData.FantasyPlayer fantasyPlayer = listdata.get(position);
        String playerTypeShortName = "";
        holder.playerName.setText(fantasyPlayer.getmFullName());
        holder.playerTotalPoints.setText(String.valueOf(fantasyPlayer.getmScore()));

        if(captainPlayer!=null  && captainPlayer.getmId() == fantasyPlayer.getmId()){
            holder.selectCaptain.setText(context.getResources().getString(R.string.benefittwice));
            holder.selectCaptain.setBackground(context.getResources().getDrawable(R.drawable.round_blackicon_with_shadow));
            holder.selectCaptain.setTextColor(context.getResources().getColor(R.color.white));
        }else{
            holder.selectCaptain.setText(context.getResources().getString(R.string.captain_short));
            holder.selectCaptain.setBackground(context.getResources().getDrawable(R.drawable.roundbg_withgreyborder));
            holder.selectCaptain.setTextColor(context.getResources().getColor(R.color.black));
        }

        if(viceCaptainPlayer!=null  && viceCaptainPlayer.getmId() == fantasyPlayer.getmId()){
            holder.selectViceCaptain.setText(context.getResources().getString(R.string.benfitonepointfive));
            holder.selectViceCaptain.setBackground(context.getResources().getDrawable(R.drawable.round_blackicon_with_shadow));
            holder.selectViceCaptain.setTextColor(context.getResources().getColor(R.color.white));
        }else{
            holder.selectViceCaptain.setText(context.getResources().getString(R.string.vicecaptain_short));
            holder.selectViceCaptain.setBackground(context.getResources().getDrawable(R.drawable.roundbg_withgreyborder));
            holder.selectViceCaptain.setTextColor(context.getResources().getColor(R.color.black));
        }

        holder.selectCaptain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(captainPlayer != null && captainPlayer.getmId() == fantasyPlayer.getmId())////Alrady Captain Unselct player
                {
                    holder.selectCaptain.setText(context.getResources().getString(R.string.captain_short));
                    holder.selectCaptain.setBackground(context.getResources().getDrawable(R.drawable.roundbg_withgreyborder));
                    holder.selectCaptain.setTextColor(context.getResources().getColor(R.color.black));
                    captainPlayer = null;
                    notifyDataSetChanged();
                }
                else if(viceCaptainPlayer == null || (viceCaptainPlayer!=null && viceCaptainPlayer.getmId() != fantasyPlayer.getmId())){
                    //this player is already vice captain
                    holder.selectCaptain.setText(context.getResources().getString(R.string.benefittwice));
                    holder.selectCaptain.setBackground(context.getResources().getDrawable(R.drawable.round_blackicon_with_shadow));
                    holder.selectCaptain.setTextColor(context.getResources().getColor(R.color.white));
                    captainPlayer = fantasyPlayer;
                    notifyDataSetChanged();
                }
                else{
                    JoshApplication.showFantasyErrorDialog(context,
                            context.getResources().getString(R.string.error_title),
                            context.getResources().getString(R.string.captain_and_vicecaptain_different));
                }
                ((TeamCapAndViceCapSelectionUpdateInterface)context).setSelectedCaptainAndViceCaptain(captainPlayer, viceCaptainPlayer);
            }
        });

        holder.selectViceCaptain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viceCaptainPlayer!=null && viceCaptainPlayer.getmId() == fantasyPlayer.getmId())//Alrady ViceCaptain Unselct player
                {
                    holder.selectViceCaptain.setText(context.getResources().getString(R.string.vicecaptain_short));
                    holder.selectViceCaptain.setBackground(context.getResources().getDrawable(R.drawable.roundbg_withgreyborder));
                    holder.selectViceCaptain.setTextColor(context.getResources().getColor(R.color.black));
                    viceCaptainPlayer = null;
                    notifyDataSetChanged();
                }
                else  if(captainPlayer == null || (captainPlayer!=null && captainPlayer.getmId() != fantasyPlayer.getmId())){
                    //this player is already captain
                    holder.selectViceCaptain.setText(context.getResources().getString(R.string.benfitonepointfive));
                    holder.selectViceCaptain.setBackground(context.getResources().getDrawable(R.drawable.round_blackicon_with_shadow));
                    holder.selectViceCaptain.setTextColor(context.getResources().getColor(R.color.white));
                    viceCaptainPlayer = fantasyPlayer;
                    notifyDataSetChanged();
                }
                else{
                    JoshApplication.showFantasyErrorDialog(context,
                            context.getResources().getString(R.string.error_title),
                            context.getResources().getString(R.string.captain_and_vicecaptain_different));
                }
                ((TeamCapAndViceCapSelectionUpdateInterface)context).setSelectedCaptainAndViceCaptain(captainPlayer, viceCaptainPlayer);
            }
        });

        if(fantasyPlayer.isTeamA()){
            holder.teamShortName.setText(JoshApplication.currentSelectedMatchData.getTeams().get(0).getmTeamShortName());
            holder.playerAvatar.setImageResource(R.mipmap.cricket_opponent_player_avatar);
        }
        else{
            holder.teamShortName.setText(JoshApplication.currentSelectedMatchData.getTeams().get(1).getmTeamShortName());
            holder.playerAvatar.setImageResource(R.mipmap.cricket_player_avatar);
        }

        if(fantasyPlayer.isAllRounder()){
            playerTypeShortName = context.getResources().getString(R.string.allrounder_shortname);
        }
        else if(fantasyPlayer.isBowler()){
            playerTypeShortName = context.getResources().getString(R.string.bowler_shortname);
        }
        else if(fantasyPlayer.isBatsman()){
            playerTypeShortName = context.getResources().getString(R.string.batsman_shortname);
        }
        else if(fantasyPlayer.isKeeper()){
            playerTypeShortName = context.getResources().getString(R.string.wicketkeeper_shortname);
        }

        holder.playerTypeShort.setText(playerTypeShortName);

        //Group by Player Types
        if(position>0 &&
                ((fantasyPlayer.isKeeper() && (!listdata.get(position-1).isKeeper())) ||
                (fantasyPlayer.isBatsman() && (!listdata.get(position-1).isBatsman())) ||
                (fantasyPlayer.isBowler() && (!listdata.get(position-1).isBowler())) ||
                (fantasyPlayer.isAllRounder() && (!listdata.get(position-1).isAllRounder()))
                )){
            holder.bigDivider.setVisibility(View.VISIBLE);
            holder.smallDivider.setVisibility(View.GONE);
        }
        else {
            holder.bigDivider.setVisibility(View.GONE);
            holder.smallDivider.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View itemView, smallDivider, bigDivider;
        private TextView selectCaptain, selectViceCaptain,
                selectCaptainPercent, selectViceCaptainPercent,
                playerName, playerTotalPoints, teamShortName, playerTypeShort;
        private ImageView playerInfo, playerAvatar;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.smallDivider = itemView.findViewById(R.id.small_divider);
            this.bigDivider = itemView.findViewById(R.id.big_divider    );

            this.selectCaptain = itemView.findViewById(R.id.captain_select);
            this.selectViceCaptain = itemView.findViewById(R.id.vicecaptain_select);
            this.selectCaptainPercent = itemView.findViewById(R.id.captain_selected_percent);
            this.selectViceCaptainPercent = itemView.findViewById(R.id.vicecaptain_selected_percent);
            this.playerName = itemView.findViewById(R.id.player_name);
            this.playerTotalPoints = itemView.findViewById(R.id.player_totalpoints);
            this.teamShortName = itemView.findViewById(R.id.teamshortname);
            this.playerTypeShort = itemView.findViewById(R.id.playertypeshortname);

            this.playerInfo = itemView.findViewById(R.id.player_info);
            this.playerAvatar = itemView.findViewById(R.id.player_avatar);
        }
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
    }
}