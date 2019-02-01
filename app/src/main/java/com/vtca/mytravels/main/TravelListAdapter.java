package com.vtca.mytravels.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.vtca.mytravels.R;
import com.vtca.mytravels.entity.Travel;
import com.vtca.mytravels.minhgiang.DiffCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

public class TravelListAdapter extends RecyclerView.Adapter<TravelListAdapter.TravelViewHolder> {

    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    public List<Travel> mList;
    private ListItemClickListener listItemClickListener;

    public void setListItemClickListener(ListItemClickListener listItemClickListener) {
        this.listItemClickListener = listItemClickListener;
    }

    public TravelListAdapter(Context context, List<Travel> list) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mList = list;
    }


    @Override
    public int getItemCount() {
        if (mList == null) return 0;
        return mList.size();
    }

    public void setList(List<Travel> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        if (getItemCount() == 0) return RecyclerView.NO_ID;
        return mList.get(position).getId();
    }

    private Travel getItem(int position) {
        if (getItemCount() == 0) return null;
        return mList.get(position);
    }

    public void updateTravelListItems(List<Travel> travels) {
        final DiffCallback diffCallback = new DiffCallback(this.mList, travels);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        this.mList.clear();
        this.mList.addAll(travels);
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public void onBindViewHolder(@NonNull TravelViewHolder holder, int position) {
        Travel item = getItem(position);
        if (item == null) {
            return;
        }
        holder.titleTxt.setText(item.getTitle());
        holder.placeTxt.setText(item.getPlaceName() + " / " + item.getPlaceAddr());
        holder.dateTxt.setText(item.getDateTimeText() + " ~ " + item.getEndDtText());
        File cacheDir = mContext.getApplicationContext().getCacheDir();
        File f = new File(cacheDir, item.getPlaceId());
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(fis);
        holder.photo.setImageBitmap(bitmap);
    }

    @NonNull
    @Override
    public TravelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.main_travel_item, parent, false);
        return new TravelViewHolder(v);
    }

    /**
     * Interface definition for the callback to call when the list item is clicked.
     */
    public interface ListItemClickListener {
        void onDeleteItemClick(Travel entity);

        void onEditItemClick(Travel entity);

        void onListItemClick(Travel entity);


    }

    class TravelViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTxt;
        private final TextView placeTxt;
        private final TextView dateTxt;
        private final ImageView photo;
        private final ImageButton moreButton;

        private TravelViewHolder(View v) {
            super(v);

            moreButton = v.findViewById(R.id.moreButton);
            photo = v.findViewById(R.id.photo);
            titleTxt = v.findViewById(R.id.title_txt);
            placeTxt = v.findViewById(R.id.place_txt);
            dateTxt = v.findViewById(R.id.date_txt);
            moreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(mContext, moreButton);
                    popupMenu.inflate(R.menu.menu_edit_delete_travel);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.mniEdit:
                                    listItemClickListener.onEditItemClick(getItem(getAdapterPosition()));
                                    break;
                                case R.id.mniDelete:
                                    listItemClickListener.onDeleteItemClick(getItem(getAdapterPosition()));
                                    break;
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listItemClickListener.onListItemClick(getItem(getAdapterPosition()));
                }
            });
        }
    }
}
