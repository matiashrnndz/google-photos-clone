package com.app.RemoteDataSource;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

public class AuthInterceptor implements Interceptor {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    private static final String NO_AUTHORIZATION = "No-Authorization";
    public static final String NO_AUTHORIZATION_VALUE = NO_AUTHORIZATION + ": true";

    private String token;
    private String uid;

    public AuthInterceptor() {
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();

        // Assuming all API endpoints need authorization, we mark with "No-Authorization"
        // in the header those whom don't.
        if (originalRequest.header(NO_AUTHORIZATION) == null) {
            Timber.v("Request with token");
            if (token == null) {
                throw new RuntimeException("Token not present for secured APIs");
            }
            Request authorisedRequest = originalRequest.newBuilder()
                    .header(AUTHORIZATION, BEARER + token)
                    .build();
            return chain.proceed(authorisedRequest);
        }

        Timber.v("Request without token");
        return chain.proceed(originalRequest);
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void clearToken() {
        this.token = null;
    }

    public boolean hasToken() {
        return token != null;
    }

    private boolean hasAuthorizationHeader(Request originalRequest) {
        return !originalRequest.header(AUTHORIZATION).isEmpty();
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }
}
