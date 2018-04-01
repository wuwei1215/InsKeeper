package com.practice.wuwei.inskeeper;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

/**
 * @author by wuwei
 * @date on 2018/2/9 下午4:44
 */
public class MainActivity extends AppCompatActivity {
    private static int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 100;

    private Switch swService;
    private Intent intent;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intent = new Intent(this, DownloadService.class);
        swService = (Switch) findViewById(R.id.sw_service);

        swService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //getPermission();
                    if(getPermission()) {
                        //bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
                        startService(intent);
                    }
                } else {
                    stopService(intent);
//                    unbindService(serviceConnection);
                }
            }
        });

//        if(getPermission()){
//            startService(intent);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        SharedPreferences sharedPreferences = getSharedPreferences("InsDownload",MODE_PRIVATE);
//        boolean isChecked = sharedPreferences.getBoolean("SwitchState",false);
//        swService.setChecked(isChecked);

    }

    @Override
    protected void onPause() {
        super.onPause();
//        SharedPreferences sharedPreferences = getSharedPreferences("InsDownload",MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putBoolean("SwitchState", swService.isChecked());
//        editor.commit();

    }

    @Override
    protected void onDestroy() {
//        if (swService.isChecked()) {
            stopService(intent);
//        }
        super.onDestroy();

    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putBoolean("serviceState", swService.isChecked());
//    }

    private boolean getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                swService.setChecked(false);
                //申请WRITE_EXTERNAL_STORAGE权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                return  false;
            } else {
                Log.e("我已有权限", "已有权限");
                //startService(intent);
                return  true;
            }
        } else {
            //startService(intent);
            return  true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //callPhone();
                //swService.setChecked(true);
                Toast.makeText(MainActivity.this, "Permission Received", Toast.LENGTH_SHORT).show();
            } else {
                // Permission Denied
                Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                //swService.setChecked(false);
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
