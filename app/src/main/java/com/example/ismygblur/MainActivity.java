package com.example.ismygblur;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import java.io.InputStream;

@SuppressWarnings("ResourceType")
public class MainActivity extends Activity {


    private ImageView in;
    private ImageView out;
    private Button add;
    private Button sub;
    private boolean isCompress = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        in = (ImageView) findViewById(R.id.displayin);
        out = (ImageView) findViewById(R.id.displayout);
        Button button = (Button) findViewById(R.id.add);

//        final GussiBlurAnimation gussiBlurAnimation = new GussiBlurAnimation(MainActivity.this, in, out, getBitmap(R.drawable.gblur), getBitmap(R.drawable.gblur2));
        button.setOnClickListener(new OnClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View v) {
                if (isCompress) {
                    GussiBlurAnimation.startGussiBlurAnimation(MainActivity.this, out, getBitmap(R.drawable.gblur), 0, 1000);
                } else {
                    GussiBlurAnimation.startGussiBlurAnimation(MainActivity.this, out, getBitmap(R.drawable.gblur2), 0, 1000);
                }
                isCompress = !isCompress;
            }
        });

    }

    public Bitmap getBitmap(int id) {
        InputStream inputStream = getResources().openRawResource(id);
        return BitmapFactory.decodeStream(inputStream);
    }
}
