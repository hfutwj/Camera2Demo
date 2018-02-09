package controler;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.support.annotation.NonNull;
import android.util.Size;
import android.util.Log;
import android.view.Surface;
import android.widget.Toast;

import com.xcwj.jcamera2demo.R;

import java.util.ArrayList;
import java.util.List;

import surface.MyTextureView;
import utils.PermissionUtil;


/**
 * Created by xcwuj on 2017/10/6.
 */

public class CameraControler {
    private static final String TAG = "WJ_CAM";
    //拍照权限请求码
    private static final int REQUEST_PICTURE_PERMISSION = 1;
    //拍照权限
    private static final String[] PICTURE_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private boolean isCameraIdLegal = false;
    private boolean isCameraOpened = false;


    private CameraDevice mCameraDevice;
    private CameraManager mCameraManager;
    private CameraCaptureSession mCameraCaptureSession;
    private Context context;
    private CameraDevice.StateCallback mCameraStateCallback;
    private MyTextureView mMyTextureView;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private List<Surface> mSurfaceList;
    private Size mPreviewSize;
    private Size mPictureSize;


    public CameraControler(Context context, MyTextureView textureView) {
        this.context = context;
        this.mMyTextureView = textureView;
    }

    //初始化相机，获取相机基本信息
    public void initCamera() {
        Log.e(TAG,"initCamera");
        mCameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        //mPreviewSize.
        mCameraStateCallback = new CameraDevice.StateCallback() {
            @Override
            public void onOpened(@NonNull CameraDevice camera) {
                Log.e(TAG,R.string.CAMERA_OPENED+"");
                mCameraDevice = camera;
                startPreview();
            }

            @Override
            public void onDisconnected(@NonNull CameraDevice camera) {
                Log.e(TAG,R.string.CAMERA_DISCONNECTED+"");
            }

            @Override
            public void onError(@NonNull CameraDevice camera, int error) {
                switch(error) {
                    case CameraDevice.StateCallback.ERROR_CAMERA_DEVICE:
                        Log.e(TAG,R.string.ERROR_CAMERA_DEVICE+"");
                        Toast.makeText(context,"相机设备异常！",Toast.LENGTH_LONG).show();
                        break;
                    case CameraDevice.StateCallback.ERROR_CAMERA_DISABLED:
                        Log.e(TAG,R.string.ERROR_CAMERA_DISABLED+"");
                        break;
                    case CameraDevice.StateCallback.ERROR_CAMERA_IN_USE:
                        Log.e(TAG,R.string.ERROR_CAMERA_IN_USE+"");
                        break;
                    case CameraDevice.StateCallback.ERROR_CAMERA_SERVICE:
                        Log.e(TAG,R.string.ERROR_CAMERA_SERVICE+"");
                        break;
                    case CameraDevice.StateCallback.ERROR_MAX_CAMERAS_IN_USE:
                        Log.e(TAG,R.string.ERROR_MAX_CAMERAS_IN_USE+"");
                }
            }
        };
    }

    //打开相机
    @SuppressLint("MissingPermission")
    public void openCamera(int cameraId) {
        //查看是否有相机权限，如果没有则请求camera权限
        PermissionUtil permissionUtil = new PermissionUtil((Activity) this.context);
        if(!permissionUtil.hasPermissionGtranted(PICTURE_PERMISSIONS)) {
            permissionUtil.requestRequiredPermissions(PICTURE_PERMISSIONS, R.string.need_permissions,REQUEST_PICTURE_PERMISSION);
            return;
        }
        //初始化相机
        initCamera();
        //需要添加cameraID是否合法的判断
        try {
            String[] cameraIds = this.mCameraManager.getCameraIdList();
            for(String cameraIdStr : cameraIds) {
                if(Integer.parseInt(cameraIdStr) == cameraId) {
                    isCameraIdLegal = true;
                    break;
                }
            }
            if (!isCameraIdLegal) {
                Log.e(TAG,"camera ID不合法！");
                return;
            }

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        try{
            mCameraManager.openCamera(cameraId+"",mCameraStateCallback,null);
            isCameraOpened = true;
        } catch (CameraAccessException camera_exception){
            camera_exception.printStackTrace();
        }
    }

    //开始预览
    public void startPreview() {
        Log.e(TAG,"startPreview");
        if (mCameraDevice == null || !mMyTextureView.isAvailable()) {
            Log.e(TAG,"mCameraDevice == null or mMyTextureView is not available!");
            return;
        }
        SurfaceTexture surfaceTexture = mMyTextureView.getSurfaceTexture();
        //设置预览大小
        surfaceTexture.setDefaultBufferSize(1280,720);
        Surface surface = new Surface(surfaceTexture);

        try {
            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surface);
            mSurfaceList = new ArrayList<Surface>();
            mSurfaceList.add(surface);
            mCameraDevice.createCaptureSession(mSurfaceList, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    if(mCameraDevice == null) {
                        return;
                    }
                    mCameraCaptureSession =  cameraCaptureSession;
                    Log.e(TAG,"startPreview onConfigured");
                    //setBuilder(mPreviewRequestBuilder);
                    try {
                        mCameraCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(),null,null);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(context,"预览失败！",Toast.LENGTH_LONG).show();
                }
            },null);
        }catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //拍照
    public void doCapture() {

    }

    //停止预览
    public void stopPreview() {

    }

    //关闭相机，释放摄像头资源
    public void closeCamera() {

    }


}
