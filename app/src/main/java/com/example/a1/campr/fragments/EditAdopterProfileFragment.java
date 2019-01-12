package com.example.a1.campr.fragments;

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

import com.bumptech.glide.Glide;
import com.example.a1.campr.AdopterActivity;
import com.example.a1.campr.R;
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

public class EditAdopterProfileFragment extends Fragment {

    private static final int RESULT_REQUEST =1;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private Uri imageUri;
    private ByteArrayOutputStream baos;
    private Adopter adopter;

    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_adopter_profile, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getRootView().getWindowToken(), 0);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getReference();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();

        final FragmentActivity activity = getActivity();
        final ImageView imageView = activity.findViewById(R.id.profile_pic);
        final Button submitButton = activity.findViewById(R.id.submit);
        final Button editButton = activity.findViewById(R.id.edit);
        final EditText firstnameEditText = activity.findViewById(R.id.firstname);
        final EditText lastnameEditText = activity.findViewById(R.id.lastname);
        final EditText emailEditText = activity.findViewById(R.id.email);
        final EditText phoneNumberEditText = activity.findViewById(R.id.phone_number);
        final Spinner stateSpinner = activity.findViewById(R.id.state);
        final Spinner citySpinner = activity.findViewById(R.id.city);

        citySpinner.setEnabled(false);
        citySpinner.setClickable(false);
        stateSpinner.setEnabled(false);
        stateSpinner.setClickable(false);

        ArrayAdapter stateAdapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(),R.array.states_no_any,R.layout.support_simple_spinner_dropdown_item);
        stateAdapter.setDropDownViewResource(R.layout.spinner_item);
        stateSpinner.setAdapter(stateAdapter);

        ArrayAdapter cityAdapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(),R.array.cities_no_any,R.layout.support_simple_spinner_dropdown_item);
        cityAdapter.setDropDownViewResource(R.layout.spinner_item);
        citySpinner.setAdapter(cityAdapter);

        mDatabaseRef.child("adopters").child(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    adopter = dataSnapshot.getValue(Adopter.class);
                    Glide.with(imageView.getContext())
                            .load(adopter.getPicUrl())
                            .into(imageView);
                    firstnameEditText.setText(adopter.getFirstname());
                    lastnameEditText.setText(adopter.getLastname());
                    emailEditText.setText(adopter.getEmail());
                    phoneNumberEditText.setText(adopter.getPhoneNumber());
                    stateSpinner.setSelection(stateAdapter.getPosition(adopter.getState()));
                    citySpinner.setSelection(cityAdapter.getPosition(adopter.getCity()));
                }

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(intent, RESULT_REQUEST);
                    }
                });
                imageView.setClickable(false);

                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageView.setClickable(true);
                        submitButton.setVisibility(View.VISIBLE);
                        editButton.setVisibility(View.GONE);
                        firstnameEditText.setEnabled(true);
                        lastnameEditText.setEnabled(true);
                        emailEditText.setEnabled(true);
                        phoneNumberEditText.setEnabled(true);
                        citySpinner.setEnabled(true);
                        citySpinner.setClickable(true);
                        stateSpinner.setEnabled(true);
                        stateSpinner.setClickable(true);
                    }
                });

                submitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageView.setClickable(false);
                        submitButton.setVisibility(View.GONE);
                        editButton.setVisibility(View.VISIBLE);
                        firstnameEditText.setEnabled(false);
                        lastnameEditText.setEnabled(false);
                        emailEditText.setEnabled(false);
                        phoneNumberEditText.setEnabled(false);
                        citySpinner.setEnabled(false);
                        citySpinner.setClickable(false);
                        stateSpinner.setEnabled(false);
                        stateSpinner.setClickable(false);

                        String firstname = firstnameEditText.getText().toString();
                        String lastname = lastnameEditText.getText().toString();
                        String email = emailEditText.getText().toString();
                        String phoneNumber = phoneNumberEditText.getText().toString();
                        String city = citySpinner.getSelectedItem().toString();
                        String state = stateSpinner.getSelectedItem().toString();

                        // Upload the adopter profile image to Firebase Cloud Storage and retrieve the link

                        if (imageUri == null) {
                            updateAdopter(firstname, lastname, email, phoneNumber, city, state, adopter.getPicUrl(), mFirebaseUser.getUid());
                        } else {
                            StorageReference imageRef = mStorage
                                    .getReference(mFirebaseUser.getUid())
                                    .child("profile_images")
                                    .child(imageUri.getLastPathSegment());
                            updateAdopterWithNewImage(imageRef, firstname, lastname, email, phoneNumber, city, state, mFirebaseUser.getUid());
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // do nothing
            }
        });
    }

    private void updateAdopterWithNewImage(final StorageReference storageReference, final String firstname, final String lastname, final String email, final String phoneNumber, final String city, final String state, final String key) {
        storageReference.putBytes(baos.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadPhotoUrl) {
                        updateAdopter(firstname, lastname, email, phoneNumber, city, state, downloadPhotoUrl.toString(), key);
                    }
                });
            }
        });
    }

    private void updateAdopter(final String firstname, final String lastname, final String email, final String phoneNumber, final String city, final String state, final String picUrl, final String key) {
        Map<String, Object> updatedPart = new HashMap<>();

        updatedPart.put("firstname", firstname);
        updatedPart.put("lastname", lastname);
        updatedPart.put("email", email);
        updatedPart.put("phoneNumber", phoneNumber);
        updatedPart.put("city", city);
        updatedPart.put("state", city);
        updatedPart.put("picUrl", picUrl);
        updatedPart.put("id", key);

        mDatabase.getReference("adopters").child(key).updateChildren(updatedPart);
        mDatabase.getReference("listers").child(key).updateChildren(updatedPart);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case RESULT_REQUEST:
                    imageUri = data.getData();
                    try {
                        baos = new ByteArrayOutputStream();
                        Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);

                        // Crop the bitmap

                        Bitmap croppedBitmap;
                        if (imageBitmap.getWidth() >= imageBitmap.getHeight()){
                            croppedBitmap = Bitmap.createBitmap(
                                    imageBitmap,
                                    imageBitmap.getWidth()/2 - imageBitmap.getHeight()/2,
                                    0,
                                    imageBitmap.getHeight(),
                                    imageBitmap.getHeight()
                            );
                        }else{
                            croppedBitmap = Bitmap.createBitmap(
                                    imageBitmap,
                                    0,
                                    imageBitmap.getHeight()/2 - imageBitmap.getWidth()/2,
                                    imageBitmap.getWidth(),
                                    imageBitmap.getWidth()
                            );
                        }

                        croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        ImageView imageView = getActivity().findViewById(R.id.profile_pic);
                        imageView.setImageBitmap(croppedBitmap);
                    } catch (IOException e) {
                        Log.i("TAG", "Some exception " + e);
                    }
                    break;
            }
    }
}
