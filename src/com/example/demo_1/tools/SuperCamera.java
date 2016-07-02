package com.example.demo_1.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.demo_1.R;

public class SuperCamera extends FrameLayout implements ISuperCamera, OnClickListener {

	
	private int[] clickableIds = new int[]{R.id.bt_back,R.id.bt_flash,R.id.bt_take_pic};
	private SurfaceView  mSurfaceView;
	private Camera mCamera ;
	private Parameters parameters;
	private ButtonCallback mButtonCallback = new ButtonCallback() {
		@Override
		public void onShootClicked() {
		}
		@Override
		public void onBackClicked() {
		}
		@Override
		public void onSuccess(String filePath, Bundle bundle) {
		}
	};
	// https://github.com/MinghuiGao/JoystickView.git
	public SuperCamera(Context context){
		this(context,null);
	}
	public SuperCamera(Context context, AttributeSet attrs){
		this(context,attrs,0);
	}
	
	public SuperCamera(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		initViews();
	}

	private void initViews() {
		// init buttons
		for(int id : clickableIds){
			View view = this.findViewById(id);
			if(view != null)
				view.setOnClickListener(this);
		}
		
		// init surfaceview
		mSurfaceView = (SurfaceView) this.findViewById(R.id.sv_camera_display);
		if(mSurfaceView != null){
			mSurfaceView.setFocusable(true);
			SurfaceHolder holder= mSurfaceView.getHolder();
			holder.setKeepScreenOn(true);
			holder.addCallback(new SurfaceCallback());
		}
	}


	@Override
	public void turnOnFlash() {
		parameters = mCamera.getParameters();
		parameters.setPictureFormat(PixelFormat.JPEG);
		parameters.setJpegQuality(50);
		parameters.setFlashMode(Parameters.FLASH_MODE_ON);
		parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 1连续对焦
		mCamera.setParameters(parameters);
		mCamera.startPreview();
		mCamera.cancelAutoFocus();// 如果要实现连续的自动对焦，这一句必须加上
	}

	@Override
	public void turnOffFlash() {
		parameters = mCamera.getParameters();
		parameters.setPictureFormat(PixelFormat.JPEG);
		parameters.setJpegQuality(50);
		parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
		parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 1连续对焦
		mCamera.setParameters(parameters);
		mCamera.startPreview();
		mCamera.cancelAutoFocus();// 如果要实现连续的自动对焦，这一句必须加上
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_back:
			Toast.makeText(getContext(), "onclick back", 0).show();
			mButtonCallback.onBackClicked();
			break;
		case R.id.bt_take_pic:
			takePhoto();
			mButtonCallback.onShootClicked();
			break;
		case R.id.bt_flash:
			String status = ((Button)v).getText().toString();
			if("开闪".equals(status)){
				turnOnFlash();
				((Button)v).setText("关闪");
			}else if("关闪".equals(status)){
				turnOffFlash();
				((Button)v).setText("开闪");
			}
			break;
		default:
			break;
		}
	}

	private final class SurfaceCallback implements android.view.SurfaceHolder.Callback{

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			// init camera
			if(mCamera == null){
				mCamera = Camera.open();
				if(mCamera != null){
					try {
						mCamera.setPreviewDisplay(holder);
					} catch (IOException e) {
						e.printStackTrace();
					}
					initCamera();
				}else{
					Log.i("gaomh", "open camera exception.");
				}
			}
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			mCamera.autoFocus(new AutoFocusCallback(){
				@Override
				public void onAutoFocus(boolean success, Camera camera) {
					if(success){
						initCamera();
					}
				}
			});
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			if(mCamera != null){
				mCamera.stopPreview();
				mCamera.release();
				mCamera = null;
			}
		}
		
	}
	
	/** 设置照相机的回调 **/
	public void setCallback(ButtonCallback buttonCallback){
		this.mButtonCallback = buttonCallback;
	}
	
	private void initCamera() {
		parameters = mCamera.getParameters();
		parameters.setPictureFormat(PixelFormat.JPEG);
		parameters.setJpegQuality(50);
		parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
		// 纠正角度
		setDispaly(parameters, mCamera);
		
		mCamera.setParameters(parameters);
		mCamera.startPreview();
		mCamera.cancelAutoFocus();
	}
	// 控制图像的正确显示方向 华为设置后角度错误
	private void setDispaly(Camera.Parameters parameters, Camera camera) {
		if (Integer.parseInt(Build.VERSION.SDK) >= 8) {
			setDisplayOrientation(camera, 90);
		} else {
			parameters.setRotation(90);
		}
	}
	// 实现的图像的正确显示
	private void setDisplayOrientation(Camera camera, int i) {
		Method downPolymorphic;
		try {
			downPolymorphic = camera.getClass().getMethod("setDisplayOrientation", new Class[] { int.class });
			if (downPolymorphic != null) {
				downPolymorphic.invoke(camera, new Object[] { i });
			}
		} catch (Exception e) {
			Log.e("Came_e", "图像出错");
		}
	}
	@Override
	public void destory() {
		if(mCamera != null){
			mCamera.release();
		}
		mCamera = null;
		mSurfaceView = null;
	}
	@Override
	public void takePhoto() {
		if(mCamera != null){
//			mCamera.takePicture(shutter, raw, jpeg);
			mCamera.takePicture(null, null, new PictureCallback() {
				@Override
				public void onPictureTaken(byte[] data, Camera camera) {
					try {
						String filePath = saveToSDCard(data);
						mButtonCallback.onSuccess(filePath, null);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
	
	
	/** 将照片保存至sdcard **/
	public static String saveToSDCard(byte[] data) throws IOException {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); // 格式化时间
		String filename = format.format(date) + ".jpeg";
		File fileFolder = new File(Environment.getExternalStorageDirectory()+ "/demo");
		if (!fileFolder.exists()) { // 如果目录不存在，则创建一个名为"finger"的目录
			fileFolder.mkdir();
		}
		File jpgFile = new File(fileFolder, filename);
		FileOutputStream outputStream = new FileOutputStream(jpgFile); // 文件输出流
		outputStream.write(data); // 写入sd卡中
		outputStream.close(); // 关闭输出流
		String jpgFilePath  =  jpgFile.getAbsolutePath();
		return jpgFilePath;
	}
	
}
