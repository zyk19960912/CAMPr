package com.example.a1.campr.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.a1.campr.R;
import com.example.a1.campr.ViewAdopterPetActivity;
import com.example.a1.campr.models.Lister;
import com.example.a1.campr.models.Pet;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
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

import org.w3c.dom.Text;

public class FavoriteFragment extends Fragment {
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
        return inflater.inflate(R.layout.fragment_favorite,container,false);
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

        Query petQuery = mDatabaseRef.child("pets").orderByChild("possibleAdopters/" + mFirebaseUser.getUid()).equalTo(true);

        FirebaseRecyclerOptions<Pet> options =
                new FirebaseRecyclerOptions.Builder<Pet>()
                        .setQuery(petQuery, parser)
                        .build();

        mRecyclerView = getActivity().findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Pet, PetViewHolder>(options) {

            @Override
            public PetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                View v = inflater.inflate(R.layout.applicant_row_layout, parent, false);
                return new PetViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(final PetViewHolder viewHolder, int position, Pet pet) {
                viewHolder.approvalTextView.setVisibility(View.GONE);
                viewHolder.layout.setClickable(true);
                viewHolder.headerTextView.setText(pet.getName());
                viewHolder.footerTextView.setText(pet.getGender());
                viewHolder.idTextView.setText(pet.getId());

                viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String key = viewHolder.idTextView.getText().toString();
                        Intent intent = new Intent(v.getContext(), ViewAdopterPetActivity.class);
                        intent.putExtra("pet_id", key);
                        v.getContext().startActivity(intent);
                    }
                });

                mDatabaseRef.child("applications").child(pet.getId()).child(mFirebaseUser.getUid()).child("approval").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null && dataSnapshot.getValue(Boolean.class)) {
                            viewHolder.approvalTextView.setVisibility(View.VISIBLE);
                            viewHolder.approvalTextView.setText("Approved");
                            viewHolder.layout.setOnClickListener((v) -> {
                                mDatabaseRef.child("listers").child(pet.getListerId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Lister lister = dataSnapshot.getValue(Lister.class);
//                                        Toast.makeText(getActivity(), "Email: " + lister.getEmail() + "\n" + "Phone Number: " + lister.getPhoneNumber(), Toast.LENGTH_LONG).show();

                                        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        View popupView = inflater.inflate(R.layout.contact_popup, null);

                                        TextView nameTextView = popupView.findViewById(R.id.name);
                                        TextView emailTextView = popupView.findViewById(R.id.email);
                                        TextView phoneTextView = popupView.findViewById(R.id.phone_number);
                                        ImageView picImageView = popupView.findViewById(R.id.pic);

                                        Glide.with(popupView.getContext())
                                                .load(lister.getPicUrl())
                                                .into(picImageView);

                                        nameTextView.setText("Lister: " + lister.getFirstname() + " " + lister.getLastname());
                                        emailTextView.setText("Email: " + lister.getEmail());
                                        phoneTextView.setText("Tel: " + lister.getPhoneNumber());

                                        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                                        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                        boolean focusable = true;
                                        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
                                        popupView.setOnTouchListener(new View.OnTouchListener() {
                                            @Override
                                            public boolean onTouch(View v, MotionEvent event) {
                                                popupWindow.dismiss();
                                                return true;
                                            }
                                        });

                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            popupWindow.setElevation(20);
                                        }

                                        popupWindow.showAtLocation(getView(), Gravity.CENTER, 0, 0);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            });
                        } else {
                            mDatabaseRef.child("applications").child(pet.getId()).child(mFirebaseUser.getUid()).child("rejection").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue() != null && dataSnapshot.getValue(Boolean.class)) {
                                        Log.d("TAG", "ABCDEFG" + pet.getListerId());
                                        viewHolder.approvalTextView.setVisibility(View.VISIBLE);
                                        viewHolder.approvalTextView.setText("Rejected");
                                        viewHolder.layout.setClickable(false);
                                    } else if (dataSnapshot.getValue() != null) {
                                        viewHolder.approvalTextView.setVisibility(View.VISIBLE);
                                        viewHolder.approvalTextView.setText("Pending");
                                        viewHolder.layout.setClickable(false);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // do nothing
                    }
                });

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
        private TextView approvalTextView;
        public View layout;

        private PetViewHolder(View v) {
            super(v);
            layout = v;
            picImageView = v.findViewById(R.id.icon);
            headerTextView = v.findViewById(R.id.first_line);
            footerTextView = v.findViewById(R.id.second_line);
            idTextView = v.findViewById(R.id.pet_id);
            approvalTextView = v.findViewById(R.id.approval);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String key = idTextView.getText().toString();
                    Intent intent = new Intent(v.getContext(), ViewAdopterPetActivity.class);
                    intent.putExtra("pet_id", key);
                    v.getContext().startActivity(intent);
                }
            });
        }
    }
}

