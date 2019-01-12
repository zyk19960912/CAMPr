package com.example.a1.campr;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.a1.campr.fragments.EditAdopterProfileFragment;
import com.example.a1.campr.fragments.FavoriteFragment;
import com.example.a1.campr.fragments.PreferenceFragment;
import com.example.a1.campr.fragments.SwipeCardsFragment;
import com.example.a1.campr.models.Pet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.huxq17.swipecardsview.BaseCardAdapter;
import com.huxq17.swipecardsview.SwipeCardsView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdopterActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private ArrayList<Pet> list;
    private SwipeCardsView swipeCardsView;

    // Filters
    private HashMap<String, String> filters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adopter);

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getReference();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        // Initialize filters
        filters = new HashMap<>();
        filters.put("species", "Any");
        filters.put("age", "Any");
        filters.put("color", "Any");
        filters.put("size", "Any");
        filters.put("feeRange", "Any");

//        mDatabaseRef.child("adopters").child(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.getValue(Adopter.class) == null) {
//                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                            new PreferenceFragment()).commit();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // do nothing
//            }
//        });

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SwipeCardsFragment()).commit();
        navigationView.setCheckedItem(R.id.nav_swipe_cards);

//        showSwipeCards();
    }

//    public void showSwipeCards() {
//        findViewById(R.id.swipe_cards_view).setVisibility(View.VISIBLE);
//
//        // Set up swipe cards
//        swipeCardsView = findViewById(R.id.swipe_cards_view);
//        swipeCardsView.retainLastCard(false);
//        swipeCardsView.enableSwipe(true);
//
//        // Add litener
//        swipeCardsView.setCardsSlideListener(new SwipeCardsView.CardsSlideListener() {
//            @Override
//            public void onShow(int index) {
//                // TODO
//            }
//
//            @Override
//            public void onCardVanish(int index, SwipeCardsView.SlideType type) {
//                Pet targetPet = list.get(index);
//                switch (type){
//                    case LEFT:
//                        mDatabaseRef.child("pets").child(targetPet.getId()).child("impossibleAdopters").child(mFirebaseUser.getUid()).setValue(true);
//                        break;
//                    case RIGHT:
//                        mDatabaseRef.child("pets").child(targetPet.getId()).child("possibleAdopters").child(mFirebaseUser.getUid()).setValue(true);
//                        mDatabaseRef.child("adopters").child(mFirebaseUser.getUid()).child("chosenPets").child(targetPet.getId()).setValue(true);
//                        break;
//                }
//            }
//
//            @Override
//            public void onItemClick(View cardImageView, int index) {
//                // TODO
//            }
//        });
//
//        Query initialQuery = mDatabaseRef.child("pets")
//                .orderByChild("possibleAdopters/" + mFirebaseUser.getUid()).equalTo(null);
//
//        initialQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//                list = new ArrayList<>();
//                for (DataSnapshot petSnapshot : snapshot.getChildren()) {
//                    HashMap<String, Boolean> impossibleAdopters = petSnapshot.getValue(Pet.class).getImpossibleAdopters();
//                    if (impossibleAdopters == null || !impossibleAdopters.containsKey(mFirebaseUser.getUid()))
//                        list.add(petSnapshot.getValue(Pet.class));
//                }
//
//                PetAdapter petAdapter = new PetAdapter(list, AdopterActivity.this);
//                swipeCardsView.setAdapter(petAdapter);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // do nothing
//            }
//        });
//    }

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


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        View view = findViewById(R.id.swipe_cards_view);
        NavigationView navigationView = findViewById(R.id.nav_view);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()) {
            case R.id.nav_swipe_cards:
                transaction.replace(R.id.fragment_container, new SwipeCardsFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_swipe_cards);
                break;
            case R.id.nav_profile:
                transaction.replace(R.id.fragment_container, new EditAdopterProfileFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_profile);
                break;
            case R.id.nav_preference:
                transaction.replace(R.id.fragment_container, new PreferenceFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_preference);
                break;
            case R.id.nav_favorite:
                transaction.replace(R.id.fragment_container, new FavoriteFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_favorite);
                break;
            case R.id.nav_switch:
                Intent intent = new Intent(AdopterActivity.this, WorkModeActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_signout:
                mFirebaseAuth.signOut();
                intent = new Intent(AdopterActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onBackPressed(){
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    private class PetAdapter extends ArrayAdapter<Pet> {
//        private Context mContext;
//        private List<Pet> mlist;
//
//        public PetAdapter(@NonNull Context context, ArrayList<Pet> list) {
//            super(context, 0 , list);
//            mContext = context;
//            mlist = list;
//        }
//
//        @NonNull
//        @Override
//        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//            View listItem = convertView;
//            if (listItem == null)
//                listItem = LayoutInflater.from(mContext).inflate(R.layout.item, parent, false);
//
//            Pet pet = mlist.get(position);
//
//            ImageView imageView = listItem.findViewById(R.id.image);
//            Glide.with(imageView.getContext())
//                    .load(pet.getPicUrl())
//                    .into(imageView);
//
//            TextView nameTextView = listItem.findViewById(R.id.name);
//            nameTextView.setText(pet.getName());
//
//            TextView genderTextView = listItem.findViewById(R.id.gender);
//            genderTextView.setText(pet.getGender());
//
//            TextView descriptionTextView = listItem.findViewById(R.id.description);
//            descriptionTextView.setText(pet.getInfo());
//
//            return listItem;
//        }
//    }

//    public class PetAdapter extends BaseCardAdapter {
//        private List<Pet> petList;
//        private Context context;
//
//        private PetAdapter(List<Pet> petList, Context context) {
//            this.petList = petList;
//            this.context = context;
//        }
//
//        @Override
//        public int getCount() {
//            return petList.size();
//        }
//
//        @Override
//        public int getCardLayoutId() {
//            return R.layout.item;
//        }
//
//        @Override
//        public void onBindData(int position, View cardview) {
//            if (petList == null || petList.size() == 0) {
//                return;
//            }
//
//            Pet pet = petList.get(position);
//
//            ImageView imageView = cardview.findViewById(R.id.image);
//            Glide.with(imageView.getContext())
//                    .load(pet.getPicUrl())
//                    .into(imageView);
//
//            TextView nameTextView = cardview.findViewById(R.id.name);
//            nameTextView.setText(pet.getName());
//
//            TextView genderTextView = cardview.findViewById(R.id.gender);
//            genderTextView.setText(pet.getGender());
//
//            TextView descriptionTextView = cardview.findViewById(R.id.description);
//            descriptionTextView.setText(pet.getInfo());
//        }
//
//        @Override
//        public int getVisibleCardCount() {
//            return super.getVisibleCardCount();
//        }
//    }

    public void changeFilters(String species, String age, String color, String size, String feeRange) {
        filters.put("species", species);
        filters.put("age", age);
        filters.put("color", color);
        filters.put("size", size);
        filters.put("feeRange", feeRange);
    }

    public HashMap<String, String> getFilters() {
        return filters;
    }
}
