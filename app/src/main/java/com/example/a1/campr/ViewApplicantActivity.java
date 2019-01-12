package com.example.a1.campr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.a1.campr.models.Adopter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ViewApplicantActivity extends AppCompatActivity {

    private static final int RESULT_REQUEST = 1;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private Uri imageUri;
    private ByteArrayOutputStream baos;
    private Adopter applicant;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_applicant);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getReference();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();

        Bundle extras = getIntent().getExtras();
        String applicantId = extras.getString("applicant_id");
        Log.d("TAG", "ABCDEF" + applicantId);
        String petId = extras.getString("pet_id");
        Log.d("TAG", "ABCDEF" + petId);

        final ImageView imageView = findViewById(R.id.profile_pic);
        final TextView firstnameTextView = findViewById(R.id.firstname);
        final TextView lastnameTextView = findViewById(R.id.lastname);
        final TextView emailTextView = findViewById(R.id.email);
        final TextView phoneNumberTextView = findViewById(R.id.phone_number);
        final TextView stateTextView = findViewById(R.id.state);
        final TextView cityTextView = findViewById(R.id.city);

        mDatabaseRef.child("adopters").child(applicantId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                applicant = dataSnapshot.getValue(Adopter.class);
                Glide.with(imageView.getContext())
                        .load(applicant.getPicUrl())
                        .into(imageView);
                firstnameTextView.setText(applicant.getFirstname());
                lastnameTextView.setText(applicant.getLastname());
                emailTextView.setText(applicant.getEmail());
                phoneNumberTextView.setText(applicant.getPhoneNumber());
                stateTextView.setText(applicant.getState());
                cityTextView.setText(applicant.getCity());

                findViewById(R.id.reject).setOnClickListener((v) -> {
                    mDatabaseRef.child("applications").child(petId).child(applicantId).child("rejection").setValue(true);
                    mDatabaseRef.child("pets").child(petId).child("numOfApplicants").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            mDatabaseRef.child("pets").child(petId).child("numOfApplicants").setValue(dataSnapshot.getValue(Integer.class) - 1);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // do nothing
                        }
                    });
                    finish();
                });

                findViewById(R.id.approve).setOnClickListener((v) -> {
                    mDatabaseRef.child("applications").child(petId).child(applicantId).child("approval").setValue(true);
                    finish();
                });
            }

            @Override
            public void onCancelled(DatabaseError erorr) {
                // do nothing
            }
        });
    }
}