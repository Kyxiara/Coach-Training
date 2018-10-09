package com.example.kellynarboux.coach_training.db;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class UserRepository {
    private UserDao userDao;
    private LiveData<List<User>> allUsers;

    UserRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        userDao = db.userDao();
        allUsers = userDao.getAll();
    }

    LiveData<List<User>> getAllUsers() {
        return allUsers;
    }


    public void insert (User User) {
        new insertAsyncTask(userDao).execute(User);
    }

    private static class insertAsyncTask extends AsyncTask<User, Void, Void> {

        private UserDao asyncTaskDao;

        insertAsyncTask(UserDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final User... params) {
            asyncTaskDao.insert(params[0]);
            return null;
        }
    }

    public void update(User User) {
        new updateAsyncTask(userDao).execute(User);
    }

    private static class updateAsyncTask extends AsyncTask<User, Void, Void> {

        private UserDao asyncTaskDao;

        updateAsyncTask(UserDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final User... params) {
            asyncTaskDao.update(params[0]);
            return null;
        }
    }
}
