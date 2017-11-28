package com.example.dengjx.stickersdemo.camera;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by dengjx on 2017/11/28.
 */

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "MySurfaceView";
    public SurfaceHolder surfaceHolder;
    MyCamera mMyCamera;
    Context context;
    public MySurfaceView(Context context) {
        this(context,null);
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.i(TAG, "surfaceCreated...");
        if (mMyCamera == null) {
           mMyCamera = new MyCamera(context);
           mMyCamera.startHodeler(surfaceHolder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.i(TAG, "surfaceChanged...");
        if(mMyCamera != null){
            mMyCamera.startPView();//该方法只有相机开启后才能调用
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.i(TAG, "surfaceChanged...");
        if (mMyCamera != null) {
            mMyCamera.stop();
        }
    }
}
