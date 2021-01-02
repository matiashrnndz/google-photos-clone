package com.app.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.app.Injection.MyApplication;
import com.app.LocalDataSource.Model.User;
import com.app.R;
import com.app.Utilities.ConnectionChecker;
import com.app.ViewModel.SignupViewModel;
import com.app.ViewModel.ViewModelFactory;
import com.google.android.material.button.MaterialButton;

import java.util.Calendar;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignupActivity extends SimpleLifecycleActivity {

    @Inject
    ViewModelFactory viewModelFactory;
    SignupViewModel viewModel;
    ProgressDialog progressDialog;

    @BindView(R.id.SignUpTxtEmail)
    EditText email;
    @BindView(R.id.SignUpTxtName)
    EditText name;
    @BindView(R.id.SignUpTxtPhone)
    EditText phone;
    @BindView(R.id.SignUpTxtPassword)
    EditText password;
    @BindView(R.id.SignUpTxtConfirmPassword)
    EditText passwordConfirmation;
    @BindView(R.id.SignupBtnDatePicker)
    MaterialButton bornDate;

    private int day;
    private int month;
    private int year;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        progressDialog = ConnectionChecker.getProgressDialog(this, "Please wait...");
        ButterKnife.bind(this);

        //Instance of viewmodel and getTags things injected
        ((MyApplication) getApplication()).getAppComponent().doInjection(this);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SignupViewModel.class);

        //Calendar instance for current date, month and year
        Calendar calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Observe the result and update our UI upon changes
        viewModel.signupResponse.observe(this, userResource -> {
            Log.d("Status ", "" + userResource.status);
            switch (userResource.status) {
                case LOADING:
                    progressDialog.show();
                    break;
                case SUCCESS:
                    progressDialog.dismiss();
                    User data = userResource.data;
                    Toast.makeText(SignupActivity.this, "ID : " + data.getId() + "Email : " + data.getEmail(), Toast.LENGTH_SHORT).show();
                    break;
                case ERROR:
                    progressDialog.dismiss();
                    Log.e("Error", userResource.message);
                    Toast.makeText(SignupActivity.this, userResource.message, Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        });
    }

    @OnClick(R.id.SignupBtnDatePicker)
    void onCalendarClicked() {
        com.wdullaer.materialdatetimepicker.date.DatePickerDialog dialog = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
                (view, year, monthOfYear, dayOfMonth) -> {
                    String date = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                    bornDate.setText(date);
                }, year, month, day);

        dialog.show(getFragmentManager(), "DatePickerDialog");
    }

    @OnClick(R.id.SignUpBtnRegister)
    void onRegisterClicked() {
        if (isValid()) {
            if (!ConnectionChecker.checkInternetConnection(this)) {
                Toast.makeText(SignupActivity.this, getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            } else {
                viewModel.signup(
                        email.getText().toString().trim(),
                        password.getText().toString().trim(),
                        passwordConfirmation.getText().toString().trim(),
                        name.getText().toString().trim(),
                        bornDate.getText().toString().trim(),
                        phone.getText().toString().trim());
            }
        } else {
            Toast.makeText(SignupActivity.this, "Not valid inputs", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.SignupBtnSignIn)
    void onSigninClicked() {
        Intent signinIntent = new Intent(this, SigninActivity.class);
        startActivity(signinIntent);
    }

    @OnClick(R.id.SignupBtnSignUp)
    void onSignupClicked() {
        clearData();
    }

    private void clearData() {
        name.setText("");
        phone.setText("");
        email.setText("");
        password.setText("");
        passwordConfirmation.setText("");
    }

    private boolean isValid() {
        return !name.getText().toString().equals("") &&
                !phone.getText().toString().equals("") &&
                !email.getText().toString().equals("") &&
                !password.getText().toString().equals("") &&
                !passwordConfirmation.getText().toString().equals("") &&
                !bornDate.getText().toString().equals("BIRTH DATE") &&
                password.getText().toString().equals(passwordConfirmation.getText().toString());
    }
}
