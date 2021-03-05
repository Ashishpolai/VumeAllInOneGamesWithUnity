package com.vume.allinonegames.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vume.allinonegames.Model.FantasyContestsForMatchResponseData;
import com.vume.allinonegames.R;
import com.vume.allinonegames.Util.JoshApplication;

import java.util.List;

;

public class FantasyAllPrizesInDialogAdapter extends RecyclerView.Adapter<FantasyAllPrizesInDialogAdapter.ViewHolder> implements View.OnClickListener {
    private List<FantasyContestsForMatchResponseData.FantasyprizesData> listdata;
    private Context context;

    // RecyclerView recyclerView;
    public FantasyAllPrizesInDialogAdapter(Context context, List<FantasyContestsForMatchResponseData.FantasyprizesData> listdata) {
        this.listdata = listdata;
        this.context= context;
    }

    public void setListdata(List<FantasyContestsForMatchResponseData.FantasyprizesData> listdata){
        this.listdata = listdata;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItemView= layoutInflater.inflate(R.layout.all_winnerprizes_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final FantasyContestsForMatchResponseData.FantasyprizesData currentData = listdata.get(position);

        if(currentData.getmMinRank() == currentData.getmMaxrank()){
            holder.mRankText.setText(JoshApplication.getOrdinalOfNumber(currentData.getmMinRank()));
        }
        else if(currentData.getmMinRank() < currentData.getmMaxrank()){
            holder.mRankText.setText(JoshApplication.getOrdinalOfNumber(currentData.getmMinRank()) + " to "+
                    JoshApplication.getOrdinalOfNumber(currentData.getmMaxrank()) );
        }

        holder.mPrizeAmountText.setText(context.getResources().getString(R.string.rupee_sign)+" "+Math.round(currentData.getmPrizeAmount()));
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
        public TextView mRankText, mPrizeAmountText;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.mRankText = itemView.findViewById(R.id.rank_value);
            this.mPrizeAmountText = itemView.findViewById(R.id.prizeamount_value);
        }


    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
    }
}