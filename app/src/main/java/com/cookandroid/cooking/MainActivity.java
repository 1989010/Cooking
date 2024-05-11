package com.cookandroid.cooking;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cookandroid.cooking.Koreanmain;
import com.cookandroid.cooking.Westernmain;
import com.cookandroid.cooking.Snack_barmain;
import com.cookandroid.cooking.Saladmain;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private Button koreanButton;
    private Button westernButton;
    private Button snackButton;
    private Button saladButton;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle(Html.fromHtml("<font color='#000000'>자취 요리 마스터</font>")); // 검은색으로 변경

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_home:
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // 현재 액티비티 종료
                    return true;

                case R.id.menu_map:
                    Intent intent2 = new Intent(MainActivity.this, MapsActivity.class);
                    startActivity(intent2);
                    finish(); // 현재 액티비티 종료
                    return true;
                case R.id.menu_profile:
                    // 프로필 기능 추가 (원하는 기능을 여기에 추가)
                    return true;
                default:
                    return false;
            }
        });

        // 각 버튼 찾기
        koreanButton = findViewById(R.id.main_korean);
        westernButton = findViewById(R.id.main_western);
        snackButton = findViewById(R.id.main_snack_bar);
        saladButton = findViewById(R.id.main_salad);

        // 각 버튼 클릭 리스너 설정
        koreanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Koreanmain.class);
                startActivity(intent);
            }
        });

        westernButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Westernmain.class);
                startActivity(intent);
            }
        });

        snackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Snack_barmain.class);
                startActivity(intent);
            }
        });

        saladButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Saladmain.class);
                startActivity(intent);
            }
        });

        // 액션바 표시 설정
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true); // 홈 아이콘 표시
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_logout_24); // 홈 아이콘 설정
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 홈 아이콘 클릭 시
        if (item.getItemId() == android.R.id.home) {
            // Firebase에서 사용자 로그아웃
            FirebaseAuth.getInstance().signOut();

            // 로그아웃 성공 메시지 표시
            Toast.makeText(MainActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();

            // 로그인 화면으로 이동
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // 현재 액티비티 종료
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
