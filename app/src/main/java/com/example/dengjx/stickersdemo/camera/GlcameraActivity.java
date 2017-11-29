package com.example.dengjx.stickersdemo.camera;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.SurfaceHolder;

import com.example.dengjx.stickersdemo.debug.Accelerometer;
import com.example.dengjx.stickersdemo.debug.MultitrackerActivity;
import com.sensetime.stmobileapi.STMobileFaceAction;
import com.sensetime.stmobileapi.STMobileMultiTrack106;
import com.sensetime.stmobileapi.STUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dengjx on 2017/10/11.
 */

public class GlcameraActivity extends Activity implements MyCamera.IPreView{

    ///< 检测脸部动作：张嘴、眨眼、抬眉、点头、摇头
    private static final int ST_MOBILE_TRACKING_ENABLE_FACE_ACTION = 0x00000020;
    private static final int ST_MOBILE_FACE_DETECT   =  0x00000001;    ///<  人脸检测
    private static final int ST_MOBILE_EYE_BLINK     =  0x00000002;  ///<  眨眼
    private static final int ST_MOBILE_MOUTH_AH      =  0x00000004;    ///<  嘴巴大张
    private static final int ST_MOBILE_HEAD_YAW      =  0x00000008;    ///<  摇头
    private static final int ST_MOBILE_HEAD_PITCH    =  0x00000010;    ///<  点头
    private static final int ST_MOBILE_BROW_JUMP     =  0x00000020;    ///<  眉毛挑动
    private static final String TAG = "GlcameraActivity" ;

    MyGLSurfaceView myGLSurfaceView;
    MySurfaceView mySurfaceView;
    private byte nv21[];
    private boolean isNV21ready = false;
    private boolean killed = false;
    private Thread thread;
    /**
     * 重力传感器
     */
    static Accelerometer acc;
    public static int fps;

    private STMobileMultiTrack106 tracker = null;
    public final int PREVIEW_WIDTH=640;
    public final int PREVIEW_HEIGHT=480;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initSurfaceview();
        /**
         * 开启重力传感器监听
         */
        acc = new Accelerometer(this);
        acc.start();

