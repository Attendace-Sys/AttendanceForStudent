package com.project.attendanceforstudent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.project.attendanceforstudent.Networking.ApiConfig;
import com.project.attendanceforstudent.Networking.AppConfig;
import com.project.attendanceforstudent.Networking.User;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SigninActivity extends AppCompatActivity {

    Button btnLogin;
    EditText userName, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        btnLogin = (Button)findViewById(R.id.btn_login);
        userName = (EditText)findViewById(R.id.usename);
        password = (EditText)findViewById(R.id.pass);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = userName.getText().toString();
                String pass = password.getText().toString();

                if(validateLogin(username, pass)) {
                    //do login
                    doLogin(username, pass);
                }
            }
        });
    }

    private boolean validateLogin(String username, String password){
        if(username == null || username.trim().length() == 0){
            Toast.makeText(this, "Username is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password == null || password.trim().length() == 0){
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void doLogin(final String username,final String password){
        ApiConfig getResponse = AppConfig.getRetrofit().create(ApiConfig.class);

        RequestBody user = RequestBody.create(MediaType.parse("text/plain"), username);
        RequestBody pass = RequestBody.create(MediaType.parse("text/plain"), password);


        Call<User> call = getResponse.login(user, pass);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    User user = (User) response.body();
                    //String token = response.body();
                    if(user.getToken() != null){
                        //login start main activity
                        Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                        intent.putExtra("username", user.getUsername());
                        intent.putExtra("email", user.getEmail());
                        intent.putExtra("password", password);
                        intent.putExtra("name", user.getName());
                        intent.putExtra("token", user.getToken());

                        Global.token = user.getToken();
                        Global.studentid = user.getUsername();
                        Global.password = password;
                        Global.studentname = user.getName();
                        Global.email = user.getEmail();

                        startActivity(intent);

                    } else {
                        Toast.makeText(SigninActivity.this, "The username or password is incorrect", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SigninActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(SigninActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
