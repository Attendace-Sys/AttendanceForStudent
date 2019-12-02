package com.project.attendanceforstudent.Networking;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface ApiConfig {
//    @Headers("Content-Type: multipart/form-data")
    @Multipart
    @POST("students/students/")
    Call<ResponseBody> upload(
            @Part("student_code") RequestBody student_id,
            @Part("student_name") RequestBody student_name,
            @Part("student_email") RequestBody student_email,
//            @PartMap Map<String, RequestBody> student_video_data
            @Part MultipartBody.Part student_video_data
            );

    @Multipart
    @POST("students/images/")
    Call<ResponseBody> uploadImage(
            @Part("student") RequestBody student,
            @Part("image_name") RequestBody image_name,
            @Part MultipartBody.Part image_data
    );
}
