package com.sas.cashier.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

//        dataMoMdels.add(new Item(title, price , image));
        getItems();

        return v;
    }

    public void switchFragment(Fragment fragment)
    {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
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
        System.out.println("my list:" + myList);
        dataModels.addAll(myList);

        adapter= new CustomAdapter(dataModels,getActivity().getApplicationContext());

        listView.setAdapter(adapter);


//        for(int i = 0 ; i<myList.size();i++)
//        {
//            Item currentItem = myList.get(i);
//            dataModels.add(currentItem);
//        }
    }
    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }
    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }


}
