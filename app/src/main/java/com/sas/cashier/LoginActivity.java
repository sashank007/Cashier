package com.sas.cashier;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class LoginActivity extends AppCompatActivity {


    public static String INTENT_PHONE = "INTENT_PHONE";
    public static String INTENT_EMAIL = "INTENT_EMAIL";
    public static String INTENT_WORD = "INTENT_WORD";
    public static String INTENT_SETTINGS = "INTENT_SETTINGS";
    public static String INTENT_FIRST_TIME = "INTENT_FIRST_TIME";
    public static String INTENT_TIME_WATCHED = "INTENT_TIME_WATCHED";
    public static String INTENT_TIME_WATCHED_VIDEO = "INTENT_TIME_WATCHED_VIDEO";
    public static String INTENT_URI = "INTENT_URI";
    public static String INTENT_SERVER_ADDRESS = "INTENT_SERVER_ADDRESS";
    public static String INTENT_PRACTICE = "INTENT_PRACTICE";
    private FirebaseAuth firebaseAuth;
    public static int RC_SIGN_IN=1;
    private DatabaseReference mDatabase;

    EditText et_email, et_phone , firstName , lastName  , et_spending , et_password;
    String email ,first_name,last_name , maxSpending;
    String phone;
    SharedPreferences sharedPreferences;
    long time_to_login;
    Button login , btn_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build());
        firebaseAuth  = FirebaseAuth.getInstance();
        ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder()
                .setAndroidPackageName(getPackageName(),true,null)
                .setHandleCodeInApp(true) // This must be set to true
                .setUrl("https://google.com") // This URL needs to be whitelisted
                .build();

        et_email = (EditText) findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);

        login = (Button) findViewById(R.id.login_btn);
        btn_signup = findViewById(R.id.signup_btn);
        time_to_login = System.currentTimeMillis();
        sharedPreferences = this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);


        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                System.out.println("clicked login");
                loginUser(et_email.getText().toString(),et_password.getText().toString());
            }
        });
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),SignUpActivity.class));
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            // No user is signed in
        } else {
            startActivity(new Intent(this,TabbedActivity.class));
        }
    }
//    private void writeNewUser(String userId, String firstName, String lastName , int highestStreak , int currentStreak ,long timeLeft , int maxSpending , String email , String phone) {
//
//        System.out.println("write new user: " );
//        final String first_name  = firstName , last_name = lastName ,_email = email  , _phone = phone;
//        final int highest_streak = highestStreak , current_streak = currentStreak , max_spending = maxSpending;
//        firebaseAuth.createUserWithEmailAndPassword(email,"Sashank@007").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//
//                if (task.isSuccessful()){
//                    FirebaseUser user = firebaseAuth.getCurrentUser(); //You Firebase user
//                    // user registered, start profile activity
//                    Toast.makeText(LoginActivity.this,"Account Created",Toast.LENGTH_LONG).show();
//
//                    User newUser = new User(first_name , last_name , 0,0,0,max_spending,_email,_phone , "");
//                    mDatabase.child("users").child(user.getUid()).setValue(newUser);
//
//                    finish();
//                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                }
//                else{
//                    Toast.makeText(LoginActivity.this,"Could not create account. Please try again",Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//
//
//    }

    private void loginUser(String email , String password)
    {
        if(TextUtils.isEmpty(email)||TextUtils.isEmpty(password)) {

            Toast.makeText(getApplicationContext(),"Please provide email and password",Toast.LENGTH_LONG).show();
        }
        else
        {
            final String f_email = email, f_password = password;
            firebaseAuth.signInWithEmailAndPassword(f_email, f_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Login Successful.", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(), TabbedActivity.class));
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Not a valid email id or password" , Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }

    public void login() {




        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?


            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        100);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

        } else {
            if (et_email.getText().toString().isEmpty() || et_phone.getText().toString().isEmpty()) {
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("ALERT");
                alertDialog.setMessage("Please Enter Login Information!");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            } else {

                email = et_email.getText().toString().trim().toLowerCase();
                first_name = firstName.getText().toString().trim().toLowerCase();
                last_name = lastName.getText().toString().trim().toLowerCase();
                phone = et_email.getText().toString().trim();
                maxSpending = et_spending.getText().toString().trim();
                System.out.println("first name :" + first_name);
                System.out.println("last name :" + last_name);
                String uniqueID = UUID.randomUUID().toString();
//                writeNewUser(uniqueID,first_name , last_name ,0 , 0 , 0 , Integer.parseInt(maxSpending), email , phone);


            }
        }
    }

}
