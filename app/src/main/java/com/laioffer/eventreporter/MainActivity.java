package com.laioffer.eventreporter;


import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

// implements CommentFragment.OnItemSelectListenerReversed
public class MainActivity extends AppCompatActivity implements EventFragment.OnItemSelectListener, CommentFragment.OnItemSelectListener {
    private EventFragment mListFragment;
    private CommentFragment mGridFragment;
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private Button mSubmitButton;
    private Button mRegisterButton;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Write a message to the database
        // Firebase uses singleton to initialize the sdk
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mUsernameEditText = (EditText) findViewById(R.id.editTextLogin);
        mPasswordEditText = (EditText) findViewById(R.id.editTextPassword);
        mSubmitButton = (Button) findViewById(R.id.submit);
        mRegisterButton = (Button) findViewById(R.id.register);

       /* // Get ListView object from xml.
        ListView eventListView = (ListView) findViewById(R.id.event_list);

        // Initialize an adapter.
        EventAdapter adapter = new EventAdapter(this);

        // Assign adapter to ListView.
        eventListView.setAdapter(adapter);*/
        // Show different fragments based on screen size.
//        if (findViewById(R.id.fragment_container) != null) {
//            Fragment fragment = isTablet() ? new  CommentFragment() : new EventFragment();
//            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
//        }
        //add list view
        /*
        mListFragment = new EventFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.event_container, mListFragment).commit();


        //add Gridview
        if (isTablet()) {
            mGridFragment = new CommentFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.comment_container, mGridFragment).commit();
        }
        */

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = mUsernameEditText.getText().toString();
                final String password = Utils.md5Encryption(mPasswordEditText.getText().toString());
                final User user = new User(username, password, System.currentTimeMillis());
                mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(username)) {
                            Toast.makeText(getBaseContext(),"username is already registered, please change one", Toast.LENGTH_SHORT).show();
                        } else if (!username.equals("") && !password.equals("")){
                            // put username as key to set value
                            mDatabase.child("users").child(user.getUsername()).setValue(user);
                            Toast.makeText(getBaseContext(),"Successfully registered", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = mUsernameEditText.getText().toString();
                final String password = Utils.md5Encryption(mPasswordEditText.getText().toString());
                mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(username) && (password.equals(dataSnapshot.child(username).child("password").getValue()))) {
                            Log.i( " Your log", "You successfully login");
                            Intent myIntent = new Intent(MainActivity.this, EventActivity.class);
                            Utils.username = username;
                            startActivity(myIntent);

                        } else {
                            Toast.makeText(getBaseContext(),"Please login again", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });


    }


    private boolean isTablet() {
        return (getApplicationContext().getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) >=
                Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public void onItemSelected(int position) {
        mGridFragment.onItemSelected(position);

    }
    /*
    @Override
    public void onItemSelectedReversed(int position) {
        mListFragment.onItemSelected(position);

    }
    */
    @Override
    public void onCommentSelected(int position) {
        mListFragment.onItemSelected(position);
    }

}