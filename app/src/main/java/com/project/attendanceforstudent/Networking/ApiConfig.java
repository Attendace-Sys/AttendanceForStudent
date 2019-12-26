package com.project.attendanceforstudent.Networking;

import android.graphics.Bitmap;

import java.util.List;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

public interface ApiConfig {
//    @Multipart
//    @POST("students/serializer/students//")
//    Call<ResponseBody> upload(
//            @Part("student_code") RequestBody student_id,
//            @Part("full_name") RequestBody student_name,
//            @Part("email") RequestBody student_email,
////            @PartMap Map<String, RequestBody> student_video_data
//            @Part MultipartBody.Part student_video_data
//            );
//
//    @Multipart
//    @POST("students/serializer/images//")
//    Call<ResponseBody> uploadImage(
//            @Part("student") RequestBody student,
//            @Part MultipartBody.Part image_data
//    );

    @Multipart
    @POST("students/new/")
    Call<ResponseBody> uploadStudentProfile(
            @Part("student_code") RequestBody student_code,
            @Part("first_name") RequestBody student_name,
            @Part("email") RequestBody student_email,
            @Part MultipartBody.Part student_video_data,
            @Part List<MultipartBody.Part> files
    );

    @Multipart
    @POST("api/v1/auth/login/")
    Call<User> login(@Part("username") RequestBody username,
                     @Part("password") RequestBody password);

    @POST("api/v1/auth/logout/")
    Call<ResponseBody> logout();

    @GET("api/v1/students/list_course/{student}/")
    Call<Courses> getListCourse(@Header("Authorization") String authorization,
                                @Path("student") String id);

    @GET("api/v1/student/schedule_attendance_status/{course_code}/{student_code}/")
    Call<Attendances> getListAttendanceOfCourse(@Header("Authorization") String authorization,
                                     @Path("course_code") String course_code,
                                     @Path("student_code") String student_code);

}
