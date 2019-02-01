package com.vtca.mytravels.entity;

import java.util.Objects;

import androidx.recyclerview.widget.DiffUtil;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "travel_plan"
        , foreignKeys = @ForeignKey(
        entity = Travel.class
        , parentColumns = "id"
        , childColumns = "travelId"
        , onDelete = ForeignKey.CASCADE
)
        , indices = {
        @Index("travelId")
}
)
public class TravelPlan extends TravelBaseEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long travelId;

    public static DiffUtil.ItemCallback<TravelPlan> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<TravelPlan>() {
                // Item details may have changed if reloaded from the database,
                // but ID is fixed.
                @Override
                public boolean areItemsTheSame(TravelPlan oldItem, TravelPlan newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(TravelPlan oldItem,
                                                  TravelPlan newItem) {
                    return oldItem.equals(newItem);
                }
            };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTravelId() {
        return travelId;
    }

    public void setTravelId(long travelId) {
        this.travelId = travelId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TravelPlan)) return false;
        if (!super.equals(o)) return false;
        TravelPlan plan = (TravelPlan) o;
        return id == plan.id &&
                travelId == plan.travelId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), id, travelId);
    }

    @Override
    public String toString() {
        return "TravelPlan{" +
                "id=" + id +
                ", travelId=" + travelId +
                ", dateTime='" + dateTime + '\'' +
                ", title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", placeId='" + placeId + '\'' +
                ", placeName='" + placeName + '\'' +
                ", placeAddr='" + placeAddr + '\'' +
                ", placeLat=" + placeLat +
                ", placeLng=" + placeLng +
                ", southwestLat=" + southwestLat +
                ", southwestLng=" + southwestLng +
                ", northeastLat=" + northeastLat +
                ", northeastLng=" + northeastLng +
                ", deleteYn=" + deleteYn +
                '}';
    }
}
