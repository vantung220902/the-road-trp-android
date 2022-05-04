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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.the_road_trip.R;
import com.example.the_road_trip.activity.MainActivity;
import com.example.the_road_trip.adapter.ImageAdapter;
import com.example.the_road_trip.api.APIPost;
import com.example.the_road_trip.api.Constant;
import com.example.the_road_trip.model.ResponseData;

import com.example.the_road_trip.utils.RealPathUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePostFragment extends Fragment {
    private static final int MY_REQUEST_CODE = 10;
    private List<Uri> mUri = new ArrayList<>();
    private EditText editText;
    private ImageView imgBtnUpload;
    private RecyclerView rcvImagePreviews;
    private MaterialButton btnSubmit, btnClear;
    private ImageView btnBack;
    private ProgressDialog progressDialog;
    private IUpdatePosts iUpdatePosts;
    private ImageAdapter imageAdapter;

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
                        mUri.add(uri);
                        imageAdapter.setData(mUri);
                    }
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View viewContainer = inflater.inflate(R.layout.fragment_create_post, container, false);
        initUI(viewContainer);
        imageAdapter = new ImageAdapter(getContext(), mUri);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rcvImagePreviews.setLayoutManager(linearLayoutManager);
        rcvImagePreviews.setAdapter(imageAdapter);
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
            mUri = null;
        });
        btnBack.setOnClickListener(view -> {
            ((MainActivity) getActivity()).getViewPager().setCurrentItem(0);
        });
        return viewContainer;
    }

    private void initUI(View view) {
        editText = view.findViewById(R.id.edit_upload_img_post);
        rcvImagePreviews = view.findViewById(R.id.rcv_preview_images);
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
        MediaType mediaType = MediaType.parse("");
        MultipartBody.Part[] fileParts = new MultipartBody.Part[mUri.size()];
        for (int i = 0; i < mUri.size(); i++) {
            File file = new File(mUri.get(i).getPath());
            RequestBody fileBody = RequestBody.create(mediaType, file);
            fileParts[i] = MultipartBody.Part.createFormData("images",file.getName(), fileBody);
        }
        APIPost.apiPOST.insertPost(requestBodyTitle, fileParts).
                enqueue(new Callback<ResponseData>() {
                    @Override
                    public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                        progressDialog.dismiss();
                        if (response.code() == 200) {
                            if (response.body().getSuccessful()) {
                                Snackbar snackbar = Snackbar
                                        .make(getView().getRootView(), response.body().getMessage(), Snackbar.LENGTH_LONG);
                                snackbar.show();
                                editText.setText("");
                                mUri = null;

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        iUpdatePosts.updateData();
                                    }
                                }, 500);
                            }
                        } else {
                            Log.d("Error", response.toString());
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseData> call, Throwable t) {
                        progressDialog.dismiss();
                        Snackbar snackbar = Snackbar
                                .make(getView().getRootView(),
                                        "Call Api Failed", Snackbar.LENGTH_LONG)
                                .setAction("Try again", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Snackbar snackbar1 = Snackbar.make(getView().getRootView(),
                                                "Loading...", Snackbar.LENGTH_SHORT);
                                        callApiCreatePost();
                                        snackbar1.show();
                                    }
                                });
                        snackbar.show();
                    }
                });
    }
}
