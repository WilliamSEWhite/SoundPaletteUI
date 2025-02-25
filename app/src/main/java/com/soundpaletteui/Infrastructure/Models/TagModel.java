package com.soundpaletteui.Infrastructure.Models;

public class TagModel {
    private int TagId;
    private String TagName;

    public int getTagId(){ return TagId; }
    public String getTagName() { return TagName; }

    public TagModel(int tagId, String tagName) {
        this.TagId = tagId;
        this.TagName = tagName;
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

}
