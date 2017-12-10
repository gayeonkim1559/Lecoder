package com.lecoder.team9.lecoder;

import android.graphics.Bitmap;

/**
 * Created by Schwa on 2017-12-10.
 */

public class GridListItem {
    String tagTime;
    String titleIfText;
    Bitmap bitmapImg;
    String description;

    public GridListItem(String tagTime, String titleIfText,String description) {//텍스트인 경우
        this.tagTime = tagTime;
        this.titleIfText = titleIfText;
        this.bitmapImg = null;
        this.description = description;
    }

    public GridListItem(String tagTime, Bitmap bitmapImg, String description) {//이미지인 경우
        this.tagTime = tagTime;
        this.bitmapImg = bitmapImg;
        this.description = description;
    }

    public String getTagTime() {
        return tagTime;
    }

    public void setTagTime(String tagTime) {
        this.tagTime = tagTime;
    }

    public String getTitleIfText() {
        return titleIfText;
    }

    public void setTitleIfText(String titleIfText) {
        this.titleIfText = titleIfText;
    }

    public Bitmap getBitmapImg() {
        return bitmapImg;
    }

    public void setBitmapImg(Bitmap bitmapImg) {
        this.bitmapImg = bitmapImg;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
