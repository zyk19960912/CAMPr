package com.example.a1.campr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.a1.campr.models.Application;
import com.example.a1.campr.models.Pet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

public class ViewAdopterPetActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference petRef;
    private String petId;

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_adopter_pet);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getReference();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();

        petId = getIntent().getStringExtra("pet_id");

        petRef = mDatabaseRef.child("pets").child(petId);

        petRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Pet pet = snapshot.getValue(Pet.class);

                ImageView picImageView = findViewById(R.id.pic);
                TextView nameTextView = findViewById(R.id.name);
                TextView speciesTextView = findViewById(R.id.species);
                TextView genderTextView = findViewById(R.id.gender);
                TextView ageTextView = findViewById(R.id.age);
                TextView colorTextView = findViewById(R.id.color);
                TextView sizeTextView = findViewById(R.id.size);
                TextView feeTextView = findViewById(R.id.adoption_fee);
                TextView descriptionTextView = findViewById(R.id.description);

                nameTextView.setText(pet.getName());
                speciesTextView.setText(pet.getSpecies());
                genderTextView.setText(pet.getGender());
                ageTextView.setText(pet.getAge());
                colorTextView.setText(pet.getColor());
                sizeTextView.setText(pet.getSize());
                feeTextView.setText("$" + pet.getFee());
                descriptionTextView.setText(pet.getInfo());

                Glide.with(picImageView.getContext())
                        .load(pet.getPicUrl())
                        .into(picImageView);

                findViewById(R.id.delete).setOnClickListener((v) -> {
                    petRef.child("possibleAdopters").child(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            snapshot.getRef().removeValue();
                            finish();
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // do nothing
                        }
                    });

                    finish();
                });

                findViewById(R.id.apply).setOnClickListener((v) -> {
                    mDatabaseRef.child("applications/" + petId + "/" + mFirebaseUser.getUid()).setValue(new Application(pet.getListerId(), mFirebaseUser.getUid(), petId));
                    mDatabaseRef.child("pets/" + petId + "/numOfApplicants").setValue(pet.getNumOfApplicants() + 1);
                    finish();
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // do nothing
            }
        });
    }
}
