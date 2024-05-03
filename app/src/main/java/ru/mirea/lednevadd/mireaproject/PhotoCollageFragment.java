package ru.mirea.lednevadd.mireaproject;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PhotoCollageFragment extends Fragment {

    private static final int REQUEST_CODE_PERMISSION = 200;
    private boolean is_permissions_granted = false;
    private List<Uri> imageUris = new ArrayList<>();
    private ActivityResultLauncher<Intent> cameraActivityResultLauncher;
    private Uri imageUri;
    private ImageView[] imageViews = new ImageView[4];

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_collage, container, false);

        imageViews[0] = view.findViewById(R.id.imageView1);
        imageViews[1] = view.findViewById(R.id.imageView2);
        imageViews[2] = view.findViewById(R.id.imageView3);
        imageViews[3] = view.findViewById(R.id.imageView4);

        Button buttonAddPhoto = view.findViewById(R.id.buttonAddPhoto);
        buttonAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnUpdatePhoto(v);
            }
        });

        cameraActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() != Activity.RESULT_OK)
                    return;

                imageUris.add(imageUri);
                for (int i = 0; i < imageUris.size(); i++) {
                    if (i < imageViews.length) {
                        imageViews[i].setImageURI(imageUris.get(i));
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        permissions_request();
    }

    private void permissions_request() {
        final boolean storage_permission = PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        final boolean camera_permission = PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA);

        if (storage_permission && camera_permission) {
            is_permissions_granted = true;
        } else {
            is_permissions_granted = false;
            requestPermissions(new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                is_permissions_granted = true;
            } else {
                is_permissions_granted = false;
            }
        }
    }

    public void OnUpdatePhoto(View v) {
        if (!is_permissions_granted) {
            permissions_request();
            return;
        }

        try {
            File file = createImageFile();
            String authorities = "ru.mirea.lednevadd.mireaproject.fileprovider";
            imageUri = FileProvider.getUriForFile(requireContext(), authorities, file);

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            cameraActivityResultLauncher.launch(cameraIntent);
        } catch (IOException e) {
            Log.d("", e.toString());
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyy_MM_dd____HH_mm_ss", Locale.ENGLISH).format(new Date());
        String imageFileName = "IMAGE_" + timeStamp + "_";
        File storageDirectory = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDirectory);
    }
}
