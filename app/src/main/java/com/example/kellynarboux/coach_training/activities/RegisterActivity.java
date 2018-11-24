package com.example.kellynarboux.coach_training.activities;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.Loader;
import android.database.Cursor;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.kellynarboux.coach_training.R;
import com.example.kellynarboux.coach_training.db.Gender;
import com.example.kellynarboux.coach_training.db.User;
import com.example.kellynarboux.coach_training.db.UserViewModel;

public class RegisterActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>, NavigationView.OnNavigationItemSelectedListener {

    private UserViewModel userViewModel;

    // UI references.
    private AutoCompleteTextView nameView;
    private EditText weightView;
    private EditText heightView;
    private EditText ageView;
    private RadioGroup genderView;

    Toolbar toolbar;
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mToogle;
    NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        toolbar = (Toolbar)findViewById(R.id.register_toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.register_drawer);
        mToogle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToogle);
        mToogle.syncState();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        mToogle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorAccent));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        navigationView = (NavigationView) findViewById(R.id.register_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.getAllUsers().observe(this, userList -> {
            if(userList != null && userList.isEmpty()){  // FIXME
                navigationView.getMenu().findItem(R.id.navigation_profil).setVisible(false);
                navigationView.getMenu().findItem(R.id.navigation_calendrier).setVisible(false);
            }else{
                navigationView.getMenu().findItem(R.id.navigation_register).setVisible(false);
            }
        });

        // Set up the registerView form.
        nameView = findViewById(R.id.name);
        weightView = findViewById(R.id.weight);
        heightView = findViewById(R.id.height);
        ageView = findViewById(R.id.age);
        Button register = findViewById(R.id.register);

        genderView = findViewById(R.id.gender);

        register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });
    }

    public void hideSoftKeyboard()
    {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        hideSoftKeyboard();
        if (mToogle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void attemptRegister() {

        RadioButton radioButton = (RadioButton)findViewById(genderView.getCheckedRadioButtonId());
        String genderVal = radioButton.getText().toString();
        Log.d("GENRE", genderVal);

        // Reset errors.
        nameView.setError(null);

        // Store values at the time of the login attempt.
        String name = nameView.getText().toString();
        float weight = Float.valueOf(weightView.getText().toString());
        int height = Integer.valueOf(heightView.getText().toString());
        int age = Integer.parseInt(ageView.getText().toString());
        Gender gender = Gender.valueOf(genderVal);

        boolean cancel = false;
        View focusView = null;

        // Check for a name
        if (!TextUtils.isEmpty(name) && !isNameValid(name)) {
            nameView.setError("this name is too short");
            focusView = nameView;
            cancel = true;
        }

        // TODO other check

        if (cancel) {
            // There was an error; don't attempt register and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            register(name, weight, height, age, gender);
        }
    }

    private boolean isNameValid(@NonNull String name) {
        return name.length() > 2;
    }

    private void register(String name, float weight, int height, int age, Gender gender) {
        User user = new User(name, weight, height, age, gender);
        userViewModel.insert(user);
        Toast.makeText(
                getApplicationContext(),
                "registered as  : " + name,
                Toast.LENGTH_LONG).show();
        Intent myIntent = new Intent(RegisterActivity.this, ProfilActivity.class);
        startActivity(myIntent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.navigation_exercices :
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                break;
            case R.id.navigation_options :
                startActivity(new Intent(RegisterActivity.this, SettingActivity.class));
                break;
            case R.id.navigation_register :
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            //super.onBackPressed();
        }
    }
}

