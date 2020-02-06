package com.project.attendanceforstudent;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class CreateProfileActivity extends AppCompatActivity {

    // Activity request codes
    private static final int CAPTURE_VIDEO_REQUEST_CODE = 200;

    private static final int SELECT_VIDEO_REQUEST_CODE = 400;

    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 7777;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    // Gallery directory name to store the images or videos
    public static final String GALLERY_DIRECTORY_NAME = "Hello Camera";

    // Image and Video file extensions
    public static final String IMAGE_EXTENSION = "jpg";
    public static final String VIDEO_EXTENSION = "mp4";

    TextView name, id, mail;
    Button btnSelect, btnRecordVideo;
    ImageView back_btn;
    String selectedVideoPath;
    String fileManagerString;
    String studentName;
    String studentId;
    String studentEmail;

    TextView txtGuide;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        checkAndRequestPermissions();

        btnSelect = (Button) findViewById(R.id.btnSelect);
        btnRecordVideo = (Button) findViewById(R.id.btnRecordVideo);
        name = (TextView) findViewById(R.id.student_name);
        id = (TextView) findViewById(R.id.student_id);
        mail = (TextView)findViewById(R.id.email);
        back_btn=(ImageView) findViewById(R.id.back);

        id.setText(Global.studentid);
        name.setText(Global.studentname);
        mail.setText(Global.email);

        txtGuide = findViewById(R.id.txtGuide);


        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isStudentInfoEntered() == false) {
                    showMissedStudentInfoDialog();
                } else {
                    selectVideoFromGallery();

                }

            }
        });
        btnRecordVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isStudentInfoEntered() == false) {
                    showMissedStudentInfoDialog();
                }
                else if (checkEmailValidation() == false)
                {
                    showWrongEmailValidation();
                }else {
                    SharedPreferences prefs = CreateProfileActivity.this.getSharedPreferences(
                            "com.project.attendancestudent", Context.MODE_PRIVATE);

                    boolean showGuide = prefs.getBoolean("showGuide", false);

                    if (showGuide == true) {
                        captureVideo();

                    } else {
                        displayDialogForGuidingFaceCollection(true);
                    }
                }
            }
        });

        txtGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDialogForGuidingFaceCollection(false);
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentBack = new Intent(CreateProfileActivity.this, MainActivity.class);
                intentBack.putExtra("fragmentName", "profile");
                startActivity(intentBack);
            }
        });

    }

    private void displayDialogForGuidingFaceCollection(boolean isFirstTime) {

        if (isFirstTime) {
            SharedPreferences prefs = this.getSharedPreferences(
                    "com.project.attendancestudent", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("showGuide", true);
            editor.commit();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Để có dữ liệu tốt nhất, lưu ý:");
        builder.setMessage( "1. Chuyển thành camera trước nếu camera trước chưa bật \n"
                +"2. Đảm bảo camera nhìn rõ mặt \n" +
                "3. Quay mặt chính diện, đánh mặt sang trái, phải, lên trên, xuống dưới một góc 45 độ.");

        if(isFirstTime) {

            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                 captureVideo();
                }
            });
        } else {
            builder.setPositiveButton("Ok", null);

        }

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private boolean isStudentInfoEntered() {
        return (name.getText().toString().trim().length() > 0 && id.getText().toString().trim().length()> 0
                && mail.getText().toString().trim().length()>0);
    }

    private boolean checkEmailValidation(){
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-zA-Z0-9.]+";
        return (mail.getText().toString().trim().matches(emailPattern));
    }

    private void showMissedStudentInfoDialog() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông báo  ");
        builder.setMessage("Vui lòng nhập đầy đủ thông tin sinh viên ");
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showWrongEmailValidation() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông báo  ");
        builder.setMessage("Vui lòng nhập lại email.");
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void captureVideo() {
        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            videoIntent.putExtra("android.intent.extras.CAMERA_FACING", android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
            videoIntent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
            videoIntent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
        } else {
            videoIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        }

        videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 3);
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
//        intent.setAction(Intent.ACTION_PICK);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, SELECT_VIDEO_REQUEST_CODE);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_VIDEO_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data.getData() != null) {
//                String selectedVideoPath = getPath(data.getData(), activity);
                Uri selectedImageUri = data.getData();

                // OI FILE Manager
                //fileManagerString = selectedImageUri.getPath();

                // MEDIA GALLERY
                selectedVideoPath = getPath(selectedImageUri);
                if (selectedVideoPath != null) {

                    studentName = name.getText().toString();
                    studentId = id.getText().toString();
                    studentEmail = mail.getText().toString();

                    Intent intent = new Intent(CreateProfileActivity.this,
                            DetectVideoActivity.class);

                    intent.putExtra("videoPath", selectedVideoPath);
                    intent.putExtra("studentName", studentName);
                    intent.putExtra("studentId", studentId);
                    intent.putExtra("studentEmail", studentEmail);
                    startActivity(intent);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Failed to take video", Toast.LENGTH_LONG).show();
            }
        }else if (requestCode == SELECT_VIDEO_REQUEST_CODE && resultCode == RESULT_OK)
        {
            if (data.getData() != null) {
//                String selectedVideoPath = getPath(data.getData(), activity);
                Uri selectedImageUri = data.getData();

                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);
                cursor.moveToFirst();
                //Get the column index of MediaStore.Images.Media.DATA
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                // MEDIA GALLERY
                selectedVideoPath = cursor.getString(columnIndex);
                if (selectedVideoPath != null) {

                    studentName = name.getText().toString();
                    studentId = id.getText().toString();
                    studentEmail = mail.getText().toString();

                    Intent intent = new Intent(CreateProfileActivity.this,
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
            cursor.moveToFirst();

            int column_index = cursor
                    .getColumnIndexOrThrow(projection[0]);
//            int column_index = cursor
//                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
//            cursor.moveToFirst();

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
