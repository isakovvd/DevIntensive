package com.softdesig.devintensive.ui.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.softdesig.devintensive.R;
import com.softdesig.devintensive.data.managers.DataManager;
import com.softdesig.devintensive.utils.ConstantManager;
import com.softdesig.devintensive.utils.RoundedAvatarDrawable;
import com.softdesig.devintensive.utils.ValidatorUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = ConstantManager.TAG + "MainActivity";

    private DataManager mDataManager;
    private int mCurrentEditMode = ConstantManager.EDIT_MODE_OFF;

    @BindView(R.id.main_coordinator_container)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.navigation_drawer)
    DrawerLayout mNavigationDrawer;

    @BindView(R.id.drawer_layout)
    NavigationView mNavigationView;

    @BindView(R.id.fab)
    FloatingActionButton mFab;

    @BindView(R.id.profile_placeholder)
    RelativeLayout mProfilePlaceholder;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;

    @BindView(R.id.appbar_layout)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.photo_placeholder)
    ImageView mProfileImage;

    @BindView(R.id.call_phone_iv)
    ImageView mCallImg;

    @BindView(R.id.send_email_iv)
    ImageView mMailImg;

    @BindView(R.id.show_vk_iv)
    ImageView mVkImg;

    @BindView(R.id.show_git_iv)
    ImageView mGitImg;

    @BindView(R.id.phone_et)
    EditText mUserPhone;

    @BindView(R.id.email_et)
    EditText mUserMail;

    @BindView(R.id.vk_et)
    EditText mUserVk;

    @BindView(R.id.git_et)
    EditText mUserGit;

    @BindView(R.id.about_et)
    EditText mUserBio;

    @BindView(R.id.phone_ti)
    TextInputLayout mUserPhoneLayout;

    @BindView(R.id.email_ti)
    TextInputLayout mUserMailLayout;

    @BindView(R.id.vk_profile_ti)
    TextInputLayout mUserVkLayout;

    @BindView(R.id.git_ti)
    TextInputLayout mUserGitLayout;

    // в данном случае не лучший вариант (возможно инициирует повторный findById)
    @BindViews({R.id.phone_et, R.id.email_et, R.id.vk_et, R.id.git_et, R.id.about_et})
    List<EditText> mUserInfoViews;

    @BindView(R.id.user_rating_txt)
    TextView mUserValueRating;
    @BindView(R.id.user_codelines_txt)
    TextView mUserValueCodeLines;
    @BindView(R.id.user_projects_txt)
    TextView mUserValueProjects;

    @BindViews({R.id.user_rating_txt, R.id.user_codelines_txt, R.id.user_projects_txt})
    List<TextView> mUserValueViews;

    private AppBarLayout.LayoutParams mAppBarParams = null;
    private File mPhotoFile = null;
    private Uri mSelectedImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        ButterKnife.bind(this);

        mDataManager = DataManager.getInstance();

        mCallImg.setOnClickListener(this);
        mMailImg.setOnClickListener(this);
        mVkImg.setOnClickListener(this);
        mGitImg.setOnClickListener(this);

        mFab.setOnClickListener(this);
        mProfilePlaceholder.setOnClickListener(this);

        setupToolbar();
        setupDrawer();
        initUserFields();
        initUserInfoValues();

        initProfileImage();

        if (savedInstanceState == null) {

        } else {
            mCurrentEditMode = savedInstanceState
                    .getInt(ConstantManager.EDIT_MODE_KEY, ConstantManager.EDIT_MODE_OFF);
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
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");

        if (validateUserInfoValues(false)) {
            saveUserFields();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                if (mCurrentEditMode == ConstantManager.EDIT_MODE_OFF) {
                    showShackbar(getString(R.string.snackbar_msg_edit_on));
                    changeEditMode(ConstantManager.EDIT_MODE_ON);
                    mCurrentEditMode = ConstantManager.EDIT_MODE_ON;
                } else {
                    if (validateUserInfoValues(true)) {
                        showShackbar(getString(R.string.snackbar_msg_data_saved));
                        changeEditMode(ConstantManager.EDIT_MODE_OFF);
                        mCurrentEditMode = ConstantManager.EDIT_MODE_OFF;
                    }
                }
                break;

            case R.id.profile_placeholder:
                showDialog(ConstantManager.LOAD_PROFILE_PHOTO);
                break;

            case R.id.call_phone_iv:
                if (mCurrentEditMode == ConstantManager.EDIT_MODE_OFF) {
                    callPhone(mUserPhone.getText().toString());
                }
                break;

            case R.id.send_email_iv:
                if (mCurrentEditMode == ConstantManager.EDIT_MODE_OFF) {
                    sendMail(mUserPhone.getText().toString());
                }
                break;

            case R.id.show_vk_iv:
                if (mCurrentEditMode == ConstantManager.EDIT_MODE_OFF) {
                    browseUrl("https://" + mUserVk.getText().toString());
                }
                break;

            case R.id.show_git_iv:
                if (mCurrentEditMode == ConstantManager.EDIT_MODE_OFF) {
                    browseUrl("https://" + mUserGit.getText().toString());
                }
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ConstantManager.EDIT_MODE_KEY, mCurrentEditMode);
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawer != null && mNavigationDrawer.isDrawerOpen(GravityCompat.START)) {
            mNavigationDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Отображает панель Shackbar, содержащую текстовое сообщение
     *
     * @param message текст сообщения
     */
    private void showShackbar(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    /**
     * Выполняет инициализацию панели Toolbar
     */
    private void setupToolbar() {
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();

        mAppBarParams = (AppBarLayout.LayoutParams) mCollapsingToolbar.getLayoutParams();

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Выполняет инициализацию фотографии в профиле пользователя.
     * Загружает фотографию с сервера. В случае неудачи использует локальное изображение.
     */
    private void initProfileImage() {
        String photoURL = getIntent().getStringExtra(ConstantManager.USER_PHOTO_URL_KEY);
        final Uri photoLocalUri = mDataManager.getPreferencesManager().loadUserPhoto();

        Picasso.with(MainActivity.this)
                .load(photoLocalUri)
                .placeholder(R.drawable.user_bg)
                .into(mProfileImage);

        Call<ResponseBody> call = mDataManager.getImage(photoURL);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                    if (bitmap != null) {
                        mProfileImage.setImageBitmap(bitmap);
                        try {
                            File file = createImageFileFromBitmap("user_photo", bitmap);
                            if (file != null) {
                                mDataManager.getPreferencesManager()
                                        .saveUserPhoto(Uri.fromFile(file));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showShackbar("Не удалось загрузить фотографию пользователя");
            }
        });
    }


    /**
     * Загружает фотографию из профиля пользователя на сервер
     *
     * @param photoFile представление файла фотографии
     */
    private void uploadPhoto(File photoFile) {
        Call<ResponseBody> call = mDataManager.uploadPhoto(photoFile);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("TAG", "Upload success");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showShackbar("Не удалось загрузить фотографию на сервер");
                //Log.e("Upload error", t.getMessage());
            }
        });
    }

    /**
     * Выполняет инициализацию выдвижной панели навигационного меню
     */
    private void setupDrawer() {
        if (mNavigationView != null) {

            View headerView = mNavigationView.getHeaderView(0);

            List<String> userNames = mDataManager.getPreferencesManager().loadUserName();
            ((TextView) headerView.findViewById(R.id.user_name_tv))
                    .setText(String.format("%s %s", userNames.get(1), userNames.get(0)));
            ((TextView) headerView.findViewById(R.id.user_email_tv))
                    .setText(mUserMail.getText());

            updateAvatar();

            mNavigationView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem item) {
                            showShackbar(item.getTitle().toString());
                            item.setChecked(true);
                            mNavigationDrawer.closeDrawer(GravityCompat.START);
                            return false;
                        }
                    });
        }
    }

    /**
     * Изменяет аватар пользователя на выдвижной панеле
     * Загружает аватар с сервера. В случае неудачи использует локальное изображение.
     */
    public void updateAvatar() {

        final ImageView avatarImg = (ImageView)
                mNavigationView.getHeaderView(0).findViewById(R.id.user_avatar_iv);

        String avatarUrl = getIntent().getStringExtra(ConstantManager.USER_AVATAR_URL_KEY);
        Uri avatarLocalUri = mDataManager.getPreferencesManager().loadUserAvatar();

        Call<ResponseBody> call = mDataManager.getImage(avatarUrl);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());

                    if (bitmap != null) {
                        RoundedAvatarDrawable avatarDrawable = new RoundedAvatarDrawable(bitmap);
                        avatarDrawable.setAntiAlias(true);
                        avatarImg.setImageDrawable(avatarDrawable);

                        try {
                            File file = createImageFileFromBitmap("user_avatar", bitmap);

                            if (file != null) {
                                mDataManager.getPreferencesManager()
                                        .saveUserAvatar(Uri.fromFile(file));
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showShackbar("Не удалось загрузить аватар пользователя");
            }
        });
    }


    /**
     * Получение результата из другой Activity (фото из камеры или галлереи)
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ConstantManager.REQUEST_GALLERY_PICTURE:
                if (resultCode == RESULT_OK && data != null) {
                    mSelectedImage = data.getData();

                    insertProfileImage(mSelectedImage);
                }
                break;
            case ConstantManager.REQUEST_CAMERA_PICTURE:
                if (resultCode == RESULT_OK && mPhotoFile != null) {
                    mSelectedImage = Uri.fromFile(mPhotoFile);

                    insertProfileImage(mSelectedImage);
                }
                break;
        }
    }

    /**
     * Переключает режим редактирования
     *
     * @param mode если 1 - режим редактирования, если 0 - режим просмотра
     */
    private void changeEditMode(int mode) {
        if (mode == ConstantManager.EDIT_MODE_ON) {
            mFab.setImageResource(R.drawable.ic_done_black_24dp);

            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(true);
                userValue.setFocusable(true);
                userValue.setFocusableInTouchMode(true);
            }
            setFieldFocus(mUserPhone, false);

            showProfilePlaceholder();
            lockToolbar();
            mCollapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);

        } else {
            mFab.setImageResource(R.drawable.ic_create_black_24dp);

            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(false);
                userValue.setFocusable(false);
                userValue.setFocusableInTouchMode(false);
            }

            hideProfilePlaceholder();
            unlockToolbar();
            mCollapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.color_white));

            saveUserFields();
        }
    }

    /**
     * Осуществляет валидацию полей профайла пользователя. Отображает соответствующие сообщения.
     *
     * @param showMsg разрешить (запретить) отображение сообщений об ошибках валидации
     * @return true, если валидация пройдена и false - в противном случае
     */
    private boolean validateUserInfoValues(boolean showMsg) {

        String userPhone = ValidatorUtils.getValidatedPhone(mUserPhone.getText().toString());
        if (userPhone != null) {
            mUserPhone.setText(ValidatorUtils.formatRegularPhone(userPhone));
            mUserPhoneLayout.setErrorEnabled(false);
        } else {
            if (showMsg) {
                setFieldFocus(mUserPhone, true);
                mUserPhoneLayout.setError(getString(R.string.validation_msg_phone));
            }
            return false;
        }

        String userMail = ValidatorUtils.getValidatedEmail(mUserMail.getText().toString());
        if (userMail != null) {
            mUserMail.setText(userMail);
            mUserMailLayout.setErrorEnabled(false);
        } else {
            if (showMsg) {
                setFieldFocus(mUserMail, true);
                mUserMailLayout.setError(getString(R.string.validation_msg_email));
            }
            return false;
        }

        String userVk = ValidatorUtils.getValidatedVkUrl(mUserVk.getText().toString());
        if (userVk != null) {
            mUserVk.setText(userVk);
            mUserVkLayout.setErrorEnabled(false);
        } else {
            if (showMsg) {
                setFieldFocus(mUserVk, true);
                mUserVkLayout.setError(getString(R.string.validation_msg_vk));
            }
            return false;
        }

        String userGit = ValidatorUtils.getValidatedGitUrl(mUserGit.getText().toString());
        if (userGit != null) {
            mUserGit.setText(userGit);
            mUserGitLayout.setErrorEnabled(false);
        } else {
            if (showMsg) {
                setFieldFocus(mUserGit, true);
                mUserGitLayout.setError(getString(R.string.validation_msg_git));
            }
            return false;
        }

        return true;
    }

    /**
     * Устанавливает фокус ввода на представление, заданное параметром
     *
     * @param view         представление, принимающее фокус
     * @param hideKeyboard запретить (разрешить) отображение клавиатуры
     */
    private void setFieldFocus(View view, boolean hideKeyboard) {
        if (view != null) {
            view.requestFocus();
            if (hideKeyboard) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mUserMail.getWindowToken(), 0);
            }
        }
    }

    /**
     * Загружает пользовательские данные в поля профайла
     */
    private void initUserFields() {
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();
        for (int i = 0; i < userData.size(); i++) {
            mUserInfoViews.get(i).setText(userData.get(i));
        }
    }

    /**
     * Сохраняет пользовательские данные, введенные в поля профайла
     */
    private void saveUserFields() {
        List<String> userData = new ArrayList<>();
        for (EditText userFieldView : mUserInfoViews) {
            userData.add(userFieldView.getText().toString());
        }
        mDataManager.getPreferencesManager().saveUserProfileData(userData);
    }

    /**
     * Загружает данные пользователя (рейтинг, строки кода, проекта) из Shared Preferences
     * в текстовые поля заголовка профиля
     */
    private void initUserInfoValues() {
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileValues();
        for (int i = 0; i < userData.size(); i++) {
            mUserValueViews.get(i).setText(userData.get(i));
        }
    }

    /**
     * Запускает получение фотографии из галлереи
     */
    private void loadPhotoFromGallery() {
        Intent takeGalleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        takeGalleryIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(takeGalleryIntent,
                getString(R.string.chose_gallery_photo)),
                ConstantManager.REQUEST_GALLERY_PICTURE);
    }

    /**
     * Запускает получение фотографии из камеры
     */
    private void loadPhotoFromCamera() {
        // проверка разрешений для Android 6
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent takeCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            try {
                mPhotoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (mPhotoFile != null) {
                takeCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
                startActivityForResult(takeCaptureIntent, ConstantManager.REQUEST_CAMERA_PICTURE);
            }
        } else {
            // запрос разрешений на использование камеры и внешнего хранилища данных
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, ConstantManager.CAMERA_REQUEST_PERMISSION_CODE);

            // предоставление пользователю возможности установить разрешения,
            // если он ранее запретил их и выбрал опцию "не показывать больше"
            Snackbar.make(mCoordinatorLayout, R.string.snackbar_msg_permissions_request, Snackbar.LENGTH_LONG)
                    .setAction(R.string.action_allow_permissions, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openApplicationSettings();
                        }
                    }).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ConstantManager.CAMERA_REQUEST_PERMISSION_CODE
                && grantResults.length == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }

            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }

    /**
     * Скрывает плейсхолдер фотографии в профайле пользователя
     */
    private void hideProfilePlaceholder() {
        mProfilePlaceholder.setVisibility(View.GONE);
    }

    /**
     * Отображает плейсхолдер фотографии в профайле пользователя
     */
    private void showProfilePlaceholder() {
        mProfilePlaceholder.setVisibility(View.VISIBLE);
    }

    /**
     * Блокирует сворачивание Collapsing Toolbar
     */
    private void lockToolbar() {
        mAppBarLayout.setExpanded(true, true);
        // сбрасывает флаги скроллинга
        mAppBarParams.setScrollFlags(0);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    /**
     * Разблокирует сворачивание Collapsing Toolbar
     */
    private void unlockToolbar() {
        mAppBarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL |
                AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case ConstantManager.LOAD_PROFILE_PHOTO:
                String[] selectItems = {
                        getString(R.string.user_profile_dialog_gallery),
                        getString(R.string.user_profile_dialog_camera),
                        getString(R.string.user_profile_dialog_cancel)
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.user_profile_placeholder_image_title));
                builder.setItems(selectItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int choiceItem) {
                        switch (choiceItem) {
                            case 0:
                                loadPhotoFromGallery();
                                break;
                            case 1:
                                loadPhotoFromCamera();
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


    /**
     * Создает файл с для хранения изображения во внешнем хранилище
     *
     * @param imageFileName имя файла
     * @param bitmap        растровое изображение для сохранения в файле
     * @return представление созданного файла изображения
     * @throws IOException
     */
    private File createImageFileFromBitmap(String imageFileName, Bitmap bitmap) throws IOException {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            File storageDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            File imageFile = null;
            try {
                imageFile = new File(storageDir, imageFileName + ".jpg");

                FileOutputStream fos = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                fos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            // Android 6 ???: сохранение изображения с добавлением в галлерею
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.MediaColumns.DATA, imageFile.getAbsolutePath());

            this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            return imageFile;
        } else {
            // запрос разрешений на использование камеры и внешнего хранилища данных
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, ConstantManager.CAMERA_REQUEST_PERMISSION_CODE);

            // предоставление пользователю возможности установить разрешения,
            // если он ранее запретил их и выбрал опцию "не показывать больше"
            Snackbar.make(mCoordinatorLayout, R.string.snackbar_msg_permissions_request, Snackbar.LENGTH_LONG)
                    .setAction(R.string.action_allow_permissions, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openApplicationSettings();
                        }
                    }).show();

            return null;
        }

    }

    /**
     * Создает файл с уникальным именем для хранения фотографии во внешнем хранилище
     *
     * @return объект {@link File} для созданного файла
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Android 6 ???: сохранение изображения с добавлением в галлерею
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, image.getAbsolutePath());

        this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        return image;
    }

    /**
     * Помещает в профиль пользователя фотографию с заданным URI,
     * сохраняет URI фотографии в Shared Preferences,
     * загружает фотографию на сервер, если URI изменился
     *
     * @param selectedImage URI изображения
     */
    private void insertProfileImage(Uri selectedImage) {

        Picasso.with(this)
                .load(selectedImage)
                .into(mProfileImage);

        // если URI фотографии изменился
        if (!selectedImage.equals(mDataManager.getPreferencesManager().loadUserPhoto())) {

            if (selectedImage.getLastPathSegment().endsWith(".jpg")) {
                uploadPhoto(new File(selectedImage.getPath()));
            } else {
                uploadPhoto(new File(getPath(selectedImage)));
            }

            mDataManager.getPreferencesManager().saveUserPhoto(selectedImage);
        }

    }

    /**
     * Возвращает путь к файлу для заданного URI
     * (используется для корректной выгрузки на сервер при выборе файла в галерее)
     *
     * @param uri URI файла
     * @return путь к файлу
     */
    @Nullable
    private String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) {
            return null;
        }
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(columnIndex);
        cursor.close();
        return path;
    }

    /**
     * Открывает настройки приложения
     */
    public void openApplicationSettings() {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getPackageName()));

        startActivityForResult(appSettingsIntent, ConstantManager.PERMISSION_REQUEST_SETTINGS_CODE);
    }


    /**
     * Инициирует телефонный звонок на заданный номер
     *
     * @param phoneStr номер телефона
     */
    private void callPhone(String phoneStr) {
        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneStr));
        startActivity(dialIntent);
    }

    /**
     * Инициирует отправку письма по электронной почте
     *
     * @param email адрес электронной почты
     */
    private void sendMail(String email) {
        Intent mailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
        startActivity(Intent.createChooser(mailIntent, getString(R.string.chooser_title_send_mail)));
    }

    /**
     * Открывает ссылку в браузере по заданному URL-адресу
     *
     * @param url URL-адрес
     */
    private void browseUrl(String url) {
        Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browseIntent);
    }

    private void setUserProfileDummyValues() {
        mUserValueRating.setText(getString(R.string.string_value_1));
        mUserValueCodeLines.setText(getString(R.string.string_value_2));
        mUserValueProjects.setText(getString(R.string.string_value_3));

        mUserPhone.setText(getString(R.string.string_phone_info));
        mUserMail.setText(getString(R.string.string_mail_info));
        mUserVk.setText(getString(R.string.string_vk_info));
        mUserGit.setText(getString(R.string.string_github_info));
        mUserBio.setText(getString(R.string.string_inform));
    }
}