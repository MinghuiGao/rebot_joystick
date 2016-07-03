package com.example.demo_1;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.demo_1.tools.ISuperCamera.ButtonCallback;
import com.example.demo_1.tools.Joystick;
import com.example.demo_1.tools.Joystick.OnJoystickMoveListener;
import com.example.demo_1.tools.SuperCamera;
//https://github.com/MinghuiGao/rebot_joystick.git
public class MainActivity extends Activity {

	private SuperCamera mSuperCamera;
	private Joystick mJoystick;
	private TextView mTextViewInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mSuperCamera = (SuperCamera) findViewById(R.id.mc_camera);
		mJoystick = (Joystick) findViewById(R.id.joystick_view);
		mTextViewInfo = (TextView) findViewById(R.id.tv_info);

		mSuperCamera.setCallback(new ButtonCallback() {

			@Override
			public void onSuccess(String filePath, Bundle bundle) {
				Toast.makeText(MainActivity.this, "filepath: " + filePath,
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onShootClicked() {
				Toast.makeText(MainActivity.this, "点击拍照", 0).show();
			}

			@Override
			public void onBackClicked() {
				mSuperCamera.destory();
				MainActivity.this.finish();
			}
		});
		mJoystick.setOnJoystickMoveListener(new OnJoystickMoveListener() {
			@Override
			public void onValueChanged(int angle, int power, int direction) {
				mTextViewInfo.setText("操作信息> 角度 : "+ angle + "速度 : "+ power + "方向 : " + direction);
			}
		}, 0);

	}

}
