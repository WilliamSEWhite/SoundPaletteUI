package com.soundpaletteui.Infrastructure.Models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class TagModel implements Parcelable {
    private int TagId;
    private String TagName;
    private boolean isSelected;
    public int getTagId(){ return TagId; }
    public String getTagName() { return TagName; }
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }

    public TagModel(int tagId, String tagName) {
        this.TagId = tagId;
        this.TagName = tagName;
        this.isSelected = false;
    }

    @Override
    public boolean equals(Object v) {
        boolean retVal = false;

        if (v instanceof TagModel){
            TagModel ptr = (TagModel) v;
            retVal =  ((TagModel) v).TagId == this.TagId;
        }

        return retVal;
    }

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {}

}
