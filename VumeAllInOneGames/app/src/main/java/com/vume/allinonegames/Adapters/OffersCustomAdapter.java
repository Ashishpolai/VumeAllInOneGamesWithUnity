package com.vume.allinonegames.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.vume.allinonegames.Model.JoshOffersResponseData;
import com.vume.allinonegames.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OffersCustomAdapter extends RecyclerView.Adapter<OffersCustomAdapter.ViewHolder> {

    private Context context;
    private List<JoshOffersResponseData> offersResponseDataList;
    private OnTermsAndConditionsClickedListener termsndconditionsListener;

    public OffersCustomAdapter(Context context, List<JoshOffersResponseData> offersResponseDataList){
        this.context = context;
        this.offersResponseDataList = offersResponseDataList;
        termsndconditionsListener = (OnTermsAndConditionsClickedListener) context;
    }


    public interface OnTermsAndConditionsClickedListener {
        public void sendTermsAndConditionsClick(JoshOffersResponseData offerresponseData);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.offers_list_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.lbl_offermsg.setText(offersResponseDataList.get(position).getmOfferTitle());
        holder.lbl_offervalidtill.setText(context.getResources().getString(R.string.valid_till, offersResponseDataList.get(position).getmOfferExpiresAt()));
        holder.btn_copyofferpromocode.setText(offersResponseDataList.get(position).getmOfferCode());

        holder.btn_termsandconditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                termsndconditionsListener.sendTermsAndConditionsClick(offersResponseDataList.get(position));
            }
        });

        holder.btn_copyofferpromocode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return offersResponseDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView lbl_offermsg, lbl_offervalidtill, btn_termsandconditions;
        private Button btn_copyofferpromocode;

        public ViewHolder(View itemView) {
            super(itemView);

            lbl_offermsg = itemView.findViewById(R.id.lbl_offer_msg);
            lbl_offervalidtill = itemView.findViewById(R.id.lbl_offer_valid_till);
            btn_termsandconditions = itemView.findViewById(R.id.lbl_termandconditions);
            btn_copyofferpromocode = itemView.findViewById(R.id.btn_offercodecopy);
        }
    }
}
