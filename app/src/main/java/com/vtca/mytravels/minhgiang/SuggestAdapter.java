package com.vtca.mytravels.minhgiang;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vtca.mytravels.R;
import com.vtca.mytravels.entity.SuggestLocation;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SuggestAdapter extends RecyclerView.Adapter<SuggestAdapter.SuggestViewHolder> {
    private List<SuggestLocation> suggestLocations;
    private Context context;

    public SuggestAdapter(List<SuggestLocation> suggestLocations, Context context) {
        this.suggestLocations = suggestLocations;
        this.context = context;
    }

    @NonNull
    @Override
    public SuggestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_sugesst_location, parent, false);
        SuggestViewHolder viewHolder = new SuggestViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final SuggestViewHolder holder, int position) {
        SuggestLocation suggestLocation = suggestLocations.get(position);
        holder.name.setText(suggestLocation.getName());
        holder.formatedAddress.setText(suggestLocation.getFormatedAddress());
        holder.rating.setText(String.valueOf(suggestLocation.getRating()));
        holder.userTotalRating.setText(String.valueOf(suggestLocation.getUserRatingTotal()) + " " + context.getString(R.string.review));
        holder.ratingBar.setRating((float) suggestLocation.getRating());
        Glide.with(context)
                .load(suggestLocation.getPhoto())
                .into(holder.photo);


    }

    @Override
    public int getItemCount() {
        if (suggestLocations == null) return 0;
        return suggestLocations.size();
    }

    class SuggestViewHolder extends RecyclerView.ViewHolder {
        ImageView photo;
        TextView name;
        TextView formatedAddress;
        TextView rating;
        TextView userTotalRating;
        RatingBar ratingBar;


        public SuggestViewHolder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.photo);
            name = itemView.findViewById(R.id.name);
            formatedAddress = itemView.findViewById(R.id.formatedAddress);
            rating = itemView.findViewById(R.id.rating);
            userTotalRating = itemView.findViewById(R.id.userTotalRating);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}
