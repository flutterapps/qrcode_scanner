package com.shinow.qrscan;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.shinow.qrscan.CheckPermissionUtils;
import com.shinow.qrscan.LightSensorEventListener;

public class SecondActivity extends AppCompatActivity {

    private static boolean isOpen = false;
    private LinearLayout lightLayout;
    private LinearLayout backLayout;
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private SensorEventListener sensorEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        CaptureFragment captureFragment = new CaptureFragment();
        CodeUtils.setFragmentArgs(captureFragment, R.layout.my_camera);
        captureFragment.setAnalyzeCallback(analyzeCallback);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_my_container, captureFragment).commit();

        lightLayout = (LinearLayout) findViewById(R.id.scan_light);
        backLayout = (LinearLayout) findViewById(R.id.scan_back);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorEventListener = new LightSensorEventListener(lightLayout);

        initView();
        initPermission();
    }

    @Override
    protected void onResume() {
        // System.out.println("---------------------|||||||||||||---onResume---|||||||||||-------------------------");
        super.onResume();
        if (lightSensor != null) {
            sensorManager.registerListener(sensorEventListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        // System.out.println("---------------------|||||||||||||---onPause---|||||||||||-------------------------");
        sensorManager.unregisterListener(sensorEventListener);
        super.onPause();
    }

    private void initPermission() {
        String[] permissions = CheckPermissionUtils.checkPermission(this);
        if (permissions.length != 0) {
            ActivityCompat.requestPermissions(this, permissions, 100);
        }
    }

    private void initView() {
        lightLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOpen) {
                    CodeUtils.isLightEnable(true);
                    isOpen = true;
                } else {
                    CodeUtils.isLightEnable(false);
                    isOpen = false;
                }
            }
        });
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SecondActivity.this.finish();
            }
        });
    }

    private CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
            bundle.putString(CodeUtils.RESULT_STRING, result);
            resultIntent.putExtras(bundle);
            SecondActivity.this.setResult(RESULT_OK, resultIntent);
            SecondActivity.this.finish();
        }

        @Override
        public void onAnalyzeFailed() {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
            bundle.putString(CodeUtils.RESULT_STRING, "");
            resultIntent.putExtras(bundle);
            SecondActivity.this.setResult(RESULT_OK, resultIntent);
            SecondActivity.this.finish();
        }
    };

}
