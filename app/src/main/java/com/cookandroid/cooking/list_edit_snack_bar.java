package com.cookandroid.cooking;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class list_edit_snack_bar extends AppCompatActivity {

    private EditText titleEditText, recipeEditText;
    private ImageView imageView;
    private Button editButton, deleteButton, commitButton, cancelButton;

    private Recipe recipe; // 전달받은 게시글의 정보를 저장할 변수

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

        // 가져온 게시글의 정보를 UI에 표시
        if (recipe != null) {
            titleEditText.setText(recipe.getTitle());
            recipeEditText.setText(recipe.getRecipe());
            // 이미지 표시를 위해 downloadImage() 메서드를 사용하여 이미지를 가져와 설정
            downloadImage(recipe.getImageUrl(), imageView);
        }

        // 수정 버튼 클릭 리스너 설정
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Firebase 데이터베이스에서 해당 레시피의 레퍼런스 가져오기
                DatabaseReference recipeRef = FirebaseDatabase.getInstance().getReference("snackbar_list").child(recipe.getUserId());

                // 해당 레퍼런스를 사용하여 해당 레시피를 삭제
                recipeRef.removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // 수정 버튼을 눌렀을 때 수정 가능한 상태로 UI 변경
                                titleEditText.setEnabled(true);
                                recipeEditText.setEnabled(true);
                                commitButton.setVisibility(View.VISIBLE); // 완료 버튼 표시
                                editButton.setVisibility(View.GONE); // 수정 버튼 숨김
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // 삭제 실패 시 토스트 메시지 표시
                                Toast.makeText(getApplicationContext(), "게시글 삭제에 실패했습니다", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        //삭제버튼 클릭시
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "User ID: " + recipe.getUserId());
                DatabaseReference recipeRef = FirebaseDatabase.getInstance().getReference("snackbar_list").child(recipe.getUserId());
                Log.d(TAG, "Recipe Reference: " + recipeRef);


                // 해당 레퍼런스를 사용하여 해당 레시피를 삭제
                recipeRef.removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // 삭제 성공 시 사용자에게 메시지 표시
                                Toast.makeText(getApplicationContext(), "게시글이 삭제되었습니다", Toast.LENGTH_SHORT).show();
                                // 현재 액티비티 종료
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // 삭제 실패 시 사용자에게 메시지 표시
                                Toast.makeText(getApplicationContext(), "게시글 삭제에 실패했습니다", Toast.LENGTH_SHORT).show();
                            }
                        });
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

                // Firebase 데이터베이스에서 해당 레시피의 레퍼런스 가져오기
                DatabaseReference recipeRef = FirebaseDatabase.getInstance().getReference("snackbar_list").child(recipe.getUserId());

                // 수정된 내용으로 Recipe 객체 생성
                Recipe updatedRecipeObject = new Recipe(updatedTitle, updatedRecipe, recipe.getUserId(), recipe.getImageUrl(), recipe.getDate());

                // Firebase에 업데이트
                recipeRef.setValue(updatedRecipeObject)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // 업데이트 성공 시 수정 버튼과 삭제 버튼을 비활성화
                                editButton.setEnabled(false);
                                deleteButton.setEnabled(false);
                                // 완료 버튼과 취소 버튼을 숨김
                                commitButton.setVisibility(View.GONE);
                                cancelButton.setVisibility(View.GONE);
                                // 제목과 레시피 내용을 수정 불가능한 상태로 변경
                                titleEditText.setEnabled(false);
                                recipeEditText.setEnabled(false);
                                // 사용자에게 메시지 표시
                                Toast.makeText(getApplicationContext(), "게시글이 수정되었습니다", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // 업데이트 실패 시 사용자에게 메시지 표시
                                Toast.makeText(getApplicationContext(), "게시글 수정에 실패했습니다", Toast.LENGTH_SHORT).show();
                            }
                        });
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

