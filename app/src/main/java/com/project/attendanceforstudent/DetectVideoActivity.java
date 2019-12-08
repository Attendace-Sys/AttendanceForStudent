package com.project.attendanceforstudent;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
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

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wseemann.media.FFmpegMediaMetadataRetriever;

public class DetectVideoActivity extends AppCompatActivity {

    private VideoView videoPreview;
    private Button btnDetectFace;
    private Button btnSendData;
    private GridView mGridView;
    //private SpinKitView spinKitView;
    private LinearLayout loadingLinearLayout;

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

//    public ProgressDialog pDialog;

    String cookie;

    int max;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_video);

        initView();

        if (videoPath != null) {

            Uri uri = Uri.parse(videoPath);
            try {
                videoPreview.setVisibility(View.VISIBLE);
                //videoPreview.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.test);
                videoPreview.setVideoURI(uri);
                MediaController mediaController = new MediaController(this);
                videoPreview.setMediaController(mediaController);
////                mediaController.setAnchorView(videoPreview);
//                // Hide the controller
//                mediaController.setVisibility(View.GONE);
//                // Show the controller
//                mediaController.setVisibility(View.VISIBLE);

                videoPreview.requestFocus();

                videoPreview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    // Close the progress bar and play the video
                    public void onPrepared(MediaPlayer mp) {
                        videoPreview.start();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

            callExtractFaceFromVideo();

            btnSendData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadingLinearLayout.setVisibility(View.VISIBLE);
                    sendDataToServer();

                }
            });
        }
    }

    private void initView() {
        videoPreview = (VideoView) findViewById(R.id.videoPreview);
        //spinKitView = (SpinKitView) findViewById(R.id.spin_kit);
        btnSendData = (Button) findViewById(R.id.send_data);
        mGridView = (GridView) findViewById(R.id.gridView);
        loadingLinearLayout = (LinearLayout) findViewById(R.id.loadingLinearLayout);

        Bundle bundle = getIntent().getExtras();
        videoPath = bundle.getString("videoPath");
        studentName = bundle.getString("studentName");
        studentId = bundle.getString("studentId");
        studentEmail = bundle.getString("studentEmail");
    }

    private void callExtractFaceFromVideo() {
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingLinearLayout.setVisibility(View.VISIBLE);
            }
        }, 1000);

        listBitmap = new ArrayList<Bitmap>();
        ExtractImageTask myAsync = new ExtractImageTask();
        myAsync.execute(videoPath);
    }

    private void sendDataToServer() {
        File fileVideo = new File(videoPath);

        List<MultipartBody.Part> imgParts = new ArrayList<>();

        for (Bitmap bitmap : listBitmap) {
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

                        loadingLinearLayout.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Success upload!", Toast.LENGTH_SHORT).show();

                    }
                } else {

                    loadingLinearLayout.setVisibility(View.GONE);

                    Toast.makeText(getApplicationContext(), "Problem uploading video", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                loadingLinearLayout.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Failure uploading video " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

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

    public class ExtractImageTask extends AsyncTask<String, Integer, ArrayList<Bitmap>> {

        private ArrayList<Bitmap> mlistFrame;

        private int mSize;
        private int mTemp;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected ArrayList<Bitmap> doInBackground(String... strings) {
            mlistFrame = new ArrayList<Bitmap>();

            FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
            mmr.setDataSource(strings[0]);

            String METADATA_KEY_DURATION = mmr
                    .extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION);

            String rotation = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
            // 270 front camera
            // 90 back camera

            Bitmap bmpOriginal = null;

            mSize = (int) (Long.parseLong(METADATA_KEY_DURATION) * 4 / 1000);

            for (int index = 0; index < mSize; index++) {
                int time = index * 1000 / 4;
                bmpOriginal = mmr.getFrameAtTime(time * 1000, FFmpegMediaMetadataRetriever.OPTION_CLOSEST);

                mlistFrame.add(rotateBitmap(bmpOriginal, rotation));
            }

            mmr.release();

            return mlistFrame;
        }

        @Override
        protected void onPostExecute(ArrayList<Bitmap> bitmaps) {

            mTemp = 0;

            for (int index = 0; index < bitmaps.size(); index++) {

                final FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmaps.get(index));
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
            }
        }

        private void processFaceResult(List<FirebaseVisionFace> faces, FirebaseVisionImage image) {
            for (FirebaseVisionFace face : faces) {
                Rect bounds = face.getBoundingBox();

                Bitmap bitmap = cropBitmap(image.getBitmap(), bounds);

                bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);

                listBitmap.add(bitmap);

                mTemp++;
            }

            if (mTemp == mSize) {
                btnSendData.setEnabled(true);
                myAdapter = new MyAdapter(DetectVideoActivity.this, listBitmap);

                mGridView.setAdapter(myAdapter);

                loadingLinearLayout.setVisibility(View.GONE);
            }
        }

        public Bitmap rotateBitmap(Bitmap bmpOriginal, String rotation) {

            if (bmpOriginal == null) {
                return null;
            }
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

        public Bitmap cropBitmap(Bitmap bitmap, Rect rect) {
            int w = rect.right - rect.left;
            int h = rect.bottom - rect.top;
            Bitmap ret = Bitmap.createBitmap(w, h, bitmap.getConfig());
            Canvas canvas = new Canvas(ret);
            canvas.drawBitmap(bitmap, -rect.left, -rect.top, null);
            return ret;
        }
    }

}
