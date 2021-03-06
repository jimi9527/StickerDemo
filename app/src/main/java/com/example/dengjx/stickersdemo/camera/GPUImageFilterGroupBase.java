package com.example.dengjx.stickersdemo.camera;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;

/**
 * Created by dengjx on 2017/12/4.
 */

public abstract class GPUImageFilterGroupBase extends GPUImageAudioFilter {

    int[] bD;
    int[] bE;
    final FloatBuffer bF;
    final FloatBuffer bG;
    final FloatBuffer bH;
    GPUImageFilter bI;
    protected IGroupStateChanged bJ;
    protected IFilterDrawListener bK;

    public GPUImageFilterGroupBase(){
        this.bF = ByteBuffer.allocateDirect(FilterConstants.CUBE.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.bF.put(FilterConstants.CUBE).position(0);

        this.bG = ByteBuffer.allocateDirect(PlaneTextureRotationUtils.TEXTURE_NO_ROTATION.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.bG.put(PlaneTextureRotationUtils.TEXTURE_NO_ROTATION).position(0);

        float[] arrayOfFloat = PlaneTextureRotationUtils.getRotation(Rotation.NORMAL, false, true);

        this.bH = ByteBuffer.allocateDirect(arrayOfFloat.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.bH.put(arrayOfFloat).position(0);
    }
    public void setGroupStateChangedListener(IGroupStateChanged paramIGroupStateChanged)
    {
        this.bJ = paramIGroupStateChanged;
    }

    public void setFilterDrawListener(IFilterDrawListener paramIFilterDrawListener)
    {
        this.bK = paramIFilterDrawListener;
    }

    public abstract List<GPUImageFilter> H();

    public abstract void addFilter(GPUImageFilter paramGPUImageFilter);

    public void setPhoneDirection(int paramInt)
    {
        super.setPhoneDirection(paramInt);
        for (GPUImageFilter localGPUImageFilter : H()) {
            localGPUImageFilter.setPhoneDirection(paramInt);
        }
    }

    public void releaseNoGLESRes()
    {
        super.releaseNoGLESRes();
        if (null != this.bI)
        {
            this.bI.releaseNoGLESRes();
            this.bI = null;
        }
    }

    public void onDestroy()
    {
        I();
        if (null != this.bI)
        {
            this.bI.destroy();
            this.bI = null;
        }
        super.onDestroy();
    }
    private void I()
    {
        if (this.bE != null)
        {
            GLES20.glDeleteTextures(this.bE.length, this.bE, 0);
            this.bE = null;
            GLES20.glDeleteFramebuffers(this.bD.length, this.bD, 0);
            this.bD = null;
        }
    }
    public void onOutputSizeChanged(int paramInt1, int paramInt2){
        super.onOutputSizeChanged(paramInt1, paramInt2);
        if(this.bD != null){
            I();
        }
        int i = H().size();
        for(int j = 0; j < i; j++){
            H().get(j).onOutputSizeChanged(paramInt1, paramInt2);
        }
        if(null != this.bI)
        {
            this.bI.onOutputSizeChanged(paramInt1, paramInt2);
        }
        if((H() != null) && (H().size() > 0 ))
        {
            this.bD = new int[2];
            this.bE = new int[2];
            for(int j = 0; j < this.bD.length; j++){
                GLES20.glGenFramebuffers(1, this.bD, j);
                GLES20.glGenTextures(1, this.bE, j);
                OpenGlUtils.bindTextureToFrameBuffer(this.bD[j], this.bE[j], paramInt1, paramInt2);
            }
        }

    }

    public void onDraw(int paramInt, FloatBuffer paramFloatBuffer1, FloatBuffer paramFloatBuffer2)
    {
        throw new RuntimeException("this method should not been call!");
    }

    public void draw(int paramInt1, int paramInt2, FloatBuffer paramFloatBuffer1, FloatBuffer paramFloatBuffer2)
    {
        z();
        C();
        start();
        if((!isInitialized()) || (this.bD == null) ||(this.bE == null) || (null == H()) ){
            return;
        }
        if(paramInt1 == -1)
        {
            return;
        }
        int i = H().size();
        int j = paramInt1;
        for (int k= 0; k <i; k++)
        {
            GPUImageFilter localGPUImageFilter = H().get(k);
            int m = k < i - 1 ? 1:0;
            if(m != 0)
            {
                GLES20.glBindFramebuffer(36160, this.bD[(k % 2)]);
                GLES20.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            }
            else if(-1 != paramInt2)
            {
                GLES20.glBindFramebuffer(36160, paramInt2);
                GLES20.glClearColor(0.0F, 0.0F, 0.0F,0.0F);
            }
            if(k == 0)
            {
                localGPUImageFilter.d(false);
                localGPUImageFilter.onDraw(j, paramFloatBuffer1, paramFloatBuffer2);
            }
            else if(k == i-1)
            {
                localGPUImageFilter.d(i % 2 == 0);
                localGPUImageFilter.onDraw(j, this.bF, i % 2 == 0 ? this.bH : this.bG);
            }
            else {
                lo
            }

        }
    }

    public interface IGroupStateChanged {
        void onTipsAndCountChanged(int paramInt1, String paramString, int paramInt2);
    }

    public interface IFilterDrawListener {
        void onSingleFilterDrawed(int paramInt1, int paramInt2);
    }

}
