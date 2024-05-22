package com.cookandroid.cooking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class profile extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private LinearLayout profileMyProfile;
    private LinearLayout profileChangepassword;
    private LinearLayout profileMylist;
    private LinearLayout profileLogout; // 로그아웃 버튼 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#C3E0FF")));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#000000'>프로필</font>")); // 검은색으로 변경

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_home:
                    Intent homeIntent = new Intent(profile.this, MainActivity.class);
                    startActivity(homeIntent);
                    finish(); // 현재 액티비티 종료
                    return true;

                case R.id.menu_map:
                    Intent mapIntent = new Intent(profile.this, MapsActivity.class);
                    startActivity(mapIntent);
                    finish(); // 현재 액티비티 종료
                    return true;
                case R.id.menu_profile:
                    Intent profileIntent = new Intent(profile.this, profile.class);
                    startActivity(profileIntent);
                    finish(); // 현재 액티비티 종료
                    return true;
                default:
                    return false;
            }
        });

        // LinearLayout profile_myprofile을 가져와 변수에 할당
        profileMyProfile = findViewById(R.id.profile_myprofile);

        profileMyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myProfileIntent = new Intent(profile.this, myprofile.class);
                startActivity(myProfileIntent);
            }
        });

        profileChangepassword = findViewById(R.id.profile_changpassword); //비밀번호 변경

        profileChangepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent password = new Intent(profile.this, changpassword.class);
                startActivity(password);
            }
        });

        profileMylist = findViewById(R.id.profile_mylist);  // 내가 쓴 게시글

        profileMylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myProfilelist = new Intent(profile.this, mylist.class);
                startActivity(myProfilelist);
            }
        });

        // 로그아웃 버튼 변수에 할당
        profileLogout = findViewById(R.id.profile_log_out);

        // 로그아웃 버튼 클릭 리스너 설정
        profileLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut(); // 파이어베이스에서 로그아웃

                // 로그아웃 성공 메시지 표시
                Toast.makeText(profile.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent4 = new Intent(profile.this, LoginActivity.class);
                startActivity(intent4);
                finish(); // 현재 액티비티 종료
            }
        });
    }
}
