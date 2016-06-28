package com.softdesig.devintensive.ui.activities;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.softdesig.devintensive.R;
import com.softdesig.devintensive.data.managers.DataManager;
import com.softdesig.devintensive.utils.CircerAvatarDrawable;
import com.softdesig.devintensive.utils.ConstantManager;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ConstantManager.TAG + "MainActivity";
    protected int mColorMode;
    private CoordinatorLayout mCoordinatorLayout;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private DataManager mDataManager;
    private EditText mUserPhone, mUserEmail, mUserVk, mUserGit, mUserBio;
    private List<EditText> mUserInfoViews;
    private FloatingActionButton mFab;
    private ImageView mUserAvatar;
    private boolean mCurrentEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDataManager = DataManager.getInstance();

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator_layout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer);
        mFab = (FloatingActionButton) findViewById(R.id.fab);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigator_view);
        mUserAvatar = (ImageView) (navigationView != null ? navigationView.getHeaderView(0).findViewById(R.id.avatar) : null);
        mUserAvatar.setImageBitmap(CircerAvatarDrawable.getRoundedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.avatar)));

//      USER resource
        mUserPhone = (EditText) findViewById(R.id.phone_et);
        mUserEmail = (EditText) findViewById(R.id.email_et);
        mUserVk = (EditText) findViewById(R.id.vk_et);
        mUserGit = (EditText) findViewById(R.id.git_et);
        mUserBio = (EditText) findViewById(R.id.about_et);

        mUserInfoViews = new ArrayList<>();
        mUserInfoViews.add(mUserPhone);
        mUserInfoViews.add(mUserEmail);
        mUserInfoViews.add(mUserVk);
        mUserInfoViews.add(mUserGit);
        mUserInfoViews.add(mUserBio);

        Log.d(TAG, mUserInfoViews.toString());

        if (!isLoadUserInfoValue()) {
            initInfoValue();
        }

        loadUserInfoValue();

        mFab.setOnClickListener(MainActivity.this);
        setupToolBar();
        setupDrawer();

        if (savedInstanceState == null) {

        } else {
            mCurrentEditMode = savedInstanceState.getBoolean(ConstantManager.EDIT_MODE_KEY, false);
            changeEditMode(mCurrentEditMode);
        }

        Log.d(TAG, "onCreate");
    }

    private void showSnackbar(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
        outState.putBoolean(ConstantManager.EDIT_MODE_KEY, mCurrentEditMode);
    }

    private void setupToolBar() {
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigator_view);

        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
//                ToastUtils.showShortMessage(item.toString(), getApplicationContext());
                showSnackbar(item.getTitle().toString());
                item.setCheckable(true);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return false;
            }
        });
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
        saveUserInfoValue();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d(TAG, "onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy");
    }

    private void initInfoValue() {
        List<String> userData = new ArrayList<>();
        userData.add(getResources().getString(R.string.string_phone_info));
        userData.add(getResources().getString(R.string.string_mail_info));
        userData.add(getResources().getString(R.string.string_vk_info));
        userData.add(getResources().getString(R.string.string_github_info));
        userData.add(getResources().getString(R.string.string_inform));
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
        Log.d(TAG, userData.toString());
        for (int index = 0; index < userData.size(); index++) {
            Log.d(TAG, userData.get(index));
            Log.d(TAG, mUserInfoViews.get(index).toString());

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                if (!mCurrentEditMode) {
                    changeEditMode(true);
                    mCurrentEditMode = true;
                } else {
                    changeEditMode(false);
                    mCurrentEditMode = false;
                }
                break;
        }
    }

    private void changeEditMode(boolean mode) {
        if (!mode) {
            mFab.setImageResource(R.drawable.ic_create_black_24dp);
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(false);
                userValue.setFocusable(false);
                userValue.setFocusableInTouchMode(false);
                saveUserInfoValue();
            }
        } else {
            mFab.setImageResource(R.drawable.ic_done_black_24dp);
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(true);
                userValue.setFocusable(true);
                userValue.setFocusableInTouchMode(true);
            }
        }
    }
}
