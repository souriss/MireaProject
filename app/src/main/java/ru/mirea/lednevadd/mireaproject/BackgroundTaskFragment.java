package ru.mirea.lednevadd.mireaproject;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class BackgroundTaskFragment extends Fragment {

    private static final String TAG = "BackgroundTaskFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_background_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Выполнение вашей фоновой задачи при создании фрагмента
        performBackgroundTask();
    }

    private void performBackgroundTask() {
        // Создание запроса на выполнение фоновой задачи
        OneTimeWorkRequest backgroundTaskRequest = new OneTimeWorkRequest.Builder(BackgroundWorker.class).build();

        // Добавление запроса в очередь для выполнения
        WorkManager.getInstance(requireContext()).enqueue(backgroundTaskRequest);
    }
}