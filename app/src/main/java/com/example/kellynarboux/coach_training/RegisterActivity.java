package com.example.kellynarboux.coach_training;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.Loader;
import android.database.Cursor;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.kellynarboux.coach_training.db.Gender;
import com.example.kellynarboux.coach_training.db.User;
import com.example.kellynarboux.coach_training.db.UserViewModel;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private UserViewModel userViewModel;

    // UI references.
    private AutoCompleteTextView nameView;
    private EditText weightView;
    private EditText heightView;
    private EditText ageView;
    private Spinner genderView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        // Set up the registerView form.
        nameView = findViewById(R.id.name);
        weightView = findViewById(R.id.weight);
        heightView = findViewById(R.id.height);
        ageView = findViewById(R.id.age);
        genderView = findViewById(R.id.gender);
        Button register = findViewById(R.id.register);

        // add values to the spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderView.setAdapter(adapter);

        register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });
    }


    private void attemptRegister() {

        // Reset errors.
        nameView.setError(null);

        // Store values at the time of the login attempt.
        String name = nameView.getText().toString();
        float weight = Float.valueOf(weightView.getText().toString());
        float height = Float.valueOf(heightView.getText().toString());
        int age = Integer.parseInt(ageView.getText().toString());
        Gender gender = Gender.valueOf(genderView.getSelectedItem().toString());

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

    private void register(String name, float weight, float height, int age, Gender gender) {
        User user = new User(name, weight, height, age, gender);
        userViewModel.insert(user);
        Toast.makeText(
                getApplicationContext(),
                "registered as  : " + name,
                Toast.LENGTH_LONG).show();
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
}

