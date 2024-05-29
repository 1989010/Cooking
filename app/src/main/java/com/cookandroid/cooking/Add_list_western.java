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

public class Add_list_western extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imageView;
    private EditText titleEditText, recipeEditText;
    private Button commitButton, cancelButton;

    private DatabaseReference databaseReference;
    private Uri imageUri;

    // userEmail 변수 선언
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list_western);

        // Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("western_list");

        // Find views
        imageView = findViewById(R.id.add_list_western_img);
        titleEditText = findViewById(R.id.add_list_western_tittle);
        recipeEditText = findViewById(R.id.add_list_western_recipe);
        commitButton = findViewById(R.id.add_list_western_commit);
        cancelButton = findViewById(R.id.add_list_western_cancel);

        // 현재 사용자로부터 이메일 가져오기
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userEmail = currentUser.getEmail();
        } else {
            // 로그인이 필요한 경우
            Toast.makeText(this, "로그인이 필요합니다", Toast.LENGTH_SHORT).show();
            finish(); // 현재 액티비티 종료
            return;
        }

        // Set click listener for image select button
        findViewById(R.id.add_list_western_imgbut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        // Set click listener for commit button
        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWesternRecipe();
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
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#000000'>양 식</font>")); // 검은색으로 변경
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
                imageView.setBackground(null); // Remove the background image
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

    private void addWesternRecipe() {
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

                                // Create new recipe object with image URL
                                Recipe westernRecipe = new Recipe(title, recipe, userId, imageUrl, currentDate, userEmail);

                                // Push recipe to database
                                String recipeId = databaseReference.push().getKey();
                                if (recipeId != null) {
                                    databaseReference.child(recipeId).setValue(westernRecipe);
                                    Toast.makeText(Add_list_western.this, "게시글이 등록되었습니다", Toast.LENGTH_SHORT).show();

                                    clearInputFields(); // Clear input fields

                                    Intent intent = new Intent(Add_list_western.this, Westernmain.class);
                                    startActivity(intent);
                                    finish(); // 현재 엑티비티 종료
                                } else {
                                    Toast.makeText(Add_list_western.this, "게시글 등록에 실패했습니다", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failed image upload
                        Toast.makeText(Add_list_western.this, "이미지 업로드에 실패했습니다", Toast.LENGTH_SHORT).show();
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
