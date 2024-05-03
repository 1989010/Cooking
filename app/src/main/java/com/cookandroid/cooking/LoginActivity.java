package com.cookandroid.cooking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth; // Firebase Authentication 객체
    private DatabaseReference mDatabaseRef; // Firebase 실시간 데이터베이스 참조
    private EditText mEtEmail, mEtPwd; // 이메일과 비밀번호를 입력하는 EditText
    private CheckBox mCheckboxAutoLogin; // 자동 로그인 체크박스
    private SharedPreferences sharedPreferences; // 자동 로그인 설정을 저장하는 SharedPreferences

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // 레이아웃 설정


        // Firebase Authentication 및 데이터베이스 초기화
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Cooking");

        // 레이아웃에서 각 위젯을 찾아와 변수에 할당
        mEtEmail = findViewById(R.id.et_email);
        mEtPwd = findViewById(R.id.et_pwd);
        mCheckboxAutoLogin = findViewById(R.id.checkbox_auto_login);

        // 로그인 버튼에 대한 클릭 이벤트 처리
        Button btn_login = findViewById(R.id.login_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 입력된 이메일과 비밀번호 가져오기
                String strEmail = mEtEmail.getText().toString();
                String strPwd = mEtPwd.getText().toString();

                // Firebase를 사용하여 이메일과 비밀번호로 로그인 시도
                mFirebaseAuth.signInWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 로그인 성공
                            if (mCheckboxAutoLogin.isChecked()) {
                                // 자동 로그인 설정이 체크되어 있으면 설정 저장
                                saveAutoLoginSetting(true);
                            }
                            // 로그인 성공 시 MainActivity로 이동
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish(); // 현재 액티비티 종료
                        } else {
                            // 로그인 실패 시 사용자에게 알림
                            Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        // 회원가입 버튼에 대한 클릭 이벤트 처리
        Button btn_register = findViewById(R.id.login_signup);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 회원가입 화면으로 이동
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // 자동 로그인 시도
        tryAutoLogin();
    }

    // 자동 로그인 시도
    private void tryAutoLogin() {
        // 자동 로그인 설정을 불러와서 확인
        boolean autoLoginEnabled = getAutoLoginSetting();
        if (autoLoginEnabled) {
            // 자동 로그인 설정이 활성화되어 있으면 현재 사용자가 로그인되어 있는지 확인
            FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
            if (currentUser != null) {
                // 이미 로그인된 사용자가 있다면 MainActivity로 이동
                Log.d("AutoLogin", "User is already signed in");
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // 현재 액티비티 종료
            } else {
                // 현재 로그인된 사용자가 없으면 아무 작업도 수행하지 않음
                Log.d("AutoLogin", "No user is signed in");
            }
        }
    }

    // 자동 로그인 설정 저장
    private void saveAutoLoginSetting(boolean autoLogin) {
        // SharedPreferences를 사용하여 자동 로그인 설정을 저장
        sharedPreferences = getSharedPreferences("AutoLoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("autoLogin", autoLogin);
        editor.apply();
    }

    // 자동 로그인 설정 불러오기
    private boolean getAutoLoginSetting() {
        // SharedPreferences에서 자동 로그인 설정을 불러옴
        sharedPreferences = getSharedPreferences("AutoLoginPrefs", MODE_PRIVATE);
        return sharedPreferences.getBoolean("autoLogin", false);
    }

}