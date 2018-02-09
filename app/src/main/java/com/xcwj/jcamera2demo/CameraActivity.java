package com.xcwj.jcamera2demo;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.TextureView;
import android.view.WindowManager;
import android.widget.Button;

import controler.CameraControler;
import surface.MyTextureView;

public class CameraActivity extends AppCompatActivity {
    private final String TAG = "WJ_CAM";
    public static Activity camerActivity;
    //private TextureView textureViewTemp;
    private MyTextureView mTextureView;
    private Button mButton;
    public static CameraControler cameraControler;

    private TextureView.SurfaceTextureListener mTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
            Log.e(TAG,"onSurfaceTextureAvailable");
            cameraControler.openCamera(0);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        camerActivity = this;
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        //初始化控件
        initUI();
        //创建CameraContorler对象
        cameraControler = new CameraControler(this,mTextureView);
    }

    private void initUI () {
        //textureViewTemp = (TextureView) findViewById(R.id.texture);
        mTextureView = (MyTextureView) findViewById(R.id.mtexture);
        mButton = (Button) findViewById(R.id.button);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mTextureView.isAvailable()) {
            Log.e(TAG,"isAvailable will open!");
            cameraControler.openCamera(0);
        }
        else {
            mTextureView.setSurfaceTextureListener(mTextureListener);
        }
    }
}
