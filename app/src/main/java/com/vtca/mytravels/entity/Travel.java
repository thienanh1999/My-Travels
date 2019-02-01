package com.vtca.mytravels.entity;

import android.graphics.Bitmap;

import com.vtca.mytravels.utils.MyDate;

import java.util.Objects;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "travel")
public class Travel extends TravelBaseEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String endDt;

    private long dateTimeLong;

    @Ignore
    private transient Bitmap bitmap;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Travel(String title) {
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEndDt() {
        return endDt;
    }

    public void setEndDt(long endDt) {
        this.endDt = MyDate.getString(endDt);
    }

    public long getEndDtLong() {
        return MyDate.getTime(endDt);
    }

    public void setEndDt(String endDt) {
        this.endDt = endDt;
    }

    @Override
    public long getDateTimeLong() {
        return dateTimeLong;
    }

    public void setDateTimeLong(long dateTimeLong) {
        this.dateTimeLong = dateTimeLong;
    }

    /**
     * Gets the string expression of the end date.
     *
     * @return string in yyyy-MM-dd format
     */
    public String getEndDtText() {
        return MyDate.getDateString(endDt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Travel)) return false;
        if (!super.equals(o)) return false;
        Travel travel = (Travel) o;
        return id == travel.id &&
                Objects.equals(endDt, travel.endDt);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), id, endDt);
    }

    @Override
    public String toString() {
        return "Travel{" +
                "id=" + id +
                ", endDt='" + endDt + '\'' +
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
