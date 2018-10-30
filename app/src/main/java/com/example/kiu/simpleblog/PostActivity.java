package com.example.kiu.simpleblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PostActivity extends AppCompatActivity {

    private ImageButton imageSelectBtn;

    public static final int GALLARY_INTENT = 1;

    private EditText nPostTItle, nPostDesc;

    private Button nPostSumbitBtn;

    private Uri imageUri;

    private StorageReference mStorage;

    private ProgressDialog mDialog;

    private DatabaseReference mDatabase;

    private DatabaseReference mDatabaseUsers;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        imageSelectBtn = findViewById(R.id.image_select);
        nPostTItle = findViewById(R.id.edit_post_title);
        nPostDesc = findViewById(R.id.edit_post_description);
        nPostSumbitBtn = findViewById(R.id.btn_submit_post);

        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blogs");
        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        mDialog = new ProgressDialog(this);

        imageSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleyIntent = new Intent(Intent.ACTION_PICK);
                galleyIntent.setType("image/*");
                startActivityForResult(galleyIntent, GALLARY_INTENT);
            }
        });

        nPostSumbitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitPost();
            }
        });

    }

    private void submitPost() {

        final String postTitle = nPostTItle.getText().toString().trim();
        final String postDesc = nPostDesc.getText().toString().trim();

        if (!TextUtils.isEmpty(postTitle) && !TextUtils.isEmpty(postDesc) && imageUri != null){

            mDialog.setMessage("Posting...");
            mDialog.show();

            StorageReference filepath = mStorage.child("BlogImages").child(imageUri.getLastPathSegment());

            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUri = taskSnapshot.getDownloadUrl();

                    final String downloadString = downloadUri.toString();

                    mDatabaseUsers.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            DatabaseReference newPost = mDatabase.push();
                            newPost.child("title").setValue(postTitle);
                            newPost.child("desc").setValue(postDesc);
                            newPost.child("image").setValue(downloadString);
                            newPost.child("post_uid").setValue(dataSnapshot.child("uid").getValue());
                            mDialog.dismiss();
                            Toast.makeText(PostActivity.this, "Blog Posted", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(PostActivity.this,MainActivity.class));
                            finish();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }
            });

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GALLARY_INTENT && resultCode == RESULT_OK){

           imageUri = data.getData();

            imageSelectBtn.setImageURI(imageUri);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
