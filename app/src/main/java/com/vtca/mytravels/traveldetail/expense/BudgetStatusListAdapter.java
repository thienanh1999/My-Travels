package com.vtca.mytravels.traveldetail.expense;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vtca.mytravels.R;
import com.vtca.mytravels.base.MyConst;
import com.vtca.mytravels.entity.TravelExpense;
import com.vtca.mytravels.main.TravelListItemClickListener;
import com.vtca.mytravels.utils.MyString;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BudgetStatusListAdapter extends RecyclerView.Adapter<BudgetStatusListAdapter.TravelViewHolder> {
    private final LayoutInflater mLayoutInflater;
    private List<TravelExpense> mList;
    private TravelListItemClickListener mTravelListItemClickListener;

    public BudgetStatusListAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setListItemClickListener(TravelListItemClickListener travelListItemClickListener) {
        mTravelListItemClickListener = travelListItemClickListener;
    }

    @Override
    public int getItemCount() {
        if (mList == null) return 0;
        return mList.size();
    }

    public void setList(List<TravelExpense> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        if (getItemCount() == 0) return RecyclerView.NO_ID;
        return mList.get(position).getId();
    }

    private TravelExpense getItem(int position) {
        if (getItemCount() == 0) return null;
        return mList.get(position);
    }

    @Override
    public void onBindViewHolder(@NonNull TravelViewHolder holder, int position) {
        TravelExpense item = getItem(position);
        if (item == null) {
            return;
        }
        holder.currency.setText(MyConst.getCurrencyCode(item.getCurrency()).value);
        holder.budget.setText(MyString.getMoneyText(item.getPlaceLat()));
        holder.expenses.setText(item.getAmountText());
        holder.balance.setText(MyString.getMoneyText(item.getPlaceLat() - item.getAmount()));
    }

    @NonNull
    @Override
    public TravelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.fragment_budget_status_item, parent, false);
        return new TravelViewHolder(v);
    }

    class TravelViewHolder extends RecyclerView.ViewHolder {
        private final TextView currency;
        private final TextView budget;
        private final TextView expenses;
        private final TextView balance;

        private TravelViewHolder(View v) {
            super(v);
            if (mTravelListItemClickListener != null) {
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mTravelListItemClickListener.onListItemClick(v
                                , getAdapterPosition()
                                , getItem(getAdapterPosition()), false);
                    }
                });
            }
            currency = v.findViewById(R.id.currency);
            budget = v.findViewById(R.id.budget);
            expenses = v.findViewById(R.id.expenses);
            balance = v.findViewById(R.id.balance);
        }
    }
}
