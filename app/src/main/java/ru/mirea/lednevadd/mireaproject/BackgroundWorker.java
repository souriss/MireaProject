package ru.mirea.lednevadd.mireaproject;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class BackgroundWorker extends Worker {

    private static final String TAG = "BackgroundWorker";

    public BackgroundWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Реализация вашей фоновой задачи здесь
        Log.d(TAG, "Фоновая задача выполнена!");
        return Result.success();
    }
}