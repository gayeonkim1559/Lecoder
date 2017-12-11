package com.lecoder.team9.lecoder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by GAYEON on 2017-11-23.
 */

public class DrawingFragment extends Fragment implements View.OnClickListener{
    EditText pageNum;
    ImageButton storeBtn;

    TextView currentTime;
    PaintBoard board;
    Button colorBtn, penBtn, eraserBtn, undoBtn;

    LinearLayout addedLayout;
    Button colorLegendBtn;
    TextView sizeLegendTxt;

    final private  static File RECORDED_FILE = Environment.getExternalStorageDirectory();
    String dirPath = RECORDED_FILE.getAbsolutePath() + "/Lecoder";

    int mColor = 0xff000000;
    int mSize =2;
    int oldColor;
    int oldSize;
    boolean eraserSelected = false;
    String savePath,fileName;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drawing, container, false);

        pageNum = view.findViewById(R.id.pageInput);
        storeBtn = view.findViewById(R.id.saveBtn);
        currentTime=view.findViewById(R.id.currentTime);
        storeBtn.setOnClickListener(this);

        LinearLayout toolsLayout = view.findViewById(R.id.toolsLayout);
        LinearLayout boardLayout = view.findViewById(R.id.boardLayout);

        colorBtn = view.findViewById(R.id.colorBtn);
        penBtn = view.findViewById(R.id.penBtn);
        eraserBtn = view.findViewById(R.id.eraserBtn);
        undoBtn = view.findViewById(R.id.undoBtn);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        board = new PaintBoard(getContext());
        board.setLayoutParams(params);
        board.setPadding(2, 2, 2, 2);

        boardLayout.addView(board);

        LinearLayout.LayoutParams addedParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 48);

        addedLayout = new LinearLayout(getContext());
        addedLayout.setLayoutParams(addedParams);
        addedLayout.setOrientation(LinearLayout.VERTICAL);
        addedLayout.setPadding(8,8,8,8);

        LinearLayout outlineLayout = new LinearLayout(getContext());
        outlineLayout.setLayoutParams(buttonParams);
        outlineLayout.setOrientation(LinearLayout.VERTICAL);
        outlineLayout.setBackgroundColor(Color.LTGRAY);
        outlineLayout.setPadding(8,8,8,8);

        colorLegendBtn = new Button(getContext());
        colorLegendBtn.setLayoutParams(buttonParams);
        colorLegendBtn.setText("");
        colorLegendBtn.setBackgroundColor(mColor);
        colorLegendBtn.setHeight(20);
        outlineLayout.addView(colorLegendBtn);
        addedLayout.addView(outlineLayout);

        sizeLegendTxt = new TextView(getContext());
        sizeLegendTxt.setLayoutParams(buttonParams);
        sizeLegendTxt.setText("Size: "+mSize);
        sizeLegendTxt.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
        sizeLegendTxt.setTextColor(Color.BLACK);
        addedLayout.addView(sizeLegendTxt);

        toolsLayout.addView(addedLayout);

        colorBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ColorPaletteDialog.listener = new OnColorSelectedListener(){
                    public void onColorSelected(int color) {
                        mColor = color;
                        board.updatePaintProperty(mColor,mSize);
                        displayPaintProperty();
                    }
                };
                Intent intent = new Intent(getContext(), ColorPaletteDialog.class);
                startActivity(intent);
            }
        });
        penBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PenPaletteDialog.listener = new OnPenSelectedListener(){

                    @Override
                    public void onPenSelected(int size) {
                        mSize = size;
                        board.updatePaintProperty(mColor, mSize);
                        displayPaintProperty();
                    }
                };
                Intent intent = new Intent(getContext(), PenPaletteDialog.class);
                startActivity(intent);
            }
        });

        eraserBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                eraserSelected = !eraserSelected;

                if(eraserSelected){
                    colorBtn.setEnabled(false);
                    penBtn.setEnabled(false);
                    undoBtn.setEnabled(false);

                    colorBtn.invalidate();
                    penBtn.invalidate();
                    undoBtn.invalidate();

                    oldColor = mColor;
                    oldSize = mSize;

                    mColor = Color.WHITE;
                    mSize = 15;

                    board.updatePaintProperty(mColor, mSize);
                    displayPaintProperty();
                }
                else{
                    colorBtn.setEnabled(true);
                    penBtn.setEnabled(true);
                    undoBtn.setEnabled(true);

                    colorBtn.invalidate();
                    penBtn.invalidate();
                    undoBtn.invalidate();

                    mColor = oldColor;
                    mSize = oldSize;

                    board.updatePaintProperty(mColor, mSize);
                    displayPaintProperty();
                }
            }
        });
        undoBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                board.undo();
            }
        });

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.saveBtn:
                if (((RecordActivity)getActivity()).isRecording()){
                    storeDrawing(view);
                }else {
                    Toast.makeText(getContext(),"녹음 버튼을 눌러주세요.",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public int getChosenColor() {
        return mColor;
    }

    public int getPenThickness() {return mSize; }

    private void displayPaintProperty() {

        colorLegendBtn.setBackgroundColor(mColor);
        sizeLegendTxt.setText("Size : "+mSize);

        addedLayout.invalidate();
    }

    public void storeDrawing(View view) {
        try {
            File file = new File(dirPath+"/"+savePath+"/"+fileName);
            view.draw(board.mCanvas);
            FileOutputStream fos = new FileOutputStream(file);
            if (fos != null) {
                Log.d("드로잉 프레그먼트", "저장 전");
                board.mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                Toast.makeText(getContext(), "저장이 완료되었습니다", Toast.LENGTH_SHORT).show();
                fos.flush();
                fos.close();
                currentTime.setText(((RecordActivity)getActivity()).getSaveTime());
            }
        } catch(Exception e) {
            Log.e("드로잉프레그먼트", "저장오류");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        savePath=((RecordActivity)getActivity()).pathToSave();
        String name=((RecordActivity)getActivity()).getSaveTime().replace(":","m");
        fileName="img"+name+".jpg";
    }
}
