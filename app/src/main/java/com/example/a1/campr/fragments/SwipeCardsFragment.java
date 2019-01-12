package com.example.a1.campr.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.a1.campr.AdopterActivity;
import com.example.a1.campr.R;
import com.example.a1.campr.ViewAdopterPetActivity;
import com.example.a1.campr.ViewListerPetActivity;
import com.example.a1.campr.ViewSwipeCardActivity;
import com.example.a1.campr.models.Pet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.huxq17.swipecardsview.BaseCardAdapter;
import com.huxq17.swipecardsview.SwipeCardsView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SwipeCardsFragment extends Fragment {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private ArrayList<Pet> list;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_swipe_cards,container,false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getReference();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();

        // Set up swipe cards
        SwipeCardsView swipeCardsView = getActivity().findViewById(R.id.swipe_cards_view);
        swipeCardsView.retainLastCard(false);
        swipeCardsView.enableSwipe(true);

        // Add litener
        swipeCardsView.setCardsSlideListener(new SwipeCardsView.CardsSlideListener() {
            @Override
            public void onShow(int index) {
                // TODO
            }

            @Override
            public void onCardVanish(int index, SwipeCardsView.SlideType type) {
                Pet targetPet = list.get(index);
                switch (type){
                    case LEFT:
                        mDatabaseRef.child("pets").child(targetPet.getId()).child("impossibleAdopters").child(mFirebaseUser.getUid()).setValue(true);
                        break;
                    case RIGHT:
                        mDatabaseRef.child("pets").child(targetPet.getId()).child("possibleAdopters").child(mFirebaseUser.getUid()).setValue(true);
                        mDatabaseRef.child("adopters").child(mFirebaseUser.getUid()).child("chosenPets").child(targetPet.getId()).setValue(true);
                        break;
                }
            }

            @Override
            public void onItemClick(View cardImageView, int index) {
                Pet targetPet = list.get(index);
                Intent intent = new Intent(cardImageView.getContext(), ViewSwipeCardActivity.class);
                intent.putExtra("pet_id", targetPet.getId());
                cardImageView.getContext().startActivity(intent);
            }
        });

        Query initialQuery = mDatabaseRef.child("pets")
                .orderByChild("possibleAdopters/" + mFirebaseUser.getUid()).equalTo(null);

        initialQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            private boolean filtersMatched(HashMap<String, String> filters, Pet pet) {
                return ((filters.get("species").equals("Any") || filters.get("species").equals(pet.getSpecies())) &&
                        (filters.get("age").equals("Any") || filters.get("age").equals(pet.getAge())) &&
                        (filters.get("size").equals("Any") || filters.get("size").equals(pet.getSize())) &&
                        (filters.get("color").equals("Any") || filters.get("color").equals(pet.getColor())) &&
                        (filters.get("feeRange").equals("Any") || filters.get("feeRange").equals(pet.getFeeRange())));
            }

            @Override
            public void onDataChange(DataSnapshot petsSnapshot) {
                list = new ArrayList<>();

                // Filters
                HashMap<String, String> filters = ((AdopterActivity)getActivity()).getFilters();

                mDatabaseRef.child("adopters/" + mFirebaseUser.getUid() + "/city").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot petSnapshot: petsSnapshot.getChildren()) {
                            Pet pet = petSnapshot.getValue(Pet.class);
                            HashMap<String, Boolean> impossibleAdopters = pet.getImpossibleAdopters();
                            if ((impossibleAdopters == null || !impossibleAdopters.containsKey(mFirebaseUser.getUid()))
                                    && filtersMatched(filters, pet) && (dataSnapshot.getValue() == null || pet.getCity().equals(dataSnapshot.getValue(String.class))))
                                list.add(pet);
                        }

                        PetAdapter petAdapter = new PetAdapter(list, getContext());
                        swipeCardsView.setAdapter(petAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

//                final PetAdapter arrayAdapter = new PetAdapter(AdopterActivity.this, petList);
//                SwipeFlingAdapterView flingContainer = findViewById(R.id.frame);
//                flingContainer.setAdapter(arrayAdapter);
//                flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
//                    @Override
//                    public void removeFirstObjectInAdapter() {
//                        // this is the simplest way to delete an object from the Adapter (/AdapterView)
//                        Log.d("LIST", "removed object!");
//                        petList.remove(0);
//                        arrayAdapter.notifyDataSetChanged();
//                    }
//
//                    @Override
//                    public void onLeftCardExit(Object dataObject) {
//                        //Do something on the left!
//                        //You also have access to the original object.
//                        //If you want to use it just cast it (String) dataObject
//                        Toast.makeText(AdopterActivity.this, "Left", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onRightCardExit(Object dataObject) {
//                        Toast.makeText(AdopterActivity.this, "Right", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onAdapterAboutToEmpty(int itemsInAdapter) {
//                    }
//
//                    @Override
//                    public void onScroll(float scrollProgressPercent) {
//
//                    }
//                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // do nothing
            }
        });
    }

    public class PetAdapter extends BaseCardAdapter {
        private List<Pet> petList;
        private Context context;

        private PetAdapter(List<Pet> petList, Context context) {
            this.petList = petList;
            this.context = context;
        }

        @Override
        public int getCount() {
            return petList.size();
        }

        @Override
        public int getCardLayoutId() {
            return R.layout.item;
        }

        @Override
        public void onBindData(int position, View cardview) {
            if (petList == null || petList.size() == 0) {
                return;
            }

            Pet pet = petList.get(position);

            ImageView imageView = cardview.findViewById(R.id.image);
            Glide.with(imageView.getContext())
                    .load(pet.getPicUrl())
                    .into(imageView);

            TextView nameTextView = cardview.findViewById(R.id.name);
            nameTextView.setText(pet.getName());

            TextView genderTextView = cardview.findViewById(R.id.gender);
            genderTextView.setText(pet.getGender());

            TextView descriptionTextView = cardview.findViewById(R.id.description);
            descriptionTextView.setText(pet.getInfo());
        }

        @Override
        public int getVisibleCardCount() {
            return super.getVisibleCardCount();
        }
    }
}