package com.vume.allinonegames.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vume.allinonegames.Model.JoshAllWithdrawalTransactionsResponseData;
import com.vume.allinonegames.R;
import com.vume.allinonegames.Util.PaymentUtils;

import java.util.List;

public class WithdrawalTransactionHistoryCustomAdapter extends RecyclerView.Adapter<WithdrawalTransactionHistoryCustomAdapter.ViewHolder> {

    private Context context;
    private List<JoshAllWithdrawalTransactionsResponseData> transactionHistoryResponseDataList;

    public WithdrawalTransactionHistoryCustomAdapter(Context context, List<JoshAllWithdrawalTransactionsResponseData> transactionHistoryResponseDataList){
        this.context = context;
        this.transactionHistoryResponseDataList = transactionHistoryResponseDataList;
    }

    public WithdrawalTransactionHistoryCustomAdapter(Context context){
        this.context = context;
    }

    public void setList(List<JoshAllWithdrawalTransactionsResponseData> transactionHistoryResponseDataList){
        this.transactionHistoryResponseDataList = transactionHistoryResponseDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_history_list_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JoshAllWithdrawalTransactionsResponseData currentTransactionData = transactionHistoryResponseDataList.get(position);

        holder.mTxnTime.setText(PaymentUtils.getmTxnTimeInFormat(currentTransactionData.getmTxnTimestamp()));
        if(currentTransactionData.getmTxnStatus() == PaymentUtils.WITHDRAWAL_TRANSACTION_TYPE_INITIATED) {
            holder.mTxnType.setText(context.getResources().getString(R.string.withdraw_txns_type_initiate));
            holder.mTxnType.setTextColor(context.getResources().getColor(R.color.green_four));
        }
        else if(currentTransactionData.getmTxnStatus() == PaymentUtils.WITHDRAWAL_TRANSACTION_TYPE_ACCEPTED){
            holder.mTxnType.setText(context.getResources().getString(R.string.withdraw_txns_type_accepted));
            holder.mTxnType.setTextColor(context.getResources().getColor(R.color.orangy_yellow));
        }
        else if(currentTransactionData.getmTxnStatus() == PaymentUtils.WITHDRAWAL_TRANSACTION_TYPE_INPROGRESS){
            holder.mTxnType.setText(context.getResources().getString(R.string.withdraw_txns_type_inprogress));
            holder.mTxnType.setTextColor(context.getResources().getColor(R.color.green_four));
        }
        else if(currentTransactionData.getmTxnStatus() == PaymentUtils.WITHDRAWAL_TRANSACTION_TYPE_REJECTED){
            holder.mTxnType.setText(context.getResources().getString(R.string.withdraw_txns_type_rejected));
            holder.mTxnType.setTextColor(context.getResources().getColor(R.color.red_button));
        }
        else if(currentTransactionData.getmTxnStatus() == PaymentUtils.WITHDRAWAL_TRANSACTION_TYPE_CANCELLEDBYUSER){
            holder.mTxnType.setText(context.getResources().getString(R.string.withdraw_txns_type_cancelledbyuser));
            holder.mTxnType.setTextColor(context.getResources().getColor(R.color.red_button));
        }

        holder.mTxnAmountWithSign.setText(context.getResources().getString(R.string.rupee_sign) + currentTransactionData.getmTxnAmount());
        holder.mTxnIdOrTableName.setText(String.valueOf(currentTransactionData.getmTxnid()));
        holder.mAccNoOrGameName.setVisibility(View.INVISIBLE);
        holder.mAccNoOrGameNameLabel.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return transactionHistoryResponseDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView mTxnTime, mTxnIdOrTableName, mTxnIdOrTableNameLabel, mAccNoOrGameName, mAccNoOrGameNameLabel, mTxnType, mTxnAmountWithSign;

        public ViewHolder(View itemView) {
            super(itemView);
            mTxnTime = itemView.findViewById(R.id.lbl_transactiondate);
            mTxnIdOrTableNameLabel = itemView.findViewById(R.id.lbl_txnid_or_tablename);
            mTxnIdOrTableName = itemView.findViewById(R.id.lbl_txnid_or_tablename_val);
            mAccNoOrGameNameLabel = itemView.findViewById(R.id.lbl_accno_gamenme);
            mAccNoOrGameName = itemView.findViewById(R.id.lbl_accno_gamenme_val);
            mTxnType = itemView.findViewById(R.id.lbl_transaction_tyoe);
            mTxnAmountWithSign = itemView.findViewById(R.id.lbl_transaction_amount_withsign);
        }
    }
}
