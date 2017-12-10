package com.lecoder.team9.lecoder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.GridView;

public class ColorPaletteDialog extends Activity{

    GridView grid;
    Button closeBtn;
    ColorDateAdapter adapter;

    public static OnColorSelectedListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog);

        this.setTitle("색상 선택");

        grid = (GridView) findViewById(R.id.colorGrid);
        closeBtn = (Button) findViewById(R.id.closeBtn);

        grid.setColumnWidth(14);
        grid.setBackgroundColor(Color.GRAY);
        grid.setVerticalSpacing(4);
        grid.setHorizontalSpacing(4);

        adapter = new ColorDateAdapter(this);
        grid.setAdapter(adapter);
        grid.setNumColumns(adapter.getNumColumns());

        closeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
    }

}

class ColorDateAdapter extends BaseAdapter{
    Context mContext;

    public static final int [] colors = new int[]{
            0xff000000, 0xff00007f, 0xff0000ff, 0xff007100, 0xff007171, 0xff00ff00, 0xff00ff71,
            0xff00ffff, 0xff710071, 0xff7f00ff, 0xff717100, 0xff717171, 0xffff0000, 0xffff0071,
            0xffff00ff, 0xffff7100, 0xffff7171, 0xffff7fff, 0xffffff00, 0xffffff71, 0xffffffff
    };

    int rowCount;
    int columnCount;

    public ColorDateAdapter(Context context){
        super();
        mContext = context;

        rowCount = 3;
        columnCount = 7;
    }

    public int getNumColumns() { return columnCount; }
    public int getRowCount() { return rowCount * columnCount; }

    @Override
    public int getCount() {
        return rowCount * columnCount;
    }

    public Object getItem(int position) { return colors[position]; }
    public long getItemId(int position) { return 0; }

    public View getView(int position, View view, ViewGroup group){
        Log.d("ColorDateAdapter", "getView(" + position + ") called.");

        int rowIndex = position / rowCount;
        int columnIndex = position % rowCount;
        Log.d("ColorDateAdapter", "Index : " + rowIndex + ", " + columnIndex);

        GridView.LayoutParams params = new GridView.LayoutParams(
                GridView.LayoutParams.MATCH_PARENT,
                GridView.LayoutParams.MATCH_PARENT);

        Button aItem = new Button(mContext);
        aItem.setText("");
        aItem.setLayoutParams(params);
        aItem.setPadding(4,4,4,4);
        aItem.setBackgroundColor(colors[position]);
        aItem.setHeight(120);
        aItem.setTag(colors[position]);

        aItem.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (ColorPaletteDialog.listener !=null) {
                    ColorPaletteDialog.listener.onColorSelected(((Integer)v.getTag()).intValue());
                }
                ((ColorPaletteDialog)mContext).finish();
            }
        });
        return aItem;
    }
}
