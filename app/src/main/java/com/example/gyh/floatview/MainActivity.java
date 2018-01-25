package com.example.gyh.floatview;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = View.inflate(MainActivity.this,R.layout.test_view,null);
                view.findViewById(R.id.vv).setBackgroundColor(0xff00ffff);
//        View view = null;
                FloatView.getInstance(MainActivity.this)
                        .setView(view)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(MainActivity.this,"555",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
            }
        });
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatView.getInstance(MainActivity.this).hide();
            }
        });
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                //启动Activity让用户授权
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 10);
            } else {
                showFloatView();
            }
        }else {
            showFloatView();
        }

    }

    private void showFloatView() {
        View view = View.inflate(this,R.layout.test_view,null);
//        View view = null;
        FloatView.getInstance(this)
                .setView(view)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this,"555",Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0) {
            showFloatView();
        }
    }
}
