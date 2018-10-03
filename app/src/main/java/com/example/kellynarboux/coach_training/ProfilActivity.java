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

import com.example.kellynarboux.coach_training.db.Gender;
import com.example.kellynarboux.coach_training.db.User;
import com.example.kellynarboux.coach_training.db.UserViewModel;

import java.util.List;
import java.util.Locale;
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
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        toolbar = findViewById(R.id.toolbarProfil);
        setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.drawerProfil);
        mToogle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToogle);
        mToogle.syncState();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        mToogle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorAccent));

        navigationView = findViewById(R.id.nav_viewProfil);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().findItem(R.id.navigation_register).setVisible(false);

        avatar = findViewById(R.id.avatar);
        weight = findViewById(R.id.weight);
        size = findViewById(R.id.size);  // TODO change size to height

        imc = findViewById(R.id.imc);

        // Create the observer which updates the UI.
        final Observer<List<User>> userObserver = users -> {
            if(users != null){
                User currentUser = users.get(0);  // FIXME bidouille
                int avatarID = R.drawable.avatar_homme;
                if(Gender.valueOf(currentUser.getGender()) == Gender.Femme)
                    avatarID = R.drawable.avatar_femme;
                avatar.setImageResource(avatarID);

                weight.setText(String.format(Locale.FRANCE, "%f5", currentUser.getWeight()));
                size.setText(String.format(Locale.FRANCE, "%d", currentUser.getHeight()));

                float myImc = calculIMC(currentUser.getWeight(), currentUser.getHeight());
                imc.setText(String.format(Locale.FRANCE, "%f5", myImc));
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        userViewModel.getAllUsers().observe(this, userObserver);

        /*weight.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                LiveData<List<User>> users = userViewModel.getAllUsers();
                //users.getValue().
            }
        });*/

    }

    public float calculIMC(float weight, int height){
        height /= 100;
        return weight / (height * height);
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
