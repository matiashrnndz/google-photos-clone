package com.app.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.app.Injection.MyApplication;
import com.app.R;
import com.app.Utilities.ConnectionChecker;
import com.app.ViewModel.PhotoViewModel;
import com.app.ViewModel.SignoutViewModel;
import com.app.ViewModel.ViewModelFactory;

import javax.inject.Inject;

public class HomeActivity extends SimpleLifecycleActivity {

    @Inject
    ViewModelFactory viewModelFactory;
    SignoutViewModel signoutViewModel;
    PhotoViewModel photoViewModel;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        progressDialog = ConnectionChecker.getProgressDialog(this, "Please wait...");

        initTopToolBar();
        initViewModel();
        initPhotoList();
    }

    private void initViewModel() {
        ((MyApplication) getApplication()).getAppComponent().doInjection(this);
        signoutViewModel = ViewModelProviders.of(this, viewModelFactory).get(SignoutViewModel.class);
        photoViewModel = ViewModelProviders.of(this, viewModelFactory).get(PhotoViewModel.class);
    }

    private void initTopToolBar() {
        Toolbar toolbar = findViewById(R.id.HomeToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_edit_account:
                    initEditAccount();
                    break;
                case R.id.action_albums:
                    initAlbums();
                    break;
                case R.id.action_new_album:
                    initNewAlbum();
                    break;
                case R.id.action_sign_out:
                    initSignout();
                    break;
                default:
                    break;
            }
            return false;
        });
    }

    private void initSignout() {
        signoutViewModel.signout();
        Intent signinIntent = new Intent(this, SigninActivity.class);
        startActivity(signinIntent);
    }

    private void initPhotoList() {
        Fragment fragment = new PhotoListFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.contentFragment, fragment);
        transaction.addToBackStack("photoList");
        transaction.commit();
    }

    private void initNewAlbum() {
        Fragment fragment = new NewAlbumFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.contentFragment, fragment);
        transaction.addToBackStack("newAlbumDetail");
        transaction.commit();
    }

    private void initAlbums() {
        Fragment fragment = new AlbumsFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.contentFragment, fragment);
        transaction.addToBackStack("albums");
        transaction.commit();
    }

    private void initEditAccount() {
        Fragment fragment = new EditAccountFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.contentFragment, fragment);
        transaction.addToBackStack("editAccount");
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_top_toolbar, menu);

        MenuItem actionMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) actionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String unformattedTags) {
                PhotoListFragment fragment = (PhotoListFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragment);
                fragment.onQueryTextSubmit(unformattedTags);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                PhotoListFragment fragment = (PhotoListFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragment);
                fragment.onQueryTextChange(newText);
                return true;
            }
        });

        return true;
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        signoutViewModel.signout();
        super.onDestroy();
    }

}
