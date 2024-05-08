package com.cookandroid.cooking;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.storage.UploadTask;
import java.io.IOException;

public class Add_list_snack_bar extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imageView;
    private EditText titleEditText, recipeEditText;
    private Button commitButton, cancelButton;

    private DatabaseReference databaseReference;
    private Uri imageUri;

    private LinearLayout Snack_barMainList;

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
        Snack_barMainList = findViewById(R.id.snack_barmain_list);

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

        // 액션바에 뒤로가기 버튼 추가
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Load Korean recipes
        loadSnackRecipes();
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

    // Load Korean recipes from Firebase
    private void loadSnackRecipes() {
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Recipe recipe = dataSnapshot.getValue(Recipe.class);
                if (recipe != null) {
                    addRecipeToLayout(recipe, dataSnapshot.getKey());
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

    // Add recipe to layout
    private void addRecipeToLayout(Recipe recipe, String recipeId) {

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 10, 0, 10);

        LinearLayout recipeLayout = new LinearLayout(this);
        recipeLayout.setLayoutParams(layoutParams);
        recipeLayout.setOrientation(LinearLayout.VERTICAL);

        // Title
        TextView titleTextView = new TextView(this);
        titleTextView.setText(recipe.getTitle());
        recipeLayout.addView(titleTextView);

        // Recipe
        TextView recipeTextView = new TextView(this);
        recipeLayout.addView(recipeTextView);

        Snack_barMainList.addView(recipeLayout);
    }


    // Clear input fields after adding recipe
    private void clearInputFields() {
        imageView.setImageResource(R.drawable.camera);
        // 버튼의 가시성 변경하지 않음
        // imageUri를 null로 설정하지 않음
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

                                // Create new recipe object with image URL
                                Recipe koreanRecipe = new Recipe(title, recipe, userId, imageUrl);

                                // Push recipe to database
                                String recipeId = databaseReference.push().getKey();
                                if (recipeId != null) {
                                    databaseReference.child(recipeId).setValue(koreanRecipe);
                                    Toast.makeText(Add_list_snack_bar.this, "게시글이 등록되었습니다", Toast.LENGTH_SHORT).show();
                                    addRecipeToLayout(koreanRecipe, recipeId); // Add recipe to layout
                                    clearInputFields(); // Clear input fields
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


}
