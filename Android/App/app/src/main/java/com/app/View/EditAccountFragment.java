package com.app.View;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.app.Injection.MyApplication;
import com.app.R;
import com.app.Utilities.ConnectionChecker;
import com.app.ViewModel.EditAccountViewModel;
import com.app.ViewModel.ViewModelFactory;
import com.google.android.material.button.MaterialButton;

import java.util.Calendar;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditAccountFragment extends Fragment {

    @Inject
    ViewModelFactory viewModelFactory;
    EditAccountViewModel viewModel;

    ProgressDialog progressDialog;

    @BindView(R.id.EditAccountTxtName)
    EditText name;
    @BindView(R.id.EditAccountTxtPhone)
    EditText phone;
    @BindView(R.id.EditAccountTxtPassword)
    EditText password;
    @BindView(R.id.EditAccountTxtConfirmPassword)
    EditText passwordConfirmation;
    @BindView(R.id.EditAccountBtnDatePicker)
    MaterialButton bornDate;

    private int day;
    private int month;
    private int year;

    public EditAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_account, container, false);
        super.onCreate(savedInstanceState);

        //Instance of viewmodel and getTags things injected
        ((MyApplication) getActivity().getApplication()).getAppComponent().doInjection(this);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(EditAccountViewModel.class);
        progressDialog = ConnectionChecker.getProgressDialog(getActivity(), "Please wait...");
        ButterKnife.bind(this, view);

        //Calendar instance for current date, month and year
        Calendar calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Observe the result and update our UI upon changes
        viewModel.userUpdateResponse.observe(this, userResource -> {
            Log.d("Status ", "" + userResource.status);
            switch (userResource.status) {
                case LOADING:
                    progressDialog.show();
                    break;
                case SUCCESS:
                    progressDialog.dismiss();
                    Log.e("UserUpdated", userResource.data.getEmail());
                    Toast.makeText(getActivity(), "Updated!", Toast.LENGTH_SHORT).show();
                    break;
                case ERROR:
                    progressDialog.dismiss();
                    Log.e("Error", userResource.message);
                    Toast.makeText(getActivity(), userResource.message, Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        });
    }

    @OnClick(R.id.EditAccountBtnDatePicker)
    void onCalendarClicked() {
        com.wdullaer.materialdatetimepicker.date.DatePickerDialog dialog = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
                (view, year, monthOfYear, dayOfMonth) -> {
                    String date = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                    bornDate.setText(date);
                }, year, month, day);

        dialog.show(getActivity().getFragmentManager(), "DatePickerDialog");
    }

    @OnClick(R.id.EditAccountBtnUpdate)
    void onUpdateClicked() {
        if (isValid()) {
            if (!ConnectionChecker.checkInternetConnection(getActivity())) {
                Toast.makeText(getActivity(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            } else {
                viewModel.userUpdate(
                        name.getText().toString().trim(),
                        password.getText().toString().trim(),
                        passwordConfirmation.getText().toString().trim(),
                        bornDate.getText().toString().trim(),
                        phone.getText().toString().trim());
            }
        } else {
            Toast.makeText(getActivity(), "Not valid inputs", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValid() {
        return !name.getText().toString().equals("") &&
                !phone.getText().toString().equals("") &&
                !password.getText().toString().equals("") &&
                !passwordConfirmation.getText().toString().equals("") &&
                !bornDate.getText().toString().equals("BIRTH DATE");
    }
}
