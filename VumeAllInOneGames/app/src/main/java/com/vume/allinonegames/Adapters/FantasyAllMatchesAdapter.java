package com.vume.allinonegames.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.vume.allinonegames.MatchContestsActivity;
import com.vume.allinonegames.Model.FantasyAllMatchesResponseData;
import com.vume.allinonegames.R;
import com.vume.allinonegames.Util.JoshApplication;

import java.util.List;

;

public class FantasyAllMatchesAdapter extends RecyclerView.Adapter<FantasyAllMatchesAdapter.ViewHolder> {
    private List<FantasyAllMatchesResponseData> listdata;
    private Context context;

    // RecyclerView recyclerView;
    public FantasyAllMatchesAdapter(Context context, List<FantasyAllMatchesResponseData> listdata) {
        this.listdata = listdata;
        this.context= context;
    }

    public void setListdata(List<FantasyAllMatchesResponseData> listdata){
        this.listdata = listdata;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItemView= layoutInflater.inflate(R.layout.all_matches_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final FantasyAllMatchesResponseData currentData = listdata.get(position);
        holder.mMatchTitle.setText(currentData.getmName());

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.app_icon)
                .fitCenter()
                .error(R.mipmap.app_icon);

        if(currentData.getTeams().size()>=2) {
            holder.mOpponentOneName.setText(currentData.getTeams().get(0).getmTeamShortName());
            Glide.with(context)
                    .load(currentData.getTeams().get(0).getmTeamIconUrl().replace("http:", "https:"))
                    .apply(options)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.d("asisi", e.getMessage());
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(holder.mOpponentOneIcon);


            holder.mOpponentTwoName.setText(currentData.getTeams().get(1).getmTeamShortName());
            Glide.with(context)
                    .load(currentData.getTeams().get(1).getmTeamIconUrl().replace("http:", "https:"))
                    .apply(options)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.d("asisi", e.getMessage());
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(holder.mOpponentTwoIcon);
        }

        if (holder.countDownTimer != null) {
            holder.countDownTimer.cancel();
        }

        if (currentData.getmStatus().equalsIgnoreCase(JoshApplication.FANTASY_MATCH_STATUS_UPCOMING)) {
            long currenttimestamp = System.currentTimeMillis();
            long expiryTime = (currentData.getmStartDateTimestampInSeconds()*1000)-currenttimestamp;
            if(expiryTime>0) {
                holder.countDownTimer = new CountDownTimer(expiryTime, 1000) {

                    public void onTick(long millisUntilFinished) {
                        holder.mMatchStartTimeCountdown.setText(context.getResources().getString(R.string.match_countdown_timer,
                                String.valueOf(JoshApplication.calculateTime(millisUntilFinished))));
                    }

                    public void onFinish() {
                        cancel();
                    }

                }.start();
            }
            else{
                holder.mMatchStartTimeCountdown.setText("");
            }
            Log.d("asisi", "FANTASY_MATCH_STATUS_UPCOMING");
        }
        else if (currentData.getmStatus().equalsIgnoreCase(JoshApplication.FANTASY_MATCH_STATUS_LIVE)) {
            holder.mMatchStartTimeCountdown.setText(context.getResources().getString(R.string.live_now_msg));
            Log.d("asisi", "FANTASY_MATCH_STATUS_LIVE");
        }
        else if (currentData.getmStatus().equalsIgnoreCase(JoshApplication.FANTASY_MATCH_STATUS_COMPLETED)) {
            holder.mMatchStartTimeCountdown.setText(context.getResources().getString(R.string.match_ended_msg));
            Log.d("asisi", "FANTASY_MATCH_STATUS_COMPLETED");
        }

        //ONly upcoming matches can see contests
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentData.getmStatus().equalsIgnoreCase(JoshApplication.FANTASY_MATCH_STATUS_UPCOMING)) {
                    JoshApplication.currentSelectedMatchData = currentData;
                    Intent contestIntent = new Intent(context, MatchContestsActivity.class);
                    contestIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(contestIntent);
                    ((Activity)context).overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
                }
                else if(currentData.getmStatus().equalsIgnoreCase(JoshApplication.FANTASY_MATCH_STATUS_LIVE)){
                    Toast.makeText(context, context.getResources().getString(R.string.match_live_message), Toast.LENGTH_SHORT).show();
                }
                else if(currentData.getmStatus().equalsIgnoreCase(JoshApplication.FANTASY_MATCH_STATUS_COMPLETED)){
                    Toast.makeText(context, context.getResources().getString(R.string.match_completed_message), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View itemView;
        public TextView mMatchTitle, mOpponentOneName, mOpponentTwoName, mMatchStartTimeCountdown;
        public ImageView mOpponentOneIcon, mOpponentTwoIcon;
        public CountDownTimer countDownTimer;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            mMatchTitle = itemView.findViewById(R.id.match_title);
            mOpponentOneName = itemView.findViewById(R.id.opponent_one_name);
            mOpponentTwoName = itemView.findViewById(R.id.opponent_two_name);
            mMatchStartTimeCountdown = itemView.findViewById(R.id.match_start_time);

            mOpponentOneIcon = itemView.findViewById(R.id.opponent_one_icon);
            mOpponentTwoIcon = itemView.findViewById(R.id.opponent_two_icon);
        }


    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        //add cancel function
        if (holder.countDownTimer != null) {
            holder.countDownTimer.cancel();
        }
    }
}