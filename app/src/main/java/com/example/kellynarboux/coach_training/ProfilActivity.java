package com.example.kellynarboux.coach_training;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kellynarboux.coach_training.db.User;
import com.example.kellynarboux.coach_training.db.UserViewModel;

import java.util.List;
import java.util.Objects;

public class ProfilActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    Toolbar toolbar;
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mToogle;
    NavigationView navigationView;

    private UserViewModel userViewModel;


    ImageView avatar;
    EditText weight;
    EditText size;
    TextView imc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // check if the user is registered  TODO

        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.getAllUsers().observe(this, userList -> {
            if(userList != null && userList.isEmpty()){  // FIXME
                Intent redirectToRegister = new Intent(this, RegisterActivity.class);
                startActivity(redirectToRegister);
            } else if(userList != null){
                Toast.makeText(
                        getApplicationContext(),
                        "found a user : " + userList.get(0).getPseudo(),
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "error while retrieving users",
                        Toast.LENGTH_LONG).show();
                Log.d("tag", "error");
            }
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        toolbar = (Toolbar)findViewById(R.id.toolbarProfil);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerProfil);
        mToogle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToogle);
        mToogle.syncState();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        mToogle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorAccent));

        navigationView = (NavigationView) findViewById(R.id.nav_viewProfil);
        navigationView.setNavigationItemSelectedListener(this);

        avatar = (ImageView) findViewById(R.id.avatar);
        ImageButton button_man = (ImageButton) findViewById(R.id.button_man);
        button_man.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                avatar.setImageResource(R.drawable.avatar_homme);
            }
        });
        ImageButton button_woman = (ImageButton) findViewById(R.id.button_woman);
        button_woman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                avatar.setImageResource(R.drawable.avatar_femme);
            }
        });

        weight = (EditText) findViewById(R.id.weight);
        size = (EditText) findViewById(R.id.size);
        imc = (TextView) findViewById(R.id.imc);
    }

    public float calculIMC(float weight, int size){
        size /= 100;
        return weight / (size * size);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToogle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.navigation_profil :
                break;
            case R.id.navigation_exercices :
                startActivity(new Intent(ProfilActivity.this, MainActivity.class));
                break;
            case R.id.navigation_calendrier :
                startActivity(new Intent(ProfilActivity.this, CalendarActivity.class));
                break;
            case R.id.navigation_options :
                startActivity(new Intent(ProfilActivity.this, SettingActivity.class));
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
