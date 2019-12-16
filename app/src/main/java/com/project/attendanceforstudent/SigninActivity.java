package com.project.attendanceforstudent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

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
//                if (userName.getText().toString().equals("teacher") &&
//                        password.getText().toString().equals("teacher"))
//                {
                startActivity(new Intent(SigninActivity.this, MainActivity.class));
//                }
            }
        });
    }
}
