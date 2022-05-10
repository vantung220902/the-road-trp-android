package com.example.the_road_trip.activity.auth;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.the_road_trip.R;
import com.example.the_road_trip.activity.MainActivity;
import com.example.the_road_trip.activity.SplashActivity;
import com.example.the_road_trip.api.APIPost;
import com.example.the_road_trip.api.APIUser;
import com.example.the_road_trip.api.ApiAuth;
import com.example.the_road_trip.api.Constant;
import com.example.the_road_trip.model.ModelLink;
import com.example.the_road_trip.model.ResponseData;
import com.example.the_road_trip.model.User.ReturnUpdateUser;
import com.example.the_road_trip.model.User.User;
import com.example.the_road_trip.shared_preference.DataLocalManager;
import com.example.the_road_trip.utils.RealPathUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateUserActivity extends AppCompatActivity {
    private static final int MY_REQUEST_CODE = 10;
    private Uri mUri;
    private EditText editFullName, editAddress;
    private ImageView imgBtnUpload, imgPreview;
    private MaterialButton btnSubmit, btnClear;
    private ImageView btnBack;
    private ProgressDialog progressDialog;
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
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            imgPreview.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);
        initUI();

        imgBtnUpload.setOnClickListener(view -> {
            onClickRequestPermission();
        });
        btnSubmit.setOnClickListener(view -> {
            if (mUri != null) callApiUpdateUser();
        });
        editFullName.addTextChangedListener(new TextWatcher() {
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
        editAddress.addTextChangedListener(new TextWatcher() {
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
            editFullName.setText("");
            editAddress.setText("");
            imgPreview.setImageResource(0);
        });
        btnBack.setOnClickListener(view -> {
            clearUI();
        });

    }

    private void onClickRequestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            openGallery();
            return;
        }
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
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

    private void callApiUpdateUser() {
        progressDialog.show();
        String fullName = editFullName.getText().toString().trim();
        String address = editAddress.getText().toString().trim();
        RequestBody requestBodyFullName = RequestBody.create(MediaType.parse("multipart/form-data"), fullName);
        RequestBody requestBodyAddress = RequestBody.create(MediaType.parse("multipart/form-data"), address);
        String strRealPath = RealPathUtil.getRealPath(UpdateUserActivity.this, mUri);
        File file = new File(strRealPath);
        RequestBody requestBodyAvt = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part mulPartBodyAvt = MultipartBody.Part.createFormData(Constant.KEY_IMAGE,
                file.getName(), requestBodyAvt);
        APIUser.apiUser.updateUser(requestBodyFullName, requestBodyAddress, mulPartBodyAvt).
                enqueue(new Callback<ReturnUpdateUser>() {
                    @Override
                    public void onResponse(Call<ReturnUpdateUser> call, Response<ReturnUpdateUser> response) {
                        progressDialog.dismiss();
                        if (response.code() == 200) {
                            if (response.body().getSuccessful()) {
                                Snackbar snackbar = Snackbar
                                        .make(findViewById(android.R.id.content).getRootView(),
                                                response.body().getMessage(), Snackbar.LENGTH_LONG);
                                snackbar.show();
                                DataLocalManager.setUserCurrent(response.body().getData());
                                Intent intent = new Intent(UpdateUserActivity.this, SplashActivity.class);
                                Bundle bundle = new Bundle();
                                ModelLink modelLink = new ModelLink(null, MainActivity.class);
                                bundle.putSerializable("screen_next", modelLink);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            SnackbarCustomer("Call Api Fail");
                            Log.d("Error", response.toString());
                        }

                    }

                    @Override
                    public void onFailure(Call<ReturnUpdateUser> call, Throwable t) {
                        progressDialog.dismiss();
                        SnackbarCustomer("Call Api Fail");
                    }
                });
    }

    private void initUI() {
        editFullName = findViewById(R.id.edit_fullName_update_user);
        editAddress = findViewById(R.id.edit_address_update_user);
        imgBtnUpload = findViewById(R.id.btn_upload_update_user);
        imgPreview = findViewById(R.id.review_img_update_user);
        btnSubmit = findViewById(R.id.btn_submit_update_user);
        btnClear = findViewById(R.id.btn_clear_all_update_user);
        btnBack = findViewById(R.id.btn_back_update_user);
        editFullName.setHint(DataLocalManager.getUserCurrent().getFullName());
        editAddress.setHint(DataLocalManager.getUserCurrent().getAddress());
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

    }

    private void clearUI() {
        editFullName.setText("");
        editAddress.setText("");
        imgPreview.setImageResource(0);
    }

    private void SnackbarCustomer(String str) {
        Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content).getRootView(),
                        str, Snackbar.LENGTH_LONG)
                .setAction("Try again", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar snackbar1 = Snackbar.make(findViewById(android.R.id.content).getRootView(),
                                "Loading...", Snackbar.LENGTH_SHORT);
                        callApiUpdateUser();
                        snackbar1.show();
                    }
                });
        snackbar.show();
    }
}