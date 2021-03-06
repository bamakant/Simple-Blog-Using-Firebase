package com.example.kiu.simpleblog;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mBlogList;

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUsers;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blogs");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.progressbar);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() == null){

                    Intent loginIntent = new Intent(MainActivity.this, LogInActivity.class);
                    loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }

            }
        };

        mBlogList = findViewById(R.id.blog_post_rectclerView);
        //mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    protected void onStart() {
        super.onStart();
/*
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Blogs")
                .limitToFirst(5);

        FirebaseRecyclerOptions<Blog> options = new FirebaseRecyclerOptions.Builder<Blog>()
                .setQuery(query, Blog.class)
                .build();
*/
       checkUserExist();

        mAuth.addAuthStateListener(mAuthListener);

      FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter =
              new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                      Blog.class,
                      R.layout.blog_row,
                      BlogViewHolder.class,
                      mDatabase
              ) {
                  @Override
                  protected void populateViewHolder(BlogViewHolder viewHolder, Blog model, int position) {

                      viewHolder.setTitle(model.getTitle());
                      viewHolder.setDesc(model.getDesc());
                      viewHolder.setImage(getApplicationContext(), model.getImage());
                      viewHolder.setUid(model.getPost_uid());
                      progressBar.setVisibility(View.GONE);
                  }
              };

        firebaseRecyclerAdapter.notifyDataSetChanged();
        mBlogList.setAdapter(firebaseRecyclerAdapter);

    }

    private void checkUserExist() {

        if(mAuth.getCurrentUser() != null) {
            final String user_id = mAuth.getCurrentUser().getUid();

            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.hasChild(user_id)) {

                        Intent setupIntent = new Intent(MainActivity.this, SetupAccountActivity.class);
                        setupIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupIntent);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTitle(String title){

            TextView postTitle = mView.findViewById(R.id.post_title_view);
            postTitle.setText(title);
        }

        public void setDesc(String desc){

            TextView postDesc = mView.findViewById(R.id.post_desc_view);
            postDesc.setText(desc);
        }

        public void setImage(Context ctx, String image){
            ImageView postImage = mView.findViewById(R.id.blog_image_view);
            Picasso.with(ctx).load(image).into(postImage);
        }

        public void setUid(String uid){
            TextView postUid = mView.findViewById(R.id.post_uid_view);
            postUid.setText(uid);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_add:
                startActivity(new Intent(MainActivity.this, PostActivity.class));
                break;
            case R.id.action_signout:
                logout();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        mAuth.signOut();
    }

}
