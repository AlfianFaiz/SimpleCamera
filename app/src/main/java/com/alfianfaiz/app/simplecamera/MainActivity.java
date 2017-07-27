package com.alfianfaiz.app.simplecamera;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialcamera.MaterialCamera;

import java.io.File;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private final static int CAMERA_RQ = 6969;
    private final static int PERMISSION_RQ = 84;
    String SaveFolder;
    EditText txtPO;
    Button btnShot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtPO = (EditText)findViewById(R.id.txtPO);
        btnShot = (Button) findViewById(R.id.launchCameraStillshot);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Request permission to save videos in external storage
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_RQ);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")


    private String readableFileSize(long size) {
        if (size <= 0) return size + " B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    private String fileSize(File file) {
        return readableFileSize(file.length());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Received recording or error from MaterialCamera
        if (requestCode == CAMERA_RQ) {
            if (resultCode == RESULT_OK) {
                final File file = new File(data.getData().getPath());
                Toast.makeText(this, String.format("Saved to: %s, size: %s",
                        file.getAbsolutePath(), fileSize(file)), Toast.LENGTH_LONG).show();
            } else if (data != null) {
                Exception e = (Exception) data.getSerializableExtra(MaterialCamera.ERROR_EXTRA);
                if (e != null) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    @Override
    public void onClick(View v) {
        btnShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String PO = txtPO.getText().toString();
                if (PO.matches("")) {
                    Toast.makeText(MainActivity.this, "Please insert PO Number first",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    File saveDir = null;
                    SaveFolder = "AlfianCamera/"+txtPO.getText().toString();

                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        // Only use external storage directory if permission is granted, otherwise cache directory is used by default
                        saveDir = new File(Environment.getExternalStorageDirectory(), SaveFolder);
                        saveDir.mkdirs();
                    }
                    MaterialCamera materialCamera = new MaterialCamera(MainActivity.this)
                            .stillShot()
                            .saveDir(saveDir)
                            .showPortraitWarning(true)
                            .allowRetry(true)
                            .defaultToFrontFacing(true)
                            .autoSubmit(false);
                    materialCamera.start(CAMERA_RQ);
                }


            }

        });
    }
}