package com.vume.allinonegames.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vume.allinonegames.Model.FantasyContestDetailsWithMatchAndPlayerData;
import com.vume.allinonegames.R;
import com.vume.allinonegames.Util.JoshApplication;

import java.util.List;

;

public class FantasyAllContestPrizesAdapter extends RecyclerView.Adapter<FantasyAllContestPrizesAdapter.ViewHolder> {
    private List<FantasyContestDetailsWithMatchAndPlayerData.FantasyprizesData> listdata;
    private Context context;

    // RecyclerView recyclerView;
    public FantasyAllContestPrizesAdapter(Context context, List<FantasyContestDetailsWithMatchAndPlayerData.FantasyprizesData> listdata) {
        this.listdata = listdata;
        this.context= context;
    }

    public void setListdata(List<FantasyContestDetailsWithMatchAndPlayerData.FantasyprizesData> listdata){
        this.listdata = listdata;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItemView= layoutInflater.inflate(R.layout.prizze_amount_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final FantasyContestDetailsWithMatchAndPlayerData.FantasyprizesData currentData = listdata.get(position);
        if(currentData.getmMinRank() == currentData.getmMaxrank()){
            holder.mRank.setText(String.valueOf(currentData.getmMinRank()));
        }
        else if(currentData.getmMinRank() < currentData.getmMaxrank()){
            holder.mRank.setText(currentData.getmMinRank() + " - "+
                    currentData.getmMaxrank());
        }

        holder.mRankPrizeMoney.setText(context.getResources().getString(R.string.rupee_sign)+""+
                JoshApplication.getNumberInWordsRupee(Math.round(currentData.getmPrizeAmount())));
    }


    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View itemView;
        public TextView mRank, mRankPrizeMoney;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            mRank = itemView.findViewById(R.id.rank_value);
            mRankPrizeMoney = itemView.findViewById(R.id.rank_prize_amount);
        }
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
    }
}