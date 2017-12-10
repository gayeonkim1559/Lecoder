package com.lecoder.team9.lecoder;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Schwa on 2017-12-10.
 */

public class GridListView extends LinearLayout {
    TextView tagTime,titleIfText,description;
    ImageView bitmapImg;
    public GridListView(Context context) {
        super(context);
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.grid_list_item,this,true);
        tagTime=findViewById(R.id.gridTime);
        titleIfText=findViewById(R.id.gridisText);
        description=findViewById(R.id.gridItemDescription);
        bitmapImg=findViewById(R.id.gridImage);
    }

    public void setTagTime(String text) {
        tagTime.setText(text);
    }

    public void setTitleIfText(String text) {
        titleIfText.setText(text);
    }

    public void setDescription(String text) {
        description.setText(text);
    }

    public void setBitmapImg(Bitmap img) {
        bitmapImg.setImageBitmap(img);
    }
}
