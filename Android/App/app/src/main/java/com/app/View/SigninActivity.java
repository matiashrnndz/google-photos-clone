package com.app.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProviders;

import com.app.Injection.MyApplication;
import com.app.LocalDataSource.Model.Session;
import com.app.R;
import com.app.Utilities.ConnectionChecker;
import com.app.ViewModel.SigninViewModel;
import com.app.ViewModel.ViewModelFactory;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SigninActivity extends SimpleLifecycleActivity {

    @Inject
    ViewModelFactory viewModelFactory;

    @BindView(R.id.SigninTxtEmail)
    EditText email;

    @BindView(R.id.SigninTxtPassword)
    EditText password;

    SigninViewModel viewModel;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        progressDialog = ConnectionChecker.getProgressDialog(this, "Please wait...");
        ButterKnife.bind(this);

        //Get an instance of our viewmodel and getTags things injected...
        ((MyApplication) getApplication()).getAppComponent().doInjection(this);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SigninViewModel.class);

        //Observe the result and update our UI upon changes
        viewModel.signinResponse.observe(this, userResource -> {
            Log.d("Status ", "" + userResource.status);
            switch (userResource.status) {
                case LOADING:
                    progressDialog.show();
                    break;
                case SUCCESS:
                    progressDialog.dismiss();
                    Session data = userResource.data;
                    Toast.makeText(SigninActivity.this, data.getUid() + " successful logged in.", Toast.LENGTH_SHORT).show();
                    Intent homeIntent = new Intent(this, HomeActivity.class);
                    startActivity(homeIntent);
                    break;
                case ERROR:
                    progressDialog.dismiss();
                    Log.e("Error", userResource.message);
                    Toast.makeText(SigninActivity.this, userResource.message, Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        });
    }

    @OnClick(R.id.SigninBtnLogIn)
    void onLoginClicked() {
        if (isValid()) {
            if (!ConnectionChecker.checkInternetConnection(this)) {
                Toast.makeText(SigninActivity.this, getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            } else {
                viewModel.signin(
                        email.getText().toString().trim(),
                        password.getText().toString().trim());
            }
        } else {
            Toast.makeText(SigninActivity.this, "Not valid inputs", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.SigninBtnSignIn)
    void onSigninClicked() {
        clearData();
    }

    @OnClick(R.id.SigninBtnSignUp)
    void onSignupClicked() {
        Intent signupIntent = new Intent(this, SignupActivity.class);
        startActivity(signupIntent);
    }

    private void clearData() {
        email.setText("");
        password.setText("");
    }

    private boolean isValid() {
        return !email.getText().toString().equals("") &&
                !password.getText().toString().equals("");
    }
}
