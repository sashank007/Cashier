package com.sas.cashier;

import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sas.cashier.Data.Item;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class ListActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseUser mUser;
    ArrayList<Item> dataModels;
    ListView listView;
    private static CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseAuth  = FirebaseAuth.getInstance();

        Intent i = getIntent();
        Double price = i.getDoubleExtra("price" , 0.0);
        String image = i.getStringExtra("image");
        String title  = i.getStringExtra("title");


        listView=(ListView)findViewById(R.id.list);

        dataModels= new ArrayList<>();

//        dataModels.add(new Item(title, price , image));
        getItems();




//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
////                DataModel dataModel= dataModels.get(position);
////
////                Snackbar.make(view, dataModel.getName()+"\n"+dataModel.getType()+" API: "+dataModel.getVersion_number(), Snackbar.LENGTH_LONG)
////                        .setAction("No action", null).show();
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

        adapter= new CustomAdapter(dataModels,getApplicationContext());

        listView.setAdapter(adapter);


//        for(int i = 0 ; i<myList.size();i++)
//        {
//            Item currentItem = myList.get(i);
//            dataModels.add(currentItem);
//        }
    }
}
