
package com.example.the_road_trip.activity.story;

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
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.the_road_trip.R;
import com.example.the_road_trip.activity.MainActivity;
import com.example.the_road_trip.activity.SplashActivity;
import com.example.the_road_trip.api.APIStory;
import com.example.the_road_trip.api.Constant;
import com.example.the_road_trip.model.ModelLink;
import com.example.the_road_trip.model.ResponseData;
import com.example.the_road_trip.utils.RealPathUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateStoryActivity extends AppCompatActivity {
    private static final int MY_REQUEST_CODE = 11;
    private Uri mUri;
    private EditText editText;
    private ImageView imgBtnUpload, imgPreview;
    private MaterialButton btnSubmit, btnClear;
    private ProgressDialog progressDialog;
    private ImageButton btnBack;
    private Socket mSocket;

    {
        try {
            mSocket = IO.socket(Constant.URL_SERVER);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
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
        setContentView(R.layout.activity_create_story);
        initUI();
        mSocket.connect();
        mSocket.emit("join_room", "Post");
        imgBtnUpload.setOnClickListener(view -> {
            onClickRequestPermission();
        });
        btnSubmit.setOnClickListener(view -> {
            if (mUri != null) callApiCreateStory();
        });
        btnBack.setOnClickListener(view -> {
            finish();
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
    }

    private void initUI() {
        editText = findViewById(R.id.edit_upload_img_story);
        imgPreview = findViewById(R.id.review_photo_upload_story);
        imgBtnUpload = findViewById(R.id.img_btn_upload_img_story);
        btnSubmit = findViewById(R.id.btn_submit_story);
        btnClear = findViewById(R.id.btn_clear_all_story);
        btnBack = findViewById(R.id.id_back_header_story);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
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

    private void callApiCreateStory() {
        progressDialog.show();
        String title = editText.getText().toString().trim();
        RequestBody requestBodyTitle = RequestBody.create(MediaType.parse("multipart/form-data"), title);
        String strRealPath = RealPathUtil.getRealPath(this, mUri);
        File file = new File(strRealPath);
        RequestBody requestBodyAvt = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part mulPartBodyAvt = MultipartBody.Part.createFormData(Constant.KEY_IMAGE,
                file.getName(), requestBodyAvt);
        APIStory.apiStory.insertStory(requestBodyTitle, mulPartBodyAvt).
                enqueue(new Callback<ResponseData>() {
                    @Override
                    public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                        progressDialog.dismiss();
                        if (response.code() == 200) {
                            if (response.body().getSuccessful()) {
                                mSocket.emit("send_post", "Story");
                                Snackbar snackbar = Snackbar
                                        .make(findViewById(android.R.id.content).getRootView(),
                                                response.body().getMessage(), Snackbar.LENGTH_LONG);
                                snackbar.show();
                                Intent intent = new Intent(CreateStoryActivity.this, SplashActivity.class);
                                Bundle bundle = new Bundle();
                                ModelLink modelLink = new ModelLink("Create Story Successfully", MainActivity.class);
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
                    public void onFailure(Call<ResponseData> call, Throwable t) {
                        progressDialog.dismiss();
                        SnackbarCustomer("Call Api Fail");
                    }
                });
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
                        callApiCreateStory();
                        snackbar1.show();
                    }
                });
        snackbar.show();
    }
}