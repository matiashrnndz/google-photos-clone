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
import com.app.LocalDataSource.Model.Album;
import com.app.R;
import com.app.Utilities.ConnectionChecker;
import com.app.Utilities.RecycleViewUtility;
import com.app.View.Adapter.AlbumsRecyclerViewAdapter;
import com.app.ViewModel.AlbumViewModel;
import com.app.ViewModel.ViewModelFactory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumsFragment extends Fragment implements AlbumsRecyclerViewAdapter.ItemClickListener {

    @Inject
    ViewModelFactory viewModelFactory;
    AlbumViewModel viewModel;

    AlbumsRecyclerViewAdapter adapter;
    List<Album> albums = new ArrayList<>();

    ProgressDialog progressDialog;

    public AlbumsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_albums, container, false);
        super.onCreate(savedInstanceState);

        //Instance of viewmodel and getTags things injected
        ((MyApplication) getActivity().getApplication()).getAppComponent().doInjection(this);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(AlbumViewModel.class);
        progressDialog = ConnectionChecker.getProgressDialog(getActivity(), "Please wait...");
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initRecycleView();
        viewModel.clear();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        observeGetAllResponse();
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.getAll();
    }

    private void initRecycleView() {
        RecyclerView recyclerView = getActivity().findViewById(R.id.AlbumsRecicleView);
        int numberOfColumns = RecycleViewUtility.calculateNoOfColumns(getContext(), 140);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));

        adapter = new AlbumsRecyclerViewAdapter(getContext(), albums);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    private void observeGetAllResponse() {
        viewModel.getAllResponse.observe(this, resource -> {
            switch (resource.status) {
                case LOADING:
                    progressDialog.show();
                    break;
                case SUCCESS:
                    progressDialog.dismiss();
                    albums.clear();
                    if (resource.data != null) {
                        for (int i = 0; i < resource.data.size(); i++) {
                            albums.add(resource.data.get(i));
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

    @Override
    public void onItemClick(View view, int position) {
        if (AlbumsRecyclerViewAdapter.longClick) {
            onDelete(view, position);
        } else {
            onDetail(view, position);
        }
    }

    private void onDelete(View view, int position) {
        viewModel.delete(albums.get(position).getId());
        albums.remove(position);
        adapter.notifyDataSetChanged();
        Toast.makeText(getContext(), "Deleted!", Toast.LENGTH_LONG).show();
    }

    private void onDetail(View view, int position) {
        Bundle bundle = new Bundle();
        bundle.putString("albumId", albums.get(position).getId());

        albums.clear();
        adapter.notifyDataSetChanged();

        Fragment fragment = new AlbumPhotoListFragment();
        fragment.setArguments(bundle);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.contentFragment, fragment);
        transaction.addToBackStack("albumDetail");
        transaction.commit();
    }
}
