package com.app.View;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
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
import com.app.ViewModel.PhotoViewModel;
import com.app.ViewModel.ViewModelFactory;
import com.google.android.material.bottomappbar.BottomAppBar;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoListFragment extends Fragment implements ThumbnailsRecyclerViewAdapter.ItemClickListener {

    @Inject
    ViewModelFactory viewModelFactory;
    PhotoViewModel photoViewModel;

    ThumbnailsRecyclerViewAdapter adapter;
    List<Photo> photos = new ArrayList<>();

    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int RESULT_LOAD_PHOTO = 1;

    Bitmap imgCapture = null;
    String albumId = "";
    boolean firstStart = true;
    boolean photoTaken = false;

    ProgressDialog progressDialog;

    public PhotoListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photo_list, container, false);
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            albumId = getArguments().getString("albumId");
        }

        initViewModel();

        progressDialog = ConnectionChecker.getProgressDialog(getActivity(), "Please wait...");
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initRecycleView();
        initBottomAppBar();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        observeGetAllResponse();
        observeSearchByTagsResponse();
        observeAddPhotoResponse();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (firstStart) {
            photoViewModel.getAll();
            firstStart = false;
        }
    }

    private void initViewModel() {
        //Instance of viewmodel and getTags things injected
        ((MyApplication) getActivity().getApplication()).getAppComponent().doInjection(this);
        photoViewModel = ViewModelProviders.of(this, viewModelFactory).get(PhotoViewModel.class);
    }

    private void initRecycleView() {
        RecyclerView recyclerView = getActivity().findViewById(R.id.PhotoListRecicleView);
        int numberOfColumns = RecycleViewUtility.calculateNoOfColumns(getContext(), 140);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));

        adapter = new ThumbnailsRecyclerViewAdapter(getContext(), photos);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    private void initBottomAppBar() {
        getActivity().findViewById(R.id.PhotoListFabTakePhoto).setOnClickListener(it -> initTakePhoto(it));
        BottomAppBar bottomAppBar = getActivity().findViewById(R.id.PhotoListBottomAppBar);
        bottomAppBar.setNavigationOnClickListener(v -> initLoadPhoto(v));
    }

    private void initLoadPhoto(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_PHOTO);
    }

    private void initTakePhoto(View view) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, IMAGE_REQUEST_CODE);
    }

    private void observeGetAllResponse() {
        photoViewModel.getAllResponse.observe(this, resource -> {
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

    private void observeSearchByTagsResponse() {
        photoViewModel.searchByTagsResponse.observe(this, resource -> {
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
                    photoViewModel.getAll();
                    Toast.makeText(getContext(), resource.message, Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        });
    }

    private void observeAddPhotoResponse() {
        //Observe the result and update our UI upon changes
        photoViewModel.photoAddResponse.observe(this, resource -> {
            switch (resource.status) {
                case LOADING:
                    progressDialog.show();
                    break;
                case SUCCESS:
                    progressDialog.dismiss();
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
            onDeletePhoto(view, position);
        } else {
            onPhotoDetail(view, position);
        }
    }

    private void onDeletePhoto(View view, int position) {
        photoViewModel.delete(photos.get(position).getId());
        photos.remove(position);
        adapter.notifyDataSetChanged();
        Toast.makeText(getContext(), "Deleted!", Toast.LENGTH_LONG).show();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case IMAGE_REQUEST_CODE:
                manageImageTaken(resultCode, intent);
                break;
            case RESULT_LOAD_PHOTO:
                manageLoadPhotoFromGallery(resultCode, intent);
                break;
            default:
                break;
        }
    }

    private void manageLoadPhotoFromGallery(int resultCode, Intent data) {
        switch (resultCode) {
            case Activity.RESULT_OK:
                try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    photoTaken = true;
                    photoViewModel.photoAdd(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                }
                break;

            case Activity.RESULT_CANCELED:
                Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_LONG).show();
                break;

            default:
                break;
        }
    }

    private void manageImageTaken(int resultCode, Intent intent) {
        switch (resultCode) {
            case Activity.RESULT_OK:
                imgCapture = (Bitmap) intent.getExtras().get("data");
                photoTaken = true;
                photoViewModel.photoAdd(imgCapture);
                break;
            case Activity.RESULT_CANCELED:
                Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

    public void onQueryTextSubmit(String unformattedTags) {
        unformattedTags = unformattedTags.trim();

        if (unformattedTags.isEmpty()) {
            photoViewModel.getAll();
        } else {
            photoViewModel.clearPhotos();
            ArrayList<String> tagsForRequest = new ArrayList<>();
            String[] tags = unformattedTags.split(",");
            for (int i = 0; i < tags.length; i++) {
                tagsForRequest.add(tags[i].trim());
            }
            photoViewModel.searchByTags(tagsForRequest);
        }
    }

    public void onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            photoViewModel.getAll();
        }
    }

}
