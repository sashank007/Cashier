package com.sas.cashier.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;


import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Context;
import com.sas.cashier.CustomAdapter;
import com.sas.cashier.Data.Item;
import com.sas.cashier.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;
import static com.firebase.ui.auth.ui.email.TroubleSigningInFragment.TAG;

public class ListFragment extends Fragment {
    EditText et_expenditureAmount , et_expenditureType , et_expenditureSubType;

    Button done;
    int expenditureValue ;
    private int maxSpendingValue;
    TextView tvDate;
    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;
    private long selectedDate;
    String expenditureTypeValue , expenditureSubTypeValue;

    Spinner dropdown;
    private static CustomAdapter adapter;
    private FirebaseUser mUser;
    private ArrayList<Item> dataModels = new ArrayList<>();
    private ListView listView;
    private BottomAppBar bottomAppBar;
    private Context context;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        firebaseAuth  = FirebaseAuth.getInstance();
        mUser  = firebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        System.out.print("got selected date in expense tracker: " + selectedDate);
        LayoutInflater lf = getActivity().getLayoutInflater();
        View v = lf.inflate ( R.layout.fragment_items, container, false );


        listView=(ListView)v.findViewById(R.id.list);

        dataModels= new ArrayList<>();

        bottomAppBar = v.findViewById(R.id.navigation);
        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_app_bar_home:
                        loadFragment(new HomeFragment());
                        return true;
                    case R.id.bottom_app_bar_cart:
                        loadFragment(new ListFragment());
                        return true;

                }
                return false;
            }
        });


        getItems();


        return v;
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment, "MY_FRAGMENT")
                    .commit();
            return true;
        }
        return false;
    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void getItems() {

        FirebaseUser user = firebaseAuth.getCurrentUser();

        Query myQuery = mDatabase.child("items").child(user.getUid());
        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            List<Item> myList = new ArrayList<>();

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Item item = snap.getValue(Item.class);
                    Log.d("TAG","item : " + item);
                    myList.add(item);
                }
                updateExpensesList(myList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateExpensesList(List<Item> myList)
    {


        dataModels.addAll(myList);

        adapter= new CustomAdapter(dataModels,getContext());

        listView.setAdapter(adapter);

//
//        adapter= new CustomAdapter(dataModels,getContext());
//
//        listView.setAdapter(adapter);

    }
    @Override
    public void onResume() {
        super.onResume();
//        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }
    @Override
    public void onStop() {
        super.onStop();
//        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }


}
