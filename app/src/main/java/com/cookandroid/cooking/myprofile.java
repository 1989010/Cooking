package com.cookandroid.cooking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class myprofile extends AppCompatActivity {

    private TextView myProfileName;
    private TextView myProfileNickname;
    private TextView myProfileEmail;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myprofile);

        getSupportActionBar().setTitle(Html.fromHtml("<font color='#000000'>내 정보</font>")); // 검은색으로 변경

        // 액션바에 뒤로가기 버튼 추가
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 초기화
        myProfileName = findViewById(R.id.myprofile_name);
        myProfileNickname = findViewById(R.id.myprofile_nickname);
        myProfileEmail = findViewById(R.id.myprofile_email);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Cooking");

        // 현재 로그인한 유저 정보 가져오기
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            mDatabaseRef.child("UserAccount").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserAccount userAccount = dataSnapshot.getValue(UserAccount.class);
                    if (userAccount != null) {
                        myProfileName.setText(userAccount.getName());
                        myProfileNickname.setText(userAccount.getNickname());
                        myProfileEmail.setText(userAccount.getEmailId());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // 디버그용 로그 추가
                    // Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                }
            });
        }
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
