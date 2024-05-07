package com.cookandroid.cooking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class Westernmain extends AppCompatActivity {

    private TextView addListIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_westernmain);

        // 액션바에 뒤로가기 버튼 추가
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 게시글 추가 아이콘 찾기
        addListIcon = findViewById(R.id.westernmian_addlist);

        // 게시글 추가 아이콘 클릭 리스너 설정
        addListIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 게시글 추가 아이콘을 클릭했을 때 실행되는 코드
                // 화면 전환을 위해 AddListActivity로 이동
                Intent intent = new Intent(Westernmain.this, Add_list_western.class);
                startActivity(intent);
            }
        });
    }

    // 뒤로가기 버튼 처리
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish(); // 현재 엑티비티 종료
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}