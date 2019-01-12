package com.example.a1.campr.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.a1.campr.models.Pet;
import com.example.a1.campr.R;
import com.example.a1.campr.ViewListerPetActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class    PetsFragment extends Fragment {
    public RecyclerView mRecyclerView;
    public LinearLayoutManager mLinearLayoutManager;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseRecyclerAdapter<Pet, PetViewHolder> mFirebaseAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pets, container,false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getReference();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();

        SnapshotParser<Pet> parser = new SnapshotParser<Pet>() {
            @NonNull
            @Override
            public Pet parseSnapshot(@NonNull DataSnapshot dataSnapshot) {
                Pet pet = dataSnapshot.getValue(Pet.class);

                if (pet != null) {
                    pet.setId(dataSnapshot.getKey());
                }
                return pet;
            }
        };

        Query petQuery = mDatabaseRef.child("pets").orderByChild("listerId").equalTo(mFirebaseUser.getUid());

        FirebaseRecyclerOptions<Pet> options =
                new FirebaseRecyclerOptions.Builder<Pet>()
                        .setQuery(petQuery, parser)
                        .build();

        mRecyclerView = getActivity().findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Pet, PetViewHolder>(options) {

            @Override
            public PetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                View v = inflater.inflate(R.layout.lister_row_layout, parent, false);
                return new PetViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(final PetViewHolder viewHolder, int position, Pet pet) {
                viewHolder.applicantsTextView.setVisibility(View.GONE);
                viewHolder.headerTextView.setText(pet.getName());
                viewHolder.footerTextView.setText(pet.getGender());
                viewHolder.idTextView.setText(pet.getId());
                if (pet.getNumOfApplicants() > 0) {
                    viewHolder.applicantsTextView.setVisibility(View.VISIBLE);
                    viewHolder.applicantsTextView.setText(String.format("Applicants\n%d", pet.getNumOfApplicants()));
                }

                Glide.with(viewHolder.picImageView.getContext())
                        .load(pet.getPicUrl())
                        .into(viewHolder.picImageView);
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the user is at the bottom of the list, scroll
                // to the bottom of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    mRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mFirebaseAdapter);
    }


    @Override
    public void onStart() {
        super.onStart();
        mFirebaseAdapter.startListening();
    }


    @Override
    public void onStop() {
        super.onStop();
        mFirebaseAdapter.stopListening();
    }


//    public class PetViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public class PetViewHolder extends RecyclerView.ViewHolder {
        private ImageView picImageView;
        private TextView headerTextView;
        private TextView footerTextView;
        private TextView idTextView;
        private TextView applicantsTextView;
        public View layout;

        private PetViewHolder(View v) {
            super(v);
            layout = v;
            picImageView = v.findViewById(R.id.icon);
            headerTextView = v.findViewById(R.id.first_line);
            footerTextView = v.findViewById(R.id.second_line);
            idTextView = v.findViewById(R.id.pet_id);
            applicantsTextView = v.findViewById(R.id.applicants);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                            new AddNewFragment()).commit();
                    String key = idTextView.getText().toString();
                    Intent intent = new Intent(v.getContext(), ViewListerPetActivity.class);
                    intent.putExtra("pet_id", key);
                    v.getContext().startActivity(intent);
                }
            });
        }
    }
}

