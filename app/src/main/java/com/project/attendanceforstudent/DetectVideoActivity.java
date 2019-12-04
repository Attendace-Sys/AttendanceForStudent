package com.project.attendanceforstudent;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import com.project.attendanceforstudent.Networking.ApiConfig;
import com.project.attendanceforstudent.Networking.AppConfig;
import com.project.attendanceforstudent.Networking.Student;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.internal.http.StatusLine;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wseemann.media.FFmpegMediaMetadataRetriever;

public class DetectVideoActivity extends AppCompatActivity {

    private VideoView videoPreview;
    private Button btnDetectFace;
    private Button btnSendData;
    GridView mGridView;

    private String videoPath;
    private String studentName;
    private String studentId;
    private String studentEmail;

    private MediaPlayer mediaPlayer = null;
    private MediaController mediaController = null;
    private int temp = 0;

    private ArrayList<Bitmap> listBitmap;
    private ArrayList<Bitmap> listFrame;

    MyAdapter myAdapter;

    public ProgressDialog pDialog;

    String cookie;

    int max;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_video);


        //db = new DBHelper(DetectVideoActivity.this);
        videoPreview = (VideoView) findViewById(R.id.videoPreview);

        btnSendData = (Button) findViewById(R.id.send_data);
        //mListView = (ListView) findViewById(R.id.list_view);
        mGridView = (GridView) findViewById(R.id.gridView);

        Bundle bundle = getIntent().getExtras();
        videoPath = bundle.getString("videoPath");
        studentName = bundle.getString("studentName");
        studentId = bundle.getString("studentId");
        studentEmail = bundle.getString("studentEmail");

        initDialog();

        if (videoPath != null) {

            Uri uri = Uri.parse(videoPath);
            try {
                videoPreview.setVisibility(View.VISIBLE);
                //videoPreview.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.test);
                videoPreview.setVideoURI(uri);
                MediaController mediaController = new MediaController(this);
                videoPreview.setMediaController(mediaController);
                mediaController.setAnchorView(videoPreview);

                btnDetectFace.setEnabled(true);

            } catch (Exception e) {
                e.printStackTrace();
            }

            showpDialog();

            extractFrameInVideo(videoPath);

            btnSendData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showpDialog();


                    File fileVideo = new File(videoPath);
                    File[] lis = new File[2];
                    lis[0] = fileVideo;
                    lis[1] = fileVideo;

                    //File file;

                    //Map<String, MultipartBody.Part> imgFiles = new HashMap<String, MultipartBody.Part>();

                    List<MultipartBody.Part> imgParts = new ArrayList<>();

                    for (Bitmap bitmap: listBitmap){
                        String fileName = studentId;
                        File file = convertToFile(bitmap, fileName);
                        RequestBody requestImg = RequestBody.create(MediaType.parse("image/*"), file);
                        MultipartBody.Part part = MultipartBody.Part.createFormData("files", file.getName(), requestImg);

                        imgParts.add(part);

                    }
                    // Parsing any Media type file
                    RequestBody requestBody = RequestBody.create(MediaType.parse("video/*"), fileVideo);
                    //map.put("student_video_data", requestBody);
                    // Create MultipartBody.Part using file request-body,file name and part name
                    MultipartBody.Part part = MultipartBody.Part.createFormData("student_video_data", studentId + ".mp4", requestBody);

                    ApiConfig getResponse = AppConfig.getRetrofit().create(ApiConfig.class);

                    RequestBody idBody = RequestBody.create(MediaType.parse("text/plain"), studentId);
                    RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), studentName);
                    RequestBody mailBody = RequestBody.create(MediaType.parse("text/plain"), studentEmail);

                    Call<ResponseBody> call = getResponse.uploadStudentProfile(idBody, nameBody, mailBody, part, imgParts);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    hidepDialog();
                                    Toast.makeText(getApplicationContext(), "Success upload!", Toast.LENGTH_SHORT).show();

                                }
                            } else {
                                hidepDialog();
                                Toast.makeText(getApplicationContext(), "Problem uploading video", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            hidepDialog();
                            Toast.makeText(getApplicationContext(), "Failure uploading video " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        }
    }

    private void uploadVideo(String videoPath) {
        File file = new File(videoPath);

        // Parsing any Media type file
        RequestBody requestBody = RequestBody.create(MediaType.parse("video/*"), file);
        //map.put("student_video_data", requestBody);
        // Create MultipartBody.Part using file request-body,file name and part name
        MultipartBody.Part part = MultipartBody.Part.createFormData("student_video_data", studentId + ".mp4", requestBody);

        ApiConfig getResponse = AppConfig.getRetrofit().create(ApiConfig.class);

        RequestBody idBody = RequestBody.create(MediaType.parse("text/plain"), studentId);
        RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), studentName);
        RequestBody mailBody = RequestBody.create(MediaType.parse("text/plain"), studentEmail);

        Call<ResponseBody> call = getResponse.upload(idBody, nameBody, mailBody, part);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        //Student serverResponse = response.body();
                        hidepDialog();
                        Toast.makeText(getApplicationContext(), response.body().toString(), Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(getApplicationContext(), "problem uploading video", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Toast.makeText(getApplicationContext(), "failure uploading video " + t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void initDialog() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Uploading...");
        pDialog.setCancelable(true);
    }

    protected void showpDialog() {

        if (!pDialog.isShowing()) pDialog.show();
    }

    protected void hidepDialog() {

        if (pDialog.isShowing()) pDialog.dismiss();
    }


    private void extractFrameInVideo(String srcPath) {
        temp = 0;

        FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
        mmr.setDataSource(srcPath);

        String METADATA_KEY_DURATION = mmr
                .extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION);

        String rotation = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        // 270 front camera
        // 90 back camera

        Bitmap bmpOriginal = null;

        max = (int) (Long.parseLong(METADATA_KEY_DURATION) * 4 / 1000);

        listBitmap = new ArrayList<Bitmap>();

        listFrame = new ArrayList<Bitmap>();

        for (int index = 0; index < max; index++) {
            int time = index * 1000 / 4;
            bmpOriginal = mmr.getFrameAtTime(time * 1000, FFmpegMediaMetadataRetriever.OPTION_CLOSEST);

            listFrame.add(rotateBitmap(bmpOriginal, rotation));

            final FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(listFrame.get(index));
            FirebaseVisionFaceDetectorOptions options = new FirebaseVisionFaceDetectorOptions.Builder()
                    .build();
            FirebaseVisionFaceDetector detector = FirebaseVision.getInstance()
                    .getVisionFaceDetector(options);

            Task<List<FirebaseVisionFace>> listTask = detector.detectInImage(image)
                    .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                        @Override
                        public void onSuccess(List<FirebaseVisionFace> faces) {
                            processFaceResult(faces, image);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });

            //Toast.makeText(this, String.format("In %d detect loop HAVE %d faces in image", index, temp), Toast.LENGTH_SHORT).show();

        }
        mmr.release();
//        Toast.makeText(this,String.format("Final HAVE %d faces in image",temp), Toast.LENGTH_SHORT).show();

    }

    private Bitmap rotateBitmap(Bitmap bmpOriginal, String rotation) {

        int mBitW = Integer.parseInt(String.valueOf(bmpOriginal.getWidth()));
        int mBitH = Integer.parseInt(String.valueOf(bmpOriginal.getHeight()));

        Matrix matrix = new Matrix();
        switch (rotation) {
            case "90":
                matrix.postRotate(90);
                break;
            case "270":
                matrix.postRotate(-90);
                break;
            default:
                return bmpOriginal;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bmpOriginal, 0, 0, mBitW, mBitH, matrix, true);
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }

    }

    private void processFaceResult(List<FirebaseVisionFace> faces, FirebaseVisionImage image) {
        //       int count = 0;
        for (FirebaseVisionFace face : faces) {
            Rect bounds = face.getBoundingBox();

            Bitmap bitmap = cropBitmap(image.getBitmap(), bounds);

            //File mFile = new File("/sdcard/pictureTest");
            bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
            //long result = db.addStudentImage(studentId, Utils.getBytes(bitmap));

            listBitmap.add(bitmap);

//            uploadImageToServer(bitmap, studentId, temp);

            temp++;
        }

        if (temp == max)
        {
            btnSendData.setEnabled(true);
            myAdapter = new MyAdapter(DetectVideoActivity.this, listBitmap);

            mGridView.setAdapter(myAdapter);
            hidepDialog();
        }
    }

    public static Bitmap cropBitmap(Bitmap bitmap, Rect rect) {
        int w = rect.right - rect.left;
        int h = rect.bottom - rect.top;
        Bitmap ret = Bitmap.createBitmap(w, h, bitmap.getConfig());
        Canvas canvas = new Canvas(ret);
        canvas.drawBitmap(bitmap, -rect.left, -rect.top, null);
        return ret;
    }

    public static byte[] convertBitmapToByteArray(Bitmap bitmap) {
        int size = bitmap.getRowBytes() * bitmap.getHeight();
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        bitmap.copyPixelsFromBuffer(byteBuffer);
        byte[] bytes = new byte[size];

        try {
            byteBuffer.get(bytes, 0, bytes.length);
        } catch (BufferUnderflowException e) {
            // always happens
        }
        return bytes;
    }

    private static File convertToFile(Bitmap bitmap, String name) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fname = name + "_" + timeStamp + ".jpg";

        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public void uploadImageToServer(Bitmap bitmap, String id, int count) {
        String fileName = id + "_" + count;

        RequestBody idBody = RequestBody.create(MediaType.parse("text/plain"), id);
        //RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), fileName);

        //byte[] bytes = convertBitmapToByteArray(bitmap);
        File imageFile = convertToFile(bitmap, fileName);

        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), imageFile);

        MultipartBody.Part part = MultipartBody.Part.createFormData("image_data", fileName + ".png", requestBody);

        ApiConfig getResponse = AppConfig.getRetrofit().create(ApiConfig.class);

        Call<ResponseBody> call = getResponse.uploadImage(idBody, part);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {

                        //Student serverResponse = response.body();
                        Toast.makeText(getApplicationContext(), "Upload " + temp + " success", Toast.LENGTH_SHORT).show();

                    }
                } else {

                    Toast.makeText(getApplicationContext(), "problem uploading image " + temp, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Toast.makeText(getApplicationContext(), "failure uploading image " + temp + t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }
}
