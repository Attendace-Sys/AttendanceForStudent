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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
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
    ListView mListView;

    private String videoPath;
    private String studentName;
    private String studentId;

    private MediaPlayer mediaPlayer=null;
    private MediaController mediaController=null;
    private int temp = 0;

    private ArrayList<Bitmap> listBitmap;
    private ArrayList<Bitmap> listFrame;

    MyAdapter myAdapter;

    public ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_video);
        //db = new DBHelper(DetectVideoActivity.this);
        videoPreview = (VideoView)findViewById(R.id.videoPreview);
        btnDetectFace = (Button)findViewById(R.id.btn_detect);
        btnSendData = (Button)findViewById(R.id.send_data);
        mListView = (ListView) findViewById(R.id.list_view);

        Bundle bundle = getIntent().getExtras();
        videoPath = bundle.getString("videoPath");
        studentName = bundle.getString("studentName");
        studentId = bundle.getString("studentId");

        initDialog();
        
        if (videoPath != null)
        {

            Uri uri = Uri.parse(videoPath);
            try {
                videoPreview.setVisibility(View.VISIBLE);
                //videoPreview.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.test);
                videoPreview.setVideoURI(uri);
                MediaController mediaController = new MediaController(this);
                videoPreview.setMediaController(mediaController);
                mediaController.setAnchorView(videoPreview);
//                videoPreview.start();
//                if (editTextStudentId.getText() != null)
//                {
                    btnDetectFace.setEnabled(true);
//                }

            }catch (Exception e)
            {
                e.printStackTrace();
            }

            showpDialog();

            // Map is used to multipart the file using okhttp3.RequestBody
           //Map<String, RequestBody> map = new HashMap<>();
            File file = new File(videoPath);

            // Parsing any Media type file
            RequestBody requestBody = RequestBody.create(MediaType.parse("video/*"), file);
            //map.put("student_video_data", requestBody);
            // Create MultipartBody.Part using file request-body,file name and part name
            MultipartBody.Part part = MultipartBody.Part.createFormData("student_video_data", studentId + ".mp4", requestBody);

            ApiConfig getResponse = AppConfig.getRetrofit().create(ApiConfig.class);
            String studentEmail = studentId + "@gm.uit.edu.vn";
            RequestBody idBody = RequestBody.create(MediaType.parse("text/plain"), studentId);
            RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), studentName);
            RequestBody mailBody = RequestBody.create(MediaType.parse("text/plain"), studentEmail);

            Call<ResponseBody> call = getResponse.upload(idBody, nameBody, mailBody, part);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()){
                        if (response.body() != null){
                            hidepDialog();
                            //Student serverResponse = response.body();
                            Toast.makeText(getApplicationContext(), response.body().toString(), Toast.LENGTH_SHORT).show();

                        }
                    }else {
                        hidepDialog();
                        Toast.makeText(getApplicationContext(), "problem uploading video", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    hidepDialog();
                    Toast.makeText(getApplicationContext(), "failure uploading video " + t.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });

//            myAdapter = new MyAdapter(DetectVideoActivity.this, listBitmap);
//            mListView.setAdapter(myAdapter);
//            studentId = editTextStudentId.getText().toString();

            btnDetectFace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    extractFrameInVideo(videoPath);

                }

            }
            );

        }
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

        int max = (int) (Long.parseLong(METADATA_KEY_DURATION)*3/1000);

        listBitmap = new ArrayList<Bitmap>();

        listFrame = new ArrayList<Bitmap>();

        for (int index = 0; index < max; index++) {
            int time = index * 1000 /3;
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

            Toast.makeText(this,String.format("In %d detect loop HAVE %d faces in image",index,temp), Toast.LENGTH_SHORT).show();

        }
        mmr.release();
