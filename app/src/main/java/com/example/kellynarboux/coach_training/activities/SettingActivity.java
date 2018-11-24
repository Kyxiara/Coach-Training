package com.example.kellynarboux.coach_training.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.example.kellynarboux.coach_training.R;
import com.example.kellynarboux.coach_training.db.AppDatabase;
import com.example.kellynarboux.coach_training.db.UserViewModel;
import com.example.kellynarboux.coach_training.notification.TrainingTimeReceiver;

import java.lang.ref.WeakReference;

public class SettingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    Toolbar toolbar;
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mToogle;
    NavigationView navigationView;
    private Button reset;
    UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        toolbar = findViewById(R.id.toolbarSetting);
        setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.drawerSetting);
        mToogle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToogle);
        mToogle.syncState();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        mToogle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorAccent));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        navigationView = findViewById(R.id.nav_viewSetting);
        navigationView.setNavigationItemSelectedListener(this);

        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.getAllUsers().observe(this, userList -> {
            if(userList != null && userList.isEmpty()){  // FIXME
                navigationView.getMenu().findItem(R.id.navigation_profil).setVisible(false);
                navigationView.getMenu().findItem(R.id.navigation_calendrier).setVisible(false);
                navigationView.getMenu().findItem(R.id.navigation_register).setVisible(true);
            }else{
                navigationView.getMenu().findItem(R.id.navigation_register).setVisible(false);
                navigationView.getMenu().findItem(R.id.navigation_profil).setVisible(true);
                navigationView.getMenu().findItem(R.id.navigation_calendrier).setVisible(true);
            }
        });

        reset = findViewById(R.id.reset);
        reset.setOnClickListener(view -> {
            ResetAsyncTask resetTask = new ResetAsyncTask(SettingActivity.this);
            resetTask.execute();
        });

        // Notification
        Intent intent = new Intent(SettingActivity.this, TrainingTimeReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(SettingActivity.this, 0, intent, 0);
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        if (am != null)
            am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
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
                startActivity(new Intent(SettingActivity.this, ProfilActivity.class));
                break;
            case R.id.navigation_exercices :
                startActivity(new Intent(SettingActivity.this, MainActivity.class));
                break;
            case R.id.navigation_calendrier :
                startActivity(new Intent(SettingActivity.this, CalendarActivity.class));
                break;
            case R.id.navigation_options :
                break;
            case R.id.navigation_register :
                startActivity(new Intent(SettingActivity.this, RegisterActivity.class));
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

    private static class ResetAsyncTask extends AsyncTask<Void, Void, Integer> {

        //Prevent leak
        private WeakReference<Context> context;

        public ResetAsyncTask(Context context) {
            this.context = new WeakReference<>(context);
        }

        @Override
        protected Integer doInBackground(Void... params) {
            AppDatabase db = AppDatabase.getInstance(context.get());
            if(db != null){
                //int previous = db.userDao().getAll().getValue().size();
                db.userDao().deleteAll();
                //return previous - db.userDao().getAll().getValue().size();
                return 1;
            }
            return 0;  // error during the reset
        }

        @Override
        protected void onPostExecute(Integer deletedUser) {
            if (deletedUser > 0) {
                Toast.makeText(context.get(), "reset the data of " + deletedUser + " user(s)", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context.get(), "Error, no data to reset", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
