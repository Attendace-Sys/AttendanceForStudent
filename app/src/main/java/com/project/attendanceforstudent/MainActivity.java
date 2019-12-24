package com.project.attendanceforstudent;


import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    BottomNavigationView bottomNavigationView;

//    String username, password, token, email, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        final Intent intent = getIntent();
//        username = intent.getStringExtra("username");
//        password = intent.getStringExtra("password");
//        email = intent.getStringExtra("email");
//        token = intent.getStringExtra("token");
//        name = intent.getStringExtra("name");

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

    }

    HomeFragment homeFragment;
    ProfileFragment profileFragment;
    NotificationFragment notificationFragment;


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.navigation_home:
                homeFragment = new HomeFragment();
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.container, homeFragment).commit();
                return true;

            case R.id.navigation_notification:
                notificationFragment = new NotificationFragment();
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.container, notificationFragment).commit();
                return true;

            case R.id.navigation_profile:
                profileFragment = new ProfileFragment();
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.container, profileFragment).commit();
                return true;



        }

        return false;
    }
}

