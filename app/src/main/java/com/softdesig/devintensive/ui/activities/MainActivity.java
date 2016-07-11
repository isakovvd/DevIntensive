package com.softdesig.devintensive.ui.activities;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.softdesig.devintensive.R;
import com.softdesig.devintensive.data.managers.DataManager;
import com.softdesig.devintensive.utils.CircerAvatarDrawable;
import com.softdesig.devintensive.utils.ConstantManager;
import com.softdesig.devintensive.utils.ToastUtils;
import com.softdesig.devintensive.utils.ValidatorUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String TAG = ConstantManager.TAG + MainActivity.class.getSimpleName();

    @BindView(R.id.navigator_view)
    NavigationView mNavigationView;

    @BindView(R.id.user_profile_iv)
    ImageView mProfileImage;

    @BindView(R.id.navigation_drawer)
    DrawerLayout mNavigationDrawer;

    @BindView(R.id.main_coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.fab)
    FloatingActionButton mFab;

    @BindView(R.id.profile_placeholder)
    RelativeLayout mProfilePlaceHolder;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;

    @BindView(R.id.appbar_layout)
    AppBarLayout mAppBarLayout;

    @BindViews({R.id.phone_et, R.id.email_et, R.id.vk_et, R.id.git_et, R.id.about_et})
    List<EditText> mUserInfoViews;

    @BindViews({R.id.call_phone_iv, R.id.send_email_iv, R.id.show_vk_iv, R.id.show_git_iv})
    List<ImageView> mUserInfoImages;

    @BindViews({R.id.phone_ti, R.id.email_ti, R.id.vk_profile_ti, R.id.git_ti})
    List<TextInputLayout> mUserInfoTil;


    private DataManager mDataManager;
    private AppBarLayout.LayoutParams mAppBarParams = null;
    private int mCurrentEditMode = 0;
    private File mPhotoFile = null;
    private Uri mSelectedImage = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mDataManager = DataManager.getInstance();

        mFab.setOnClickListener(this);
        mProfilePlaceHolder.setOnClickListener(this);

        mUserInfoImages.get(ConstantManager.USER_PHONE_IV).setOnClickListener(this);
        mUserInfoImages.get(ConstantManager.USER_EMAIL_IV).setOnClickListener(this);
        mUserInfoImages.get(ConstantManager.USER_VK_IV).setOnClickListener(this);
        mUserInfoImages.get(ConstantManager.USER_GIT_IV).setOnClickListener(this);

        mProfileImage.setImageBitmap(CircerAvatarDrawable.getRoundedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.avatar)));

        mUserInfoViews.get(ConstantManager.USER_PHONE_ET).addTextChangedListener(new MyTextWatcher(mUserInfoViews.get(ConstantManager.USER_PHONE_ET), this, getWindow()));
        mUserInfoViews.get(ConstantManager.USER_EMAIL_ET).addTextChangedListener(new MyTextWatcher(mUserInfoViews.get(ConstantManager.USER_EMAIL_ET), this, getWindow()));
        mUserInfoViews.get(ConstantManager.USER_VK_ET).addTextChangedListener(new MyTextWatcher(mUserInfoViews.get(ConstantManager.USER_VK_ET), this, getWindow()));
        mUserInfoViews.get(ConstantManager.USER_GIT_ET).addTextChangedListener(new MyTextWatcher(mUserInfoViews.get(ConstantManager.USER_GIT_ET), this, getWindow()));

        setupToolBar();
        setupDrawer();

        Log.d(TAG, "onCreate");

        if (!isLoadUserInfoValue()) {
            initUserInfoValue();
        }

        loadUserInfoValue();

        Picasso.with(this)
                .load(mDataManager.getPreferencesManager().loadUserPhoto())
                .placeholder(R.drawable.user_profile)
                .into(mProfileImage);

        if (savedInstanceState == null) {

        } else {
            mCurrentEditMode = savedInstanceState.getInt(ConstantManager.EDIT_MODE_KEY, 0);
            changeEditMode(mCurrentEditMode);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (mNavigationDrawer.isDrawerOpen(GravityCompat.START)) {
            mNavigationDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                if (mCurrentEditMode == 0) {
                    changeEditMode(1);
                    mCurrentEditMode = 1;
                } else {
                    changeEditMode(0);
                    mCurrentEditMode = 0;
                }
                break;

            case R.id.photo_placeholder:
                showDialog(ConstantManager.LOAD_PROF_PHOTO);
                break;

            case R.id.call_phone_iv:
                callByPhoneNumber(mUserInfoViews.get(ConstantManager.USER_PHONE_ET).getText().toString());
                break;

            case R.id.send_email_iv:
                String[] addresses = {mUserInfoViews.get(ConstantManager.USER_EMAIL_ET).getText().toString()};
                sendEmail(addresses);
                break;

            case R.id.show_vk_iv:
                browseLink(mUserInfoViews.get(ConstantManager.USER_VK_ET).getText().toString());
                break;

            case R.id.show_git_iv:
                browseLink(mUserInfoViews.get(ConstantManager.USER_GIT_ET).getText().toString());
                break;
        }
    }

    /**
     * Событие onStart
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    /**
     * Событие onResume
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    /**
     * Событие onPause
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        saveUserInfoValue();
    }

    /**
     * событие onStop
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    /**
     * событие onDestroy
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ConstantManager.PIC_REQUEST:
                if (resultCode == RESULT_OK && data != null) {
                    mSelectedImage = data.getData();
                    insertProfileImage(mSelectedImage);
                }
                break;

            case ConstantManager.CAMERA_REQUEST:
                if (resultCode == RESULT_OK && mPhotoFile != null) {
                    mSelectedImage = Uri.fromFile(mPhotoFile);
                    insertProfileImage(mSelectedImage);
                }
                break;
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
        outState.putInt(ConstantManager.EDIT_MODE_KEY, mCurrentEditMode);
    }


    private void setupToolBar() {
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        mAppBarParams = (AppBarLayout.LayoutParams) mCollapsingToolbar.getLayoutParams();

        if (actionBar != null) {
            actionBar.setTitle(R.string.app_name);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    private void setupDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigator_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                ToastUtils.showShortMessage(item.toString(), getApplicationContext());
                item.setCheckable(true);
                mNavigationDrawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });
    }


    private void changeEditMode(int mode) {
        if (mode == 1) {
            mFab.setImageResource(R.drawable.ic_done_black_24dp);
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(true);
                userValue.setFocusable(true);
                userValue.setFocusableInTouchMode(true);
            }

            showProfilePlaceHolder();
            lockToolbar();
            mCollapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);
            setFocusForInput(mUserInfoViews.get(ConstantManager.USER_PHONE_ET));
        } else {
            mFab.setImageResource(R.drawable.ic_create_black_24dp);
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(false);
                userValue.setFocusable(false);
                userValue.setFocusableInTouchMode(false);
                saveUserInfoValue();
            }

            hideProfilePlaceHolder();
            unLockToolbar();
            mCollapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.color_white));
        }
    }


    private void setFocusForInput(final EditText field) {
        field.post(new Runnable() {
            @Override
            public void run() {
                field.requestFocus();
                field.onKeyUp(KeyEvent.KEYCODE_DPAD_CENTER, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_CENTER));
            }
        });
    }


    private void initUserInfoValue() {
        List<String> userData = new ArrayList<>();
        userData.add(getResources().getString(R.string.string_phone_info));
        userData.add(getResources().getString(R.string.string_mail_info));
        userData.add(getResources().getString(R.string.string_vk_info));
        userData.add(getResources().getString(R.string.string_github_info));
        userData.add(getResources().getString(R.string.string_about));
        mDataManager.getPreferencesManager().saveUserProfileData(userData);
    }


    private boolean isLoadUserInfoValue() {
        boolean isLoadUserInfo = false;
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();
        for (int index = 0; index < userData.size(); index++) {
            if (!userData.get(index).isEmpty() && !userData.get(index).equals("null")) {
                isLoadUserInfo = true;
            }
        }
        return isLoadUserInfo;
    }


    private void loadUserInfoValue() {
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();
        for (int index = 0; index < userData.size(); index++) {
            mUserInfoViews.get(index).setText(userData.get(index));
        }
    }


    private void saveUserInfoValue() {
        List<String> userData = new ArrayList<>();
        for (EditText userFieldView : mUserInfoViews) {
            userData.add(userFieldView.getText().toString());
        }
        mDataManager.getPreferencesManager().saveUserProfileData(userData);
    }


    private void loadPhotoGallery() {
        Intent takeGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        takeGalleryIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(takeGalleryIntent, getString(R.string.chose_gallery_photo)), ConstantManager.PIC_REQUEST);
    }


    private void loadPhotoCamera() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent takeCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                mPhotoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (mPhotoFile != null) {
                takeCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
                startActivityForResult(takeCaptureIntent, ConstantManager.CAMERA_REQUEST);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, ConstantManager.CAMERA_REQUEST_PERMISSION_CODE);

            showGiveAllowPermissionSnackBar();
        }
    }


    private void showGiveAllowPermissionSnackBar() {
        Snackbar.make(mCoordinatorLayout, R.string.give_permission_message, Snackbar.LENGTH_LONG)
                .setAction(R.string.allow_message, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openApplicationSettings();
                    }
                }).show();
    }


    private void callByPhoneNumber(String phoneNumber) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
            startActivity(dialIntent);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.CALL_PHONE
            }, ConstantManager.CALL_PHONE_REQUEST_PERMISSION_CODE);
            showGiveAllowPermissionSnackBar();
        }
    }


    public void sendEmail(String[] addresses) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    public void browseLink(String link) {
        Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + link));
        startActivity(browseIntent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ConstantManager.CAMERA_REQUEST_PERMISSION_CODE && grantResults.length >= 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                int ii = 0;
            }
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                int ii = 0;
            }
        }
        if (requestCode == ConstantManager.CALL_PHONE_REQUEST_PERMISSION_CODE && grantResults.length >= 1) {
            int ii = 0;
        }
    }


    private void hideProfilePlaceHolder() {
        mProfilePlaceHolder.setVisibility(View.GONE);
    }


    private void showProfilePlaceHolder() {
        mProfilePlaceHolder.setVisibility(View.VISIBLE);
    }


    private void lockToolbar() {
        mAppBarLayout.setExpanded(true, true);
        mAppBarParams.setScrollFlags(0);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }


    private void unLockToolbar() {
        mAppBarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case ConstantManager.LOAD_PROF_PHOTO:
                String[] selectItems = {getString(R.string.user_profile_dialog_gallery), getString(R.string.user_profile_dialog_camera), getString(R.string.user_profile_dialog_cancel)};
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.user_profile_placeholder_image_title));
                builder.setItems(selectItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int choiceItem) {
                        switch (choiceItem) {
                            case 0:
                                loadPhotoGallery();
                                break;

                            case 1:
                                loadPhotoCamera();
                                break;

                            case 2:
                                dialog.cancel();
                                break;
                        }
                    }
                });
                return builder.create();
            default:
                return null;
        }
    }


    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, image.getAbsolutePath());
        this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        return image;
    }


    private void insertProfileImage(Uri selectedImage) {
        Picasso.with(this)
                .load(selectedImage)
                .into(mProfileImage);
        mDataManager.getPreferencesManager().saveUserPhoto(selectedImage);
    }


    public void openApplicationSettings() {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        startActivityForResult(appSettingsIntent, ConstantManager.PERMISSION_REQUEST_SETTINGS_CODE);
    }


    private class MyTextWatcher implements TextWatcher {
        private View view;
        boolean skipOnChange = false;
        private ValidatorUtils validateUtils;

        private MyTextWatcher(View view, Context context, Window window) {
            this.view = view;
            validateUtils = new ValidatorUtils(mUserInfoViews, mUserInfoTil, context, window);
        }


        private String cutNoNeedPathAtUrl(String url) {
            String myUrl = url;
            return myUrl.replace("http://", "").replace("https://", "").replace("http:// www.", "").replace("www.", "");
        }


        private void checkUrlFieldOnNoNeedPath(int indexFieldAtUserInfoViews) {
            String originString = mUserInfoViews.get(indexFieldAtUserInfoViews).getText().toString();
            String cutString = cutNoNeedPathAtUrl(originString);
            if (originString != cutString) {
                mUserInfoViews.get(indexFieldAtUserInfoViews).setText(cutString);
            }
        }

        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        }


        public void afterTextChanged(Editable editable) {
            if (skipOnChange)
                return;
            skipOnChange = true;
            try {
                int indexFieldAtUserInfoViews = 0;
                switch (view.getId()) {
                    case R.id.phone_et:
                        indexFieldAtUserInfoViews = ConstantManager.USER_PHONE_ET;
                        break;

                    case R.id.email_et:
                        indexFieldAtUserInfoViews = ConstantManager.USER_EMAIL_ET;
                        break;

                    case R.id.vk_et:
                        indexFieldAtUserInfoViews = ConstantManager.USER_VK_ET;
                        checkUrlFieldOnNoNeedPath(indexFieldAtUserInfoViews);
                        break;

                    case R.id.git_et:
                        indexFieldAtUserInfoViews = ConstantManager.USER_GIT_ET;
                        checkUrlFieldOnNoNeedPath(indexFieldAtUserInfoViews);
                        break;
                }

                validateUtils.isValidate(indexFieldAtUserInfoViews);

            } catch (NumberFormatException e) {
                e.printStackTrace();
            } finally {
                skipOnChange = false;
            }
        }
    }
}