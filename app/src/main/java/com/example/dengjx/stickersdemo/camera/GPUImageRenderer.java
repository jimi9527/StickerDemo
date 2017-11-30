package com.example.dengjx.stickersdemo.camera;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Queue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by dengjx on 2017/11/30.
 */

public class GPUImageRenderer implements GLSurfaceView.Renderer ,
        Camera.PreviewCallback, IFaceDetector.FaceDetectorListener {
    @IntDef(value = {
            CMD_PROCESS_FRAME,
            CMD_SETUP_SURFACE_TEXTURE,
            CMD_SET_FILTER,
            CMD_RERUN_ONDRAW_RUNNABLE,
            CMD_RERUN_DRAWEND_RUNNABLE,
    }
    )
    @Retention(RetentionPolicy.SOURCE)
    public @interface RenderCmd {
    }

    final static int CMD_PROCESS_FRAME = 0;
    final static int CMD_SETUP_SURFACE_TEXTURE = 1;
    final static int CMD_SET_FILTER = 2;
    final static int CMD_RERUN_ONDRAW_RUNNABLE = 5;
    final static int CMD_RERUN_DRAWEND_RUNNABLE = 6;
    /**
     * 命令的一项
     */
    static class CmdItem {
        @RenderCmd
        int cmdId;
        Object param1;
        Object param2;
    }
    static final float CUBE[] = {-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f};

    int mGLTextureId;

    SurfaceTexture mSurfaceTexture = null;
    final FloatBuffer mGLCubeBuffer;
    final FloatBuffer mGLTextureBuffer;
    ByteBuffer mGLRgbBuffer;

    int mOutputWidth = 0;
    int mOutputHeight = 0;
    int mImageWidth = 1;
    int mImageHeight = 1;

    // 输入图像可能会和屏幕比例不一样，所以这两个变量存储放大后整个图像的大小
    int mImageScaleWidth = -1;
    int mImageScaleHeight = -1;

    final Queue<CmdItem> mRunOnDraw;
    final Queue<CmdItem> mRunOnDrawEnd;

    Rotation mRotation;
    boolean mFlipHorizontal;
    boolean mFlipVertical;
   // GPUImage.ScaleType mScaleType = GPUImage.ScaleType.CENTER_CROP;
   //  GPUImageFilterGroupBase mGroupBase;

    // 用来缓存当前摄像头的信息，这里假设了一个camera的实例的预览大小一旦设置了，就不会再变
    Camera mCacheCamera = null;
    Point mCachePrevSize;
    DirectionDetector mDirectionDetector;

    final Object mFaceDetectorLock = new Object();
    int mFaceCount = 0;
    PointF[][] mFaceDetectResultLst;

    boolean mSurfaceCreated = false; // surface是否创建了，如果surface没有创建，意味着render线程还没开始执行

    GLSurfaceView mSurfaceView;

    int mCameraFrameRate = 30; // 录制的时候,不好改变摄像头的帧率,所以需要在收到数据的时候丢帧
    long mFirstFrameTick = -1;
    long mFrameCount = 0;


    public void setDirectionDetector(DirectionDetector detector) {
        mDirectionDetector = detector;
    }

    ObjectCache<CmdItem> mCmdItemCacher = new ObjectCache<CmdItem>(20) {
        @Override
        protected CmdItem newInstance() {
            return new CmdItem();
        }
    };
    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {

    }

    @Override
    public void onDetectFinish() {
        if(null != mSurfaceView){
            mSurfaceView.requestRender();
        }
    }


    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {

    }

    @Override
    public void onDrawFrame(GL10 gl10) {

    }
}
