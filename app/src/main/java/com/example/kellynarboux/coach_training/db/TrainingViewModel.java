package com.example.kellynarboux.coach_training.db;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class TrainingViewModel extends AndroidViewModel {

    private TrainingRepository trainingRepository;

    private LiveData<List<Training>> allTraining;

    public TrainingViewModel(Application application) {
        super(application);
        trainingRepository = new TrainingRepository(application);
        allTraining = trainingRepository.getAllTraining();
    }

    public LiveData<List<Training>> getAllTraining() {
        return allTraining;
    }

    public void insert(Training training) {
        trainingRepository.insert(training);
    }

    public void update(Training training) {
        trainingRepository.update(training);
    }
}
