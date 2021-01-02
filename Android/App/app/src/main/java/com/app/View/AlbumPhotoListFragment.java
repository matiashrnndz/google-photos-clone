package com.app.View;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.Injection.MyApplication;
import com.app.LocalDataSource.Model.Photo;
import com.app.R;
import com.app.Utilities.ConnectionChecker;
import com.app.Utilities.RecycleViewUtility;
import com.app.View.Adapter.ThumbnailsRecyclerViewAdapter;
import com.app.ViewModel.AlbumViewModel;
import com.app.ViewModel.PhotoViewModel;
import com.app.ViewModel.ViewModelFactory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumPhotoListFragment extends Fragment implements ThumbnailsRecyclerViewAdapter.ItemClickListener {

    @Inject
    ViewModelFactory viewModelFactory;
    PhotoViewModel photoViewModel;
    AlbumViewModel albumViewModel;

    ThumbnailsRecyclerViewAdapter adapter;
    List<Photo> photos = new ArrayList<>();

    String albumId = "";

    ProgressDialog progressDialog;

    public AlbumPhotoListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album_photo_list, container, false);
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            albumId = getArguments().getString("albumId");
        }

        //Instance of viewmodel and getTags things injected
        ((MyApplication) getActivity().getApplication()).getAppComponent().doInjection(this);
        photoViewModel = ViewModelProviders.of(this, viewModelFactory).get(PhotoViewModel.class);
        albumViewModel = ViewModelProviders.of(this, viewModelFactory).get(AlbumViewModel.class);

        progressDialog = ConnectionChecker.getProgressDialog(getActivity(), "Please wait...");
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initRecycleView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        observeGetAllByAlbumResponse();
    }

    @Override
    public void onStart() {
        super.onStart();
        albumViewModel.getAllByAlbum(albumId);
    }

    private void initRecycleView() {
        RecyclerView recyclerView = getActivity().findViewById(R.id.AlbumPhotosRecicleView);
        int numberOfColumns = RecycleViewUtility.calculateNoOfColumns(getContext(), 140);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));

        adapter = new ThumbnailsRecyclerViewAdapter(getContext(), photos);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    private void observeGetAllByAlbumResponse() {
        albumViewModel.getAllByAlbumResponse.observe(this, resource -> {
            switch (resource.status) {
                case LOADING:
                    progressDialog.show();
                    break;
                case SUCCESS:
                    progressDialog.dismiss();
                    photos.clear();
                    for (int i = 0; i < resource.data.size(); i++) {
                        photos.add(resource.data.get(i));
                    }
                    adapter.notifyDataSetChanged();
                    break;
                case ERROR:
                    progressDialog.dismiss();
                    Log.e("Error", resource.message);
                    Toast.makeText(getContext(), resource.message, Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        if (ThumbnailsRecyclerViewAdapter.longClick) {

        } else {
            onPhotoDetail(view, position);
        }
    }

    private void onPhotoDetail(View view, int position) {
        Bundle bundle = new Bundle();
        bundle.putString("photoId", photos.get(position).getId());

        photos.clear();
        adapter.notifyDataSetChanged();

        Fragment fragment = new PhotoDetailFragment();
        fragment.setArguments(bundle);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.contentFragment, fragment);
        transaction.addToBackStack("photoDetail");
        transaction.commit();
    }

}
