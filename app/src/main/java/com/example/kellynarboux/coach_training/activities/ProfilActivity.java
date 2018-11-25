package com.example.kellynarboux.coach_training.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.kellynarboux.coach_training.R;
import com.example.kellynarboux.coach_training.db.Gender;
import com.example.kellynarboux.coach_training.db.User;
import com.example.kellynarboux.coach_training.db.UserViewModel;

import java.util.List;
import java.util.Locale;

public class ProfilActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    Toolbar toolbar;
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mToogle;
    NavigationView navigationView;

    private UserViewModel userViewModel;

    TextView pseudo;
    ImageView avatar;
    EditText weight;
    EditText size;
    TextView imc;
    SeekBar seekBar;
    TextView state_imc;

    private boolean modification = false;

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
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        navigationView = findViewById(R.id.nav_viewProfil);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().findItem(R.id.navigation_register).setVisible(false);

        pseudo = findViewById(R.id.pseudo);
        avatar = findViewById(R.id.avatar);
        weight = findViewById(R.id.weight);
        size = findViewById(R.id.size);  // TODO change size to height

        imc = findViewById(R.id.imc);
        seekBar = findViewById(R.id.seek_bar);
        state_imc = findViewById(R.id.state_imc);

        // Create the observer which updates the UI.
        final Observer<List<User>> userObserver = users -> {
            if (users != null && !users.isEmpty()) {
                User currentUser = users.get(0);  // FIXME bidouille
                int avatarID = R.drawable.avatar_homme;
                if (Gender.valueOf(currentUser.getGender()) == Gender.Femme)
                    avatarID = R.drawable.avatar_femme;
                avatar.setImageResource(avatarID);

                weight.setText(String.format(Locale.FRANCE, "%.2f", currentUser.getWeight()));
                size.setText(String.format(Locale.FRANCE, "%d", currentUser.getHeight()));

                refreshImc(currentUser);
                pseudo.setText(currentUser.getPseudo());
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        userViewModel.getAllUsers().observe(this, userObserver);

        weight.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0)
                    return;  // Exit the modification

                LiveData<List<User>> users = userViewModel.getAllUsers();
                if(users.getValue() == null)
                    return;  // FIXME
                User currentUser = users.getValue().get(0);
                try {
                    String myString = s.toString();
                    myString = myString.replace(',', '.');
                    currentUser.setWeight(Float.parseFloat(myString.toString()));
                } catch (NumberFormatException nfe){
                    Log.d("Warning", nfe.toString());
                }
                refreshImc(currentUser);
                modification = true;
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        size.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0)
                    return;  // Exit the modification

                LiveData<List<User>> users = userViewModel.getAllUsers();
                if(users.getValue() != null && !users.getValue().isEmpty()){
                    User currentUser = users.getValue().get(0);
                    currentUser.setHeight(Integer.parseInt(s.toString()));
                    refreshImc(currentUser);
                    modification = true;
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    private void save(){
        if(modification){
            LiveData<List<User>> users = userViewModel.getAllUsers();
            User currentUser = users.getValue().get(0);
            userViewModel.update(currentUser);
        }
    }

    public float calculIMC(float weight, int height){
        float h = height / (float)100;
        return weight / (h * h);
    }

    public void refreshImc(User currentUser){
        float value = calculIMC(currentUser.getWeight(), currentUser.getHeight());
        imc.setText("IMC : " + String.format(Locale.FRANCE, "%.2f", value));
        int progressBarValue = (int) value;
        if (progressBarValue < 15) progressBarValue = 15;
        else if (progressBarValue > 42) progressBarValue = 42;
        seekBar.setProgress(progressBarValue - 15);

        int color;
        String state = "";
        if(progressBarValue <= 16){
            state = "Dénutrition";
            color = Color.parseColor("#0034f4");
        } else if (progressBarValue <= 18){
            state = "Maigreur";
            color = Color.parseColor("#00ebf4");
        } else if (progressBarValue <= 25){
            state = "Corpulence normale";
            color = Color.parseColor("#00b100");
        } else if (progressBarValue <= 30){
            state = "Surpoids";
            color = Color.parseColor("#ffff00");
        } else if (progressBarValue <= 35){
            state = "Obésité modérée";
            color = Color.parseColor("#ffae00");
        } else if (progressBarValue <= 40){
            state = "Obésité sévère";
            color = Color.parseColor("#ff0000");
        } else {
            state = "Obésité morbide";
            color = Color.parseColor("#ff78ff");
        }

        state_imc.setTextColor(color);
        state_imc.setText(state);
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
        save();
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
            save();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        save();
    }
}
