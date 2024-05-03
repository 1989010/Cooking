package com.cookandroid.cooking;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 액션바 표시 설정
        ActionBar actionBar = getSupportActionBar();
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


