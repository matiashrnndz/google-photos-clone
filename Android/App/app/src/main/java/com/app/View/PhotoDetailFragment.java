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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.Injection.MyApplication;
import com.app.LocalDataSource.Model.Photo;
import com.app.LocalDataSource.Model.Tag;
import com.app.R;
import com.app.Utilities.ConnectionChecker;
import com.app.View.Adapter.TagsRecyclerViewAdapter;
import com.app.ViewModel.PhotoViewModel;
import com.app.ViewModel.ViewModelFactory;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoDetailFragment extends Fragment implements TagsRecyclerViewAdapter.ItemClickListener, OnMapReadyCallback {

    @Inject
    ViewModelFactory viewModelFactory;
    PhotoViewModel viewModel;

    private TagsRecyclerViewAdapter adapter;
    private List<Tag> tags = new ArrayList<>();

    GoogleMap googleMap;
    MapView mMapView;
    View mView;

    @BindView(R.id.PhotoDetailTxtLocationDate)
    TextView locationDate;
    @BindView(R.id.PhotoDetailTxtLatitudeLongitude)
    TextView latitudeLongitude;
    @BindView(R.id.PhotoDetailImg)
    ImageView image;
    @BindView(R.id.PhotoDetailTxtAddTag)
    TextView addTag;
    @BindView(R.id.PhotoDetailTweet)
    ImageView tweet;

    private String photoId = "";
    private Photo photo;

    ProgressDialog progressDialog;

    public PhotoDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_photo_detail, container, false);
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            photoId = getArguments().getString("photoId");
        }

        //Instance of viewmodel and getTags things injected
        ((MyApplication) getActivity().getApplication()).getAppComponent().doInjection(this);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(PhotoViewModel.class);
        progressDialog = ConnectionChecker.getProgressDialog(getActivity(), "Please wait...");
        ButterKnife.bind(this, mView);

        addTag.setOnClickListener(v -> onAddTagClick(v));
        tweet.setOnClickListener(v -> onTweetClick(v));

        return mView;
    }

    private void onTweetClick(View v) {
        TweetComposer.Builder builder = new TweetComposer.Builder(getContext())
                .text(photo.getUrl());
        builder.show();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up the RecyclerView
        RecyclerView recyclerView = getView().findViewById(R.id.PhotoDetailTagsRecyclerView);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getView().getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        adapter = new TagsRecyclerViewAdapter(getView().getContext(), tags);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onMapReady(GoogleMap gMap) {
        MapsInitializer.initialize(getContext());

        googleMap = gMap;
        googleMap.setMapType(googleMap.MAP_TYPE_SATELLITE);

        if (photo != null) {
            if (photo.getLatitude() != null && photo.getLongitude() != null) {
                LatLng location = new LatLng(Float.parseFloat(photo.getLatitude()), Float.parseFloat(photo.getLongitude()));
                googleMap.addMarker(new MarkerOptions().position(location).title("Photo"));
                CameraPosition cameraPosition = CameraPosition.builder().target(new LatLng(Float.parseFloat(photo.getLatitude()), Float.parseFloat(photo.getLongitude()))).zoom(16).bearing(0).tilt(45).build();
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            } else {
                LatLng uruguay = new LatLng(-34.906604, -56.203852);
                googleMap.addMarker(new MarkerOptions().position(uruguay).title("Uruguay"));
                CameraPosition cameraPosition = CameraPosition.builder().target(new LatLng(-34.906604, -56.203852)).zoom(8).bearing(0).tilt(45).build();
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        observeGetTagsResponse();
        observeGetPhotoResponse();
        observeAddTagResponse();
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.getPhoto(photoId);
    }

    private void initMap() {
        mMapView = (MapView) mView.findViewById(R.id.map);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    private void observeGetPhotoResponse() {
        viewModel.getPhotoResponse.observe(this, resource -> {
            switch (resource.status) {
                case LOADING:
                    progressDialog.show();
                    break;
                case SUCCESS:
                    progressDialog.dismiss();
                    photo = resource.data;
                    if (!photoId.isEmpty()) {
                        viewModel.getTags(photoId);
                    }
                    initMap();
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

    private void observeGetTagsResponse() {
        viewModel.getTagsResponse.observe(this, resource -> {
            switch (resource.status) {
                case LOADING:
                    progressDialog.show();
                    break;
                case SUCCESS:
                    progressDialog.dismiss();
                    photo.setTags(resource.data);

                    displayLatAndLon();
                    displayLocation();
                    loadImage();
                    clearTags();

                    if (resource.data != null) {
                        for (int i = 0; i < resource.data.size(); i++) {
                            tags.add(resource.data.get(i));
                        }
                        adapter.notifyDataSetChanged();
                    }

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

    private void observeAddTagResponse() {
        //Observe the result and update our UI upon changes
        viewModel.addTagResponse.observe(this, resource -> {
            switch (resource.status) {
                case LOADING:
                    progressDialog.show();
                    break;
                case SUCCESS:
                    progressDialog.dismiss();

                    photo = resource.data;
                    photoId = resource.data.getId();

                    clearTags();

                    if (resource.data != null) {
                        if (resource.data.getTags() != null) {
                            for (int i = 0; i < resource.data.getTags().size(); i++) {
                                tags.add(resource.data.getTags().get(i));
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }

                    Toast.makeText(getContext(), "Added!", Toast.LENGTH_LONG).show();

                    break;
                case ERROR:
                    progressDialog.dismiss();
                    Log.e("Error", resource.message);
                    break;
                default:
                    break;
            }
        });
    }

    private void clearTags() {
        tags.clear();
    }

    private void loadImage() {
        Picasso.get().load(photo.getUrl()).into(image);
    }

    private void displayLocation() {
        String location = "";
        if (photo.getLocation() != null) {
            location = photo.getLocation();
        } else {
            location = location + " -- ";
        }
        locationDate.setText(location);
    }

    private void displayLatAndLon() {
        String latitude = "lat: ";
        if (photo.getLatitude() != null) {
            latitude = latitude + photo.getLatitude() + " ";
        } else {
            latitude = latitude + " -- " + " ";
        }
        String longitude = "lon: ";
        if (photo.getLongitude() != null) {
            longitude = longitude + photo.getLongitude() + " ";
        } else {
            longitude = longitude + " -- ";
        }
        latitudeLongitude.setText(latitude + longitude);
    }

    @Override
    public void onItemClick(View view, int position) {
        if (TagsRecyclerViewAdapter.longClick) {
            onDeleteTag(view, position);
        }
    }

    private void onDeleteTag(View view, int position) {
        viewModel.deleteTag(photo.getId(), tags.get(position).getId());
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

        String tagName = "";
        alert.setPositiveButton("Add!", (dialog, whichButton) -> {
            //Editable YouEditTextValue = edittext.getText();
            final String editText = edittext.getText().toString().trim();

            if (!editText.isEmpty()) {
                viewModel.addTag(photoId, editText);
            }
        });

        alert.show();
    }

}
