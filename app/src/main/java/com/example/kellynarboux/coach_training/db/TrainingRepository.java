package com.example.kellynarboux.coach_training.db;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class TrainingRepository {
    private TrainingDao trainingDao;
    private LiveData<List<Training>> allTraining;

    TrainingRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        trainingDao = db.trainingDao();
        allTraining = trainingDao.getAll();
    }

    LiveData<List<Training>> getAllTraining() {
        return allTraining;
    }


    public void insert (Training training) {
        new insertAsyncTask(trainingDao).execute(training);
    }

    private static class insertAsyncTask extends AsyncTask<Training, Void, Void> {

        private TrainingDao asyncTaskDao;

        insertAsyncTask(TrainingDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Training... params) {
            asyncTaskDao.insert(params[0]);
            return null;
        }
    }

    public void update(Training training) {
        new updateAsyncTask(trainingDao).execute(training);
    }

    private static class updateAsyncTask extends AsyncTask<Training, Void, Void> {

        private TrainingDao asyncTaskDao;

        updateAsyncTask(TrainingDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Training... params) {
            asyncTaskDao.update(params[0]);
            return null;
        }
    }
}
