package com.example.demo_1;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.demo_1.tools.ISuperCamera.ButtonCallback;
import com.example.demo_1.tools.SuperCamera;
//https://github.com/MinghuiGao/rebot_joystick.git
public class MainActivity extends Activity {
	
		private SuperCamera mSuperCamera;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mSuperCamera = (SuperCamera) findViewById(R.id.mc_camera);
        
        mSuperCamera.setCallback(new ButtonCallback() {
			
			@Override
			public void onSuccess(String filePath, Bundle bundle) {
				Toast.makeText(MainActivity.this, "filepath: "+filePath, Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onShootClicked() {
				Toast.makeText(MainActivity.this, "点击拍照",0).show();
			}
			
			@Override
			public void onBackClicked() {
				mSuperCamera.destory();
				MainActivity.this.finish();
			}
		});

    }



}
