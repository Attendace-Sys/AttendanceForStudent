package com.project.attendanceforstudent;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.camera2.CameraCharacteristics;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    // Activity request codes
    private static final int CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAPTURE_VIDEO_REQUEST_CODE = 200;

    private static final int SELECT_IMAGE_REQUEST_CODE = 300;
    private static final int SELECT_VIDEO_REQUEST_CODE = 400;

    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 7777;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    // Bitmap sampling size
    public static final int BITMAP_SAMPLE_SIZE = 8;

    // Gallery directory name to store the images or videos
    public static final String GALLERY_DIRECTORY_NAME = "Hello Camera";

    // Image and Video file extensions
    public static final String IMAGE_EXTENSION = "jpg";
    public static final String VIDEO_EXTENSION = "mp4";

    EditText name, id, mail;
    Button btnSelect, btnRecordVideo;

    String selectedVideoPath;
    String fileManagerString;
    String studentName;
    String studentId;
    String studentEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkAndRequestPermissions();

        btnSelect = (Button) findViewById(R.id.btnSelect);
        btnRecordVideo = (Button) findViewById(R.id.btnRecordVideo);
        name = (EditText) findViewById(R.id.student_name);
        id = (EditText) findViewById(R.id.student_id);
        mail = (EditText)findViewById(R.id.email);

//        btnSelect.setEnabled(false);
//        btnRecordVideo.setEnabled(false);

//        if ((name.getText().toString() != null) && (id.getText().toString() != null) && (mail.getText()).toString() != null) {
//            studentName = name.getText().toString();
//            studentId = id.getText().toString();
//            studentEmail = mail.getText().toString();
//        }

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectVideoFromGallery();
            }
        });
        btnRecordVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureVideo();
            }
        });
    }

    private void captureVideo() {
        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//        videoIntent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
//        videoIntent.putExtra("android.intent.extras.CAMERA_FACING", 0);
//        videoIntent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//            videoIntent.putExtra("android.intent.extras.CAMERA_FACING", CameraCharacteristics.LENS_FACING_FRONT);  // Tested on API 24 Android version 7.0(Samsung S6)
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            videoIntent.putExtra("android.intent.extras.CAMERA_FACING", CameraCharacteristics.LENS_FACING_FRONT); // Tested on API 27 Android version 8.0(Nexus 6P)
//            videoIntent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
//        } else {
//            videoIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);  // Tested API 21 Android version 5.0.1(Samsung S4)
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            videoIntent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
        } else {
            videoIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        }

        videoIntent.putExtra(android.provider.MediaStore.EXTRA_DURATION_LIMIT, 4);
        videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

        if (videoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(videoIntent, CAPTURE_VIDEO_REQUEST_CODE);
        }
    }

    public void selectVideoFromGallery() {
        Intent intent;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        } else {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.INTERNAL_CONTENT_URI);
        }
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, SELECT_VIDEO_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == SELECT_VIDEO_REQUEST_CODE && resultCode == RESULT_OK)
                || (requestCode == CAPTURE_VIDEO_REQUEST_CODE && resultCode == RESULT_OK)) {
            if (data.getData() != null) {
//                String selectedVideoPath = getPath(data.getData(), activity);
                Uri selectedImageUri = data.getData();

                // OI FILE Manager
                fileManagerString = selectedImageUri.getPath();

                // MEDIA GALLERY
                selectedVideoPath = getPath(selectedImageUri);
                if (selectedVideoPath != null) {

                    studentName = name.getText().toString();
                    studentId = id.getText().toString();
                    studentEmail = mail.getText().toString();

                    Intent intent = new Intent(MainActivity.this,
                            DetectVideoActivity.class);

                    intent.putExtra("videoPath", selectedVideoPath);
                    intent.putExtra("studentName", studentName);
                    intent.putExtra("studentId", studentId);
                    intent.putExtra("studentEmail", studentEmail);
                    startActivity(intent);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Failed to select video", Toast.LENGTH_LONG).show();
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    private boolean checkAndRequestPermissions() {
        int camerapermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int writepermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readpermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (camerapermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (writepermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (readpermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //cameraView.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS) {
            Map<String, Integer> perms = new HashMap<>();
            // Initialize the map with both permissions
            perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            // Fill with actual results from user
            if (grantResults.length > 0) {
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
            }
        }
    }

}