        nv21 = new byte[PREVIEW_WIDTH * PREVIEW_HEIGHT * 2];
        mySurfaceView.getmMyCamera().setOnPreView(this);
    }

    void initSurfaceview(){
        mySurfaceView = new MySurfaceView(this);
        setContentView(mySurfaceView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"acc:"+acc);
        if (acc != null)
            acc.start();
        Log.d(TAG,"onResume");
        if (tracker == null) {
            long start_init = System.currentTimeMillis();
//			int config = 0; //default config
            int config = ST_MOBILE_TRACKING_ENABLE_FACE_ACTION;
            tracker = new STMobileMultiTrack106(this, config);
            int max = 40;
            tracker.setMaxDetectableFaces(max);
            long end_init = System.currentTimeMillis();
            Log.i("track106", "init cost "+(end_init - start_init) +" ms");
        }

        killed = false;
        final byte[] tmp = new byte[PREVIEW_WIDTH *  PREVIEW_HEIGHT * 2];
        thread = new Thread(){
            @Override
            public void run() {
                super.run();
                List<Long> timeCounter = new ArrayList<Long>();
                int start = 0;
                while (!killed){
                    if(!isNV21ready)
                        continue;
                    Log.d(TAG,"isNV21ready:"+isNV21ready);

                    synchronized (nv21) {
                        System.arraycopy(nv21, 0, tmp, 0, nv21.length);
                        isNV21ready = false;
                    }
                    /**
                     * 获取重力传感器返回的方向
                     */
                    int dir = Accelerometer.getDirection();

                    Log.d(TAG,"mySurfaceView.getmMyCamera().getmCameraInfo().orientation:"
                            +mySurfaceView.getmMyCamera().getmCameraInfo().orientation);
                    Log.d(TAG, "dir: "+dir);
                    /**
                     * 请注意前置摄像头与后置摄像头旋转定义不同
                     * 请注意不同手机摄像头旋转定义不同
                     */
                    if (((mySurfaceView.getmMyCamera().getmCameraInfo().orientation == 270 && (dir & 1) == 1) ||
                                    (mySurfaceView.getmMyCamera().getmCameraInfo().orientation == 90 && (dir & 1) == 0)))
                        dir = (dir ^ 2);

                    /**
                     * 调用实时人脸检测函数，返回当前人脸信息
                     */
                    long start_track = System.currentTimeMillis();
//					STMobile106[] faces = tracker.track(tmp, dir,PREVIEW_WIDTH,PREVIEW_HEIGHT);
                    Log.d(TAG, "dir st-test: "+dir);
                    STMobileFaceAction[] faceActions = tracker.trackFaceAction(tmp, 1,PREVIEW_WIDTH,
                            PREVIEW_HEIGHT);
                    long end_track = System.currentTimeMillis();
                    Log.i(TAG, "track cost :"+(end_track - start_track)+" ms");

                    long timer = System.currentTimeMillis();
                    timeCounter.add(timer);
                    while (start < timeCounter.size()
                            && timeCounter.get(start) < timer - 1000) {
                        start++;
                    }
                    fps = timeCounter.size() - start;
                    try {
                        Log.i(TAG, "-->> faceActions: faceActions[0].face="+faceActions[0].face.rect.toString()+
                                ", pitch = "+faceActions[0].face.pitch+", roll="+faceActions[0].face.roll+", yaw="
                                +faceActions[0].face.yaw+", face_action = "+faceActions[0].face_action+", " +
                                "face_count = "+faceActions.length);
//						mListener.onTrackdetected(fps, faces[0].pitch, faces[0].roll, faces[0].yaw);
                       /* mListener.onTrackdetected(fps,  faceActions[0].face.pitch, faceActions[0].face.roll, faceActions[0].face.yaw, faceActions[0].face.eye_dist, faceActions[0].face.ID,
                                checkFlag(faceActions[0].face_action, ST_MOBILE_EYE_BLINK), checkFlag(faceActions[0].face_action, ST_MOBILE_MOUTH_AH), checkFlag(faceActions[0].face_action, ST_MOBILE_HEAD_YAW),
                                checkFlag(faceActions[0].face_action, ST_MOBILE_HEAD_PITCH), checkFlag(faceActions[0].face_action, ST_MOBILE_BROW_JUMP));*/
                    } catch(Exception e) {
                        e.printStackTrace();
                        Log.d(TAG,"Exception:"+e.toString());
                    }
                    if (start > 100) {
                        timeCounter = timeCounter.subList(start,
                                timeCounter.size() - 1);
                        start = 0;
                    }
                    Log.d(TAG, "faceActions: "+faceActions);
                    if(faceActions != null) {
                        for (int i = 0; i < faceActions.length; i++) {
                            Log.i(TAG, "detect faces: " + faceActions[i].getFace().getRect().toString());
                        }
                    }
                    Canvas canvas = mySurfaceView.surfaceHolder.lockCanvas();
                    if (canvas == null)
                        continue;

                    boolean rotate270 =  mySurfaceView.getmMyCamera().getmCameraInfo().orientation == 270;
//						for (STMobile106 r : faces) {
                    for (STMobileFaceAction r : faceActions) {
                        // Rect rect = r.getRect();
                        Log.i(TAG, "-rotate270 = " + rotate270);
                        Log.i(TAG, "-->> face count = " + faceActions.length);
                        Rect rect;
                        if (rotate270) {
//								rect = STUtils.RotateDeg270(r.getRect(),PREVIEW_WIDTH, PREVIEW_HEIGHT);
                            rect = STUtils.RotateDeg270(r.getFace().getRect(), PREVIEW_WIDTH, PREVIEW_HEIGHT);
                        } else {
//								rect = STUtils.RotateDeg90(r.getRect(),PREVIEW_WIDTH, PREVIEW_HEIGHT);
                            rect = STUtils.RotateDeg90(r.getFace().getRect(), PREVIEW_WIDTH, PREVIEW_HEIGHT);
                        }

                        STUtils.drawFaceRect(canvas, rect, PREVIEW_HEIGHT,
                                PREVIEW_WIDTH, true);
                    }

                    mySurfaceView.surfaceHolder.unlockCanvasAndPost(canvas);

                }
            }
        };
        thread.start();

    }


    @Override
    public void onPreView(byte[] bytes, Camera camera) {
        Log.d(TAG,"onPreView");
        synchronized (nv21) {
            System.arraycopy(bytes, 0, nv21, 0, bytes.length);
            isNV21ready = true;
        }
    }
}
