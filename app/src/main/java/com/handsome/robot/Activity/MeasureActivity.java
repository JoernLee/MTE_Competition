package com.handsome.robot.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.handsome.robot.R;

/**
 * Created by Joern on 2017/08/20.
 */

public class MeasureActivity extends AppCompatActivity {

    private ImageView ivPhoto;
    private Button btnClear;
    private Button btnAddPoint;
    private Bitmap bmGetPhoto;
    private TextView tvTest;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);

        ivPhoto = (ImageView)findViewById(R.id.iv_measurement_photo);
        btnAddPoint = (Button)findViewById(R.id.btn_add_point);
        btnClear = (Button)findViewById(R.id.btn_clear_point);
        tvTest = (TextView)findViewById(R.id.tv_test_XY);

        Intent intent=getIntent();
        byte[] buff = intent.getByteArrayExtra("image");
        bmGetPhoto = BitmapFactory.decodeByteArray(buff,0,buff.length);
        ivPhoto.setImageBitmap(bmGetPhoto);

        btnAddPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        ivPhoto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                    tvTest.setText(Float.toString(motionEvent.getX()) + "___" + Float.toString(motionEvent.getY()));
                }

                return false;
            }
        });
    }


}
