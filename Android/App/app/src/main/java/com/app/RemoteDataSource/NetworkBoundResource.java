package com.app.RemoteDataSource;


import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.app.Utilities.AppExecutors;

import java.util.Objects;

/**
 * A generic class that can provide a resource backed by both the SQLite database and the network.
 * <p>You can read more about it in the <a href="https://developer.android.com/arch">Architecture
 * Guide</a>.</p>
 *
 * @param <ResultType>  The SQLite type entity
 * @param <RequestType> The Network type entity
 */
public abstract class NetworkBoundResource<ResultType, RequestType> {

    private final AppExecutors appExecutors;
    private final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();

    @MainThread
    public NetworkBoundResource(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;

        result.setValue(Resource.loading(null));

        // Always load the data from DB intially so that we have
        LiveData<ResultType> dbSource = loadFromDb();

        // Fetch the data from network and add it to the resource
        result.addSource(dbSource, data -> {
            result.removeSource(dbSource);
            if (shouldFetch(data)) {
                fetchFromNetwork(dbSource);
            } else {
                result.addSource(dbSource, newData -> {
                    processData(newData);
                    result.setValue(Resource.success(newData));
                });
            }
        });
    }

    @MainThread
    private void setValue(Resource<ResultType> newValue) {
        if (!Objects.equals(result.getValue(), newValue)) {
            result.setValue(newValue);
        }
    }

    @MainThread
    private void fetchFromNetwork(final LiveData<ResultType> dbSource) {
        LiveData<ApiResponse<RequestType>> apiResponse = createCall();
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(dbSource, newData -> setValue(Resource.loading(newData)));
        result.addSource(apiResponse, response -> {
            result.removeSource(apiResponse);
            result.removeSource(dbSource);
            //noinspection ConstantConditions
            if (response.isSuccessful()) {
                appExecutors.diskIO().execute(() -> {
                    saveCallResult(processResponse(response));
                    appExecutors.mainThread().execute(() ->
                            // we specially request a new live data,
                            // otherwise we will getTags immediately last cached value,
                            // which may not be updated with latest results received from network.
                            result.addSource(loadFromDb(), newData -> {
                                processData(newData);
                                result.setValue(Resource.success(newData));
                            })
                    );
                });
            } else {
                onFetchFailed();
                result.addSource(dbSource,
                        newData -> setValue(Resource.error(response.errorMessage, newData)));
            }
        });
    }

    /**
     * Returns a LiveData object that represents the resource that's implemented
     * in the base class.
     *
     * @return
     */
    public LiveData<Resource<ResultType>> asLiveData() {
        return result;
    }

    /**
     * Called to getTags the cached data from the database.
     *
     * @return
     */
    @NonNull
    @MainThread
    protected abstract LiveData<ResultType> loadFromDb();

    /**
     * Called with the data in the database to decide whether to fetch
     * potentially updated data from the network.
     *
     * @param data
     * @return
     */
    @MainThread
    protected abstract boolean shouldFetch(@Nullable ResultType data);

    /**
     * Called to create the API call.
     *
     * @return
     */
    @NonNull
    @MainThread
    protected abstract LiveData<ApiResponse<RequestType>> createCall();

    /**
     * Called to save the result of the API response into the database.
     *
     * @param item
     */
    @WorkerThread
    protected abstract void saveCallResult(@NonNull RequestType item);

    /**
     * Called when the fetch fails. The child class may want to reset components
     * like rate limiter.
     */
    protected void onFetchFailed() {

    }

    @WorkerThread
    protected void processData(ResultType newData) {
    }

    @WorkerThread
    protected RequestType processResponse(ApiResponse<RequestType> response) {
        return response.body;
    }
}
