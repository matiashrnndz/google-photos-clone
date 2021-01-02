package com.app.View;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.Injection.MyApplication;
import com.app.LocalDataSource.Model.Tag;
import com.app.R;
import com.app.Utilities.ConnectionChecker;
import com.app.View.Adapter.TagsRecyclerViewAdapter;
import com.app.ViewModel.AlbumViewModel;
import com.app.ViewModel.ViewModelFactory;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewAlbumFragment extends Fragment implements TagsRecyclerViewAdapter.ItemClickListener {

    @Inject
    ViewModelFactory viewModelFactory;
    AlbumViewModel viewModel;

    private TagsRecyclerViewAdapter adapter;
    private List<Tag> tags = new ArrayList<>();

    @BindView(R.id.NewAlbumTxtAddTag)
    TextView addTag;
    @BindView(R.id.NewAlbumTxtAlbumName)
    EditText name;
    @BindView(R.id.NewAlbumBtnGenerate)
    MaterialButton generate;

    ProgressDialog progressDialog;

    public NewAlbumFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_album, container, false);
        super.onCreate(savedInstanceState);

        //Instance of viewmodel and getTags things injected
        ((MyApplication) getActivity().getApplication()).getAppComponent().doInjection(this);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(AlbumViewModel.class);
        progressDialog = ConnectionChecker.getProgressDialog(getActivity(), "Please wait...");
        ButterKnife.bind(this, view);

        addTag.setOnClickListener(v -> onAddTagClick(v));
        generate.setOnClickListener(v -> onGenerateClicked(v));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up the RecyclerView
        RecyclerView recyclerView = getView().findViewById(R.id.NewAlbumTagsRecyclerView);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getView().getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        adapter = new TagsRecyclerViewAdapter(getView().getContext(), tags);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        observeAddAlbumResponse();
    }

    private void observeAddAlbumResponse() {
        viewModel.albumAddResponse.observe(this, resource -> {
            switch (resource.status) {
                case LOADING:
                    progressDialog.show();
                    break;
                case SUCCESS:
                    progressDialog.dismiss();
                    tags.clear();
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getActivity(), "Album added!", Toast.LENGTH_LONG).show();
                    break;
                case ERROR:
                    progressDialog.dismiss();
                    Log.e("Error", resource.message);
                    Toast.makeText(getActivity(), resource.message, Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        if (TagsRecyclerViewAdapter.longClick) {
            onDeleteTag(view, position);
        }
    }

    private void onDeleteTag(View view, int position) {
        tags.remove(position);
        adapter.notifyDataSetChanged();

        Toast.makeText(view.getContext(), "Deleted!", Toast.LENGTH_LONG).show();
    }

    public void onAddTagClick(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);

        final EditText edittext = new EditText(getActivity());
        edittext.setTextColor(Color.parseColor("#c96f53"));

        alert.setTitle("Name the Tag :");
        alert.setView(edittext);

        alert.setPositiveButton("Add!", (dialog, whichButton) -> {
            final String editText = edittext.getText().toString().trim();

            if (!editText.isEmpty()) {
                tags.add(new Tag("0", "0", editText));
                adapter.notifyDataSetChanged();
            }
        });

        alert.show();
    }

    void onGenerateClicked(View v) {
        if (isValid()) {
            if (!ConnectionChecker.checkInternetConnection(getActivity())) {
                Toast.makeText(getActivity(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            } else {
                viewModel.add(name.getText().toString(), tags);
            }
        } else {
            Toast.makeText(getActivity(), "Not valid inputs", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValid() {
        return !name.getText().equals("");
    }

}
