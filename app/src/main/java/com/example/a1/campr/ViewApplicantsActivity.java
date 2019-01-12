package com.example.a1.campr;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.a1.campr.models.Adopter;
import com.example.a1.campr.models.Application;
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

public class ViewApplicantsActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference petRef;
    private String petId;
    private FirebaseRecyclerAdapter<Application, AppViewHolder> mFirebaseAdapter;

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_applicants);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getReference();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();

        petId = getIntent().getStringExtra("pet_id");

        SnapshotParser<Application> parser = new SnapshotParser<Application>() {
            @NonNull
            @Override
            public Application parseSnapshot(@NonNull DataSnapshot dataSnapshot) {
                Application application = dataSnapshot.getValue(Application.class);
                return application;
            }
        };

        Query appQuery = mDatabaseRef.child("applications/" + petId).orderByChild("rejection").equalTo(false);

        FirebaseRecyclerOptions<Application> options =
                new FirebaseRecyclerOptions.Builder<Application>()
                        .setQuery(appQuery, parser)
                        .build();

        RecyclerView mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Application, AppViewHolder>(options) {

            @Override
            public AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                View v = inflater.inflate(R.layout.applicant_row_layout, parent, false);
                return new AppViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(final AppViewHolder viewHolder, int position, Application application) {
                mDatabaseRef.child("adopters/" + application.getAdopterId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        viewHolder.layout.setClickable(true);

                        viewHolder.approvalTextView.setVisibility(View.GONE);
                        Adopter adopter = dataSnapshot.getValue(Adopter.class);

                        viewHolder.headerTextView.setText(adopter.getFirstname() + " " + adopter.getLastname());
                        viewHolder.footerTextView.setText(adopter.getEmail());
                        viewHolder.idTextView.setText(adopter.getId());

                        if (application.isApproval()) {
                            viewHolder.approvalTextView.setVisibility(View.VISIBLE);
                            viewHolder.approvalTextView.setText("Approved");
                            viewHolder.layout.setClickable(false);
                        }

                        Glide.with(viewHolder.picImageView.getContext())
                                .load(adopter.getPicUrl())
                                .into(viewHolder.picImageView);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // do nothing
                    }
                });
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
    public class AppViewHolder extends RecyclerView.ViewHolder {
        private ImageView picImageView;
        private TextView headerTextView;
        private TextView footerTextView;
        private TextView idTextView;
        private TextView approvalTextView;
        public View layout;

        private AppViewHolder(View v) {
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
//                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                            new AddNewFragment()).commit();
                    String key = idTextView.getText().toString();
                    Intent intent = new Intent(v.getContext(), ViewApplicantActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("applicant_id", key);
                    Log.d("TAG", "ABCDEF" + key);
                    extras.putString("pet_id", petId);
                    intent.putExtras(extras);
                    v.getContext().startActivity(intent);
                }
            });
        }
    }
}
