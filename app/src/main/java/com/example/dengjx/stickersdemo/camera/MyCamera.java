package com.example.dengjx.stickersdemo.camera;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

/**
 * Created by dengjx on 2017/10/11.
 */

public class MyCamera {
    private static final String TAG = "MyCamera";
    private Camera mCamera;
    private Camera.Parameters parameters;
    protected Camera.CameraInfo mCameraInfo = null;
    private boolean isCamera = false;
    Context context;
    public final int PREVIEW_WIDTH=640;
    public final int PREVIEW_HEIGHT=480;
    public IPreView mIPreView;

    public interface IPreView{
        void onPreView(byte[] bytes, Camera camera);
    }
    public MyCamera(Context context) {
        this.context = context;
    }
    public void setOnPreView(IPreView iPreView){
        mIPreView = iPreView;
        Log.d(TAG, " iPreView:"+ iPreView);
        Log.d(TAG, " mIPreView:"+ mIPreView);
    }


    void start(SurfaceTexture surfaceTexture){
        mCamera = Camera.open(1);
        initCamera();

        try {
            mCamera.setPreviewTexture(surfaceTexture);
            mCamera.startPreview();
            isCamera = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startHodeler(SurfaceHolder surfaceHolder){
        mCamera = Camera.open(1);
        initCamera();
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "IOException");
        }

    }
    public Camera.CameraInfo getmCameraInfo(){
        if(mCameraInfo == null){
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(1, info);
            mCameraInfo = info;
        }
        return mCameraInfo;
    }


    public void startPView(){
        if(mCamera != null){
            mCamera.startPreview();
        }
    }


    private void initCamera()
    {
        if (null != mCamera) {
            try {
                parameters = mCamera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                parameters.setPictureFormat(ImageFormat.JPEG);
                parameters.setPreviewFormat(ImageFormat.NV21);

                List<Camera.Size> previewSizes = mCamera.getParameters()
                        .getSupportedPreviewSizes();
                List<Camera.Size> pictureSizes = mCamera.getParameters()
                        .getSupportedPictureSizes();

                for (int i = 0; i < previewSizes.size(); i++) {
                    Camera.Size psize = previewSizes.get(i);
                    Log.i(TAG + "initCamera", "PreviewSize,width: "
                            + psize.width + " height: " + psize.height);
                }
               parameters.setPreviewSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);

                Camera.Size fs = null;
                for (int i = 0; i < pictureSizes.size(); i++) {
                    Camera.Size psize = pictureSizes.get(i);
                    if(fs == null && psize.width >= 1280)
                        fs = psize;
                    Log.i(TAG + "initCamera", "PictrueSize,width: "
                            + psize.width + " height" + psize.height);
                }
                parameters.setPictureSize(fs.width, fs.height);

                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.getCameraInfo(1, info);
                mCameraInfo = info;

                if (context.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                    parameters.set("orientation", "portrait");
                    parameters.set("rotation", 90);
                    // Front camera will display mirrored on the preview
                    int orientation =  360 - mCameraInfo.orientation;
                    mCamera.setDisplayOrientation(orientation);
                    Log.d(TAG, "orientation:"+orientation);
                    Log.d(TAG, "orientation: portrait");
                } else {
                    parameters.set("orientation", "landscape");
                    mCamera.setDisplayOrientation(0);
                    Log.d(TAG, "orientation: landscape");
                }
                mCamera.setParameters(parameters);
                mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] bytes, Camera camera) {
                        Log.d(TAG, "onPreviewFrame ");
                        Log.d(TAG, "this.iPreView:"+mIPreView);
                        if(mIPreView != null){
                            mIPreView.onPreView(bytes,camera);
                        }
                    }
                });

                Camera.Size csize = mCamera.getParameters().getPreviewSize();
                Log.i(TAG + "initCamera", "after setting, previewSize:width: "
                        + csize.width + " height: " + csize.height);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void stop(){
        if(isCamera){
            if(null != mCamera){
                mCamera.stopPreview();
                mCamera.release();
                isCamera = false;
            }
        }
    }



}
