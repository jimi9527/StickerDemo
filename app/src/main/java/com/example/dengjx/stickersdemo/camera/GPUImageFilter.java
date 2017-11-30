package com.example.dengjx.stickersdemo.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.opengl.GLES20;

import com.example.dengjx.stickersdemo.R;
import com.example.dengjx.stickersdemo.util.ShaderUtil;

import java.util.LinkedList;

/**
 * Created by dengjx on 2017/11/30.
 */

public class GPUImageFilter {
    protected static final Bitmap aK = BitmapFactory.decodeResource(GlobalConfig.context.getResources(),
            R.drawable.filter_res_hold);
    private final LinkedList<Runnable> al;
    private String vertexSource;
    protected String fragmentSource;
    protected int aO;
    protected int aP;
    protected int aQ;
    protected int aR;

    public int aS;
    public int aT;
    private boolean aU;
    protected UnnamedA aV = new UnnamedA();
    protected int aW;
    protected int aX;
    protected boolean aY = false;
    protected boolean aZ = false;
    protected int ba = 1;
    protected float[] bb;
    protected boolean bc = false;
    private int bd;
    private int be;
    private int bf;
    private int bg;
    protected String bh = null;
    protected int bi = 0;
    protected int bj = 0;
    protected int bk = 0;

    public GPUImageFilter()
    {
        this("attribute vec4 position;\nattribute vec4 inputTextureCoordinate;\n \nvarying vec2 textureCoordinate;\n" +
                " \nvoid main()\n{\n    gl_Position = position;\n    textureCoordinate = inputTextureCoordinate.xy;\n}",
                "varying highp vec2 textureCoordinate;\n \nuniform sampler2D inputImageTexture;\n \nvoid main()\n{\n  " +
                        "   gl_FragColor = texture2D(inputImageTexture, textureCoordinate);\n}");
    }
    public GPUImageFilter(String paramString1, String paramString2)
    {
        this.al = new LinkedList();
        this.vertexSource = paramString1;
        this.fragmentSource = paramString2;
    }
    public void setPhoneDirection(int paramInt)
    {
        this.ba = paramInt;
    }

    public final void init (){
        l();
        this.aU = true;
        w();
    }

    public void c(boolean paramBoolean)
    {
        this.aY = paramBoolean;
    }

    public void d(boolean paramBoolean)
    {
        this.aZ = paramBoolean;
    }

    public void w() {
    }
    protected float g(int paramInt1, int paramInt2)
    {
        return this.aV.i[paramInt1][paramInt2].x;
    }

    protected float h(int paramInt1, int paramInt2)
    {
        if (!this.aY) {
            return this.aV.i[paramInt1][paramInt2].y;
        }
        return this.aX - this.aV.i[paramInt1][paramInt2].y;
    }

    private void l() {
        this.aO = k();
        this.aP = GLES20.glGetAttribLocation(this.aO,"position");
        this.aQ = GLES20.glGetUniformLocation(this.aO, "inputImageTexture");
        this.aR = GLES20.glGetAttribLocation(this.aO, "inputTextureCoordinate");

        this.bd = GLES20.glGetUniformLocation(this.aO, "isAndroid");
        this.be = GLES20.glGetUniformLocation(this.aO , "surfaceWidth");
        this.bf = GLES20.glGetUniformLocation(this.aO,"surfaceHeight");
        this.bg = GLES20.glGetUniformLocation(this.aO , "needFlip");
        this.aU = true;

    }

    protected int k (){
        return ShaderUtil.createProgram(vertexSource, fragmentSource);
    }
    public String x()
    {
        return this.bh;
    }

    public PointF[][] setFaceDetResult(int paramInt1, PointF[][] paramArrayOfPointF, int paramInt2, int paramInt3)
    {
        this.aV.a(paramInt1, paramArrayOfPointF);
        this.aW = paramInt2;
        this.aX = paramInt3;
        return paramArrayOfPointF;
    }
    public void t()
    {
        this.bc = true;
    }

    public void u()
    {
        this.bc = false;
    }
    public void releaseNoGLESRes() {}
    public final
}
