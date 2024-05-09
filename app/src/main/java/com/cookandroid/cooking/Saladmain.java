package com.cookandroid.cooking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Saladmain extends AppCompatActivity {

    private static final String TAG = "Saladmain";

    private TextView addListIcon;
    private LinearLayout SaladMainList;

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saladmain);

        // Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("salad_list");
        mAuth = FirebaseAuth.getInstance();

        // 액션바에 뒤로가기 버튼 추가
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 액션바 배경색 및 제목 색상 변경
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(195, 224, 255)));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#000000'>샐 러 드</font>")); // 검은색으로 변경

        // 게시글 추가 아이콘 찾기
        addListIcon = findViewById(R.id.saladmain_addlist);

        // 게시글 목록을 표시할 레이아웃 찾기
        SaladMainList = findViewById(R.id.saladmain_list);

        // 게시글 추가 아이콘 클릭 리스너 설정
        addListIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 게시글 추가 아이콘을 클릭했을 때 실행되는 코드
                // 화면 전환을 위해 AddListActivity로 이동
                Intent intent = new Intent(Saladmain.this, Add_list_salad.class);
                startActivity(intent);
            }
        });

        // 한식 레시피 목록 불러오기
        loadSaladRecipes();

        // 현재 날짜 로그에 기록
        Log.d(TAG, "현재 날짜: " + getCurrentDate());
    }

    // 현재 날짜를 문자열로 반환하는 메서드
    private String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
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

    // 한식 레시피 목록 불러오기
    private void loadSaladRecipes() {
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Recipe recipe = dataSnapshot.getValue(Recipe.class);
                if (recipe != null) {
                    // 레시피를 화면에 추가하는 메서드 호출
                    addRecipeToLayout(recipe);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // Not used
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // Not used
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // Not used
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Not used
            }
        });
    }

    // 레시피를 화면에 추가
    private void addRecipeToLayout(Recipe recipe) {
        // 새로운 레시피를 표시할 레이아웃 생성
        View recipeItemView = getLayoutInflater().inflate(R.layout.activity_saladlist, null);

        // 각 뷰에 데이터 설정
        TextView titleTextView = recipeItemView.findViewById(R.id.saladlist_tittle);
        titleTextView.setText(recipe.getTitle());

        TextView hostIdTextView = recipeItemView.findViewById(R.id.saladlist_hostid);
        hostIdTextView.setText(getUserEmail(recipe.getUserId()));

        // 이미지를 표시하는 ImageView를 찾아옴
        ImageView imageView = recipeItemView.findViewById(R.id.saladlist_img);

        // 레시피 객체에서 이미지 URL 가져오기
        String imageUrl = recipe.getImageUrl();

        // 이미지 다운로드 및 설정
        downloadImage(imageUrl, imageView);

        // 게시글 목록에 새로운 레시피 레이아웃 추가
        SaladMainList.addView(recipeItemView);
    }

    private void downloadImage(String imageUrl, final ImageView imageView) {
        Log.d(TAG, "Image URL: " + imageUrl); // Add this line to log the image URL

        // imageUrl이 null인지 확인
        if (imageUrl == null) {
            Log.e(TAG, "Image URL is null");
            return;
        }

        // Firebase Storage에서 이미지 다운로드
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);

        storageRef.getBytes(Long.MAX_VALUE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        // 이미지 다운로드 성공 시 이미지뷰에 설정
                        imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // 이미지 다운로드 실패 시 처리
                        Log.e(TAG, "이미지 다운로드 실패: " + exception.getMessage());
                    }
                });
    }

    // 사용자의 이메일 주소 가져오기
    private String getUserEmail(String userId) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            if (email != null && email.contains("@")) {
                return email.substring(0, email.indexOf("@"));
            } else {
                return "Unknown";
            }
        } else {
            return "Unknown";
        }
    }

}
