package com.example.the_road_trip.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.the_road_trip.R;
import com.example.the_road_trip.activity.MainActivity;
import com.example.the_road_trip.api.APIPost;
import com.example.the_road_trip.api.Constant;
import com.example.the_road_trip.model.ResponseData;

import com.example.the_road_trip.utils.RealPathUtil;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePostFragment extends Fragment {
    private static final int MY_REQUEST_CODE = 10;
    private Uri mUri;
    private EditText editText;
    private ImageView imgBtnUpload, imgPreview;
    private MaterialButton btnSubmit, btnClear;
    private ImageView btnBack;
    private ProgressDialog progressDialog;
    private IUpdatePosts iUpdatePosts;

    public interface IUpdatePosts {
        void updateData();
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        iUpdatePosts = (IUpdatePosts) getActivity();
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data == null) return;
                        Uri uri = data.getData();
                        mUri = uri;
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                            imgPreview.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View viewContainer = inflater.inflate(R.layout.fragment_create_post, container, false);
        initUI(viewContainer);
        imgBtnUpload.setOnClickListener(view -> {
            onClickRequestPermission();
        });
        btnSubmit.setOnClickListener(view -> {
            if (mUri != null) callApiCreatePost();
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                btnClear.setEnabled(true);
            }
        });
        btnClear.setOnClickListener(view -> {
            editText.setText("");
            imgPreview.setImageResource(0);
        });
        btnBack.setOnClickListener(view -> {
            ((MainActivity) getActivity()).getViewPager().setCurrentItem(0);
        });
        return viewContainer;
    }

    private void initUI(View view) {
        editText = view.findViewById(R.id.edit_upload_img_post);
        imgPreview = view.findViewById(R.id.review_photo_upload);
        imgBtnUpload = view.findViewById(R.id.img_btn_upload_img);
        btnSubmit = view.findViewById(R.id.btn_submit_post);
        btnClear = view.findViewById(R.id.btn_clear_all);
        btnBack = view.findViewById(R.id.btn_back_post);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please wait...");
    }

    private void onClickRequestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            openGallery();
            return;
        }
        if (getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permission, MY_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            }
        }

    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    private void callApiCreatePost() {
        progressDialog.show();
        String title = editText.getText().toString().trim();
        RequestBody requestBodyTitle = RequestBody.create(MediaType.parse("multipart/form-data"), title);
        String strRealPath = RealPathUtil.getRealPath(getContext(), mUri);
        File file = new File(strRealPath);
        RequestBody requestBodyAvt = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part mulPartBodyAvt = MultipartBody.Part.createFormData(Constant.KEY_IMAGE,
                file.getName(), requestBodyAvt);
        APIPost.apiPOST.insertPost(requestBodyTitle, mulPartBodyAvt).
                enqueue(new Callback<ResponseData>() {
                    @Override
                    public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                        progressDialog.dismiss();
                        if (response.code() == 200) {
                            if (response.body().getSuccessful()) {
                                Toast.makeText(getContext(), response.body().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                                editText.setText("");
                                imgPreview.setImageResource(0);
                                iUpdatePosts.updateData();
                            }
                        } else {
                            Log.d("Error", response.toString());
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseData> call, Throwable t) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Call Api Fail", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
