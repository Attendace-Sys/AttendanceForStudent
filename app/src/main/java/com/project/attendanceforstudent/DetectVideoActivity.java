package com.project.attendanceforstudent;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.project.attendanceforstudent.Networking.ApiConfig;
import com.project.attendanceforstudent.Networking.AppConfig;
import com.project.attendanceforstudent.Utils.Utils;

import org.bytedeco.javacv.AndroidFrameConverter;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetectVideoActivity extends AppCompatActivity {

    private VideoView videoPreview;
    private Button btnSendData;
    private RecyclerView recyclerView;
    GridLayoutManager gridLayoutManager;

    private String videoPath;
    private String studentName;
    private String studentId;
    private String studentEmail;

    private ArrayList<Bitmap> listFrames = new ArrayList<>();
    private ArrayList<Bitmap> listBitmapFaces = new ArrayList<>();

    FaceImageDataAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_video);
        initView();

        if (videoPath != null) {

            Uri uri = Uri.parse(videoPath);
            try {
                videoPreview.setVisibility(View.VISIBLE);
                videoPreview.setVideoURI(uri);
                videoPreview.seekTo(100);

            } catch (Exception e) {
                e.printStackTrace();
            }

            callExtractFaceFromVideo();
            btnSendData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String message = "Đang tải dữ liệu lên server.\n Vui lòng chờ...";
                    Utils.showLoadingIndicator(DetectVideoActivity.this, message);
                    sendDataToServer();
                }
            });
        }
    }

    private void initView() {
        videoPreview = (VideoView) findViewById(R.id.videoPreview);
        btnSendData = (Button) findViewById(R.id.send_data);
        recyclerView = findViewById(R.id.recyclerView);

        gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);

        int spanCount = 3; // 3 columns
        int spacing = 20; // 50px
        boolean includeEdge = true;
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));

        adapter = new FaceImageDataAdapter(getApplicationContext(), listBitmapFaces);
        recyclerView.setAdapter(adapter);

        Bundle bundle = getIntent().getExtras();
        videoPath = bundle.getString("videoPath");
        studentName = bundle.getString("studentName");
        studentId = bundle.getString("studentId");
        studentEmail = bundle.getString("studentEmail");
    }

    private void callExtractFaceFromVideo() {

        String message = "  Đang trích xuất \n  dữ liệu khuôn mặt từ video. \n  Vui lòng chờ...";
        Utils.showLoadingIndicator(DetectVideoActivity.this, message);
        startVideoParsing(videoPath);

    }

    private void sendDataToServer() {
        File fileVideo = new File(videoPath);

        List<MultipartBody.Part> imgParts = new ArrayList<>();

        for (Bitmap bitmap : listBitmapFaces) {
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

                        Utils.hideLoadingIndicator();
                        Toast.makeText(getApplicationContext(), "Gửi thành công!", Toast.LENGTH_SHORT).show();

                    }
                } else {

                    Utils.hideLoadingIndicator();
                    Toast.makeText(getApplicationContext(), "Xảy ra vấn đề khi gửi dữ liệu. \nVui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Utils.hideLoadingIndicator();
                Toast.makeText(getApplicationContext(), "Gửi thất bại. \nVui lòng thử lại. " + t.getMessage(), Toast.LENGTH_SHORT).show();
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


    private void startVideoParsing(final String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    doConvert(path);
                } catch (FrameGrabber.Exception e) {
                    e.printStackTrace();
                } catch (FrameRecorder.Exception e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    //extract images using JavaCV
    private void doConvert(String path) throws IOException {

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        String rotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
//        Log.d("Metadata", "doConvert: " + rotation);

        //extract frames
        extractFrames(path, rotation);

        //extract faces in each frames
        extractFaces();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.hideLoadingIndicator();
            }
        });
    }

    private void extractFrames(String path, String rotation) throws FrameGrabber.Exception {
        final int NUM_FRAME_EXTRACT_PER_SECOND = 3;
        final float ONE_SECOND_IN_MICRO_SECONDS = 1000000.0f;
        float frameInterval = ONE_SECOND_IN_MICRO_SECONDS / NUM_FRAME_EXTRACT_PER_SECOND - ONE_SECOND_IN_MICRO_SECONDS / 10.0f;
        final int NUM_FRAMES = NUM_FRAME_EXTRACT_PER_SECOND * 3;

        int frameCount = 0;

        FFmpegFrameGrabber videoGrabber = new FFmpegFrameGrabber(path);
        Map<String, String> metaData = videoGrabber.getVideoMetadata();

        Frame frame;

        videoGrabber.start();
        AndroidFrameConverter bitmapConverter = new AndroidFrameConverter();

        //calculate in microsecond (10^-6 second)
        long startTime = 0;

        //extract frames in video
        while (true) {
            frame = videoGrabber.grabFrame();

            if (frame == null) {
//                Log.e("extractFrame", "No more frame");

                break;
            }
            if (frame.image == null) {
                continue;
            }


            //sometime timestamp return 0
            if (frame.timestamp <= 1) continue;
//            Log.e("timestamp", "" + frame.timestamp);

            long endTime = frame.timestamp;
            long delta = frame.timestamp - startTime;

            //Only process frame after time interval
            if (delta > frameInterval) {

                startTime = endTime;

                Bitmap currentImage = bitmapConverter.convert(frame);

                //need to clone, although get the same image
                Bitmap extractedFrameBitmap = currentImage.copy(currentImage.getConfig(), true);

                listFrames.add(rotateBitmap(extractedFrameBitmap, rotation));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        adapter.notifyDataSetChanged();
                    }
                });

                frameCount++;
                if (frameCount >= NUM_FRAMES) {
//                    Log.d("Extract frames", "Finish");
                    break;
                }
            }
        }
    }

    private void extractFaces() {
        for (int index = 0; index < listFrames.size(); index++) {

            Bitmap frame = listFrames.get(index);
            final FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(frame);

            FirebaseVisionFaceDetectorOptions options = new FirebaseVisionFaceDetectorOptions.Builder()
                    .build();
            FirebaseVisionFaceDetector detector = FirebaseVision.getInstance()
                    .getVisionFaceDetector(options);


            Task<List<FirebaseVisionFace>> detectionTask = detector.detectInImage(firebaseVisionImage);

            try {
                // Block on a task and get the result synchronously. This is generally done
                // when executing a task inside a separately managed background thread. Doing this
                // on the main (UI) thread can cause your application to become unresponsive.
                List<FirebaseVisionFace> facesInfo= Tasks.await(detectionTask);
                List<Bitmap> facesInFrame = processFaceResult(facesInfo, firebaseVisionImage, true);
                listBitmapFaces.addAll(facesInFrame);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });

            } catch (ExecutionException e) {
                // The Task failed, this is the same exception you'd get in a non-blocking
                // failure handler.
                // ...
            } catch (InterruptedException e) {
                // An interrupt occurred while waiting for the task to complete.
                // ...
            }
        }
    }


    private List<Bitmap> processFaceResult(List<FirebaseVisionFace> faces, FirebaseVisionImage image, boolean keepOriginal) {

        final int FACE_IMG_WIDTH = 200;
        final int FACE_IMG_HEIGHT = 200;

        List<Bitmap> facesInFrame = new ArrayList<>();
        for (FirebaseVisionFace face : faces) {
            Rect bounds = face.getBoundingBox();
            Bitmap bitmap = cropBitmap(image.getBitmap(), bounds);
            if(keepOriginal == false) {
                bitmap = Bitmap.createScaledBitmap(bitmap, FACE_IMG_WIDTH, FACE_IMG_HEIGHT, true);
            }
            facesInFrame.add(bitmap);

        }
        return facesInFrame;
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
