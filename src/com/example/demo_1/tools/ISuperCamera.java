package com.example.demo_1.tools;

import android.os.Bundle;

/**
 * 
 * 
 * @author gaominghui
 *
 */
public interface ISuperCamera {

	public void takePhoto();
	
	public void turnOnFlash();

	public void turnOffFlash();
	
	public void destory();
	/** 相机的按钮回调接口 **/
	public interface ButtonCallback{
		public void onBackClicked();
		public void onShootClicked();
		/**
		 * 功能描述：拍照成功后的回调，传回照片的存储路径，及图片字节数据保存在bundle当中。
		 */
		public void onSuccess(String filePath,Bundle bundle);
	}
}
