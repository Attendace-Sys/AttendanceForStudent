package com.project.attendanceforstudent;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private static final int CAPTURE_VIDEO_REQUEST_CODE = 200;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 7777;

    TextView studentId, studentName, studentEmail;
    Button collectDatabtn;

    String id, name, mail;

    public ProfileFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);

        studentId = (TextView) view.findViewById(R.id.studentIdTxt2);
        studentName = (TextView) view.findViewById(R.id.fullNameTxt2);
        studentEmail = (TextView) view.findViewById(R.id.emailTxt2);
        collectDatabtn = (Button) view.findViewById(R.id.collect_data_btn);

        id = (String) studentId.getText();
        name = (String) studentName.getText();
        mail = (String) studentEmail.getText();

        collectDatabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                captureVideo();
                Intent intent = new Intent(getContext(), CreateProfileActivity.class);
                startActivity(intent);
            }
        });

        // Inflate the layout for this fragment
        return view;
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

        if (videoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(videoIntent, CAPTURE_VIDEO_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_VIDEO_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data.getData() != null) {
//                String selectedVideoPath = getPath(data.getData(), activity);
                Uri selectedImageUri = data.getData();

                // OI FILE Manager
                //fileManagerString = selectedImageUri.getPath();

                // MEDIA GALLERY
                String selectedVideoPath = getPath(selectedImageUri);
                if (selectedVideoPath != null) {

                    Intent intent = new Intent(getContext(),
                            DetectVideoActivity.class);

                    intent.putExtra("videoPath", selectedVideoPath);
                    intent.putExtra("studentName", name);
                    intent.putExtra("studentId", id);
                    intent.putExtra("studentEmail", mail);
                    startActivity(intent);
                }
            } else {
                Toast.makeText(getContext(), "Failed to take video", Toast.LENGTH_LONG).show();
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
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
        int camerapermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
        int writepermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readpermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);

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
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
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
