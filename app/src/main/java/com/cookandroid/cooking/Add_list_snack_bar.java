package com.cookandroid.cooking;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Add_list_snack_bar extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imageView;
    private EditText titleEditText, recipeEditText;
    private Button commitButton, cancelButton;

    private DatabaseReference databaseReference;
    private Uri imageUri;

    private String userEmail; // 사용자 이메일 주소 저장 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list_snack_bar);

        // Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("snackbar_list");

        // Find views
        imageView = findViewById(R.id.add_list_snack_bar_img);
        titleEditText = findViewById(R.id.add_list_snack_bar_tittle);
        recipeEditText = findViewById(R.id.add_list_snack_bar_recipe);
        commitButton = findViewById(R.id.add_list_snack_bar_commit);
        cancelButton = findViewById(R.id.add_list_snack_bar_cancel);

        // Set click listener for image select button
        findViewById(R.id.add_list_snack_bar_imgbut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        // Set click listener for commit button
        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSnackRecipe();
            }
        });

        // Set click listener for cancel button
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

        // 사용자 이메일 주소 가져오기
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userEmail = currentUser.getEmail();
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

    // Open file chooser for image selection
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Handle result of image selection
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Clear input fields after adding recipe
    private void clearInputFields() {
        imageView.setImageResource(R.drawable.camera);
        titleEditText.setText("");
        recipeEditText.setText("");
        imageUri = null;
    }

    private void addSnackRecipe() {
        // Get current user ID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "로그인이 필요합니다", Toast.LENGTH_SHORT).show();
            return;
        }
        final String userId = currentUser.getUid();

        // Get input values
        final String title = titleEditText.getText().toString().trim();
        final String recipe = recipeEditText.getText().toString().trim();

        // Check if title or recipe is empty
        if (title.isEmpty() || recipe.isEmpty()) {
            Toast.makeText(this, "제목과 레시피를 모두 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if an image is selected
        if (imageUri == null) {
            Toast.makeText(this, "이미지를 선택해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        // Upload image to Firebase Storage
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/" + System.currentTimeMillis() + ".jpg");
        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Image uploaded successfully
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Image download URL retrieved
                                String imageUrl = uri.toString();

                                // Get current date
                                String currentDate = getCurrentDate();

                                // Create new recipe object with image URL and user email
                                Recipe koreanRecipe = new Recipe(title, recipe, userId, imageUrl, currentDate, userEmail);

                                // Push recipe to database
                                String recipeId = databaseReference.push().getKey();
                                if (recipeId != null) {
                                    databaseReference.child(recipeId).setValue(koreanRecipe);
                                    Toast.makeText(Add_list_snack_bar.this, "게시글이 등록되었습니다", Toast.LENGTH_SHORT).show();
                                    clearInputFields(); // Clear input fields

                                    Intent intent = new Intent(Add_list_snack_bar.this, Snack_barmain.class);
                                    startActivity(intent);
                                    finish(); // 현재 엑티비티 종료
                                } else {
                                    Toast.makeText(Add_list_snack_bar.this, "게시글 등록에 실패했습니다", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failed image upload
                        Toast.makeText(Add_list_snack_bar.this, "이미지 업로드에 실패했습니다", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // 현재 날짜를 문자열로 반환하는 메서드
    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
