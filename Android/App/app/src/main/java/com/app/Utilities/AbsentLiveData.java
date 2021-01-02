package com.app.Utilities;

import androidx.lifecycle.LiveData;

public class AbsentLiveData<T> extends LiveData {

    private AbsentLiveData() {
        postValue(null);
    }

    private AbsentLiveData(T data) {
        postValue(data);
    }

    public static <T> LiveData<T> create() {
        //noinspection unchecked
        return new AbsentLiveData();
    }

    public static <T> LiveData<T> create(T data) {
        return new AbsentLiveData(data);
    }
}
