package com.vume.allinonegames.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.vume.allinonegames.GameWebviewActivity;
import com.vume.allinonegames.Model.JoshLobbyCardListResponseData;
import com.vume.allinonegames.Model.JoshLobbyCardListResponseFilteredData;
import com.vume.allinonegames.Model.JoshOpenGamesResponseData;
import com.vume.allinonegames.R;
import com.vume.allinonegames.Util.JoshApplication;
import com.vume.allinonegames.Util.JoshSharedPreferences;

import java.util.List;

import static com.vume.allinonegames.GameSelectPriceActivity.ARGS_BUYIN_AMOUNT;
import static com.vume.allinonegames.GameSelectPriceActivity.ARGS_BUYIN_TABLEID;
import static com.vume.allinonegames.GameSelectPriceActivity.ARGS_BUYIN_TABLE_TICKETID;
import static com.vume.allinonegames.GameSelectPriceActivity.ARGS_BUYIN_TABLE_WAGERID;
import static com.vume.allinonegames.GameSelectPriceActivity.ARGS_LAUNCHINGFROM;

public class OpenGamesCustomAdapter extends RecyclerView.Adapter<OpenGamesCustomAdapter.ViewHolder> {

    private Context context;
    private List<JoshOpenGamesResponseData> openGamesResponseData;

    public OpenGamesCustomAdapter(Context context, List<JoshOpenGamesResponseData> openGamesResponseData){
        this.context = context;
        this.openGamesResponseData = openGamesResponseData;
    }

    public void setList(List<JoshOpenGamesResponseData> openGamesResponseData){
        this.openGamesResponseData = openGamesResponseData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.open_games_card_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final JoshOpenGamesResponseData currentOpenGameData = openGamesResponseData.get(position);
        JoshLobbyCardListResponseData currentTableData = null;
        if(JoshApplication.getCurrentGameType().equalsIgnoreCase(JoshApplication.RUMMY_GAME_TYPE)) {
            currentTableData = JoshApplication.joshLobbyCardDataForTableID(currentOpenGameData.getmTableId());
        }
        else if(JoshApplication.getCurrentGameType().equalsIgnoreCase(JoshApplication.CALLBREAK_GAME_TYPE)){
            currentTableData = JoshApplication.joshLobbyCallbreakCardDataForTableID(currentOpenGameData.getmTableId());
        }

        if(currentTableData!=null) {
            holder.mGameName.setText(currentTableData.getmTableName());
            holder.mBetAmount.setText(context.getResources().getString(R.string.rupee_sign) + currentTableData.getmBetValue());

            if (currentTableData.getmCategory().equalsIgnoreCase(JoshLobbyCardListResponseFilteredData.TABLE_CATEGORY_BESTOF) &&
                    (JoshApplication.getCurrentGameType().equalsIgnoreCase(JoshApplication.RUMMY_GAME_TYPE))) { //Show deals icons only for Rummy game, IKf callbreak, show players only
                holder.mNoOfPlayersOrDeals.setText(String.valueOf(currentTableData.getmTableLimit()));
                if (currentTableData.getmTableLimit() <= JoshLobbyCardListResponseFilteredData.TABLE_NO_OF_DEALS_TWO) {//For the 2 deals
                    holder.mNoOfPlayerorDealsImg.setImageResource(R.mipmap.icon_twodeals);
                } else if (currentTableData.getmTableLimit() > JoshLobbyCardListResponseFilteredData.TABLE_NO_OF_DEALS_TWO) {//for the 3 deals
                    holder.mNoOfPlayerorDealsImg.setImageResource(R.mipmap.icon_multipledeals);
                }
            } else {//show player icons
                holder.mNoOfPlayersOrDeals.setText(String.valueOf(currentTableData.getmNumOfPlayers()));
                if (currentTableData.getmNumOfPlayers() <= JoshLobbyCardListResponseFilteredData.TABLE_NO_OF_PLAYERS_TWO) {//For 2 players
                    holder.mNoOfPlayerorDealsImg.setImageResource(R.mipmap.icon_twoplayers);
                } else if (currentTableData.getmNumOfPlayers() > JoshLobbyCardListResponseFilteredData.TABLE_NO_OF_PLAYERS_TWO) {//For 6 players
                    holder.mNoOfPlayerorDealsImg.setImageResource(R.mipmap.icon_multiplayers);
                }
            }

            holder.mCardLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openGameinWebView(currentOpenGameData.getmWagerId(), currentOpenGameData.getmTicketId(), currentOpenGameData.getmTableId(), 0.0f);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return openGamesResponseData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView mGameName, mNoOfPlayersOrDeals, mBetAmount;
        ImageView mNoOfPlayerorDealsImg;
        ConstraintLayout mCardLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            mGameName = itemView.findViewById(R.id.label_gamename);
            mNoOfPlayersOrDeals = itemView.findViewById(R.id.label_noof_playerordeals);
            mNoOfPlayerorDealsImg = itemView.findViewById(R.id.img_noofplayersordeals);
            mBetAmount = itemView.findViewById(R.id.label_rupees);
            mCardLayout = itemView.findViewById(R.id.card_layout);
        }
    }

    private void openGameinWebView(String wagerId, String ticketId, String tableId, Float buyInAmount){
        Intent gameWebviewIntent = new Intent(context, GameWebviewActivity.class);
        JoshApplication.sharedPreferences(context).saveToSharedPreferences(JoshSharedPreferences.SHOULD_REFRESH_BALANCE_ON_LOBBYRESUME, true);
        Bundle buyInBundles = new Bundle();
        buyInBundles.putString(ARGS_BUYIN_TABLE_TICKETID, ticketId);
        buyInBundles.putString(ARGS_BUYIN_TABLE_WAGERID, wagerId);
        buyInBundles.putString(ARGS_BUYIN_TABLEID, tableId);
        buyInBundles.putFloat(ARGS_BUYIN_AMOUNT, buyInAmount);
        buyInBundles.putString(ARGS_LAUNCHINGFROM, GameWebviewActivity.LAUNCHING_FROM_MYOPENGAMES);
        gameWebviewIntent.putExtras(buyInBundles);
        gameWebviewIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(gameWebviewIntent);
        ((Activity)context).overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
    }
}
