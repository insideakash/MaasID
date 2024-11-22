package com.telescope.maasid;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.telescope.maasid.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class HomeFragment extends Fragment {

    private static final String TAG = HomeFragment.class.getSimpleName();
    private static final int PERMISSION_STATE = 0;
    private static final int CAMERA_REQUEST = 1;
    private static final int GALLERY_REQUEST = 2;

    private Button btnCamera, btnGallery, btnDetect;
    private ImageView imgResult;
    private TextView txtPrediction;
    private Bitmap bitmap;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI elements
        btnCamera = view.findViewById(R.id.camerabtn);
        btnGallery = view.findViewById(R.id.gallarybtn);
        btnDetect = view.findViewById(R.id.detectbtn);
        imgResult = view.findViewById(R.id.resultImage);
        txtPrediction = view.findViewById(R.id.textViewPrediction);

        // Set click listeners
        btnCamera.setOnClickListener(this::onClick);
        btnGallery.setOnClickListener(this::onClick);
        btnDetect.setOnClickListener(this::onClick);

        checkPermissions();
    }

    private void onClick(View view) {
        int id = view.getId();
        if (id == R.id.detectbtn) {
            predict();  // Perform prediction when detect button is pressed
        } else if (id == R.id.camerabtn) {
            launchCamera();  // Launch camera
        } else if (id == R.id.gallarybtn) {
            openGallery();  // Open gallery
        }
    }

    private void launchCamera() {
        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), CAMERA_REQUEST);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    private void predict() {
        if (bitmap == null) {
            txtPrediction.setText("No image selected!");
            return;
        }
        bitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
        try {
            Model model = Model.newInstance(requireContext());
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.UINT8);
            TensorImage tensorImage = new TensorImage(DataType.UINT8);
            tensorImage.load(bitmap);
            ByteBuffer byteBuffer = tensorImage.getBuffer();

            inputFeature0.loadBuffer(byteBuffer);
            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
            model.close();

            float[] outputArray = outputFeature0.getFloatArray();
            Log.d("ModelOutput", "Output array length: " + outputArray.length);  // Log array length

            String result = getMax(outputArray);
            txtPrediction.setText(result);
        } catch (IOException e) {
            Log.e(TAG, "IOException " + e.getMessage());
        }
    }

    private String getMax(float[] outputs) {
        Log.d(TAG, "getMax( " + Arrays.toString(outputs) + ")");

        if (outputs.length < 4) {
            Log.e(TAG, "Error: Insufficient data in output array");
            return "Error: Insufficient data";
        }

        // Find the index of the maximum value
        int maxIndex = 0;
        float maxValue = outputs[0];

        for (int i = 1; i < outputs.length; i++) {
            if (outputs[i] > maxValue) {
                maxValue = outputs[i];
                maxIndex = i;
            }
        }

        // Return the corresponding label based on the index of the maximum value
        switch (maxIndex) {
            case 0:
                return "Bahu";
            case 1:
                return "Sitol";
            case 2:
                return "Eilish";
            case 3:
                return "Borali";
            case 4:
                return "Bhagon";
            case 5:
                return "Magur";
            case 6:
                return "Kuhi";
            case 7:
                return "Rou";
            case 8:
                return "Mirika";
            case 9:
                return "Rupchanda";
            default:
                return "Unknown result";
        }
    }

    private void checkPermissions() {
        String[] manifestPermissions = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        for (String permission : manifestPermissions) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions();
                break;
            }
        }
    }

    private void requestPermissions() {
        String[] manifestPermissions = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        ActivityCompat.requestPermissions(requireActivity(), manifestPermissions, PERMISSION_STATE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            bitmap = (Bitmap) data.getExtras().get("data");
            imgResult.setImageBitmap(bitmap);
        } else if (requestCode == GALLERY_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), selectedImage);
                imgResult.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
