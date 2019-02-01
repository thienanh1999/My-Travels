package com.vtca.mytravels.minhgiang;

import com.vtca.mytravels.entity.Travel;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

public class DiffCallback extends DiffUtil.Callback {

    private final List<Travel> mOldTravelList;
    private final List<Travel> mNewTravelList;

    public DiffCallback(List<Travel> oldTravelList, List<Travel> newTravelList) {
        this.mOldTravelList = oldTravelList;
        this.mNewTravelList = newTravelList;
    }

    @Override
    public int getOldListSize() {
        return mOldTravelList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewTravelList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldTravelList.get(oldItemPosition).getId() == mNewTravelList.get(
                newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        final Travel oldTravel = mOldTravelList.get(oldItemPosition);
        final Travel newTravel = mNewTravelList.get(newItemPosition);

        return oldTravel.equals(newTravel);
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}