//        Toast.makeText(this,String.format("Final HAVE %d faces in image",temp), Toast.LENGTH_SHORT).show();

    }

    private Bitmap rotateBitmap(Bitmap bmpOriginal, String rotation) {
//        int mBitW = Integer.parseInt(String.valueOf(bmpOriginal.getWidth()));
//        int mBitH = Integer.parseInt(String.valueOf(bmpOriginal.getHeight()));
//
//        int orientation = getResources().getConfiguration().orientation;
//        Bitmap fixedBitmapRotation = null;
//
//        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            // Do nothing because Landscape doesn't give wrong Bitmap rotation
//        } else {
//            if (mBitW>mBitH){
//                Matrix matrix = new Matrix();
//                matrix.postRotate(-90);
//                fixedBitmapRotation = Bitmap.createBitmap(bmpOriginal, 0, 0, bmpOriginal.getWidth(), bmpOriginal.getHeight(), matrix, true);
//                fixedBitmapRotation.getHeight();
//            }
//        }
//        return fixedBitmapRotation;

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
            Bitmap bmRotated = Bitmap.createBitmap(bmpOriginal, 0, 0, bmpOriginal.getWidth(), bmpOriginal.getHeight(), matrix, true);
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }

    }

    private void processFaceResult(List<FirebaseVisionFace> faces, FirebaseVisionImage image) {
 //       int count = 0;
        for(FirebaseVisionFace face: faces)
        {
            Rect bounds = face.getBoundingBox();

            Bitmap bitmap = cropBitmap(image.getBitmap(), bounds);

            //File mFile = new File("/sdcard/pictureTest");
            bitmap = Bitmap.createScaledBitmap(bitmap,160, 160, true);
            //long result = db.addStudentImage(studentId, Utils.getBytes(bitmap));

            listBitmap.add(bitmap);

            myAdapter = new MyAdapter(DetectVideoActivity.this, listBitmap);
            mListView.setAdapter(myAdapter);

            //File file = new File(String.valueOf(bitmap));

            showpDialog();

            uploadImageToServer(bitmap, studentId, temp);

//            Toast.makeText(this,String.format("Save bitmap"+count +" ("+ bitmap.getHeight()+ ":"+bitmap.getWidth()+")  result = "+ "student id = " + studentId), Toast.LENGTH_SHORT).show();
//            count ++;
            temp++;
        }
        Toast.makeText(this,String.format(""+temp), Toast.LENGTH_SHORT).show();
    }

    public static Bitmap cropBitmap(Bitmap bitmap, Rect rect) {
        int w = rect.right - rect.left;
        int h = rect.bottom - rect.top;
        Bitmap ret = Bitmap.createBitmap(w, h, bitmap.getConfig());
        Canvas canvas = new Canvas(ret);
        canvas.drawBitmap(bitmap, -rect.left, -rect.top, null);
        return ret;
    }

    public static byte[] convertBitmapToByteArray(Bitmap bitmap)
    {
        int size     = bitmap.getRowBytes() * bitmap.getHeight();
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
//        File filesDir = Environment.getExternalStorageDirectory();
//        File imageFile = new File(filesDir + "/faceimage/", name + ".png");
//
//        OutputStream os;
//        try {
//            os = new FileOutputStream(imageFile);
//            bitmap.compress(Bitmap.CompressFormat.PNG, 85, os);
//            os.flush();
//            os.close();
//
//        } catch (Exception e) {
//            //Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
//        }
//        return imageFile;
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fname = name + "_"+ timeStamp +".jpg";

        File file = new File(myDir, fname);
        if (file.exists()) file.delete ();
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

    public void uploadImageToServer(Bitmap bitmap, String id, int count)
    {
        String fileName = id + "_" + count;

        RequestBody idBody = RequestBody.create(MediaType.parse("text/plain"), id);
        RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), fileName);

        //byte[] bytes = convertBitmapToByteArray(bitmap);
        File imageFile = convertToFile(bitmap, fileName);

        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), imageFile);

        MultipartBody.Part part = MultipartBody.Part.createFormData("image_data", fileName + ".png", requestBody);

        ApiConfig getResponse = AppConfig.getRetrofit().create(ApiConfig.class);

        Call<ResponseBody> call = getResponse.uploadImage(idBody, nameBody, part);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        hidepDialog();
                        //Student serverResponse = response.body();
                        Toast.makeText(getApplicationContext(), "Upload " + temp + " success", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    hidepDialog();
                    Toast.makeText(getApplicationContext(), "problem uploading image " + temp, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                hidepDialog();
                Toast.makeText(getApplicationContext(), "failure uploading image " + temp + t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }
}
