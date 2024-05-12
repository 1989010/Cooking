package com.cookandroid.cooking;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class list_edit_snack_bar extends AppCompatActivity {

    private EditText titleEditText, recipeEditText;
    private ImageView imageView;
    private Button editButton, deleteButton, commitButton, cancelButton;

    private Recipe recipe; // 전달받은 게시글의 정보를 저장할 변수
    private String recipeKey; // 게시글의 고유 키를 저장할 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_edit_snack_bar);

        // UI 요소 초기화
        titleEditText = findViewById(R.id.list_edit_snack_bar_title);
        recipeEditText = findViewById(R.id.list_edit_snack_bar_recipe);
        imageView = findViewById(R.id.list_edit_snack_bar_img);
        editButton = findViewById(R.id.list_edit_snack_bar_edit);
        deleteButton = findViewById(R.id.list_edit_snack_bar_delete);
        commitButton = findViewById(R.id.list_edit_snack_bar_commit);
        cancelButton = findViewById(R.id.list_edit_snack_bar_cancel);

        // 전달받은 게시글의 정보 가져오기
        recipe = (Recipe) getIntent().getSerializableExtra("recipe");
        recipeKey = getIntent().getStringExtra("recipeKey"); // 고유 키 가져오기

        // 가져온 게시글의 정보를 UI에 표시
        if (recipe != null) {
            titleEditText.setText(recipe.getTitle());
            recipeEditText.setText(recipe.getRecipe());
            downloadImage(recipe.getImageUrl(), imageView);
        }

        // 수정 버튼 클릭 리스너 설정
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 수정 버튼을 눌렀을 때 수정 가능한 상태로 UI 변경
                titleEditText.setEnabled(true);
                recipeEditText.setEnabled(true);
                commitButton.setVisibility(View.VISIBLE); // 완료 버튼 표시
                editButton.setVisibility(View.GONE); // 수정 버튼 숨김

                // 삭제 버튼을 보이게 설정
                deleteButton.setVisibility(View.VISIBLE);
            }
        });

        // 삭제 버튼 클릭 리스너 설정
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recipeKey != null) {
                    // Firebase 데이터베이스에서 해당 게시물의 레퍼런스 가져오기
                    DatabaseReference recipeRef = FirebaseDatabase.getInstance().getReference("snackbar_list").child(recipeKey);

                    // 해당 레퍼런스를 사용하여 게시물 삭제
                    recipeRef.removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // 삭제 성공
                                        Log.d(TAG, "게시물이 삭제되었습니다");
                                        Toast.makeText(getApplicationContext(), "게시물이 삭제되었습니다", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(list_edit_snack_bar.this,Snack_barmain.class);
                                        startActivity(intent);
                                        finish(); // 현재 엑티비티 종료
                                    } else {
                                        // 삭제 실패
                                        Log.e(TAG, "게시물 삭제에 실패했습니다", task.getException());
                                        Toast.makeText(getApplicationContext(), "게시물 삭제에 실패했습니다", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    // recipeKey가 null인 경우 처리할 코드 작성
                    Log.e(TAG, "recipeKey is null");
                }
            }
        });

// 완료 버튼 클릭 리스너 설정
        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 수정된 내용 가져오기
                String updatedTitle = titleEditText.getText().toString().trim();
                String updatedRecipe = recipeEditText.getText().toString().trim();

                // 수정된 내용이 비어있는지 확인
                if (updatedTitle.isEmpty() || updatedRecipe.isEmpty()) {
                    // 제목 또는 레시피 내용이 비어있으면 사용자에게 메시지 표시
                    Toast.makeText(getApplicationContext(), "제목 또는 레시피 내용을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (recipeKey != null) {
                    // Firebase 데이터베이스에서 해당 레시피의 레퍼런스 가져오기
                    DatabaseReference recipeRef = FirebaseDatabase.getInstance().getReference("snackbar_list");

                    // 새로운 레퍼런스를 생성하여 레시피 추가
                    String newRecipeKey = recipeRef.push().getKey();
                    Recipe updatedRecipeObject = new Recipe(updatedTitle, updatedRecipe, recipe.getUserId(), recipe.getImageUrl(), recipe.getDate());

                    // 기존 레시피 삭제
                    recipeRef.child(recipeKey).removeValue();

                    // 수정된 내용으로 새로운 레시피 추가
                    recipeRef.child(newRecipeKey).setValue(updatedRecipeObject)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // 사용자에게 메시지 표시
                                    Toast.makeText(getApplicationContext(), "게시글이 수정되었습니다", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(list_edit_snack_bar.this, Snack_barmain.class);
                                    startActivity(intent);
                                    finish(); // 현재 엑티비티 종료
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // 업데이트 실패 시 사용자에게 메시지 표시
                                    Toast.makeText(getApplicationContext(), "게시글 수정에 실패했습니다", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    // recipeKey가 null인 경우 처리할 코드 작성
                    Log.e(TAG, "recipeKey is null");
                }
            }
        });

        // 취소 버튼 클릭 리스너 설정
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 현재 액티비티 종료
                finish();
            }
        });

        // 액션바에 뒤로가기 버튼 추가
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 액션바 배경색 및 제목 색상 변경
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(195, 224, 255)));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#000000'>분 식</font>")); // 검은색으로 변경
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

    // 이미지 다운로드 메소드
    private void downloadImage(String imageUrl, final ImageView imageView) {
        // Firebase Storage에서 이미지 다운로드
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);

        storageRef.getBytes(Long.MAX_VALUE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        // 이미지 다운로드 성공 시 이미지뷰에 설정
                        imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                    }
                });
    }
}
