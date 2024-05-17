package ru.mirea.lednevadd.mireaproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private EditText editTextSuperheroName;
    private EditText editTextSuperheroPower;
    private RadioGroup radioGroupUniverse;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Инициализация элементов управления
        editTextSuperheroName = view.findViewById(R.id.editTextSuperheroName);
        editTextSuperheroPower = view.findViewById(R.id.editTextSuperheroPower);
        radioGroupUniverse = view.findViewById(R.id.radioGroupUniverse);
        Button buttonSaveProfile = view.findViewById(R.id.buttonSaveProfile);

        // Инициализация SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("profile_data", Context.MODE_PRIVATE);

        // Загрузка сохраненных данных, если они есть
        loadProfileData();

        // Обработчик нажатия на кнопку "Сохранить"
        buttonSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileData();
            }
        });

        return view;
    }

    // Метод для загрузки сохраненных данных из SharedPreferences
    private void loadProfileData() {
        String superheroName = sharedPreferences.getString("superhero_name", "");
        String superheroPower = sharedPreferences.getString("superhero_power", "");
        int universeSelection = sharedPreferences.getInt("universe_selection", R.id.radioButtonMarvel);

        editTextSuperheroName.setText(superheroName);
        editTextSuperheroPower.setText(superheroPower);
        radioGroupUniverse.check(universeSelection);
    }

    // Метод для сохранения данных в SharedPreferences
    private void saveProfileData() {
        String superheroName = editTextSuperheroName.getText().toString();
        String superheroPower = editTextSuperheroPower.getText().toString();
        int universeSelection = radioGroupUniverse.getCheckedRadioButtonId();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("superhero_name", superheroName);
        editor.putString("superhero_power", superheroPower);
        editor.putInt("universe_selection", universeSelection);
        editor.apply();
    }
}
