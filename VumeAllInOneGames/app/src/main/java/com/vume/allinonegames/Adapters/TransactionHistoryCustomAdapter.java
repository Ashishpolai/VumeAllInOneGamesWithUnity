package com.vume.allinonegames.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vume.allinonegames.Model.JoshAllTransactionsResponseData;
import com.vume.allinonegames.R;
import com.vume.allinonegames.Util.PaymentUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TransactionHistoryCustomAdapter extends RecyclerView.Adapter<TransactionHistoryCustomAdapter.ViewHolder> {

    private Context context;
    private List<JoshAllTransactionsResponseData> transactionHistoryResponseDataList;

    public TransactionHistoryCustomAdapter(Context context, List<JoshAllTransactionsResponseData> transactionHistoryResponseDataList){
        this.context = context;
        this.transactionHistoryResponseDataList = transactionHistoryResponseDataList;
    }

    public TransactionHistoryCustomAdapter(Context context){
        this.context = context;
    }

    public void setList(List<JoshAllTransactionsResponseData> transactionHistoryResponseDataList){
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
        JoshAllTransactionsResponseData currentTransactionData = transactionHistoryResponseDataList.get(position);

        holder.mTxnTime.setText(PaymentUtils.getmTxnTimeInFormat(currentTransactionData.getmTxnTimestamp()));
        if(currentTransactionData.getmTxnType() == PaymentUtils.TRANSACTION_TYPE_DEPOSIT) {
            holder.mTxnType.setText(context.getResources().getString(R.string.transactiontype_deposit));
            holder.mTxnType.setTextColor(context.getResources().getColor(R.color.green_four));
            holder.mTxnAmountWithSign.setText("+"+context.getResources().getString(R.string.rupee_sign) + currentTransactionData.getmTxnAmount());
        }
        else if(currentTransactionData.getmTxnType() == PaymentUtils.TRANSACTION_TYPE_WITHDRAWAL){
            holder.mTxnType.setText(context.getResources().getString(R.string.transactiontype_withdrawal));
            holder.mTxnType.setTextColor(context.getResources().getColor(R.color.green_four));
            holder.mTxnAmountWithSign.setText("-"+context.getResources().getString(R.string.rupee_sign) + currentTransactionData.getmTxnAmount());
        }
        else if(currentTransactionData.getmTxnType() == PaymentUtils.TRANSACTION_TYPE_WITHDRAWAL_REVERSAL){
            holder.mTxnType.setText(context.getResources().getString(R.string.transactiontype_withdrawal_reversal));
            holder.mTxnType.setTextColor(context.getResources().getColor(R.color.green_four));
            holder.mTxnAmountWithSign.setText("+"+context.getResources().getString(R.string.rupee_sign) + currentTransactionData.getmTxnAmount());

        }
        else if(currentTransactionData.getmTxnType() == PaymentUtils.TRANSACTION_TYPE_WAGER){
            holder.mTxnType.setText(context.getResources().getString(R.string.transactiontype_wager));
            holder.mTxnType.setTextColor(context.getResources().getColor(R.color.orangy_yellow));
            holder.mTxnAmountWithSign.setText("-"+context.getResources().getString(R.string.rupee_sign) + currentTransactionData.getmTxnAmount());
        }
        else if(currentTransactionData.getmTxnType() == PaymentUtils.TRANSACTION_TYPE_WINNINGS){
            holder.mTxnType.setText(context.getResources().getString(R.string.transactiontype_winnings));
            holder.mTxnType.setTextColor(context.getResources().getColor(R.color.green_four));
            holder.mTxnAmountWithSign.setText("+"+context.getResources().getString(R.string.rupee_sign) + currentTransactionData.getmTxnAmount());
        }

        if(currentTransactionData.getmTxnType() == PaymentUtils.TRANSACTION_TYPE_WINNINGS ||
                currentTransactionData.getmTxnType() == PaymentUtils.TRANSACTION_TYPE_WAGER){
            holder.mAccNoOrGameNameLabel.setText("");
            holder.mAccNoOrGameName.setText(currentTransactionData.getmTxnDesc());
            holder.mTxnIdOrTableNameLabel.setText("");
            holder.mTxnIdOrTableName.setText(currentTransactionData.getmTxnTitle());
        }
        else if(currentTransactionData.getmTxnType() == PaymentUtils.TRANSACTION_TYPE_DEPOSIT ||
                currentTransactionData.getmTxnType() == PaymentUtils.TRANSACTION_TYPE_WITHDRAWAL_REVERSAL ||
                currentTransactionData.getmTxnType() == PaymentUtils.TRANSACTION_TYPE_WITHDRAWAL){
            holder.mAccNoOrGameNameLabel.setText(context.getResources().getString(R.string.account_label));
            holder.mTxnIdOrTableName.setText(String.valueOf(currentTransactionData.getmTxnid()));
            holder.mTxnIdOrTableNameLabel.setText(context.getResources().getString(R.string.transaction_id_label));
            holder.mAccNoOrGameName.setText("NA");
        }
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
