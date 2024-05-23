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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class mylist extends AppCompatActivity {

    private static final String TAG = "MyListActivity";

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private LinearLayout add_mylist_list;
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mylist);

        // Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Cooking"); // "Cooking"으로 변경
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            currentUserEmail = currentUser.getEmail(); // 현재 사용자의 이메일 가져오기
        } else {
            // 사용자가 로그인하지 않은 경우 처리
            finish();
            return;
        }

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#C3E0FF")));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#000000'>내가 쓴 게시글</font>")); // 검은색으로 변경

        // 액션바에 뒤로가기 버튼 추가
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 게시글 목록을 표시할 레이아웃 찾기
        add_mylist_list = findViewById(R.id.mylist_list);

        // 사용자가 작성한 모든 게시글 불러오기
        loadUserRecipes();
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

    // 사용자가 작성한 모든 게시글 불러오기
    private void loadUserRecipes() {

        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            if (snap.getKey().endsWith("_list")) {
                                for (DataSnapshot snap2 : snap.getChildren()) {
                                    Recipe recipe = snap2.getValue(Recipe.class);
                                    if (recipe != null && recipe.getUserEmail() != null && recipe.getUserEmail().equals(currentUserEmail)) {
                                        // 사용자가 작성한 게시글인 경우에만 화면에 추가
                                        addRecipeToLayout(recipe, getCurrentDate(), snap2.getKey());
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );

//       final String currentDate = getCurrentDate(); // 현재 날짜 가져오기
//        databaseReference.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                Recipe recipe = dataSnapshot.getValue(Recipe.class);
//                if (recipe != null && recipe.getUserEmail() != null && recipe.getUserEmail().equals(currentUserEmail)) {
//                    // 사용자가 작성한 게시글인 경우에만 화면에 추가
//                    addRecipeToLayout(recipe, currentDate, dataSnapshot.getKey());
//                }
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                // 레시피가 변경되었을 때 화면을 다시 로드
//                loadUserRecipes();
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//                // 레시피가 삭제되었을 때 처리
//                removeRecipeFromLayout(dataSnapshot.getKey());
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                // Not used
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Not used
//            }
//        });
    }

    // 레시피를 화면에 추가
    private void addRecipeToLayout(final Recipe recipe, final String currentDate, String recipeKey) {
        // 새로운 레시피를 표시할 레이아웃 생성
        View recipeItemView = getLayoutInflater().inflate(R.layout.activity_mylist_list, null);

        // 날짜를 표시할 TextView 찾아오기
        TextView dateTextView = recipeItemView.findViewById(R.id.mylist_list_date);
        dateTextView.setText(recipe.getDate());

        // 각 뷰에 데이터 설정
        TextView titleTextView = recipeItemView.findViewById(R.id.mylist_list_tittle);
        titleTextView.setText(recipe.getTitle());

        TextView hostIdTextView = recipeItemView.findViewById(R.id.mylist_list_hostid);
        hostIdTextView.setText(getUserEmail()); // 현재 사용자의 이메일 설정

        // 이미지를 표시하는 ImageView를 찾아옴
        ImageView imageView = recipeItemView.findViewById(R.id.mylsit_list_img);

        // 레시피 객체에서 이미지 URL 가져오기
        String imageUrl = recipe.getImageUrl();

        // 이미지 다운로드 및 설정
        downloadImage(imageUrl, imageView);

        // 게시글 목록에 새로운 레시피 레이아웃 추가
        add_mylist_list.addView(recipeItemView);

        // 제목을 클릭하는 이벤트 처리
        titleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 클릭한 게시글의 정보를 수정하는 액티비티로 이동
                Intent intent = new Intent(mylist.this, list_edit_korean.class);
                intent.putExtra("recipe", recipe); // 클릭한 게시글의 정보를 전달
                intent.putExtra("recipeKey", recipeKey);
                startActivity(intent);
            }
        });
    }

    // 레이아웃에서 레시피를 화면에서 제거
    private void removeRecipeFromLayout(String recipeKey) {
        // 레이아웃에서 해당 레시피를 찾아서 제거하는 로직을 구현해야 합니다.
        // 이는 데이터셋의 변경에 따라 UI를 업데이트하는 예제입니다.
        for (int i = 0; i < add_mylist_list.getChildCount(); i++) {
            View recipeItemView = add_mylist_list.getChildAt(i);


        }
    }

    // 사용자의 이메일 주소 가져오기
    private String getUserEmail() {
        return currentUserEmail; // 현재 사용자의 이메일 반환
    }

    // 이미지 다운로드 및 설정
    private void downloadImage(String imageUrl, final ImageView imageView) {
        Log.d(TAG, "Image URL: " + imageUrl); // 이미지 URL 로그에 출력

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
}
