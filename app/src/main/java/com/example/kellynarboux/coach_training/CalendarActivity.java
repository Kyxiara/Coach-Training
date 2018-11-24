package com.example.kellynarboux.coach_training;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.example.kellynarboux.coach_training.db.UserViewModel;

import java.util.List;
import java.util.Objects;

public class CalendarActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private UserViewModel userViewModel;

    Toolbar toolbar;
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mToogle;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // check if the user is registered  TODO
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        List users = userViewModel.getAllUsers().getValue();
        if(users != null && users.isEmpty()){  // FIXME
            Intent redirectToRegister = new Intent(this, RegisterActivity.class);
            startActivity(redirectToRegister);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        toolbar = (Toolbar)findViewById(R.id.toolbarCalendar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerCalendar);
        mToogle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToogle);
        mToogle.syncState();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        mToogle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorAccent));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        navigationView = (NavigationView) findViewById(R.id.nav_viewCalendar);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().findItem(R.id.navigation_register).setVisible(false);
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
                startActivity(new Intent(CalendarActivity.this, ProfilActivity.class));
                break;
            case R.id.navigation_exercices :
                startActivity(new Intent(CalendarActivity.this, MainActivity.class));
                break;
            case R.id.navigation_calendrier :
                break;
            case R.id.navigation_options :
                startActivity(new Intent(CalendarActivity.this, SettingActivity.class));
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