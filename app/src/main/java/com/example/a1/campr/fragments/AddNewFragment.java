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

import com.example.a1.campr.models.Pet;
import com.example.a1.campr.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddNewFragment extends Fragment {
    private static final int RESULT_REQUEST =1;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private Uri imageUri;
    private ByteArrayOutputStream baos;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_new_pet,container,false);
//        View view = inflater.inflate(this,)
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getReference();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();

        FragmentActivity activity = getActivity();

        Spinner spinner_gender;
        Spinner spinner_species;
        Spinner spinner_age;
        Spinner spinner_color;
        Spinner spinner_size;
        ArrayAdapter<CharSequence> adapter_species;
        ArrayAdapter<CharSequence> adapter_gender;
        ArrayAdapter<CharSequence> adapter_age;
        ArrayAdapter<CharSequence> adapter_color;
        ArrayAdapter<CharSequence> adapter_size;

        spinner_species = (Spinner) getActivity().findViewById(R.id.spinner_species);
        adapter_species = ArrayAdapter.createFromResource(getActivity().getBaseContext(),R.array.species_no_any,R.layout.support_simple_spinner_dropdown_item);
        adapter_species.setDropDownViewResource(R.layout.spinner_item);
        spinner_species.setAdapter(adapter_species);

        spinner_gender = (Spinner) getActivity().findViewById(R.id.spinner_gender);
        adapter_gender = ArrayAdapter.createFromResource(getActivity().getBaseContext(),R.array.genders_no_any,R.layout.support_simple_spinner_dropdown_item);
        adapter_gender.setDropDownViewResource(R.layout.spinner_item);
        spinner_gender.setAdapter(adapter_gender);

        spinner_age = (Spinner) getActivity().findViewById(R.id.spinner_age);
        adapter_age = ArrayAdapter.createFromResource(getActivity().getBaseContext(),R.array.age_no_any,R.layout.support_simple_spinner_dropdown_item);
        adapter_age.setDropDownViewResource(R.layout.spinner_item);
        spinner_age.setAdapter(adapter_age);

        spinner_color = (Spinner) getActivity().findViewById(R.id.spinner_color);
        adapter_color = ArrayAdapter.createFromResource(getActivity().getBaseContext(),R.array.color_no_any,R.layout.support_simple_spinner_dropdown_item);
        adapter_color.setDropDownViewResource(R.layout.spinner_item);
        spinner_color.setAdapter(adapter_color);

        spinner_size = (Spinner) getActivity().findViewById(R.id.spinner_size);
        adapter_size = ArrayAdapter.createFromResource(getActivity().getBaseContext(),R.array.size_no_any,R.layout.support_simple_spinner_dropdown_item);
        adapter_size.setDropDownViewResource(R.layout.spinner_item);
        spinner_size.setAdapter(adapter_size);

        ImageView image = activity.findViewById(R.id.image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, RESULT_REQUEST);
            }
        });

        Button addPetButton = activity.findViewById(R.id.add_pet_button);
        addPetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getRootView().getWindowToken(), 0);

                // Get all information

                FragmentActivity activity = getActivity();

                EditText nameEditText = activity.findViewById(R.id.name);
                String name = nameEditText.getText().toString();

                Spinner genderSpinner = activity.findViewById(R.id.spinner_gender);
                String gender = genderSpinner.getSelectedItem().toString();

                Spinner speciesSpinner = activity.findViewById(R.id.spinner_species);
                String species = speciesSpinner.getSelectedItem().toString();

                Spinner ageSpinner = activity.findViewById(R.id.spinner_age);
                String age = ageSpinner.getSelectedItem().toString();

                Spinner colorSpinner = activity.findViewById(R.id.spinner_color);
                String color = colorSpinner.getSelectedItem().toString();

                Spinner sizeSpinner = activity.findViewById(R.id.spinner_size);
                String size = sizeSpinner.getSelectedItem().toString();

                EditText feeEditText = activity.findViewById(R.id.adoption_fee);
                int fee = Integer.parseInt(feeEditText.getText().toString());

                EditText infoEditText = activity.findViewById(R.id.description);
                String info = infoEditText.getText().toString();

                // TODO

                // Get the new pet's key in the database for storing the image

                DatabaseReference petsRef = mDatabaseRef.child("pets");
                String key = petsRef.push().getKey();

                // Upload the pet image to Firebase Cloud Storage and retrieve the link

                StorageReference imageRef = mStorage
                        .getReference(mFirebaseUser.getUid())
                        .child(key)
                        .child(imageUri.getLastPathSegment());

                addNewPetToDatabase(imageRef, imageUri, petsRef, name, gender, info, key, species, age, color, size, fee);
            }
        });
    }

    private void addNewPetToDatabase(final StorageReference storageReference, final Uri uri, final DatabaseReference petsRef, final String name, final String gender, final String info, final String key, final String species, final String age, final String color, final String size, final int fee) {
        storageReference.putBytes(baos.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadPhotoUrl) {
                        mDatabaseRef.child("listers/" + mFirebaseUser.getUid() + "/city").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                petsRef.child(key).setValue(new Pet(name, gender, info, key, downloadPhotoUrl.toString(), mFirebaseUser.getUid(), species, age, color, size, fee, dataSnapshot.getValue(String.class)));
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddNewFragment()).commit();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // do nothing
                            }
                        });
                    }
                });
            }
        });
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
                        ImageView imageView = getActivity().findViewById(R.id.image);
                        imageView.setImageBitmap(croppedBitmap);
                    } catch (IOException e) {
                        Log.i("TAG", "Some exception " + e);
                    }
                    break;
            }
    }
}

