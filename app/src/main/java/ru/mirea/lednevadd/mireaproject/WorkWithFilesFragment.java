package ru.mirea.lednevadd.mireaproject;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class WorkWithFilesFragment extends Fragment {

    private EditText editText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_work_with_files, container, false);

        editText = view.findViewById(R.id.editTextText);

        // Обработчик нажатия на FAB
        view.findViewById(R.id.floatingActionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Обработка нажатия на FAB
                // Шифрование и сохранение
                String content = editText.getText().toString();
                String encryptedText = encryptText(content);
                saveTextToFile(encryptedText);
            }
        });

        // Обработчик нажатия на кнопку "Расшифровать"
        view.findViewById(R.id.buttonDecrypt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Обработка нажатия на кнопку "Расшифровать"
                // Получение текста из файла и дешифрование
                String encryptedText = getTextFromFile();
                String decryptedText = decryptText(encryptedText);
                editText.setText(decryptedText);
            }
        });

        return view;
    }

    // Метод для сохранения текста в файл
    private void saveTextToFile(String text) {
        String fileName = "encrypted_text.txt";
        try {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File file = new File(path, fileName);
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(text.getBytes());
            outputStream.close();
            Toast.makeText(getActivity(), "Файл успешно сохранен", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Ошибка сохранения файла", Toast.LENGTH_SHORT).show();
        }
    }

    // Метод для чтения текста из файла
    private String getTextFromFile() {
        String fileName = "encrypted_text.txt";
        StringBuilder text = new StringBuilder();
        try {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File file = new File(path, fileName);
            FileInputStream inputStream = new FileInputStream(file);
            int character;
            while ((character = inputStream.read()) != -1) {
                text.append((char) character);
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Ошибка чтения файла", Toast.LENGTH_SHORT).show();
        }
        return text.toString();
    }

    // Метод для шифрования текста
    private String encryptText(String text) {
        StringBuilder encryptedText = new StringBuilder();
        int shift = 3; // Сдвиг для шифра Цезаря
        for (int i = 0; i < text.length(); i++) {
            char currentChar = text.charAt(i);
            if (Character.isLetter(currentChar)) {
                char encryptedChar = (char) (currentChar + shift);
                if ((Character.isLowerCase(currentChar) && encryptedChar > 'z')
                        || (Character.isUpperCase(currentChar) && encryptedChar > 'Z')) {
                    encryptedChar = (char) (currentChar - (26 - shift));
                }
                encryptedText.append(encryptedChar);
            } else {
                encryptedText.append(currentChar);
            }
        }
        return encryptedText.toString();
    }

    private String decryptText(String encryptedText) {
        StringBuilder decryptedText = new StringBuilder();
        int shift = 3; // Сдвиг для шифра Цезаря
        for (int i = 0; i < encryptedText.length(); i++) {
            char currentChar = encryptedText.charAt(i);
            if (Character.isLetter(currentChar)) {
                char decryptedChar = (char) (currentChar - shift);
                if ((Character.isLowerCase(currentChar) && decryptedChar < 'a')
                        || (Character.isUpperCase(currentChar) && decryptedChar < 'A')) {
                    decryptedChar = (char) (currentChar + (26 - shift));
                }
                decryptedText.append(decryptedChar);
            } else {
                decryptedText.append(currentChar);
            }
        }
        return decryptedText.toString();
    }
}
